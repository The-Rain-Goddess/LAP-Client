package com.raingoddess.lapclient.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Black Lotus on 7/19/2016.
 */
public class TabMatchHistory2 extends Fragment implements View.OnClickListener, AbsListView.OnScrollListener, ScrollViewListener{
    public final static String EXTRA_MESSAGE = "com.raingoddess.lapclient.MESSAGE";
    ArrayList<Match> temp_storage;
    ImageView bv; ImageView iv; ImageView bv1;
    ImageView iv1; ImageView iv2; ImageView iv3; ImageView iv4; ImageView iv5;
    ImageView iv6; ImageView iv7; TextView iv8; TextView iv9; TextView iv10;
    TextView iv11; TextView iv12;
    public int i = 1;
    public int end = 9;
    public int resourceWidth = 0;
    public int resourceHeight = 0;

    private RelativeLayout layout;

    //private WindowManager.LayoutParams windowParam;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tab_match_history2, container, false);

        temp_storage = SendInputToHost.getMatchDump();

        ObservableScrollView scrollView = (ObservableScrollView) v.findViewById(R.id.tab_scroll_view);
        scrollView.setScrollViewListener(this);

        layout = (RelativeLayout) v.findViewById(R.id.match_screen2);
        layout.setBackgroundColor(Color.BLACK);

        resourceHeight = ((getContext().getResources().getDisplayMetrics().heightPixels - (8 * (int)getResources().getDimension(R.dimen.image_height3))) / 5);
        resourceWidth = ((getContext().getResources().getDisplayMetrics().heightPixels - (8 * (int)getResources().getDimension(R.dimen.image_height3))) / 5);

        ImageView stat = new ImageView(v.getContext()); // to give the rest of the screen a reference point
        stat.setId(((i*100) - 100) + 1);
        layout.addView(stat);

//main loop for populating screen
        int in;
        for(in = i; (in < end-1); in++){
           setupMatch(in);
        }
        setupBotBar(in);

        return v;
    }

    private void setupBotBar(int in){
        //int uid = (100 * in);
        int temp1 = ((100 * in) - 100) + 1;
        bv = new ImageView(getContext());
        bv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        //bv.setId(uid);
        RelativeLayout.LayoutParams bvRelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bvRelParam.addRule(RelativeLayout.BELOW,temp1);
        //bvRelParam.setMargins(40,40,40,40);

        //bv.setImageResource(R.drawable.load_more_button);
        bv.setLayoutParams(bvRelParam);
        bv.getLayoutParams().height = (10 * (int) getResources().getDimension(R.dimen.image_height3));
        bv.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        bv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadMoreItemsTask(getParentFragment(), SendInputToHost.summoner_name + "::get_match_history::" + 8 + "::" + (i+7) + "::" + (end+7)).execute();
            }
        });
        layout.addView(bv);

        ImageView bv1 = new ImageView(getContext());
        RelativeLayout.LayoutParams bvRelParam1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bvRelParam1.addRule(RelativeLayout.BELOW,temp1);
        bvRelParam1.setMargins( 2 * (int)getResources().getDimension(R.dimen.image_height3),
                                2 * (int)getResources().getDimension(R.dimen.image_height3),
                                40,
                                40);

        bv1.setImageResource(R.drawable.load_more_button);
        layout.addView(bv1, bvRelParam1);

        Button loadMoreMatches = new Button(getContext());
        loadMoreMatches.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, temp1);
        loadMoreMatches.setLayoutParams(layoutParams);
        loadMoreMatches.getLayoutParams().height = (5 * (int) getResources().getDimension(R.dimen.image_height3));
        loadMoreMatches.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        loadMoreMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadMoreItemsTask(getParentFragment(), SendInputToHost.summoner_name + "::get_match_history::" + 8 + "::" + (i+7) + "::" + (end+7)).execute();
            }
        });
        //layout.addView(loadMoreMatches);
    }

//imple for OnClickListener
    @Override
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), ViewMatch.class);
        intent.putExtra(EXTRA_MESSAGE, view.getContentDescription());
        startActivity(intent);
    }

