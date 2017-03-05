package com.raingoddess.lapclient.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by The-Rain-Goddess on 1/30/2017.
 */

public class TabProfile extends Fragment {
    private final String TITLE = "Profile";
    private String profileDataFromServer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tab_profile, container, false);
        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.profile);

        List<String> masteryDataFromServer = SendInputToHost.getMasteryDataResponse();
        profileDataFromServer = SendInputToHost.getProfileDataResponse();

        System.out.println(profileDataFromServer);

        parseServerProfileData(profileDataFromServer, rootView);

        parseServerMasteryData(masteryDataFromServer, rootView);

        return rootView;
    }

    private void parseServerMasteryData(List<String> mastery, View root){
        if(mastery!=null){
            for(int i = 1; i<mastery.size(); i++){
                displayMastery(mastery.get(i), root, i);
            }
        }
    }

    private void displayMastery(String masteryChamp, View root, int index){
        String[] data = masteryChamp.split(":");
        String name = data[0].toLowerCase().replace("'", "").replace(" ", "");
        String masteryLevel = data[1];
        String masteryPoints = data[2];
        ImageView champImage = (ImageView) root.findViewById(getStringIdentifier(root.getContext(), "profile_mastery_champ" + (index-1), "id"));
        champImage.setImageDrawable(getResources().getDrawable(getStringIdentifier(root.getContext(), name, "drawable")));

        ImageView champSkirt = (ImageView) root.findViewById(getStringIdentifier(root.getContext(), "profile_mastery_champ" + (index-1) + "_skirt", "id"));
        champSkirt.setImageDrawable(getResources().getDrawable(getStringIdentifier(root.getContext(), "mastery_skirt_" + masteryLevel, "drawable")));

        TextView champName = (TextView) root.findViewById(getStringIdentifier(root.getContext(), "profile_mastery_champ" + (index-1) + "_name", "id"));
        champName.setText(data[0]);

        TextView champPoints = (TextView) root.findViewById(getStringIdentifier(root.getContext(), "profile_mastery_champ" + (index-1) + "_points", "id"));
        champPoints.setText(String.format(Locale.US, "%,d", Integer.valueOf(masteryPoints)));
    }

    private void parseServerProfileData(String dataFromServer, View rootView){
        if(dataFromServer!=null){
            List<String> arrayOfRankedStatus = Arrays.asList(dataFromServer.split("/"));

            setupTitle(rootView);
            setupRank(rootView, arrayOfRankedStatus);
        }
    }

    private void setupTitle(View root){
        TextView title = (TextView) root.findViewById(R.id.profile_title);
        SpannableString underlinedName = new SpannableString(SendInputToHost.orig_summoner_name);
        underlinedName.setSpan(new UnderlineSpan(), 0, underlinedName.length(), 0);
        title.setPaintFlags(title.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        title.setText(underlinedName);
    }

    private String parseRankToImageFile(String tier){
        String rank;
        if(tier.equals("BRONZE")){
            rank = "ranks_bronze_icon";
        } else if(tier.equals("SILVER")){
            rank = "ranks_silver_icon";
        } else if(tier.equals("GOLD")){
            rank = "ranks_gold_icon";
        } else if(tier.equals("PLATINUM")){
            rank = "ranks_platinum_icon";
        } else if(tier.equals("DIAMOND")){
            rank = "ranks_diamond_icon";
        } else{
            rank = "default_icon";
        } return rank;
    }

    private void setupRank(View root, List<String> rankedStatus){

//5x5 queue
        List<String> ranked_queue_status = Arrays.asList(rankedStatus.get(0).split(":"));

    //Image processing
        String rank = parseRankToImageFile(ranked_queue_status.get(1));
        ImageView rankImage = (ImageView) root.findViewById(R.id.profile_rank_image);
        Bitmap bImage = BitmapFactory.decodeResource(getResources(), getStringIdentifier(getContext(), rank, "drawable"));
        rankImage.setImageBitmap(bImage);

    //ranked queue identifier
        TextView ranked_queue_identifier = (TextView) root.findViewById(R.id.ranked_queue_identifier);
        ranked_queue_identifier.setText(ranked_queue_status.get(0));

    //ranked division and lp
        TextView division_lp = (TextView) root.findViewById(R.id.profile_tier_lp);
        String divisionText = ranked_queue_status.get(1) + " " + ranked_queue_status.get(2) + ": " + ranked_queue_status.get(3);
        division_lp.setText(divisionText);

//flex  queue
        if(rankedStatus.size()>1) {
            List<String> ranked_flex_status = Arrays.asList(rankedStatus.get(1).split(":"));

        //image
            String flex_rank = parseRankToImageFile(ranked_flex_status.get(1));
            ImageView rankFlexImage = (ImageView) root.findViewById(R.id.profile_flex_rank_image);
            Bitmap beforeImage = BitmapFactory.decodeResource(getResources(), getStringIdentifier(getContext(), flex_rank, "drawable"));
            rankFlexImage.setImageBitmap(beforeImage);

        //ranked flex identifier
            TextView ranked_flex_queue_identifier = (TextView) root.findViewById(R.id.ranked_flex_identifier);
            ranked_flex_queue_identifier.setText(ranked_flex_status.get(0));

        //ranked flex division and lp
            //ranked division and lp
            TextView flex_division_lp = (TextView) root.findViewById(R.id.profile_flex_tier_lp);
            String flex_divisionText = ranked_flex_status.get(1) + " " + ranked_flex_status.get(2) + ": " + ranked_flex_status.get(3);
            flex_division_lp.setText(flex_divisionText);
        }
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }
}
