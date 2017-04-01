package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Black Lotus on 7/22/2016.
 */
public class ViewMatch extends AppCompatActivity {
    private Toolbar toolbar;
    private List<Game> temp_storage;
    private TextView temp_text;
    private ImageView temp_image;
    private int gameNumber;
    private Game gameToBeDisplayed;
    int darkGreen;
    final int orange = Color.argb(100, 255, 128, 0);
    final int orangeYellow = Color.argb(100, 241, 162, 14);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_view);
        darkGreen = getResources().getColor(R.color.good_green);

//intent setup
        Intent intent = getIntent();
        String input = intent.getStringExtra(Main.EXTRA_MESSAGE);
        temp_storage = SendInputToHost.getGameDump();
        gameNumber = Integer.parseInt(input);
        gameToBeDisplayed = temp_storage.get(gameNumber);

//toolbar setup
        setupToolbar();

//LayoutSetup
        RelativeLayout layout = setupLayout(input);
        if(layout!=null)
            layout.setVisibility(View.VISIBLE);

//Data Visualization
        fillInData(gameNumber);

        setupScoreboard(gameNumber);

        setupPlayerStats(0);

    }

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        String viewMatchTitle = "Match ID#: " + temp_storage.get(gameNumber).get(0).getStat("matchId");
        titleBar.setText(viewMatchTitle);
    }

    private RelativeLayout setupLayout(String input){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.match_view);
        System.out.println(temp_storage.get(Integer.parseInt(input)).get(0).IsWinner);
        if(temp_storage.get(Integer.parseInt(input)).get(0).IsWinner.contains("true"))
            layout.setBackgroundColor(getResources().getColor(R.color.win_color));
        else if(Integer.parseInt(temp_storage.get(Integer.parseInt(input)).get(0).getStat("matchLength")) <= 300)
            layout.setBackgroundColor(getResources().getColor(R.color.remake_color));
        else
            layout.setBackgroundColor(getResources().getColor(R.color.loss_color_alternate));
        return layout;
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
        cImage.setImageResource(getStringIdentifier(getApplicationContext(), matchStats.getChampion(), "drawable"));
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

            showItems(matchStats, "", index);

        //cs
            TextView cs = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_cs", "id"));
            cs.setText(matchStats.getStat("cs"));

        TextView sName;
        if(matchStats.isClient()){
            //Summoner name
                sName = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_name", "id"));
                sName.setText(SendInputToHost.orig_summoner_name);
                sName.setBackgroundColor(Color.GREEN);
                sName.setDrawingCacheBackgroundColor(Color.GREEN);

        } else{
            //Summoner name
                sName = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + index + "_name", "id"));
                sName.setText(matchStats.getStat("playerName"));
        } sName.setOnClickListener(getScoreboardPlayerNameClickListener());
    }

    private void showItems(Match matchStats, String scoreboard, int index){
        for(int i = 0; i<7; i++){
            showItemForScoreboard(matchStats, scoreboard, index, "item"+i, i+1);
        }
    }

    private void showItemForScoreboard(Match matchStats, String tableId, int playerIndex, String itemId, int itemIndex){
        ImageView item1 = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_"+tableId+"table_" + playerIndex + "_item" + itemIndex, "id"));
        item1.setImageResource(getStringIdentifier(getApplicationContext(), TabMatchHistory2.matchItemToId(Integer.parseInt(matchStats.getStat(itemId))), "drawable"));
        item1.setOnClickListener(getItemClickListener(matchStats.getStat(itemId)));
    }

    private View.OnClickListener getItemClickListener(final String itemId){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.getMenu().add(itemId);
                popup.getMenu().add(TabMatchHistory2.matchItemToId(Integer.parseInt(itemId)));
                popup.inflate(R.menu.item_menu);
                popup.show();
            }
        };
    }

    private View.OnClickListener getScoreboardPlayerNameClickListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setAllNameBackgroundsToTransparent();
                view.setBackgroundColor(Color.GREEN);
                view.setDrawingCacheBackgroundColor(Color.GREEN);
                setupPlayerStats(findSelectedPlayerNumber());
            }
        };
    }

    private void setAllNameBackgroundsToTransparent(){
        TextView name;
        for(int i = 1; i<6; i++){
            name = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + i + "_name", "id"));
            name.setBackgroundColor(Color.TRANSPARENT);
            name.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        }

        for(int i = 1; i<6; i++){
            name = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + i + "_name", "id"));
            name.setBackgroundColor(Color.TRANSPARENT);
            name.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        }
    }

    private int findSelectedPlayerNumber(){
        TextView name;
        for(int i = 1; i<6; i++){
            name = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_table_" + i + "_name", "id"));
            if(name.getDrawingCacheBackgroundColor()==Color.GREEN)
                return gameToBeDisplayed.findPlayerIndex(name.getText().toString().trim());
        }

        for(int i = 1; i<6; i++){
            name = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + i + "_name", "id"));
            if(name.getDrawingCacheBackgroundColor()==Color.GREEN)
                return gameToBeDisplayed.findPlayerIndex(name.getText().toString().trim());
        }
        return 0;
    }

    private void showPlayerScoreBotBoard(Match matchStats, int index){

        //Summoner name
            TextView sName = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_name", "id"));
            sName.setText(matchStats.getStat("playerName"));
            sName.setOnClickListener(getScoreboardPlayerNameClickListener());

        //champ Image
            ImageView cImage = (ImageView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_players_2_table_" + index + "_champ", "id"));
            cImage.setImageResource(getStringIdentifier(getApplicationContext(), matchStats.getChampion(), "drawable"));
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

        showItems(matchStats, "2_", index);

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

    private void setupPlayerStats(int playerNumber){ //TODO: implement all stats
        showPlayerName(playerNumber);
        setStatBackgroundColors(playerNumber);
        showTotalStats(playerNumber);
        showTeamStats(playerNumber);
        showCombinedStats(playerNumber);
        showTimeStats(playerNumber);
    }

    private void showPlayerName(int player){
        TextView playerTitle = (TextView) findViewById(R.id.match_view_stats_title);
        String titleText = getPlayerName(player) + " Stats";
        playerTitle.setText(titleText);
    }

    private String getPlayerName(int player){
        if(gameToBeDisplayed.get(player).isClient())
            return (SendInputToHost.orig_summoner_name);
        else
            return (gameToBeDisplayed.get(player).getStat("playerName"));
    }

    private void setStatBackgroundColors(int player){
        List<String> layoutNames = Arrays.asList("total", "team", "combined", "time");
        for(int i = 0; i<layoutNames.size(); i++)
            setLayoutBackground(player, layoutNames.get(i));
    }

    private void setLayoutBackground(int player, String layoutName){
        RelativeLayout layout = (RelativeLayout) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_" + layoutName, "id"));
        if(gameToBeDisplayed.get(player).getStat("winner").contains("true"))
            layout.setBackgroundColor(getResources().getColor(R.color.win_color_alternate));
        else
            layout.setBackgroundColor(getResources().getColor(R.color.loss_color));
    }

    private void showTotalStats(int player){
        List<String> statDescriptions = Arrays.asList("dmg", "pdmg", "mdmg", "creep", "gold", "dtaken", "heals", "wards");
        List<String> statIdentifiers = Arrays.asList("dmgToChamp", "PhysicalDmgDealtToChampions", "magicDmgDealtChamps", "cs", "goldEarned", "dmgTaken", "totalUnitHealed", "wardsPlaced");
        for(int i = 0; i<statDescriptions.size(); i++)
            fillInStatForPlayer(player, statDescriptions.get(i), statIdentifiers.get(i));
    }

    private void fillInStatForPlayer(int player, String stat, String statId){
        TextView statNumberField = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_total_" + stat + "_numbers", "id"));
        if(gameToBeDisplayed.get(player).getStat(statId)!=null && statNumberField!=null)
            statNumberField.setText(gameToBeDisplayed.get(player).getStat(statId));
    }

    private void showTeamStats(int player){
        List<String> statBarNames = Arrays.asList("dmgpercent", "goldpercent");
        List<String> statKeys = Arrays.asList("dmgToChamp", "goldEarned");
        for(int i = 0; i<statBarNames.size(); i++){
            showStatBar(player, statBarNames.get(i), statKeys.get(i));
        }
    }

    private void showStatBar(int player, String statName, String statKey){
        ProgressBar statBar = (ProgressBar) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_team_" + statName + "_bar", "id"));
        statBar.setMax(100);
        double percent = getBarPercent(player, statKey, gameToBeDisplayed.get(player).isWinner());
        statBar.setProgress((int) (100.0 * percent));

        TextView statTextField = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_team_" + statName + "_numbers", "id"));
        statTextField.setText(String.format(Locale.US, "%.2f%%", percent*100.0));

        TextView pmTextField = (TextView)  findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_team_" + statName + "_pmnumbers", "id"));
        pmTextField.setText(String.format(Locale.US, "%+.2f%%", (percent*100 - 20.0)));
        pmTextField.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
    }

    private double getBarPercent(int player, String statKey, boolean isWinner){
        return (gameToBeDisplayed.get(player).getStatAsDouble(statKey) / gameToBeDisplayed.getTeamTotal(isWinner, statKey));
    }

    private void showCombinedStats(int player){
        List<String> statId =               Arrays.asList("dmggold",    "pdmggold",                     "mdmggold",             "dmgkill",      "goldkill",     "golddeath",    "dtakendeath",      "pdtakendeath",     "mdtakendeath");
        List<String> statNumerator =        Arrays.asList("dmgToChamp", "PhysicalDmgDealtToChampions" , "magicDmgDealtChamps",  "dmgToChamp",   "goldEarned",   "goldEarned",   "totalDmgTaken",    "physicalDmgTaken", "magicDmgTaken");
        List<String> statDenominator =      Arrays.asList("goldEarned", "goldEarned",                   "goldEarned",           "kills",        "kills",        "deaths",       "deaths",           "deaths",           "deaths");
        List<Double> statRatioDomains =     Arrays.asList( 3.0,          3.0,                            3.0,                    -10_000.0,      -10_000.0,      10_000.0,       10_000.0,           10_000.0,            10_000.0);
        for(int i = 0; i<statId.size(); i++){
            fillInCombinedStatsForPlayer(player, statId.get(i), statNumerator.get(i), statDenominator.get(i), statRatioDomains.get(i));
        }
    }

    private void fillInCombinedStatsForPlayer(int player, String id, String statNumerator, String statDenominator, double statRatioDomain){
        setupStatRatio(player, id, statNumerator, statDenominator, statRatioDomain);
        setupEvaluationText(id, getEvaluationColor(getStatRatio(player, statNumerator, statDenominator), statRatioDomain));

    }

    private void setupStatRatio(int player, String id, String statNumerator, String statDenominator, double domain){
        TextView statRatio = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_combined_" + id + "_numbers", "id"));
        double ratio = getStatRatio(player, statNumerator, statDenominator);
        if(domain>1000||domain<0)
            statRatio.setText(String.format(Locale.US, "%,.0f", ratio));
        else
            statRatio.setText(String.format(Locale.US, "%,.2f", ratio));
    }

    private int getEvaluationColor(double ratio, double domain){
        if(domain>0){
            if(ratio>=domain)
                return darkGreen;
            else if(ratio>=((domain+1)/2))
                return Color.GREEN;
            else if(ratio>=(1.0))
                return Color.YELLOW;
            else if(ratio>=1/((domain+1)/2))
                return orangeYellow;
            else if(ratio>=(1/ratio))
                return orange;
            else return Color.RED;
        } else if(domain<0){
            domain*=-1;
            if(ratio>=domain*8/10)
                return Color.RED;
            else if(ratio>=((domain+1)/3))
                return orange;
            else if(ratio>=(1.0+domain/7))
                return orangeYellow;
            else if(ratio>=1/((domain+1)/2) + domain/10)
                return Color.YELLOW;
            else if(ratio>=(1/ratio) + domain/20)
                return Color.GREEN;
            else return darkGreen;
        } else{
            return 0;
        }
    }

    private double getStatRatio(int player, String numerator, String denominator){
        double ratio = gameToBeDisplayed.get(player).getStatAsDouble(denominator);
        if((int)ratio == 0)
            return Double.NaN;
        return gameToBeDisplayed.get(player).getStatAsDouble(numerator) / ratio;
    }

    private void setupEvaluationText(String id, int evalColor){
        TextView statEvaluation = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_combined_" + id + "_evaluation", "id"));
        statEvaluation.setBackgroundColor(evalColor);
        statEvaluation.setTextColor(Color.BLACK);
        statEvaluation.setText(getEvaluationString(evalColor));
    }

    private String getEvaluationString(int evalColor){
        if(evalColor==Color.RED)
            return (getResources().getString(R.string.extremelyinefficient));
        else if(evalColor==darkGreen)
            return (getResources().getString(R.string.extremelyefficient));
        else if(evalColor==Color.YELLOW)
            return (getResources().getString(R.string.efficient));
        else if(evalColor==orangeYellow)
            return (getResources().getString(R.string.inefficient));
        else if(evalColor==Color.GREEN)
            return (getResources().getString(R.string.veryefficient));
        else return (getResources().getString(R.string.veryinefficient));
    }

    private void showTimeStats(int player){
        double gameLengthInMinutes = gameToBeDisplayed.getGameLengthInMinutes();
        List<String> statToBeRetrieved = Arrays.asList("cs", "goldEarned", "wardsPlaced", "dmgToChamp", "PhysicalDmgDealtToChampions", "magicDmgDealtChamps");
        List<Double> statDomain = Arrays.asList(10.0, 500.0, 2.0, 1000.0, 1000.0, 1000.0);
        for(int i = 0; i<statToBeRetrieved.size(); i++){
            fillInTimeStatsForPlayer(player, (i+1), statToBeRetrieved.get(i), statDomain.get(i), gameLengthInMinutes);

        } showMatchLength();
    }

    private void showMatchLength(){
        TextView matchLength = (TextView) findViewById(R.id.match_view_stats_time_length);
        matchLength.setText(String.format(Locale.US, "Length %.0fm", gameToBeDisplayed.getGameLengthInMinutes()));
    }

    private void fillInTimeStatsForPlayer(int player, int statIndex, String statId, double domain, double matchLength){
        TextView statNumbers = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_time_stat" + statIndex + "_numbers", "id"));
        double ratio = gameToBeDisplayed.get(player).getStatAsDouble(statId) / matchLength;
        statNumbers.setText(String.format(Locale.US, "%.1f", ratio));

        fillInTimeEvaluationForPlayer(statIndex, domain, ratio);
    }

    private void fillInTimeEvaluationForPlayer(int statIndex, double domain, double ratio){
        TextView eval = (TextView) findViewById(getStringIdentifier(getApplicationContext(), "match_view_stats_time_stat" + statIndex + "_eval", "id"));
        int evalColor = getEvaluationColor(ratio, domain);
        eval.setBackgroundColor(evalColor);
        String evalText = getEvaluationString(evalColor);
        eval.setText(evalText);
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
