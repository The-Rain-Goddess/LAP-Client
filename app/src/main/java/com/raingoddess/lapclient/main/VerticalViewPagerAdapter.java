package com.raingoddess.lapclient.main;

import android.support.v4.app.*;

/**
 * Created by Black Lotus on 7/19/2016.
 */
public class VerticalViewPagerAdapter extends FragmentStatePagerAdapter{

    CharSequence Titles[];
    int NumOfTabs;

    public VerticalViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public VerticalViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumOfTabsumb){
        super(fm);

        this.Titles = mTitles;
        this.NumOfTabs = mNumOfTabsumb;
    }

    @Override
    public Fragment getItem(int position){

            TabMatchHistory2 tab2 = new TabMatchHistory2();
            return tab2;
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
