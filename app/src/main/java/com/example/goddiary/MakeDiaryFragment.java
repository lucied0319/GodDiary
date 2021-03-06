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



        //?????????????????????????????????
        realm = Realm.getDefaultInstance();

        //?????????????????????????????????????????????
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        date = String.format("%d???%02d???%02d???",  calendar.get(Calendar.YEAR),  calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

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
        //??????????????????????????????????????????
        View view = inflater.inflate(R.layout.fragment_make_diary, container, false);
        buttonDate = view.findViewById(R.id.buttonDate);
        buttonSave = view.findViewById(R.id.buttonSave);
        spinnerTag = view.findViewById(R.id.spinnerTag);
        spinnerWeather = view.findViewById(R.id.spinnerWeather);
        editBodyText = view.findViewById(R.id.editBodyText);
        editTitle = view.findViewById(R.id.editTitle);
        imageDiaryPhoto = view.findViewById(R.id.imageDiaryPhoto);
        //imageDiaryPhoto.setImageResource(R.drawable.paku);


        //????????????????????????????????????????????????
        imageDiaryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScrollingActivity)getActivity()).requestWriteStorage(ScrollingActivity.flagImageDiary);
               // imageDiaryPhoto.setImageURI(((ScrollingActivity)getActivity()).uri);
                //imageDiaryPhoto.setImageBitmap( ((ScrollingActivity)getActivity()).bitmapMakeDiaryImage);
            }
        });
        // ?????????????????????????????????
        buttonDate.setText(date);
        //???????????????????????????????????????????????????????????????????????????
        buttonDate.setOnClickListener(new onButtonDateListener());

        //?????????????????????????????????????????????????????????
        buttonSave.setOnClickListener(new onButtonSaveListener());

        //?????????????????????????????????????????????
        spinnerWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weather = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //?????????????????????????????????????????????
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            //????????????????????????????????????????????????????????????
            public void execute(Realm realm) {
                //String[] array ={"??????"};
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

        //????????????????????????????????????????????????????????????????????????????????????????????????????????????
        showDiary();

        return view;

    }
    //???????????????????????????
    private class onButtonDateListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Calendar???????????????????????????
            final Calendar finalCalender = Calendar.getInstance();
            //DatePickerDialog???????????????????????????
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year2, int month2, int dayOfMonth) {
                            //set?????????????????????????????????
                            date = String.format("%d???%02d???%02d???", year2, month2 + 1, dayOfMonth);
                            year = year2;
                            month = month2 + 1;
                            buttonDate.setText(date);
                            //????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            showDiary();
                        }
                    },
                    finalCalender.get(Calendar.YEAR),
                    finalCalender.get(Calendar.MONTH),
                    finalCalender.get(Calendar.DATE)
            );
            //dialog?????????
            datePickerDialog.show();

        }
    }
    //???????????????????????????
    private class onButtonSaveListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("????????????");      //??????????????????
            alertDialog.setMessage("?????????????????????????????????????????????");  //??????(???????????????)??????

            // OK(????????????)??????????????????
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OK???????????????????????????
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
                    //Toast.makeText(getActivity(),"??????????????????",Toast.LENGTH_LONG);
                    Snackbar.make(view,"??????????????????",Snackbar.LENGTH_SHORT).show();
                }
            });
            // NG(????????????)??????????????????
            alertDialog.setNegativeButton("???????????????", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.show();

        }
    }


    //??????????????????????????????????????????????????????????????????????????????????????????

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