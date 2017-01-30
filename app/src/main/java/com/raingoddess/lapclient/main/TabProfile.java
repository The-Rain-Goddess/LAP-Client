package com.raingoddess.lapclient.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.raingoddess.lapclient.R;

/**
 * Created by The-Rain-Goddess on 1/30/2017.
 */

public class TabProfile extends Fragment {
    private final String TITLE = "Profile";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tab_profile, container, false);
        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.profile);

        return rootView;
    }
}
