package com.example.goddiary;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    List<Fragment> fragmentList;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull List<Fragment> fragmentList) {
        super(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentList = fragmentList;

        //lifecycle = new
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String[] array = {"日記一覧","日記作成","タグ作成"};
        return array[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
