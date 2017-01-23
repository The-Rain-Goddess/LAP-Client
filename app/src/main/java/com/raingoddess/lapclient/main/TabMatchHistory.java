package com.raingoddess.lapclient.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raingoddess.lapclient.R;

/**
 * Created by Black Lotus on 7/19/2016.
 */
public class TabMatchHistory extends Fragment {
    VerticalViewPager pager;
    VerticalViewPagerAdapter adapter;
    CharSequence Titles[] = {"Tab1" , "Tab2"};
    int NumOfTabs = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.tab_match_history, container, false);
        adapter = new VerticalViewPagerAdapter(getChildFragmentManager(), Titles, NumOfTabs);
        pager = (VerticalViewPager) v.findViewById(R.id.pagerVertical);
        pager.setAdapter(adapter);
        return v;
    }
}
