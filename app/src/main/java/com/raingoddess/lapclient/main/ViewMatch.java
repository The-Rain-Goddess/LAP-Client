package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Black Lotus on 7/22/2016.
 */
public class ViewMatch extends AppCompatActivity {
    private Toolbar toolbar;
    private List<Game> temp_storage;
    private TextView temp_text;
    private ImageView temp_image;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_view);

//intent setup
        Intent intent = getIntent();
        String input = intent.getStringExtra(Main.EXTRA_MESSAGE);
        temp_storage = SendInputToHost.getGameDump();

//toolbar setup
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        String viewMatchTitle = "Match ID#: " + temp_storage.get(Integer.parseInt(input)).get(0).getStat("matchId");
        titleBar.setText(viewMatchTitle);

//LayoutSetup
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.match_view);
        System.out.println(temp_storage.get(Integer.parseInt(input)).get(0).IsWinner);
        if(temp_storage.get(Integer.parseInt(input)).get(0).IsWinner.contains("true"))
            layout.setBackgroundColor(getResources().getColor(R.color.win_color));
        else if(Integer.parseInt(temp_storage.get(Integer.parseInt(input)).get(0).getStat("matchLength")) <= 300)
            layout.setBackgroundColor(getResources().getColor(R.color.remake_color));
        else
            layout.setBackgroundColor(getResources().getColor(R.color.loss_color_alternate));


        RelativeLayout.LayoutParams relParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relParam.addRule(RelativeLayout.BELOW, R.id.tool_bar);

        int gameNumber = Integer.parseInt(input);

        fillInData(gameNumber);

        setupScoreboard(gameNumber);



    }

    private void setupScoreboard(int gameNumber){
        Game game = temp_storage.get(gameNumber);
        int clientTeamNumber = Integer.parseInt(game.get(0).getStat("teamId"));
        TableLayout topTable = (TableLayout) findViewById(R.id.match_view_players_table);
        TextView topTitle = (TextView) findViewById(R.id.match_view_players_table_title);

        TableLayout botTable = (TableLayout) findViewById(R.id.match_view_players_2_table);
        TextView botTitle = (TextView) findViewById(R.id.match_view_players_2_table_title);

        if(game.get(0).getStat("winner").equals("true")){
            topTable.setBackgroundColor(getResources().getColor(R.color.win_color_alternate));
            botTable.setBackgroundColor(getResources().getColor(R.color.loss_color));
            topTitle.setText(getResources().getString(R.string.champion_view_victory));
            botTitle.setText(getResources().getString(R.string.champion_view_defeat));
        } else{
            topTable.setBackgroundColor(getResources().getColor(R.color.loss_color));
            botTable.setBackgroundColor(getResources().getColor(R.color.win_color_alternate));
            botTitle.setText(getResources().getString(R.string.champion_view_victory));
            topTitle.setText(getResources().getString(R.string.champion_view_defeat));
        }
        int top = 1;
        int bot = 1;
        for(int i = 0; i<10; i++){
            if(Integer.parseInt(game.get(i).getStat("teamId")) == clientTeamNumber)
                showPlayerScoreTopBoard(game.get(i), top++);
            else
                showPlayerScoreBotBoard(game.get(i), bot++);
        }
    }

    private void showPlayerScoreTopBoard(Match matchStats, int index){

        //champ Image
        ImageView cImage = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_champ", "id"));
        cImage.setImageResource(getStringIdentifier(getApplicationContext(), matchStats.getStat("champion").toLowerCase().replace(" ", "").replace("'", ""), "drawable"));
        //cImage.setImageDrawable(getResources().getDrawable(getStringIdentifier(getApplicationContext(), matchStats.getStat("champion").toLowerCase().replace(" ", "").replace("'", ""), "drawable")));

        //SSpells
            ImageView sspell1 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_sspell1", "id"));
            sspell1.setImageResource(getStringIdentifier(getApplicationContext(), "s" + matchStats.getStat("sspell1"), "drawable"));

            ImageView sspell2 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_sspell2", "id"));
            sspell2.setImageResource(getStringIdentifier(getApplicationContext(), "s" + matchStats.getStat("sspell2"), "drawable"));

        //scoreline
            TextView score = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_score", "id"));
            String scoreText = matchStats.getStat("kills") + "/" + matchStats.getStat("deaths") + "/" + matchStats.getStat("assists");
            score.setText(scoreText);

        //Items 1
            ImageView item1 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item1", "id"));
            item1.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item0"))), "drawable"));

        //item2
            ImageView item2 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item2", "id"));
            item2.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item1"))), "drawable"));

        //item3
            ImageView item3 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item3", "id"));
            item3.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item2"))), "drawable"));

        //item4
            ImageView item4 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item4", "id"));
            item4.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item3"))), "drawable"));

        //item5
            ImageView item5 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item5", "id"));
            item5.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item4"))), "drawable"));

        //item6
            ImageView item6 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item6", "id"));
            item6.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item5"))), "drawable"));

        //item 7
            ImageView item7 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_item7", "id"));
            item7.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item6"))), "drawable"));

        //cs
            TextView cs = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_cs", "id"));
            cs.setText(matchStats.getStat("cs"));

        if(matchStats.isClient()){
            //Summoner name
                TextView sName = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_name", "id"));
                sName.setText(SendInputToHost.orig_summoner_name);

        } else{
            //Summoner name
                TextView sName = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_name", "id"));
                sName.setText(matchStats.getStat("playerName"));
        }
    }

    private void showPlayerScoreBotBoard(Match matchStats, int index){

        //Summoner name
            TextView sName = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_name", "id"));
            sName.setText(matchStats.getStat("playerName"));

        //champ Image
            ImageView cImage = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_champ", "id"));
            cImage.setImageResource(getStringIdentifier(getApplicationContext(), matchStats.getStat("champion").toLowerCase().replace(" ", "").replace("'", ""), "drawable"));
            //cImage.setImageDrawable(getResources().getDrawable(getStringIdentifier(getApplicationContext(), matchStats.getStat("champion").toLowerCase().replace(" ", "").replace("'", ""), "drawable")));

        //SSpells
            ImageView sspell1 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_sspell1", "id"));
            sspell1.setImageResource(getStringIdentifier(getApplicationContext(), "s" + matchStats.getStat("sspell1"), "drawable"));

            ImageView sspell2 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_sspell2", "id"));
            sspell2.setImageResource(getStringIdentifier(getApplicationContext(), "s" + matchStats.getStat("sspell2"), "drawable"));

        //scoreline
            TextView score = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_score", "id"));
            String scoreText = matchStats.getStat("kills") + "/" + matchStats.getStat("deaths") + "/" + matchStats.getStat("assists");
            score.setText(scoreText);

        //Items 1
            ImageView item1 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item1", "id"));
            item1.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item0"))), "drawable"));

        //item2
            ImageView item2 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item2", "id"));
            item2.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item1"))), "drawable"));

        //item3
            ImageView item3 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item3", "id"));
            item3.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item2"))), "drawable"));

        //item4
            ImageView item4 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item4", "id"));
            item4.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item3"))), "drawable"));

        //item5
            ImageView item5 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item5", "id"));
            item5.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item4"))), "drawable"));

        //item6
            ImageView item6 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item6", "id"));
            item6.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item5"))), "drawable"));

        //item 7
            ImageView item7 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_item7", "id"));
            item7.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat("item6"))), "drawable"));

        //cs
            TextView cs = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_cs", "id"));
            cs.setText(matchStats.getStat("cs"));
    }

    private boolean fillInData(int matchNum){
        int text_color = 0;
        String text_outcome = "";
        if(temp_storage.get(matchNum).get(0).IsWinner.contains("true")){
            text_color = getResources().getColor(R.color.good_green);
            text_outcome = "Victory";
        } else if(Integer.parseInt(temp_storage.get(matchNum).get(0).getStat("matchLength")) <= 300) { // for remakes
            text_color = getResources().getColor(R.color.black);
            text_outcome = "Remake";
        }else{
            text_color = getResources().getColor(R.color.regular_red);
            text_outcome = "Defeat";
        }
//ChampImage
        temp_image = (ImageView) findViewById(R.id.image_view_1);
        String nameChamp = temp_storage.get(matchNum).get(0).getStat("champion").toLowerCase().replace("'", "").replace(" ", "");
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), nameChamp, "drawable"));

