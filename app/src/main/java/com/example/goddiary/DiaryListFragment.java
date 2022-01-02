package com.example.goddiary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiaryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryListFragment extends Fragment  {

    //各ウィジェットの変数
    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerTag;

    RecyclerView recyclerView;

    private Realm realm;

    int year;
    int month;

    public DiaryListFragment() {
        // Required empty public constructor
    }


    public static DiaryListFragment newInstance(String param1, String param2) {
        DiaryListFragment fragment = new DiaryListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //リアムデータベース取得
        realm = Realm.getDefaultInstance();

        //日付取得
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1 ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //各ウィジェットのオブジェクトを取得
        View view = inflater.inflate(R.layout.fragment_diary_list, container, false);

        spinnerYear = view.findViewById(R.id.spinnerYear);
        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerTag = view.findViewById(R.id.spinnerTag);

        //スピナーの初期位置を設定
        spinnerYear.setSelection(year - 2020);
        spinnerMonth.setSelection(month -1);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String[] array ={"全て"};
                RealmResults<System> results = realm.where(System.class).equalTo("id",1).findAll();
                if(results != null)
                array = results.first().tag.split(",",-1);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner4, array);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                spinnerTag.setAdapter(adapter);

            }
        });

        //スピナーのリスナーをセット
        spinnerYear.setOnItemSelectedListener(new SpinnerListener());
        spinnerMonth.setOnItemSelectedListener(new SpinnerListener());
        spinnerTag.setOnItemSelectedListener(new SpinnerListener());


        //リサイクルビューの取得とアダプターのセット
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //現時点の月のデータを全て取得
        RealmResults<Diary> realmResults = realm.where(Diary.class).equalTo("year",year).equalTo("month", month).findAll().sort("date", Sort.DESCENDING);
        DiaryRealmAdapter adapter = new DiaryRealmAdapter(getActivity(),realm,realmResults,true);
        recyclerView.setAdapter(adapter);

        return view;
    }


    class SpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int intYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
            int intMonth = Integer.parseInt(spinnerMonth.getSelectedItem().toString());
            int intTag = spinnerTag.getSelectedItemPosition();
            RealmResults<Diary> realmResults;
            if(intTag == 0){
                realmResults = realm.where(Diary.class).equalTo("year",intYear).equalTo("month", intMonth)
                        .findAll().sort("date", Sort.DESCENDING);;
            }else {
                realmResults = realm.where(Diary.class).equalTo("year", intYear).equalTo("month", intMonth)
                        .equalTo("tag", intTag).findAll().sort("date", Sort.DESCENDING);

            }
                DiaryRealmAdapter adapter = new DiaryRealmAdapter(getActivity(), realm, realmResults, true);
                recyclerView.setAdapter(adapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       // if(context instanceof OnFragmentInteractionListener){
       //     mLis = (OnFragmentInteractionListener)context;
//        }else {
//            //throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mLis = null;
    }






}