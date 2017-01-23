package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Black Lotus on 7/22/2016.
 */
public class ViewMatch extends AppCompatActivity {
    private Toolbar toolbar;
    private List<Match> temp_storage;
    private TextView temp_text;
    private ImageView temp_image;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_view);

//intent setup
        Intent intent = getIntent();
        String input = intent.getStringExtra(Main.EXTRA_MESSAGE);
        temp_storage = SendInputToHost.getMatchDump();

//toolbar setup
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //TODO: find a way to set the title bar to something else
//LayoutSetup
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.match_view);
        System.out.println(temp_storage.get(Integer.parseInt(input)).IsWinner);
        if(temp_storage.get(Integer.parseInt(input)).IsWinner.contains("true"))
            layout.setBackgroundColor(getResources().getColor(R.color.win_color));
        else
            layout.setBackgroundColor(getResources().getColor(R.color.loss_color_alternate));


        RelativeLayout.LayoutParams relParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relParam.addRule(RelativeLayout.BELOW, R.id.tool_bar);

        fillInData(Integer.parseInt(input));

    }

    private boolean fillInData(int matchNum){
        int text_color = 0;
        String text_outcome = "";
        if(temp_storage.get(matchNum).IsWinner.contains("true")){
            text_color = getResources().getColor(R.color.good_green);
            text_outcome = "Victory";
        } else{
            text_color = getResources().getColor(R.color.regular_red);
            text_outcome = "Defeat";
        }
//ChampImage
        temp_image = (ImageView) findViewById(R.id.image_view_1);
        String nameChamp = temp_storage.get(matchNum).statLine.get(57);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), nameChamp, "drawable"));

//Summ Spell 1 and 2
    //spell 1
        temp_image = (ImageView) findViewById(R.id.image_view_2);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), temp_storage.get(matchNum).SSpell1.replace("sspell1:", "s"), "drawable"));

    //spell 2
        temp_image = (ImageView) findViewById(R.id.image_view_3);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), temp_storage.get(matchNum).SSpell2.replace("sspell2:", "s"), "drawable"));

//Victory/Defeat
        temp_text = (TextView) findViewById(R.id.text_9);
        temp_text.setTextColor(text_color);
        temp_text.setText(text_outcome);
        temp_text.setTextSize(50);

        temp_text = (TextView) findViewById(R.id.text_8);
        temp_text.setTextSize(50);

//KDA stats
        temp_text = (TextView) findViewById(R.id.text_1);
        temp_text.setText(temp_storage.get(matchNum).Kills.replace("kills:", ""));

        temp_text = (TextView) findViewById(R.id.text_3);
        temp_text.setText(temp_storage.get(matchNum).Deaths.replace("deaths:", ""));

        temp_text = (TextView) findViewById(R.id.text_5);
        temp_text.setText(temp_storage.get(matchNum).Assists.replace("assists:", "").replace("|","")); //substring(temp_storage.get(matchNum).Assists.indexOf("|"))

        temp_text = (TextView) findViewById(R.id.text_6);
        if(Integer.parseInt(temp_storage.get(matchNum).Deaths.replace("deaths:", ""))==0){
            String place_holder = "";
            temp_text.setText(place_holder);
            temp_text.setTextColor(getResources().getColor(R.color.good_green));
        } else {
            double kda = (Integer.parseInt(temp_storage.get(matchNum).Kills.replace("kills:", "")) + 0.0 +
                    Integer.parseInt(temp_storage.get(matchNum).Assists.replace("assists:", "").replace("|","")) + 0.0)
                    / (Integer.parseInt(temp_storage.get(matchNum).Deaths.replace("deaths:", "")) + 0.0);
            DecimalFormat df = new DecimalFormat("#.##");

            String kda2 = df.format(kda) + "";
            temp_text.setText(kda2);
            if (kda < 1) temp_text.setTextColor(getResources().getColor(R.color.regular_red));
            else if (1 <= kda && kda <= 2.5) temp_text.setTextColor(getResources().getColor(R.color.okay_orange));
            else if (2.5 < kda) temp_text.setTextColor(getResources().getColor(R.color.good_green));
        }
//items 1-7
        temp_image = (ImageView) findViewById(R.id.image_4);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item0.replace("item0:", ""))), "drawable"));
    //item 2
        temp_image = (ImageView) findViewById(R.id.image_5);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item1.replace("item1:", ""))), "drawable"));
    //item 3
        temp_image = (ImageView) findViewById(R.id.image_6);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item2.replace("item2:", ""))), "drawable"));
    //item 4
        temp_image = (ImageView) findViewById(R.id.image_7);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item3.replace("item3:", ""))), "drawable"));
    //item 5
        temp_image = (ImageView) findViewById(R.id.image_8);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item4.replace("item4:", ""))), "drawable"));
    //item 6
        temp_image = (ImageView) findViewById(R.id.image_9);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item5.replace("item5:", ""))), "drawable"));
    //trinket
        //TODO: complete teh item registry
        temp_image = (ImageView) findViewById(R.id.image_10);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).Item6.replace("item6:", ""))), "drawable"));

//gold stats + icon
        temp_text = (TextView) findViewById(R.id.text_11);
        temp_text.setText(temp_storage.get(matchNum).GoldEarned.replace("goldEarned:", ""));
    //icon TODO: find a gold icon
        temp_image = (ImageView) findViewById((R.id.image_11));
        //temp_image.setImageResource(getStringIdentifier(getApplicationContext(), "gold", "drawable"));

//Level
        temp_text = (TextView) findViewById(R.id.text_13);
        temp_text.setText(temp_storage.get(matchNum).ChampLevel.replace("champLevel:",""));

//Creep Score
        temp_text = (TextView) findViewById(R.id.text_14);
        String temp_string = "CS: " + temp_storage.get(matchNum).Cs.replace("cs:", "");
        temp_text.setText(temp_string);

        return true;
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }

    private void createStatView(RelativeLayout layout, int input, int statRequest, int idForView){
        temp_text = new TextView(this);
        int temp1 = idForView - 1;
        temp_text.setId(idForView);
        temp_text.setText(temp_storage.get(input).statLine.get(statRequest));
        temp_text.setTextSize(30);
        RelativeLayout.LayoutParams relParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relParam.addRule(RelativeLayout.BELOW, temp1);
        layout.addView(temp_text, relParam);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }
}
