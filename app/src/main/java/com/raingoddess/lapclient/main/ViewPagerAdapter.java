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

        if(position == 1){
            TabMatchHistory2 tab1 = new TabMatchHistory2();
            return tab1;
        } else if(position == 0){
            TabProfile tab2 = new TabProfile();
            return tab2;
        } else if(position == 2){
            TabAnalysis tab3 = new TabAnalysis();
            return tab3;
        } else
            return new Fragment();
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