//impl for OnScrollListener
    int currentFirstVisibleItem = 0;
    int currentVisibleItemCount = 0;
    int totalItemCount = 0;
    int currentScrollState = 0;
    boolean loadingMore = false;
    //Long startIndex = 0L;
    //Long offset = 10L;
    //View footerView;

    @Override
    public void onScrollEnded(ObservableScrollView scrollView, int x, int y, int oldx, int oldy){
        //new LoadMoreItemsTask(this, SendInputToHost.summoner_name + "::get_match_history::" + 8 + "::" + (i+7) + "::" + (end+7)).execute(); //would load more matches when scrolled to the bottom
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        //System.out.println("First item: "  + currentFirstVisibleItem);
        //System.out.println("Item Count: "  + currentVisibleItemCount);
        this.totalItemCount = totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void setupMatch(int in) {
        int uid;
        int temp1;
        int temp2;
        int color_background;
        if (temp_storage.get(in - 1).IsWinner.contains("true")) {
            color_background = getResources().getColor(R.color.win_color);
        } else if(Integer.parseInt(temp_storage.get(in-1).getStat("matchLength")) <= 300){
            color_background = getResources().getColor(R.color.remake_color);
        } else{
            color_background = getResources().getColor(R.color.loss_color_alternate);
        }

//above topbar
        uid = (100 * in) + 9;
        temp1 = ((100 * in) - 100) + 1;
        bv1 = new ImageView(getContext());
        bv1.setBackgroundColor(Color.BLACK);
        bv1.setId(uid);
        RelativeLayout.LayoutParams bv1RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bv1RelParam.addRule(RelativeLayout.BELOW,temp1);
        bv1RelParam.setMargins(0,10,0,10);
        bv1.setLayoutParams(bv1RelParam);
        bv1.getLayoutParams().height = 10;
        bv1.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        layout.addView(bv1);

//topbar
        uid = (100 * in);
        temp1 = ((100 * in)) + 9;
        bv = new ImageView(getContext());
        bv.setBackgroundColor(color_background);
        bv.setId(uid);
        RelativeLayout.LayoutParams bvRelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bvRelParam.addRule(RelativeLayout.BELOW,temp1);
        bv.setLayoutParams(bvRelParam);
        bv.getLayoutParams().height = (5 * (int) getResources().getDimension(R.dimen.image_height3));
        bv.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        //System.out.println(bv.getId());
        layout.addView(bv);

//Queue type
        uid = (100 * in) + 99;
        temp1 = ((100 * in)) + 9;
        TextView qType = new TextView(getContext());
        RelativeLayout.LayoutParams qTypeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        qTypeParam.addRule(RelativeLayout.BELOW,temp1);
        qType.setLayoutParams(qTypeParam);
        qType.setId(uid);
        String qString = temp_storage.get(in-1).getStat("queueType");
        if(qString.equals("TEAM_BUILDER_RANKED_SOLO"))
            qString = "RANKED_SOLO_5x5";
        qType.setText(qString);
        qType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        qType.setTextColor(getResources().getColor(R.color.black));
        qType.setGravity(1);
        layout.addView(qType);

//Vic/Def
        uid = (100 * in) + 98;
        temp1 = ((100 * in) + 99);
        TextView vd = new TextView(getContext());
        RelativeLayout.LayoutParams vdParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        vdParam.addRule(RelativeLayout.BELOW,temp1);
        vd.setLayoutParams(vdParam);
        vd.setId(uid);
        if(temp_storage.get(in-1).getStat("winner").contains("true")){
            vd.setText(getResources().getString(R.string.champion_view_victory));
            vd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            vd.setTextColor(getResources().getColor(R.color.good_green));
            vd.setGravity(1);
        } else if(Integer.parseInt(temp_storage.get(in-1).getStat("matchLength")) <= 300){ //For Remakes
            vd.setText(getResources().getString(R.string.champion_view_remake));
            vd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            vd.setTextColor(getResources().getColor(R.color.black));
            vd.setGravity(1);
        } else{
            vd.setText(getResources().getString(R.string.champion_view_defeat));
            vd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            vd.setTextColor(getResources().getColor(R.color.regular_red));
            vd.setGravity(1);
        } layout.addView(vd);

//Match Length
        uid = (100 * in) + 97;
        temp1 = ((100 * in)) + 99;
        temp2 = ((100 * in) + 98);
        TextView mType = new TextView(getContext());
        RelativeLayout.LayoutParams mTypeParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mTypeParam.addRule(RelativeLayout.BELOW,temp1);
        //mTypeParam.addRule(RelativeLayout.END_OF, temp2);
        mTypeParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        mType.setLayoutParams(mTypeParam);
        mType.setId(uid);
        mType.setText(String.format(Locale.US, "%dm %ds", Integer.parseInt(temp_storage.get(in-1).getStat("matchLength"))/60, Integer.parseInt(temp_storage.get(in-1).getStat("matchLength"))%60 ));
        mType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mType.setTextColor(getResources().getColor(R.color.black));
        //mType.setGravity(1);
        layout.addView(mType);

//clickable
        uid = (100 * in) + 2;
        temp1 = (100 * in);     //below
        iv1 = new ImageView(getContext());
        iv1.setId(uid);
        iv1.setBackgroundColor(color_background);
        iv1.setContentDescription( "" + (in-1));
        RelativeLayout.LayoutParams iv1RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv1RelParam.addRule(RelativeLayout.BELOW,temp1);
        iv1.setLayoutParams(iv1RelParam);
        iv1.getLayoutParams().height =  resourceHeight;
        iv1.getLayoutParams().width =   RelativeLayout.LayoutParams.MATCH_PARENT;

        iv1.setOnClickListener(this);
        layout.addView(iv1);

//champImage
        uid = (100 * in) + 1;
        temp1 = (100 * in);         //below
        iv = new ImageView(getContext());
        iv.setId(uid);
        iv.setBackgroundColor(color_background);

        RelativeLayout.LayoutParams ivRelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivRelParam.addRule(RelativeLayout.BELOW,temp1);
        iv.setLayoutParams(ivRelParam);
        iv.getLayoutParams().height =   resourceHeight;
        iv.getLayoutParams().width =    resourceWidth;
        String nameChamp = temp_storage.get(in-1).Champion.replace("champion:", "").replace("|", "").replace("'", "").replace(" ", "").toLowerCase();
        //System.err.println(nameChamp);
        iv.setImageResource(getStringIdentifier(getContext(), nameChamp, "drawable"));
        layout.addView(iv);

//item1
        uid = (100 * in) + (10);
        temp1 = (100 * in);         //below
        temp2 = (100 * in)  + 1;    //to right of
        iv2 = new ImageView(getContext());
        iv2.setId(uid);
        RelativeLayout.LayoutParams iv2RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv2RelParam.addRule(RelativeLayout.BELOW,temp1);
        iv2RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv2RelParam.addRule(RelativeLayout.ALIGN_TOP, temp2);
        //iv2RelParam.setMargins(5,5,5,5);
        iv2.setLayoutParams(iv2RelParam);
        iv2.setBackgroundColor(color_background);
        iv2.getLayoutParams().height =  (resourceHeight / 2);
        iv2.getLayoutParams().width =   (resourceWidth  / 2);
        iv2.setBackgroundColor(Color.BLACK);
        iv2.setImageResource(getStringIdentifier(getContext(), matchItemToId(Integer.parseInt(temp_storage.get(in-1).Item0.replace("item0:", ""))), "drawable"));
        layout.addView(iv2);

//item2
        uid = (100 * in) + (2 * 10);
        temp1 = (100) * in;                 //below
        temp2 = (100 * in)  + (10) ;    //to right of
        iv3 = new ImageView(getContext());
        iv3.setId(uid);
        RelativeLayout.LayoutParams iv3RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv3RelParam.addRule(RelativeLayout.BELOW,temp1);
        iv3RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv3RelParam.addRule(RelativeLayout.ALIGN_TOP, temp2);
        //iv3RelParam.setMargins(0,5,0,5);
        iv3.setLayoutParams(iv3RelParam);
        iv3.getLayoutParams().height = (resourceHeight  / 2);
        iv3.getLayoutParams().width =  (resourceWidth   / 2);
        iv3.setBackgroundColor(color_background);
        iv3.setImageResource(getStringIdentifier(getContext(), matchItemToId(Integer.parseInt(temp_storage.get(in-1).Item1.replace("item1:", ""))), "drawable"));
        layout.addView(iv3);
//item3
        uid = (100 * in) + (3 * 10);
        temp1 = (100) * in;                 //below
        temp2 = (100 * in)  + (2 * 10) ;    //to right of
        iv4 = new ImageView(getContext());
        iv4.setId(uid);
        RelativeLayout.LayoutParams iv4RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv4RelParam.addRule(RelativeLayout.BELOW,temp1);
        iv4RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv4RelParam.addRule(RelativeLayout.ALIGN_TOP, temp2);
        //iv4RelParam.setMargins(5,5,5,5);
        iv4.setLayoutParams(iv4RelParam);
        iv4.getLayoutParams().height =  (resourceHeight / 2);
        iv4.getLayoutParams().width =   (resourceWidth  / 2);
        iv4.setBackgroundColor(color_background);
        iv4.setImageResource(getStringIdentifier(getContext(), matchItemToId(Integer.parseInt(temp_storage.get(in-1).Item2.replace("item2:", ""))), "drawable"));
        layout.addView(iv4);
//item4
        uid = (100 * in) + (4 * 10);
        temp1 = (100 * in) + (10); //below
        temp2 = (100 * in)  + (1) ;     //to right of
        iv5 = new ImageView(getContext());
        iv5.setId(uid);
        RelativeLayout.LayoutParams iv5RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv5RelParam.addRule(RelativeLayout.BELOW,temp1);
        iv5RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv5RelParam.addRule(RelativeLayout.ALIGN_BOTTOM, temp2);
        //iv5RelParam.setMargins(5,5,5,5);
        iv5.setLayoutParams(iv5RelParam);
        iv5.getLayoutParams().height =  (resourceHeight / 2);
        iv5.getLayoutParams().width =   (resourceWidth  / 2);
        iv5.setBackgroundColor(color_background);
        iv5.setImageResource(getStringIdentifier(getContext(), matchItemToId(Integer.parseInt(temp_storage.get(in-1).Item3.replace("item3:", ""))), "drawable"));
        layout.addView(iv5);
//item5
        uid = (100 * in) + (5 * 10);
        temp1 = (100 * in) + (10); //below
        temp2 = (100 * in)  + (4 * 10) ; //to right of
        iv6 = new ImageView(getContext());
        iv6.setId(uid);
        RelativeLayout.LayoutParams iv6RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv6RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv6RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv6RelParam.addRule(RelativeLayout.ALIGN_BOTTOM, temp2);
        //iv6RelParam.setMargins(0,5,0,5);
        iv6.setLayoutParams(iv6RelParam);
        iv6.getLayoutParams().height =  (resourceHeight / 2);
        iv6.getLayoutParams().width =   (resourceWidth  / 2);
        iv6.setBackgroundColor(color_background);
        iv6.setImageResource(getStringIdentifier(getContext(), matchItemToId(Integer.parseInt(temp_storage.get(in-1).Item4.replace("item4:", ""))), "drawable"));
        layout.addView(iv6);
//item6
        uid = (100 * in) + (6 * 10);
        temp1 = (100 * in) + (10);      //below
        temp2 = (100 * in)  + (5 * 10) ;    //torightof
        iv7 = new ImageView(getContext());
        iv7.setId(uid);
        RelativeLayout.LayoutParams iv7RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv7RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv7RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv7RelParam.addRule(RelativeLayout.ALIGN_BOTTOM, temp2);
        //iv7RelParam.setMargins(5,5,5,5);
        iv7.setLayoutParams(iv7RelParam);
        iv7.setBackgroundColor(color_background);
        iv7.setImageResource(getStringIdentifier(getContext(), matchItemToId(Integer.parseInt(temp_storage.get(in-1).Item5.replace("item5:", ""))), "drawable"));
        iv7.getLayoutParams().height = (resourceHeight / 2);
        iv7.getLayoutParams().width =  (resourceWidth  / 2);
        layout.addView(iv7);
//kills
        uid = (100 * in) + (3);
        temp1 = (100 * in);                 //below
        temp2 = (100 * in)  + (3 * 10) ;    //rightof
        iv8 = new TextView(getContext());
        iv8.setId(uid);
        RelativeLayout.LayoutParams iv8RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv8RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv8RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv8RelParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        iv8.setLayoutParams(iv8RelParam);
        //iv8.getLayoutParams().height = 50; iv8.getLayoutParams().width = 50;
        iv8.setBackgroundColor(Color.BLACK);
        iv8.setTextColor(getResources().getColor(R.color.white));
        iv8.setText(temp_storage.get(in-1).Kills.replace(":", ":      "));
        layout.addView(iv8);

//deaths
        uid = (100 * in) + (4);
        temp1 = (100 * in) + (3);           //below
        temp2 = (100 * in)  + (3 * 10) ;    //rightof
        iv9 = new TextView(getContext());
        iv9.setId(uid);
        RelativeLayout.LayoutParams iv9RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv9RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv9RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv9RelParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        iv9.setLayoutParams(iv9RelParam);
        //iv8.getLayoutParams().height = 50; iv8.getLayoutParams().width = 50;
        iv9.setBackgroundColor(Color.BLACK);
        iv9.setTextColor(getResources().getColor(R.color.white));
        iv9.setText(temp_storage.get(in-1).Deaths.replace(":", ": "));
        layout.addView(iv9);

//assists
        uid = (100 * in) + (5);
        temp1 = (100 * in) + (4);           //below
        temp2 = (100 * in)  + (3 * 10) ;    //rightof
        iv10 = new TextView(getContext());
        iv10.setId(uid);
        RelativeLayout.LayoutParams iv10RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv10RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv10RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv10RelParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        iv10.setLayoutParams(iv10RelParam);
        //iv8.getLayoutParams().height = 50; iv8.getLayoutParams().width = 50;
        iv10.setBackgroundColor(Color.BLACK);
        iv10.setTextColor(getResources().getColor(R.color.white));
        iv10.setText(temp_storage.get(in-1).Assists.replace(":", ": "));
        layout.addView(iv10);

//cs
        uid = (100 * in) + (6);
        temp1 = (100 * in) + (5);           //below
        temp2 = (100 * in)  + (3 * 10) ;    //rightof
        iv11 = new TextView(getContext());
        iv11.setId(uid);
        RelativeLayout.LayoutParams iv11RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv11RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv11RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv11RelParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        iv11.setLayoutParams(iv11RelParam);
        //iv8.getLayoutParams().height = 50; iv8.getLayoutParams().width = 50;
        iv11.setBackgroundColor(Color.BLACK);
        iv11.setTextColor(getResources().getColor(R.color.white));
        iv11.setText(temp_storage.get(in-1).Cs.replace(":", ":       "));
        layout.addView(iv11);

//champLevel
        uid = (100 * in) + (7);
        temp1 = (100 * in) + (6);           //below
        temp2 = (100 * in)  + (3 * 10) ;    //rightof
        iv12 = new TextView(getContext());
        iv12.setId(uid);
        RelativeLayout.LayoutParams iv12RelParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv12RelParam.addRule(RelativeLayout.BELOW,    temp1);
        iv12RelParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        iv12RelParam.addRule(RelativeLayout.RIGHT_OF, temp2);
        iv12.setLayoutParams(iv12RelParam);
        //iv8.getLayoutParams().height = 50; iv8.getLayoutParams().width = 50;
        iv12.setBackgroundColor(Color.BLACK);
        iv12.setTextColor(getResources().getColor(R.color.white));
        iv12.setText(temp_storage.get(in-1).ChampLevel.replace("champL","l").replace(":", ":   "));
        layout.addView(iv12);
    }

    static String matchItemToId(int in){
        String out;

        switch (in){
            case 3020: out = "sorcerers_shoes_item"; break;
            case 3116: out = "rylais_crystal_scepter_item"; break;
            case 3135: out = "void_staff_item"; break;
            case 3165: out = "morellonomicon_item"; break;
            case 3030: out = "hextech_glp800_item"; break;
            case 1058: out = "needlessly_large_rod_item"; break;
            case 3025: out = "iceborn_gauntlet_item"; break;
            case 3071: out = "the_black_cleaver_item"; break;
            case 3042: out = "muramana_item"; break;
            case 3140: out = "quicksilver_sash_item"; break;
            case 3157: out = "zhonyas_hourglass_item"; break;
            case 3006: out = "berserkers_greaves_item"; break;
            case 1053: out = "vampiric_scepter_item"; break;
            case 2043: out = "vision_ward_item"; break;
            case 3151: out = "liandrys_torment_item"; break;
            case 3145: out = "hextech_revolver_item"; break;
            case 3067: out = "kindlegem_item"; break;
            case 0:    out = "default_no_item"; break;
            case 2003: out = "health_potion_item"; break;
            case 3191: out = "seekers_armguard_item"; break;
            case 3108: out = "fiendish_codex_item"; break;
            case 1056: out = "dorans_ring_item"; break;
            case 1400: out = "stalkers_blade_item"; break;
            case 3156: out = "maw_of_malmortius_item"; break;
            case 3117: out = "boots_of_mobility_item"; break;
            case 3102: out = "banshees_veil_item"; break;
            case 3142: out = "youmuus_ghostblade_item"; break;
            case 3812: out = "deaths_dance_item"; break;
            case 2032: out = "hunters_potion_item"; break;
            case 3074: out = "ravenous_hydra_item"; break;
            case 3077: out = "tiamat_item"; break;
            case 1036: out = "long_sword_item"; break;
            case 3340: out = "warding_totem_item"; break;
            case 3147: out = "duskblade_of_draktharr_item"; break;
            case 3004: out = "manamune_item"; break;
            case 3035: out = "last_whisper_item"; break;
            case 3802: out = "lost_chapter_item"; break;
            case 3026: out = "guardian_angel_item"; break;
            case 3089: out = "rabadons_deathcap_item"; break;
            case 3027: out = "rod_of_ages_item"; break;
            case 3040: out = "seraphs_embrace_item"; break;
            case 1031: out = "chain_vest_item"; break;
            case 1055: out = "dorans_blade_item"; break;
            case 3072: out = "the_bloodthirster_item"; break;
            case 3085: out = "runaans_hurricane_item"; break;
            case 3001: out = "abyssal_scepter_item"; break;
            case 3285: out = "ludens_echo_item"; break;
            case 1052: out = "amplifying_tome_item"; break;
            case 3031: out = "infinity_edge_item"; break;
            case 3087: out = "statikk_shiv_item"; break;
            case 1038: out = "b_f_sword_item"; break;
            case 3133: out = "caulfields_warhammer_item"; break;
            case 3363: out = "farsight_alteration_item"; break;
            case 2045: out = "ruby_sightstone_item"; break;
            case 3092: out = "frost_queens_claim_item"; break;
            case 3082: out = "wardens_mail_item"; break;
            case 2303: out = "eye_of_the_equinox_item"; break;
            case 3114: out = "forbidden_idol_item"; break;
            case 1029: out = "cloth_armor_item"; break;
            case 3801: out = "crystalline_bracer_item"; break;
            case 2015: out = "kircheis_shard_item"; break;
            case 1037: out = "pickaxe_item"; break;
            case 1018: out = "cloak_of_agility_item"; break;
            case 3086: out = "zeal_item"; break;
            case 1419: out = "bloodrazor_item"; break;
            case 2031: out = "refillable_potion_item"; break;
            case 3111: out = "mercurys_treads_item"; break;
            case 1028: out = "ruby_crystal_item"; break;
            case 3508: out = "essence_reaver_item"; break;
            case 3009: out = "boots_of_swiftness_item"; break;
            case 1409: out = "trackers_knife_item"; break;
            case 3742: out = "dead_mans_plate_item"; break;
            case 1011: out = "giants_belt_item"; break;
            case 3748: out = "titanic_hydra_item"; break;
            case 2053: out = "raptor_cloak_item"; break;
            case 2033: out = "corrupting_potion_item"; break;
            case 3068: out = "sunfire_cape_item"; break;
            case 3158: out = "ionian_boots_of_lucidity_item"; break;
            case 3190: out = "locket_of_the_iron_solari_item"; break;
            case 1042: out = "dagger_item"; break;
            case 1401: out = "cinderhulk_item"; break;
            case 3065: out = "spirit_visage_item"; break;
            case 3153: out = "blade_of_the_ruined_king_item"; break;
            case 2140: out = "elixir_of_wrath_item"; break;
            case 3046: out = "phantom_dancer_item"; break;
            case 3155: out = "hexdrinker_item"; break;
            case 3123: out = "executioners_calling_item"; break;
            case 2049: out = "sightstone_item"; break;
            case 1026: out = "blasting_wand_item"; break;
            case 3512: out = "zzrot_portal_item"; break;
            case 2301: out = "eye_of_the_watchers_item"; break;
            case 3174: out = "athenes_unholy_grail_item"; break;
            case 2055: out = "control_ward_item"; break;
            case 2302: out = "eye_of_the_oasis_item"; break;
            case 3504: out = "ardent_censor_item"; break;
            case 3107: out = "redemption_item"; break;
            case 3110: out = "frozen_heart_item"; break;
            case 3143: out = "randuins_omen_item"; break;
            case 3036: out = "lord_dominiks_regards_item"; break;
            case 3047: out = "ninja_tabi_item"; break;
            case 1057: out = "negatron_cloak_item"; break;
            case 3069: out = "talisman_of_ascension_item"; break;
            case 3113: out = "aether_wisp_item"; break;
            case 3078: out = "trinity_force_item"; break;
            case 3034: out = "giant_slayer_item"; break;
            case 3136: out = "haunting_guise_item"; break;
            case 3033: out = "mortal_reminder_item"; break;
            case 1412: out = "warrior_item"; break;
            case 3364: out = "oracle_alteration_item"; break;
            case 3070: out = "tear_of_the_goddess_item"; break;
            default: out = "default_icon"; break;
        }

        return out;
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/
            if (!loadingMore) {
                loadingMore = true;
            } loadingMore=false;
        }
    }

 //private Load in Background Class
    private class LoadMoreItemsTask extends AsyncTask<String, Integer, List<String>> { //do in Background thread
        private DataInputStream in;
        private DataOutputStream out;
        private Socket s;

        private String commandForServer;
        private ArrayList<String> responses;
        private ProgressDialog mProgressDialog;


        LoadMoreItemsTask(Fragment fragment, String commandForServer) { //, Context context
            try {
                this.commandForServer = commandForServer;
                responses = new ArrayList<>();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getContext(), "Loading Extra Matches...", "Please Wait", true);
        }

        protected List<String> doInBackground(String... strings) {
            try {
                this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                //////////very important for net
                this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                out.writeUTF(commandForServer);
                out.flush();

                String rsp;// = in.readUTF();
                //System.out.println("RSP: " + rsp);
                for(int i = 0; i<8; i++){
                    rsp = in.readUTF();
                    //System.out.println("RSP: " + rsp);
                    responses.add(rsp);
                }

                in.close();
                out.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            } return responses;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(List<String> result) {
            // ((TextView)findViewById(R.id.textView2)).setText(result);  //good for testing server output
            if(parseOutputString(result)){
                mProgressDialog.dismiss();
                i+=8; end+=8;
                displayEightMoreMatches(i, end);
            } setupBotBar(end-1);
        }

        private boolean displayEightMoreMatches(int start, int stop){
            for(int j = start-1; j < stop-1; j++){
                setupMatch(j);
            }
            return true;
        }

        private boolean parseOutputString(List<String> output) {
            if ((!output.contains("Exception"))) {

                Match temp_match;
                for (int i = 0; i < output.size(); i++) {
                    if(output.get(i).contains("/")){
                        //System.out.println("Num: " + i);
                        temp_match = new Match(output.get(i).replace("|MATCH:", ""));
                        SendInputToHost.matchDump.add(temp_match);
                    } else if(i!=0){
                        //System.out.println("num:" + i);
                        temp_match = new Match(output.get(i-1).replace("|MATCH:", ""));
                        SendInputToHost.matchDump.add(temp_match);
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
