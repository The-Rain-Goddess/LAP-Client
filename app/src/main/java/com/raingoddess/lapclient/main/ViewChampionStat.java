package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by Black Lotus on 8/26/2016.
 */
public class ViewChampionStat extends AppCompatActivity {
    //private List<Match> temp_match_storage;
    private List<RankedChampionStat> temp_stat_storage;
    private String champName;
    private int totalNumPlayed, totalNumWon, totalKills, totalDeaths, totalAssists;
    private int champViewNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.champion_view);

//intent setup
        Intent intent = getIntent();
        String input = intent.getStringExtra(TabAnalysis.EXTRA_MESSAGE);

//toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleBar.setText(SendInputToHost.orig_summoner_name);

//champion stat setup
        //temp_match_storage = SendInputToHost.getGameDump();
        temp_stat_storage = TabAnalysis.getTempList();
        champViewNumber = Integer.parseInt(input);
        champName = temp_stat_storage.get(champViewNumber).getStatAtIndex(1).replace("champ:", "");
        totalNumPlayed = Integer.parseInt(temp_stat_storage.get(champViewNumber).getStatAtIndex(temp_stat_storage.get(champViewNumber).getStatSize()-5).replace("totalSessionsPlayed:", ""));
        totalNumWon = Integer.parseInt(temp_stat_storage.get(champViewNumber).getStatAtIndex(temp_stat_storage.get(champViewNumber).getStatSize()-3).replace("totalSessionsWon:", ""));
        System.out.println("Num: " + totalNumPlayed);

        //RelativeLayout layout = (RelativeLayout) findViewById(R.id.champion_view);
        //TextView view = (TextView) findViewById(R.id.tet1);
        //view.setText();

        setNameAndPortrait(champViewNumber);
        setBasicChampStats();
        setAveragePerGameStats();
        setKdaPerGameStat();
    }

    private void setKdaPerGameStat(){
        TextView kda = (TextView) findViewById(R.id.champion_view_average_kda_numbers);
        if(totalDeaths!=0){
            double kdaNumber = ( (double) totalKills + (double) totalAssists ) / (double) totalDeaths;
            if (kdaNumber < 1) kda.setTextColor(getResources().getColor(R.color.regular_red));
            else if (1 <= kdaNumber && kdaNumber <= 2.5) kda.setTextColor(getResources().getColor(R.color.okay_orange));
            else if (2.5 < kdaNumber && kdaNumber < 4.0) kda.setTextColor(getResources().getColor(R.color.good_green));
            else if (4.0 <= kdaNumber && kdaNumber < 5.0) kda.setTextColor(getResources().getColor(R.color.bluekda));
            else if (5.0 <= kdaNumber ) kda.setTextColor(getResources().getColor(R.color.goldkda));
            kda.setText(String.format(Locale.US, "%,.2f", kdaNumber));
        } else{
            kda.setTextColor(getResources().getColor(R.color.goldkda));
            kda.setText(getResources().getString(R.string.perfect));
        }
    }

    private void setAveragePerGameStats(){
        String identifiers[] = {"kills", "deaths", "assists", "cs", "gold", "damage", "pdamage",
        "mdamage", "dtaken", "heals"};
        String statLocators[] = {"totalKills", "totalDeaths", "totalAssists", "totalMinionKills", "totalGold",
        "totalDmgDealt", "totalPhysicalDmgDealt", "totalMagicDmgDealt", "totalDmgTaken", "totalHeal"};


        for(int i = 0; i<identifiers.length; i++){
            displayStatNumber(identifiers[i], statLocators[i]);
        }
    }

    private boolean displayStatNumber(String id, String stat){
        TextView stat_number = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "champion_view_average_" + id + "_numbers", "id"));
        for(int i = 0; i<temp_stat_storage.get(champViewNumber).getStatSize(); i++) {
            if (temp_stat_storage.get(champViewNumber).getStatAtIndex(i).contains(stat)) {
                double averageStat = Double.parseDouble(temp_stat_storage.get(champViewNumber).getStatAtIndex(i).replace(stat + ":", "")) / totalNumPlayed;
                stat_number.setText(String.format(Locale.US, "%,.1f",averageStat));
                if(id.equals("kills")){
                    totalKills = Integer.parseInt(temp_stat_storage.get(champViewNumber).getStatAtIndex(i).replace(stat + ":", ""));
                } else if(id.equals("deaths")){
                    totalDeaths = Integer.parseInt(temp_stat_storage.get(champViewNumber).getStatAtIndex(i).replace(stat + ":", ""));
                } else if(id.equals("assists")){
                    totalAssists = Integer.parseInt(temp_stat_storage.get(champViewNumber).getStatAtIndex(i).replace(stat + ":", ""));
                } break;
            }
        }return true;
    }

    private void setBasicChampStats(){
    //Num Played
        TextView numPlayed = (TextView) findViewById(R.id.num);
        numPlayed.setText(totalNumPlayed + "");

    //Percentage won
        TextView percentWon = (TextView) findViewById(R.id.champion_view_percentWon);
        double percent = (totalNumWon + 0.0) / (totalNumPlayed + 0.0) * 100.0;
        int roundedPercent = (int)Math.round(percent);
        percentWon.setText(roundedPercent + "");
        if(roundedPercent>=90){
            percentWon.setTextColor(getResources().getColor(R.color.goldkda));
        } else if(roundedPercent>=80){
            percentWon.setTextColor(getResources().getColor(R.color.bluekda));
        } else if(roundedPercent>=60){
            percentWon.setTextColor(getResources().getColor(R.color.greenkda));
        } else if(roundedPercent<=40){
            percentWon.setTextColor(getResources().getColor(R.color.loss_color));
        }

    //#W #L
        TextView numWon = (TextView) findViewById(R.id.champion_view_numberWon);
        numWon.setText(totalNumWon + "");

        TextView numLost = (TextView) findViewById(R.id.champion_view_numberLost);
        numLost.setText((totalNumPlayed-totalNumWon) + "");
    }

    private void setNameAndPortrait(int championToDisplay){
        TextView cName = (TextView)  findViewById(R.id.champion_view_champname);
        SpannableString underlinedName = new SpannableString(champName);
        underlinedName.setSpan(new UnderlineSpan(), 0, underlinedName.length(), 0);
        cName.setPaintFlags(cName.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        cName.setText(underlinedName);


        ImageView cPortrait = (ImageView) findViewById(R.id.champion_view_portrait);
        cPortrait.setImageResource(getStringIdentifier(getApplicationContext(), champName.toLowerCase().replace("'", "").replace(" ", ""), "drawable"));
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuActions.activateMenuItem(item.getItemId(), getApplicationContext());
        return super.onOptionsItemSelected(item);
    }
}
