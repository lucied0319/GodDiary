package com.example.goddiary;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import io.realm.Realm;
import io.realm.RealmResults;

import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity{

    //各ウィジェットの変数
    private ImageView imageMain;
    private ViewPager viewPager;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter2 viewPagerAdapter2;
    private TabLayout tabLayout;

    private Realm realm;

    private int flagImage;
    public final static int flagImageMain = 0;
    public final static int flagImageDiary = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        //ツールバー作成
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("djodas");
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolBarLayout.setTitle(getTitle());
       // toolBarLayout.setCollapsedTitleTypeface(TyperRoboto.ROBOTO_REGULAR());
        //各ウィジェットのオブジェクトを取得
        imageMain = findViewById(R.id.imageMain);
        //viewPager = findViewById(R.id.viewPager);
        viewPager2 = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        realm = Realm.getDefaultInstance();

        //タグデータがあるかチェック
        createRealm();

        List<Fragment> arrayFragment = new ArrayList<>();
        arrayFragment.add(new DiaryListFragment());
        arrayFragment.add(new MakeDiaryFragment());
        arrayFragment.add(new MakeTagFragment());
        viewPagerAdapter2 = new ViewPagerAdapter2(this,arrayFragment);
        viewPager2.setAdapter(viewPagerAdapter2);

        String[] array = {"日記一覧","日記作成","タグ作成"};
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(array[position])
        ).attach();
    }

    @Override
    public void onBackPressed(){
        int i = viewPager2.getCurrentItem()-1;
        if(i<0){
            i=2;
        }
        viewPager2.setCurrentItem(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            requestWriteStorage(flagImageMain);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    void createRealm(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System system = realm.where(System.class).equalTo("id", 1).findFirst();
                //初めての起動でタグデータとメイン画像がrealmに無いときデータを登録
                if (system == null) {
                    system = realm.createObject(System.class, 1);
                    system.tag = "全て,1,2,3,4,5,6,7,8,9,10";


                    imageMain.setImageResource(R.drawable.paku2);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable)imageMain.getDrawable();
                    system.mainImage = MyUtils.getByteFromImage(bitmapDrawable.getBitmap());

                }
                imageMain.setImageBitmap(MyUtils.getImageFromByte(system.mainImage));

            }
        });
    }
    //アダプターの作成のメソッド　※タグ作成フラグメントから呼び出される
    void makeTagAdapter(){
        List<Fragment> arrayFragment = new ArrayList<>();
        arrayFragment.add(new DiaryListFragment());
        arrayFragment.add(new MakeDiaryFragment());
        arrayFragment.add(new MakeTagFragment());
        viewPagerAdapter2 = new ViewPagerAdapter2(this,arrayFragment);
        viewPager2.setAdapter(viewPagerAdapter2);
        viewPager2.setCurrentItem(2);
    }

    //アダプターの作成のメソッド　※日記リスト長押し再編集から呼び出される
    void makeDiaryAdapter(String date){
        List<Fragment> arrayFragment = new ArrayList<>();
        arrayFragment.add(new DiaryListFragment());
        arrayFragment.add(MakeDiaryFragment.newInstance(date));
        arrayFragment.add(new MakeTagFragment());
        viewPagerAdapter2 = new ViewPagerAdapter2(this,arrayFragment);
        viewPager2.setAdapter(viewPagerAdapter2);
        viewPager2.setCurrentItem(1);

        //viewPager2.setAdapter(viewPagerAdapter2);
    }
    //ストレージパーミッション許可確認
     void requestWriteStorage(int flagImage){
        this.flagImage = flagImage;
        //許可されていない時
        if(ActivityCompat.checkSelfPermission(ScrollingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            //許可されてる時
        }else {
            pickImage();
        }
    }
    //パーミッションダイアログ許可処理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //画像読み込みのダイアログの許可がOKの時
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        }
    }
    //画像読み込みの暗黙インテントの呼び出し
    private  void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,1000);

    }

    //暗黙インテントの結果処理

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //画像読みこみ暗黙インテントの結果処理
        if(requestCode == 1000 && resultCode == RESULT_OK){
            Uri uri; uri = (data == null) ? null : data.getData();
            if(uri != null){
                //ダイアリー画面の画像
                if(flagImage == flagImageDiary)
                    ((MakeDiaryFragment)viewPagerAdapter2.listFragment.get(1)).imageDiaryPhoto.setImageURI(uri);
                //メイン画面の画像
                else if(flagImage == flagImageMain)
                    imageMain.setImageURI(uri);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Diary diary = realm.createObject(Diary.class,"djiaas");
                        System system = realm.where(System.class).equalTo("id",1).findFirst();


                        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageMain.getDrawable();
                        byte[] bytes = MyUtils.getByteFromImage(bitmapDrawable.getBitmap());
                        if(bytes != null && bytes.length > 0){
                            system.mainImage = bytes;
                        }

                    }
                });


            }
        }
    }


}