//Summ Spell 1 and 2
    //spell 1
        temp_image = (ImageView) findViewById(R.id.image_view_2);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), temp_storage.get(matchNum).get(0).SSpell1.replace("sspell1:", "s"), "drawable"));

    //spell 2
        temp_image = (ImageView) findViewById(R.id.image_view_3);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), temp_storage.get(matchNum).get(0).SSpell2.replace("sspell2:", "s"), "drawable"));

//Victory/Defeat
        temp_text = (TextView) findViewById(R.id.text_9);
        temp_text.setTextColor(text_color);
        temp_text.setText(text_outcome);
        temp_text.setTextSize(50);

        temp_text = (TextView) findViewById(R.id.text_8);
        temp_text.setTextSize(50);

//KDA stats
        temp_text = (TextView) findViewById(R.id.text_1);
        temp_text.setText(temp_storage.get(matchNum).get(0).Kills.replace("kills:", ""));

        temp_text = (TextView) findViewById(R.id.text_3);
        temp_text.setText(temp_storage.get(matchNum).get(0).Deaths.replace("deaths:", ""));

        temp_text = (TextView) findViewById(R.id.text_5);
        temp_text.setText(temp_storage.get(matchNum).get(0).Assists.replace("assists:", "").replace("|","")); //substring(temp_storage.get(matchNum).Assists.indexOf("|"))

        temp_text = (TextView) findViewById(R.id.text_6);
        if(Integer.parseInt(temp_storage.get(matchNum).get(0).Deaths.replace("deaths:", ""))==0){
            String place_holder = "Perfect";
            temp_text.setText(place_holder);
            temp_text.setTextColor(getResources().getColor(R.color.goldkda));
        } else {
            double kda = (Integer.parseInt(temp_storage.get(matchNum).get(0).Kills.replace("kills:", "")) + 0.0 +
                    Integer.parseInt(temp_storage.get(matchNum).get(0).Assists.replace("assists:", "").replace("|","")) + 0.0)
                    / (Integer.parseInt(temp_storage.get(matchNum).get(0).Deaths.replace("deaths:", "")) + 0.0);
            DecimalFormat df = new DecimalFormat("#.##");

            String kda2 = df.format(kda) + "";
            temp_text.setText(kda2);
            if (kda < 1) temp_text.setTextColor(getResources().getColor(R.color.regular_red));
            else if (1 <= kda && kda <= 2.5) temp_text.setTextColor(getResources().getColor(R.color.okay_orange));
            else if (2.5 < kda) temp_text.setTextColor(getResources().getColor(R.color.good_green));
        }
