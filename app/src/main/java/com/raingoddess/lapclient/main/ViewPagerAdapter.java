package com.raingoddess.lapclient.main;

import android.support.v4.app.*;

/**
 * Created by Black Lotus on 7/19/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    CharSequence Titles[];
    int NumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumOfTabsumb){
        super(fm);

        this.Titles = mTitles;
        this.NumOfTabs = mNumOfTabsumb;
    }

    @Override
    public Fragment getItem(int position){

        if(position == 0){
            TabMatchHistory tab1 = new TabMatchHistory();
            return tab1;
        } else{
            TabAnalysis tab2 = new TabAnalysis();
            return tab2;
        }
    }

    @Override
    public CharSequence getPageTitle(int position){
        return Titles[position];
    }

    @Override
    public int getCount(){
        return NumOfTabs;
    }
}
