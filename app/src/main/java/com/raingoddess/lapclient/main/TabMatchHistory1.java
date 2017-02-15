package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Black Lotus on 7/19/2016.
 */
public class TabMatchHistory1 extends Fragment implements View.OnClickListener {
    List<Game> temp_storage;
    public final static String EXTRA_MESSAGE = "com.raingoddess.lapclient.MESSAGE";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tab_match_history1, container, false);

        temp_storage = SendInputToHost.getGameDump();

        for(int i = 0; i<4; i++){
            int ii = i+1;
            String bjerg = "match" + ii;
            String temp = "";
            //System.out.println("Num: " + i + " String: " + bjerg);
            //System.out.println(getStringIdentifier(v.getContext(), bjerg));

            ImageView iv = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), bjerg));
            String nameChamp = temp_storage.get(i).get(0).Champion.replace("champion:", "").replace("|", "").replace("'", "").replace(" ", "").toLowerCase();
            iv.setImageResource(getStringIdentifier(v.getContext(), nameChamp, "drawable"));

            temp = bjerg + "_border";
            ImageView clicker = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            //clicker.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            clicker.setOnClickListener(this);

            temp = bjerg + "_item1";
            ImageView it0 = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            it0.setImageResource(getStringIdentifier(v.getContext(), matchItemToId(Integer.parseInt(temp_storage.get(i).get(0).Item0.replace("item0:", ""))), "drawable"));

            temp = bjerg + "_item2";
            ImageView it1 = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            it1.setImageResource(getStringIdentifier(v.getContext(), matchItemToId(Integer.parseInt(temp_storage.get(i).get(0).Item1.replace("item1:", ""))), "drawable"));

            temp = bjerg + "_item3";
            ImageView it2 = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            it2.setImageResource(getStringIdentifier(v.getContext(), matchItemToId(Integer.parseInt(temp_storage.get(i).get(0).Item2.replace("item2:", ""))), "drawable"));

            temp = bjerg + "_item4";
            ImageView it3 = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            it3.setImageResource(getStringIdentifier(v.getContext(), matchItemToId(Integer.parseInt(temp_storage.get(i).get(0).Item3.replace("item3:", ""))), "drawable"));

            temp = bjerg + "_item5";
            ImageView it4 = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            it4.setImageResource(getStringIdentifier(v.getContext(), matchItemToId(Integer.parseInt(temp_storage.get(i).get(0).Item4.replace("item4:", ""))), "drawable"));

            temp = bjerg + "_item6";
            ImageView it5 = (ImageView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            it5.setImageResource(getStringIdentifier(v.getContext(), matchItemToId(Integer.parseInt(temp_storage.get(i).get(0).Item5.replace("item5:", ""))), "drawable"));

            temp = bjerg + "_kills";
            TextView t1 =(TextView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            t1.setTextColor(getResources().getColor(R.color.white));
            t1.setText(temp_storage.get(i).get(0).Kills);

            temp = bjerg + "_deaths";
            TextView t2 =(TextView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            t2.setTextColor(getResources().getColor(R.color.white));
            t2.setText(temp_storage.get(i).get(0).Deaths);

            temp = bjerg + "_assists";
            TextView t3 =(TextView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            t3.setTextColor(getResources().getColor(R.color.white));
            t3.setText(temp_storage.get(i).get(0).Assists);

            temp = bjerg + "_cs";
            TextView t4 =(TextView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            t4.setTextColor(getResources().getColor(R.color.white));
            t4.setText(temp_storage.get(i).get(0).Cs);

            temp = bjerg + "_champLevel";
            TextView tv =(TextView) v.findViewById(getStringIdentifier(v.getContext(), temp));
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setText(temp_storage.get(i).get(0).ChampLevel);
        }
        return v;
    }

    private String matchItemToId(int in){
        String out = "";

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
            case 0: out = "default_no_item"; break;
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
            default: out = "default_icon"; break;
        }

        return out;
    }

    private int getStringIdentifier(Context context, String in){
        return context.getResources().getIdentifier(in, "id", context.getPackageName());
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }

    @Override
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), ViewMatch.class);
        intent.putExtra(EXTRA_MESSAGE, view.getContentDescription());
        startActivity(intent);
    }
}