//items 1-7
        temp_image = (ImageView) findViewById(R.id.image_4);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item0.replace("item0:", ""))), "drawable"));
    //item 2
        temp_image = (ImageView) findViewById(R.id.image_5);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item1.replace("item1:", ""))), "drawable"));
    //item 3
        temp_image = (ImageView) findViewById(R.id.image_6);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item2.replace("item2:", ""))), "drawable"));
    //item 4
        temp_image = (ImageView) findViewById(R.id.image_7);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item3.replace("item3:", ""))), "drawable"));
    //item 5
        temp_image = (ImageView) findViewById(R.id.image_8);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item4.replace("item4:", ""))), "drawable"));
    //item 6
        temp_image = (ImageView) findViewById(R.id.image_9);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item5.replace("item5:", ""))), "drawable"));
    //trinket
        //TODO: complete teh item registry
        temp_image = (ImageView) findViewById(R.id.image_10);
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(temp_storage.get(matchNum).get(0).Item6.replace("item6:", ""))), "drawable"));

//gold stats + icon
        temp_text = (TextView) findViewById(R.id.text_11);
        temp_text.setText(temp_storage.get(matchNum).get(0).GoldEarned.replace("goldEarned:", ""));
    //icon TODO: find a gold icon
        temp_image = (ImageView) findViewById((R.id.image_11));
        temp_image.setImageResource(getStringIdentifier(getApplicationContext(), "gold", "drawable"));

//Level
        temp_text = (TextView) findViewById(R.id.text_13);
        temp_text.setText(temp_storage.get(matchNum).get(0).ChampLevel.replace("champLevel:",""));

//Creep Score
        temp_text = (TextView) findViewById(R.id.text_14);
        String temp_string = "CS: " + temp_storage.get(matchNum).get(0).Cs.replace("cs:", "");
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
        temp_text.setText(temp_storage.get(input).get(0).getStat(statRequest));
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
