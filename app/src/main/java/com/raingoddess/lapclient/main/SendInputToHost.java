package com.raingoddess.lapclient.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.net.ConnectException;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.raingoddess.lapclient.main.Main.EXTRA_MESSAGE;

/**
 * Created by Black Lotus on 7/1/2016.
 */
public class SendInputToHost extends AppCompatActivity {
    public static List<Game> gameDump;
    public static boolean waitHere = true;
    public static String summoner_name;
    public static String orig_summoner_name;
    public static List<RankedChampionStat> ranked_summoner_stats = new ArrayList<>(150);

    private static List<RankedChampionStat> championStatResponse;
    private static List<String> masteryDataResponse;
    private static String profileDataResponse;
    private boolean matchDataCollected = false, champDataCollected = false, debugMode = false;
    private ProgressBar pB;
    private Toolbar toolbar;
    private WindowManager.LayoutParams windowParam;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Profile", "Match History", "Analysis"};
    int NumOfTabs = 3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_send_input_to_host);

//initiate dataHolds
        gameDump = new ArrayList<>();
        championStatResponse = new ArrayList<>();

//intent setup
        Intent intent = getIntent();
        String input = intent.getStringExtra(EXTRA_MESSAGE);
        orig_summoner_name = input;
        summoner_name = input.toLowerCase().replaceAll(" ", "");

//debug mode
        if(summoner_name.contains("debug_")){
            debugMode=true;
        }

//creates the background task for retrieving match data
        DataRetrieveTask drt = new DataRetrieveTask(summoner_name + "::get_match_history::8::0::8"); //second part of string hardcoded
        drt.execute();
//background task for retrieving profile data
        ProfileDataRetrieveTask pdrt = new ProfileDataRetrieveTask(summoner_name + "::get_profile::0::0::0");
        pdrt.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
//creates the background task for retrieving ranked champ stats                                                  // for concurrent AsyncTask execution
        ChampionStatRetrieveTask csrt = new ChampionStatRetrieveTask(summoner_name + "::get_analysis::0::0::0"); // = new MyTask().executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
        csrt.execute();

//the listener that waits for both task to be completed
        //android.os.Handler handler = new android.os.Handler();
        //handler.postDelayed(new RetrieveTaskListener(), 12000L);
        //Thread networkTaskListener = new Thread();
        //networkTaskListener.start();

