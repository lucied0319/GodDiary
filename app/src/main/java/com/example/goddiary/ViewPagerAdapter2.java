package com.example.goddiary;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter2 extends FragmentStateAdapter {

    List<Fragment> listFragment;

    public ViewPagerAdapter2(@NonNull FragmentActivity fragmentActivity , @NonNull List<Fragment> listFragment) {

        super(fragmentActivity);
        this.listFragment = listFragment;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return listFragment.get(position);
    }

    @Override
    public int getItemCount() {
        return listFragment.size();
    }
}
