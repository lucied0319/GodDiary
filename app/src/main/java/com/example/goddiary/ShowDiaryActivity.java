package com.example.goddiary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.palette.graphics.Palette;
import io.realm.Realm;

import android.os.CpuUsageInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowDiaryActivity extends AppCompatActivity {

    private TextView textBody;
    private TextView textTitle;
    private TextView textWeather;
    private TextView textTag;

    private ImageView imageView;

    private Realm realm;
    private Bitmap bitmap;

    final String[] arrayWeather = {"晴れ","曇り","雨","雷雨","雪","吹雪"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        textTitle = findViewById(R.id.textTitle);
        textWeather = findViewById(R.id.textWeather);
        textTag = findViewById(R.id.textTag);
        textBody = findViewById(R.id.textBody);

        NestedScrollView scrollView = findViewById(R.id.nestedView);
        imageView = findViewById(R.id.imageView);

        realm = Realm.getDefaultInstance();


        String[] arrayTag = realm.where(System.class).equalTo("id",1).findAll().first().tag.split(",",-1);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");

        Diary diary = realm.where(Diary.class).equalTo("date",date).findFirst();

        bitmap = MyUtils.getImageFromByte(diary.image);
        imageView.setImageBitmap(MyUtils.getImageFromByte(diary.image));
        textTitle.setText(diary.title);
        textWeather.setText(arrayWeather[diary.weather]);
        textTag.setText(arrayTag[diary.tag]);
        textBody.setText(diary.bodyText);
        toolBarLayout.setTitle(diary.date);

        Palette palette = Palette.from(bitmap).generate();
//
        int titleColor = palette.getLightVibrantColor(Color.WHITE);
        int bodyColor = palette.getDarkMutedColor(Color.BLACK);
        int scrimColor = palette.getMutedColor(Color.DKGRAY);
//
        toolBarLayout.setExpandedTitleColor(bodyColor);
//        toolBarLayout.setContentScrimColor(scrimColor);
//        scrollView.setBackgroundColor(bodyColor);
//        textBody.setTextColor(titleColor);
//        textTag.setTextColor(titleColor);
//        textWeather.setTextColor(titleColor);
//        textTitle.setTextColor(titleColor);



    }
    public void onBack(View view) {
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}