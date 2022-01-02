package com.example.goddiary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import io.realm.Realm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeTagFragment extends Fragment {

    int arrayEditTag[] = {R.id.editTag1,R.id.editTag2,R.id.editTag3,R.id.editTag4,R.id.editTag5,
            R.id.editTag6,R.id.editTag7,R.id.editTag8,R.id.editTag9,R.id.editTag10,};
    TextInputEditText editTag[] = new TextInputEditText[10];

    Button buttonTagSave;
    Realm realm;



    public MakeTagFragment() {
        // Required empty public constructor
    }


    public static MakeTagFragment newInstance(String param1, String param2) {
        MakeTagFragment fragment = new MakeTagFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_make_tag, container, false);

        //タグ文字列と１０個のタグエディットを取得してタグエディットに表示
        String[] array = realm.where(System.class).equalTo("id",1).findAll()
                .first().tag.split(",",-1);
        for(int i = 0;i<10;i++){
            editTag[i]=view.findViewById(arrayEditTag[i]);
            editTag[i].setText(array[i+1]);
        }
        //タグ保存ボタンの取得とリスナーをセット
        buttonTagSave = view.findViewById(R.id.buttonTagSave);
        buttonTagSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        System system = realm.where(System.class).equalTo("id",1).findFirst();
                        StringBuilder stringBuilder = new StringBuilder() ;
                        stringBuilder.append("全て,");
                        for(int i = 0;i<9;i++){
                            stringBuilder.append(editTag[i].getText().toString()).append(",");
                        }
                        stringBuilder.append(editTag[9].getText().toString());
                        system.tag = stringBuilder.toString();
                        Snackbar.make(view,"保存しました",Snackbar.LENGTH_SHORT).show();
                    }
                });
                //ビューページャー２のフラグメントリストの更新
                ((ScrollingActivity)getActivity()).makeTagAdapter();
                //((ScrollingActivity)getActivity()).getViewPagerAdapter2().cheangeMakeDiaryFragment2();
            }
        });



        return view;
    }
}