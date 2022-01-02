package com.example.goddiary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ScrollingView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.realm.Realm;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeDiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeDiaryFragment extends Fragment {
    Button buttonDate;
    Button buttonSave;

    Spinner spinnerWeather;
    Spinner spinnerTag;
    TextInputEditText editTitle;
    TextInputEditText editBodyText;
    ImageView imageDiaryPhoto;

    int year;
    int month;
    int weather = 0;
    int tag = 0;
    String date;




    private Realm realm;

    public MakeDiaryFragment() {
        // Required empty public constructor
    }
    public static MakeDiaryFragment newInstance(String date) {
        MakeDiaryFragment fragment = new MakeDiaryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date",date);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //リアムデータベース取得
        realm = Realm.getDefaultInstance();

        //日付ボタンに今日の日付をセット
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        date = String.format("%d年%02d月%02d日",  calendar.get(Calendar.YEAR),  calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

        if(getArguments() != null){
            date = getArguments().getString("date");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //各ビューのインスタンスを取得
        View view = inflater.inflate(R.layout.fragment_make_diary, container, false);
        buttonDate = view.findViewById(R.id.buttonDate);
        buttonSave = view.findViewById(R.id.buttonSave);
        spinnerTag = view.findViewById(R.id.spinnerTag);
        spinnerWeather = view.findViewById(R.id.spinnerWeather);
        editBodyText = view.findViewById(R.id.editBodyText);
        editTitle = view.findViewById(R.id.editTitle);
        imageDiaryPhoto = view.findViewById(R.id.imageDiaryPhoto);
        //imageDiaryPhoto.setImageResource(R.drawable.paku);


        //画像をクリックすると画像選択起動
        imageDiaryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScrollingActivity)getActivity()).requestWriteStorage(ScrollingActivity.flagImageDiary);
               // imageDiaryPhoto.setImageURI(((ScrollingActivity)getActivity()).uri);
                //imageDiaryPhoto.setImageBitmap( ((ScrollingActivity)getActivity()).bitmapMakeDiaryImage);
            }
        });
        // 日付ボタンに日付セット
        buttonDate.setText(date);
        //日付ボタンにリスナーセット、押すとカレンダーを起動
        buttonDate.setOnClickListener(new onButtonDateListener());

        //保存ボタンにリスナーセット、押すと保存
        buttonSave.setOnClickListener(new onButtonSaveListener());

        //天気スピナーにリスナーをセット
        spinnerWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weather = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //タグスピナーにリスナーをセット
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            //タグデータを取得してアダプターを作成する
            public void execute(Realm realm) {
                //String[] array ={"全て"};
                String[] array = realm.where(System.class).equalTo("id",1).findAll().first().tag.split(",",-1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner4, array);
                spinnerTag.setAdapter(adapter);
            }
        });

        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tag = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //今日の日付のデータの有無を確認、データがあれば表示するメソッドの呼び出し
        showDiary();

        return view;

    }
    //日付ボタンリスナー
    private class onButtonDateListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Calendarインスタンスを取得
            final Calendar finalCalender = Calendar.getInstance();
            //DatePickerDialogインスタンスを取得
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year2, int month2, int dayOfMonth) {
                            //setした日付を取得して表示
                            date = String.format("%d年%02d月%02d日", year2, month2 + 1, dayOfMonth);
                            year = year2;
                            month = month2 + 1;
                            buttonDate.setText(date);
                            //今日の日付のデータの有無を確認、データがあれば表示するメソッドの呼び出し
                            showDiary();
                        }
                    },
                    finalCalender.get(Calendar.YEAR),
                    finalCalender.get(Calendar.MONTH),
                    finalCalender.get(Calendar.DATE)
            );
            //dialogを表示
            datePickerDialog.show();

        }
    }
    //保存ボタンリスナー
    private class onButtonSaveListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("保存確認");      //タイトル設定
            alertDialog.setMessage("この内容で保存していいですか？");  //内容(メッセージ)設定

            // OK(肯定的な)ボタンの設定
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OKボタン押下時の処理
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // Diary diary = realm.createObject(Diary.class,"djiaas");
                            Diary diary = realm.where(Diary.class).equalTo("date",date).findFirst();

                            if(diary==null){
                                diary = realm.createObject(Diary.class,date);
                            }
                            diary.title =editTitle.getText().toString();
                            diary.bodyText = editBodyText.getText().toString();
                            diary.year = year;
                            diary.month = month;
                            diary.tag = tag;
                            diary.weather = weather;
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageDiaryPhoto.getDrawable();
                            byte[] bytes = MyUtils.getByteFromImage(bitmapDrawable.getBitmap());
                            if(bytes != null && bytes.length > 0){
                                diary.image = bytes;
                            }

                        }
                    });
                    //Toast.makeText(getActivity(),"保存しました",Toast.LENGTH_LONG);
                    Snackbar.make(view,"保存しました",Snackbar.LENGTH_SHORT).show();
                }
            });
            // NG(否定的な)ボタンの設定
            alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.show();

        }
    }


    //日の日付のデータの有無を確認、データがあれば表示するメソッド

    void showDiary(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Diary diary = realm.createObject(Diary.class,"djiaas");
                Diary diary = realm.where(Diary.class).equalTo("date",date).findFirst();

                if(diary!=null) {
                    editTitle.setText(diary.title);
                    editBodyText.setText(diary.bodyText);
                    spinnerTag.setSelection(diary.tag);
                    spinnerWeather.setSelection(diary.weather);
                    if(diary.image != null && diary.image.length != 0){
                        Bitmap bitmap = MyUtils.getImageFromByte(diary.image);
                        imageDiaryPhoto.setImageBitmap(bitmap);
                    }
                }else{
                    editTitle.setText("");
                    editBodyText.setText("");
                    spinnerTag.setSelection(0);
                    spinnerWeather.setSelection(0);
                    imageDiaryPhoto.setImageResource(R.drawable.noimage);
                }
            }
        });

    }


}