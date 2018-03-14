package com.example.npttest.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.npttest.R;
import com.example.npttest.viewpager.My_Viewpager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liuji on 2017/7/28.
 */

public class Fragment2 extends Fragment {


    @Bind(R.id.fg2_tabs)
    TabLayout fg2Tabs;
    @Bind(R.id.fg2_vp)
    My_Viewpager fg2Vp;
    private FragmentManager fragmentManager;
    private List<Fragment> list = new ArrayList<Fragment>();
    //private String tabTitles[] = new String[]{getString(R.string.admission_records), getString(R.string.appearance_record), getString(R.string.charge_records)};
    private String[] tabTitles;
    //private String tabTitles[] = new String[]{"入场", "出场","收费"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2, null);
        ButterKnife.bind(this, v);
        fg2Vp.setOffscreenPageLimit(2);
        tabTitles = getResources().getStringArray(R.array.tabTitles);
        //获取碎片管理者对象
        fragmentManager = getChildFragmentManager();
        list.add(new Fg2_Fragment1());
        list.add(new Fg2_Fragment2());
        list.add(new Fg2_Fragment3());

        fg2Vp.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles[position];
            }
        });

        fg2Tabs.setupWithViewPager(fg2Vp);
        fg2Tabs.setTabMode(TabLayout.MODE_FIXED);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "onResume: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}