//toolbar setup
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleBar.setText(input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


//progress bar
        pB = (ProgressBar) findViewById(R.id.progressBar);

//layout setup
        //LinearLayout layout = (LinearLayout) findViewById(R.id.content1);
    }

    public boolean isDebugMode(){ return debugMode; }

    public void setDebugMode(boolean b){ debugMode = b;}

    public static List<RankedChampionStat> getChampionStatResponse(){ return championStatResponse; }

    public static List<String> getMasteryDataResponse(){ return masteryDataResponse; }

    public static String getProfileDataResponse(){ return profileDataResponse; }

    public static List<Game> getGameDump(){return gameDump;}

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

    //subclass below
    //match history retrieval
    private class DataRetrieveTask extends AsyncTask<String, Integer, List<String>> { //do in Background thread
        private DataInputStream in;
        private DataOutputStream out;
        private Socket s;

        private int responseCode = 0;
        private String errorMessage;
        private String commandForServer;
        private ArrayList<String> responses;
        private TextView statusText;

        public DataRetrieveTask(String commandForServer) { //, Context context
            try {
                this.commandForServer = commandForServer;
                responses = new ArrayList<>(32);
                errorMessage = "";
            } catch (Exception e) {
                errorMessage = "Unhandled Exception.";
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
            statusText = (TextView) findViewById(R.id.loading_text);
            String loadString = "Loading User Match Data...";
            statusText.setText(loadString);
        }

        protected List<String> doInBackground(String... strings) {
            try {
                if(debugMode){
                    for(int i = 0; i<8; i++){
                        String debug_response = "assists:8/champLevel:12/cs:12/deaths:4/dmgToChamp:4317/dmgTaken:18436/doubleKills:0/firstBloodAssist:false/firtBloodKill:false/goldEarned:8177/item0:2055/item1:2302/item2:3117/item3:3504/item4:3107/item5:1028/item6:3364/kills:0/largestKillSpree:0/inhibKills:0/largestCritStrike:0/magicDmgDealt:4253/magicDmgDealtChamps:1746/magicDmgTaken:11071/minionsKilled:12/neutralMinionsKilled:0/neutralMinionsKilledEnemyJngl:0/neutralMinionsKilledTeamJngl:0/nodeCaptures:0/nodeCaptureAssist:0/nodeNeutralized:0/nodeNeutralizedAssist:0/objectivePlayerScore:0/pentaKills:0/physicalDmgDealt:3236/PhysicalDmgDealtToChampions:1312/physicalDmgTaken:6560/quadraKills:0/sightWardsBoughtInGame:0/teamObjective:0/totalDmgDealt:9748/totalDmgDealtToChampions:4317/totalDmgTaken:18436/totalHeal:2319/totalPlayerScore:0/totalScoreRank:0/totalTimeCrowdControlDealt:157/totalUnitHealed:2/towerKills:0/tripleKills:0/trueDmgTaken:804/trueDmgDealt:2259/trueDmgDealtChamps:1259/unrealKills:0/visionWardsBoughtInGame:4/wardsKilled:6/wardsPlaced:27/winner:false/champion:Lulu/sspell1:4/sspell2:3/matchLength:1829/matchId:2419170770/matchMode:CLASSIC/matchType:MATCHED_GAME/matchStartTime:1486527598928/queueType:TEAM_BUILDER_RANKED_SOLO/totalTeamDmg:45192/totalEnemyDmg:70863/";
                        responses.add(debug_response);
                        publishProgress((int)Math.ceil(50/8));
                    }
                } else{
                    this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                    this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                    this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                    out.writeUTF(commandForServer);
                    out.flush();

                    String rsp = in.readUTF();
                    if(rsp.contains("Exception")){
                        responseCode = -2;
                        return responses;
                    } responseCode = Integer.parseInt(rsp);
                    if(responseCode==-1){
                        String str = "Server-Side Error Retrieving Data";
                        errorMessage = str;
                        responseCode = -1;
                        responses.add(str);
                        return responses;

                    } else{
                        for(int i = 0; i<responseCode; i++){
                            rsp = in.readUTF();
                            //System.out.println("RSP: " + rsp);
                            responses.add(rsp);
                            publishProgress((int)Math.round(80.0/(double)responseCode));
                        } in.close(); out.close(); s.close();
                    }
                }
            } catch(ConnectException e){
                errorMessage = "Connection Was Refused, Unable to Connect to Server.";
                responseCode = -2;

                System.out.println(errorMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } return responses;
        }

        protected void onProgressUpdate(Integer... progress) {
            pB.incrementProgressBy(progress[0]);
        }

        protected void onPostExecute(List<String> result) {
            if(responseCode==-1){ //Server Exception
                statusText.setText(errorMessage);
                throw new NullPointerException();
            } else if(responseCode == -2){ //Client Exception
                statusText.setText(errorMessage);
                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                } //System.exit(0);
            } else{ //connection completed
                if(parseOutputString(result)){
                    String text = "Match History Data Recieved";
                    statusText.setText(text);
                    matchDataCollected = true;
                }
            }
        }

        private boolean parseOutputString(List<String> output){
            if((!output.contains("Exception"))) {
                //System.out.println("Size: " + output.size());
                Game temp_game;
                for (int i = 0; i < output.size(); i+=10) {
                    temp_game = new Game(   output.get(i), output.get(i+1),output.get(i+2),output.get(i+3)
                                            ,output.get(i+4),output.get(i+5),output.get(i+6),output.get(i+7)
                                            ,output.get(i+8),output.get(i+9));
                    gameDump.add(temp_game);
                } return true;
            } else {
                return false;
            }
        }
    }

    //Analysis retrieval
    private class ChampionStatRetrieveTask extends AsyncTask<String, Integer, Integer> { //do in Background thread
        private DataInputStream in;
        private DataOutputStream out;
        private Socket s;

        private String input;
        private String errorMessage;
        private ArrayList<String> responses;
       // private  List<RankedChampionStat> temp_list;
        private TextView loadText;

        ChampionStatRetrieveTask(String input) { //, Context context
            try {
                this.input = input;
                responses = new ArrayList<>(5);
                //temp_list = new ArrayList<>(150);
                errorMessage = "";
            } catch (Exception e) {
                errorMessage = "Unhandled Exception";
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
            loadText = (TextView) findViewById(R.id.loading_text);
            String statusText = "Loading User Analysis Data...";
            loadText.setText(statusText);
        }

        protected Integer doInBackground(String... strings) {
            try {
               if(debugMode){
                    String debug_response = "/cmp:/champ:Janna/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:7/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/totalDeaths:0/rankedSolo:0/totalAssists:82/totalKills:6/totalDmgDealt:86528/totalDmgTaken:104867/doubleKills:0/totalFirstBlood:0/totalGold:71022/totalHeal:0/totalMagicDmgDealt:47466/totalMinionKills:218/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:36180/totalQuadraKills:0/totalSessionsPlayed:7/totalSessionsLost:3/totalSessionsWon:4/totalTripleKills:0/totalTurretKills:4";
                    responses.add(debug_response);
               } else{
                   this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                   //////////very important for net
                   this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                   this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                   out.writeUTF(input);
                   out.flush();

                   String rsp = in.readUTF();
                   int responseCode = Integer.parseInt(rsp);
                   if(responseCode<=0)
                       return responseCode;
                   for(int i = 0; i<responseCode; i++){
                       rsp = in.readUTF();
                       responses.add(rsp);
                       publishProgress(1);
                   }

                   in.close();
                   out.close();
                   s.close();
               }
            } catch(ConnectException e){
                e.printStackTrace();
                errorMessage = "Connection Refused. Could not connect to Server.";
                return -2;
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "IOException during Analysis Data Retrieval.";
                return -2;
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                errorMessage = e.getMessage();
                return -2;
            }

            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
            pB.incrementProgressBy(progress[0]);
        }

        protected void onPostExecute(Integer responseCode) {
            if(responseCode==0){
                championStatResponse =  sort(parseOutputString(responses));
                System.out.println("Champ " + championStatResponse);
                String text = "User Analysis Data loaded.";
                loadText.setText(text);
                pB.setVisibility(View.GONE);
                findViewById(R.id.loading_text).setVisibility(View.GONE);

                setupTabs();
            } else if(responseCode==-1){ //Server-side Exception
                loadText.setText(errorMessage);
                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } //System.exit(0);
            } else if(responseCode==-2){ //Client-side exception
                loadText.setText(errorMessage);
                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } //System.exit(0);
            }
        }

        private List<RankedChampionStat> parseOutputString(List<String> output){
            List<RankedChampionStat> stats_list = new ArrayList<>(150);
            if((!output.contains("Exception"))) {
                output.remove(0);
                RankedChampionStat temp_stat;
                for (int i = 0; i < output.size(); i++) {
                    temp_stat = new RankedChampionStat(output.get(i));
                    stats_list.add(temp_stat);
                }
                for(int i = 0; i< stats_list.size(); i++){
                    //System.out.println(temp_list.get(i));
                } return stats_list;
            } else {
                return stats_list;
            }
        }

        private List<RankedChampionStat> sort(List<RankedChampionStat> stat_list){
            //temp_list = stat_list;
            //System.err.println("length " + temp_length);
            //System.err.println("list: " + temp_list);
            try{
                quickSort(0, stat_list.size()-1, stat_list);
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                for(int i = 0; i<stat_list.size(); i++){
                    if(stat_list.get(i).getStats().size() < 35){
                        stat_list.remove(i);
                    }
                } sort(stat_list);
            } return stat_list;
        }

        private void quickSort(int low, int high, List<RankedChampionStat> temp_list){
            int i = low;
            int j = high;

            RankedChampionStat pivot = temp_list.get(i + (j-i) / 2);
            while(i <= j){
                //System.out.println("Place: " + i);
                //System.out.println("i: " + (temp_list.get(i).getStatAtIndex(1)));
                //System.out.println("j: " + (temp_list.get(j).getStatAtIndex(1)));
                while(Integer.parseInt(temp_list.get(i).getStatAtIndex(37).replace("totalSessionsPlayed:","")) < Integer.parseInt(pivot.getStatAtIndex(37).replace("totalSessionsPlayed:","")) ){
                    i++;
                }
                while( Integer.parseInt(temp_list.get(j).getStatAtIndex(37).replace("totalSessionsPlayed:","")) > Integer.parseInt(pivot.getStatAtIndex(37).replace("totalSessionsPlayed:","")) ){
                    j--;
                }
                if(i <= j){
                    exchangePos(i,j, temp_list); //move index to next position on both sides
                    i++;
                    j--;
                }
            }
            //call quickSort Recursively
            if(low<j)
                quickSort(low,j, temp_list);
            if(i<high)
                quickSort(i, high, temp_list);
        }

        private void exchangePos(int i, int j, List<RankedChampionStat> stat_list){
            RankedChampionStat temp = stat_list.get(i);
            stat_list.add(i, stat_list.get(j));
            stat_list.remove(i+1);

            stat_list.add(j, temp);
            stat_list.remove(j+1);
        }

        private void setupTabs(){
            //ViewPagerAdapter
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);
            pager = (ViewPager) findViewById(R.id.pagerView);
            pager.setAdapter(adapter);

            //Sliding Tab Setup
            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true);

            //settign custom color
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            //setting the ViewPager for Sliding Tab Layout
            tabs.setViewPager(pager);
        }
    }

    //Profile retrieval
    private class ProfileDataRetrieveTask extends AsyncTask<String, Integer, Integer>{
        protected DataInputStream in;
        protected DataOutputStream out;
        protected Socket s;

        private ArrayList<String> responses;
        private String errorMessage;
        private String serverCommand;

        private TextView loadText;

        public ProfileDataRetrieveTask( String commandForServer){
            serverCommand = commandForServer;
            responses = new ArrayList<>(4);
        }

        protected void onPreExecute(){
            loadText = (TextView) findViewById(R.id.loading_text);
        }

        protected Integer doInBackground(String... strings){
            try {
                if(debugMode){
                    String debug_response = "RANKED_SOLO_5x5:PLATINUM:V:39LP/RANKED_FLEX_SR:DIAMOND:V:100L";
                    responses.add(debug_response);
                    debug_response = "Bard:5:128986"; responses.add(debug_response);
                    debug_response = "Taliyah:7:122121"; responses.add(debug_response);
                    debug_response = "Braum:5:58415"; responses.add(debug_response);
                    debug_response = "Lux:7:55414"; responses.add(debug_response);
                    debug_response = "Zyra:6:54680"; responses.add(debug_response);
                } else{
                    this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                    //////////very important for net
                    this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                    this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                    out.writeUTF(serverCommand);
                    out.flush();

                    String rsp = in.readUTF();
                    int limit = Integer.parseInt(rsp);
                    for(int i = 0; i<limit; i++){
                        rsp = in.readUTF();
                        responses.add(rsp);
                        publishProgress(10/limit);

                    }
                    in.close();
                    out.close();
                    s.close();
                }
            } catch(ConnectException e){
                e.printStackTrace();
                errorMessage = "Could not connect to server";
                return -2;
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "IOException occurred during Profile retrieval.";
                return -2;
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                errorMessage = e.getMessage();
                return -2;
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress){
            pB.incrementProgressBy(progress[0]);
        }

        protected void onPostExecute(Integer responseCode){
            if(responseCode==0){
                profileDataResponse = responses.get(0);
                masteryDataResponse = responses;
                String ui = "Profile Data Loaded Successfully";
                loadText.setText(ui);

            } else if(responseCode==-1){ //Server-side Exception
                loadText.setText(errorMessage);
                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } //System.exit(0);
            } else if(responseCode==-2){ //Client-side exception
                loadText.setText(errorMessage);
                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } //System.exit(0);
            }
        }
    }
}

