package com.raingoddess.lapclient.main;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.net.ConnectException;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Black Lotus on 7/1/2016.
 */
public class SendInputToHost extends AppCompatActivity {
    public static ArrayList<Match> matchDump;
    public static boolean waitHere = true;
    public static String summoner_name;
    public static String orig_summoner_name;
    public static ArrayList<RankedChampionStat> ranked_summoner_stats = new ArrayList<>(150);

    private static String championStatResponse;
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
        matchDump = new ArrayList<>();

//intent setup
        Intent intent = getIntent();
        String input = intent.getStringExtra(Main.EXTRA_MESSAGE);
        orig_summoner_name = input;
        summoner_name = input.toLowerCase().replaceAll(" ", "");

//debug mode
        if(summoner_name.contains("debug_")){
            debugMode=true;
        }

//creates the background task for retrieving match data
        DataRetrieveTask drt = new DataRetrieveTask(summoner_name + "::get_match_history::8::0::8"); //second part of string hardcoded
        drt.execute();
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

    public static String getChampionStatResponse(){ return championStatResponse; }

    public static ArrayList<Match> getMatchDump(){return matchDump;}

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

    //subclass below
    private class DataRetrieveTask extends AsyncTask<String, Integer, List<String>> { //do in Background thread
        protected DataInputStream in;
        protected DataOutputStream out;
        protected Socket s;

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
                        String debug_response = "assists:8/champLevel:12/cs:12/deaths:4/dmgToChamp:4317/dmgTaken:18436/doubleKills:0/firstBloodAssist:false/firtBloodKill:false/goldEarned:8177/item0:2055/item1:2302/item2:3117/item3:3504/item4:3107/item5:1028/item6:3364/kills:0/largestKillSpree:0/inhibKills:0/largestCritStrike:0/magicDmgDealt:4253/magicDmgDealtChamps:1746/magicDmgTaken:11071/minionsKilled:12/neutralMinionsKilled:0/neutralMinionsKilledEnemyJngl:0/neutralMinionsKilledTeamJngl:0/nodeCaptures:0/nodeCaptureAssist:0/nodeNeutralized:0/nodeNeutralizedAssist:0/objectivePlayerScore:0/pentaKills:0/physicalDmgDealt:3236/PhysicalDmgDealtToChampions:1312/physicalDmgTaken:6560/quadraKills:0/sightWardsBoughtInGame:0/teamObjective:0/totalDmgDealt:9748/totalDmgDealtToChampions:4317/totalDmgTaken:18436/totalHeal:2319/totalPlayerScore:0/totalScoreRank:0/totalTimeCrowdControlDealt:157/totalUnitHealed:2/towerKills:0/tripleKills:0/trueDmgTaken:804/trueDmgDealt:2259/trueDmgDealtChamps:1259/unrealKills:0/visionWardsBoughtInGame:4/wardsKilled:6/wardsPlaced:27/winner:false/champion:Lulu/sspell1:4/sspell2:3/matchLength:1839/totalTeamDmg:45192/totalEnemyDmg:70863/";
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
                    responseCode = Integer.parseInt(rsp);
                    if(responseCode==-1 || rsp.equals("RiotApiException")){
                        String str = "Error Retrieving Data";
                        responseCode = -1;
                        responses.add(str);
                        return responses;
                        //
                    } else{
                        for(int i = 0; i<8; i++){
                            rsp = in.readUTF();
                            //System.out.println("RSP: " + rsp);
                            responses.add(rsp);
                            publishProgress((int)Math.ceil(50/responseCode));
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
                statusText.setText(result.get(0));
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
                    //getWindowManager().removeView(pB);
                    matchDataCollected = true;
                }
            }
        }

        protected boolean parseOutputString(List<String> output){
            if((!output.contains("Exception"))) {
                //System.out.println("Size: " + output.size());
                Match temp_match;
                for (int i = 0; i < output.size(); i++) {
                    temp_match = new Match(output.get(i).replace("|MATCH:", ""));
                    matchDump.add(temp_match);

                }
            } else {
                return false;
            }   return true;
        }
    }

    private class ChampionStatRetrieveTask extends AsyncTask<String, Integer, Integer> { //do in Background thread
        protected DataInputStream in;
        protected DataOutputStream out;
        protected Socket s;

        private String input;
        private String errorMessage;
        private ArrayList<String> responses;
        private Context context;
        private ProgressDialog mProgressDialog;
        private TextView loadText;

        public ChampionStatRetrieveTask(String input) { //, Context context
            try {
                this.input = input;
                responses = new ArrayList<>(5);
                errorMessage = "";
            } catch (Exception e) {
                errorMessage = "Unhandled Exception";
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
           loadText = (TextView) findViewById(R.id.loading_text);
        }

        protected Integer doInBackground(String... strings) {
            try {
               if(debugMode){
                    String debug_response = "/cmp:/champ:Janna/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:7/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:82/totalKills:6/totalDmgDealt:86528/totalDmgTaken:104867/doubleKills:0/totalFirstBlood:0/totalGold:71022/totalHeal:0/totalMagicDmgDealt:47466/totalMinionKills:218/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:36180/totalQuadraKills:0/totalSessionsPlayed:7/totalSessionsLost:3/totalSessionsWon:4/totalTripleKills:0/totalTurretKills:4/cmp:/champ:Varus/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:11/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:11/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:8/totalKills:19/totalDmgDealt:435661/totalDmgTaken:54399/doubleKills:3/totalFirstBlood:0/totalGold:35564/totalHeal:0/totalMagicDmgDealt:22362/totalMinionKills:616/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:409839/totalQuadraKills:0/totalSessionsPlayed:3/totalSessionsLost:2/totalSessionsWon:1/totalTripleKills:1/totalTurretKills:3/cmp:/champ:Karma/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:2/largestCrit:0/largestKillingSpree:0/maxDeaths:10/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:2/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:36/totalKills:2/totalDmgDealt:27756/totalDmgTaken:40669/doubleKills:0/totalFirstBlood:0/totalGold:20250/totalHeal:0/totalMagicDmgDealt:18306/totalMinionKills:54/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:8292/totalQuadraKills:0/totalSessionsPlayed:2/totalSessionsLost:1/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Ziggs/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:1/largestCrit:0/largestKillingSpree:0/maxDeaths:6/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:1/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:1/totalKills:1/totalDmgDealt:95020/totalDmgTaken:9947/doubleKills:0/totalFirstBlood:0/totalGold:7224/totalHeal:0/totalMagicDmgDealt:81718/totalMinionKills:173/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:13082/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Sona/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:6/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:23/totalKills:3/totalDmgDealt:34986/totalDmgTaken:31080/doubleKills:0/totalFirstBlood:0/totalGold:14939/totalHeal:0/totalMagicDmgDealt:24209/totalMinionKills:34/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:6260/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Lulu/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:4/largestCrit:0/largestKillingSpree:0/maxDeaths:10/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:4/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:57/totalKills:6/totalDmgDealt:81188/totalDmgTaken:81954/doubleKills:1/totalFirstBlood:0/totalGold:42818/totalHeal:0/totalMagicDmgDealt:52242/totalMinionKills:73/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:24884/totalQuadraKills:0/totalSessionsPlayed:4/totalSessionsLost:2/totalSessionsWon:2/totalTripleKills:0/totalTurretKills:1/cmp:/champ:Vi/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:9/largestCrit:0/largestKillingSpree:0/maxDeaths:10/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:9/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:20/totalKills:12/totalDmgDealt:483759/totalDmgTaken:70293/doubleKills:1/totalFirstBlood:0/totalGold:31164/totalHeal:0/totalMagicDmgDealt:5424/totalMinionKills:97/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:446334/totalQuadraKills:0/totalSessionsPlayed:2/totalSessionsLost:0/totalSessionsWon:2/totalTripleKills:0/totalTurretKills:3/cmp:/champ:Shyvana/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:11/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:11/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:20/totalKills:25/totalDmgDealt:963900/totalDmgTaken:183591/doubleKills:4/totalFirstBlood:0/totalGold:63451/totalHeal:0/totalMagicDmgDealt:372650/totalMinionKills:261/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:531770/totalQuadraKills:0/totalSessionsPlayed:6/totalSessionsLost:3/totalSessionsWon:3/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Caitlyn/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:5/largestCrit:0/largestKillingSpree:0/maxDeaths:11/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:5/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:53/totalKills:19/totalDmgDealt:757876/totalDmgTaken:110888/doubleKills:1/totalFirstBlood:0/totalGold:64179/totalHeal:0/totalMagicDmgDealt:19210/totalMinionKills:1017/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:731198/totalQuadraKills:0/totalSessionsPlayed:6/totalSessionsLost:4/totalSessionsWon:2/totalTripleKills:0/totalTurretKills:11/cmp:/champ:Taric/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:1/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:1/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:19/totalKills:1/totalDmgDealt:25318/totalDmgTaken:42895/doubleKills:0/totalFirstBlood:0/totalGold:11820/totalHeal:0/totalMagicDmgDealt:9554/totalMinionKills:56/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:7601/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Veigar/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:5/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:5/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:2/totalKills:5/totalDmgDealt:92450/totalDmgTaken:17186/doubleKills:0/totalFirstBlood:0/totalGold:8624/totalHeal:0/totalMagicDmgDealt:82939/totalMinionKills:140/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:9511/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Maokai/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:4/largestCrit:0/largestKillingSpree:0/maxDeaths:9/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:4/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:32/totalKills:10/totalDmgDealt:434924/totalDmgTaken:102585/doubleKills:0/totalFirstBlood:0/totalGold:38748/totalHeal:0/totalMagicDmgDealt:364990/totalMinionKills:646/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:66948/totalQuadraKills:0/totalSessionsPlayed:3/totalSessionsLost:2/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:4/cmp:/champ:Blitzcrank/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:4/largestCrit:0/largestKillingSpree:0/maxDeaths:14/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:4/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:38/totalKills:5/totalDmgDealt:67466/totalDmgTaken:71612/doubleKills:0/totalFirstBlood:0/totalGold:22363/totalHeal:0/totalMagicDmgDealt:45899/totalMinionKills:38/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:17671/totalQuadraKills:0/totalSessionsPlayed:2/totalSessionsLost:1/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:2/cmp:/champ:Malphite/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:5/largestCrit:0/largestKillingSpree:0/maxDeaths:10/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:5/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:7/totalKills:5/totalDmgDealt:115726/totalDmgTaken:36259/doubleKills:0/totalFirstBlood:0/totalGold:10474/totalHeal:0/totalMagicDmgDealt:87413/totalMinionKills:144/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:27248/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:1/cmp:/champ:Lee Sin/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:4/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:4/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:10/totalKills:4/totalDmgDealt:129314/totalDmgTaken:29411/doubleKills:0/totalFirstBlood:0/totalGold:10692/totalHeal:0/totalMagicDmgDealt:15936/totalMinionKills:26/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:103113/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:2/cmp:/champ:Kha'Zix/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:1/largestCrit:0/largestKillingSpree:0/maxDeaths:5/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:1/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:7/totalKills:1/totalDmgDealt:127878/totalDmgTaken:26727/doubleKills:0/totalFirstBlood:0/totalGold:9824/totalHeal:0/totalMagicDmgDealt:4335/totalMinionKills:16/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:113544/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Orianna/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:4/largestCrit:0/largestKillingSpree:0/maxDeaths:6/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:4/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:6/totalKills:4/totalDmgDealt:143451/totalDmgTaken:17496/doubleKills:0/totalFirstBlood:0/totalGold:12337/totalHeal:0/totalMagicDmgDealt:121527/totalMinionKills:191/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:20671/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:2/cmp:/champ:Brand/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:6/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:6/totalKills:3/totalDmgDealt:144541/totalDmgTaken:17459/doubleKills:0/totalFirstBlood:0/totalGold:12805/totalHeal:0/totalMagicDmgDealt:118047/totalMinionKills:230/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:24888/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:1/cmp:/champ:Annie/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:12/largestCrit:0/largestKillingSpree:0/maxDeaths:12/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:12/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:32/totalKills:44/totalDmgDealt:573442/totalDmgTaken:112770/doubleKills:6/totalFirstBlood:0/totalGold:57676/totalHeal:0/totalMagicDmgDealt:501538/totalMinionKills:798/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:64241/totalQuadraKills:0/totalSessionsPlayed:5/totalSessionsLost:2/totalSessionsWon:3/totalTripleKills:1/totalTurretKills:3/cmp:/champ:Bard/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:7/largestCrit:0/largestKillingSpree:0/maxDeaths:12/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:7/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:27/totalKills:7/totalDmgDealt:51285/totalDmgTaken:35785/doubleKills:1/totalFirstBlood:0/totalGold:15074/totalHeal:0/totalMagicDmgDealt:36192/totalMinionKills:49/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:10758/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Annie/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:193/maxAssists:0/maxKills:19/largestCrit:1895/largestKillingSpree:9/maxDeaths:15/maxTeamObj:0/maxTime:3292/maxTimeLive:1422/mostChampKillSession:19/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:1191/totalKills:493/totalDmgDealt:10967866/totalDmgTaken:2828963/doubleKills:54/totalFirstBlood:0/totalGold:1293348/totalHeal:592095/totalMagicDmgDealt:4440545/totalMinionKills:11670/totalNeutralKills:2289/totalPentaKills:0/totalPhysicalDmgDealt:5909882/totalQuadraKills:0/totalSessionsPlayed:111/totalSessionsLost:57/totalSessionsWon:54/totalTripleKills:8/totalTurretKills:100/cmp:/champ:Xin Zhao/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:8/largestCrit:0/largestKillingSpree:0/maxDeaths:12/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:8/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:17/totalKills:14/totalDmgDealt:285019/totalDmgTaken:73122/doubleKills:1/totalFirstBlood:0/totalGold:24643/totalHeal:0/totalMagicDmgDealt:44992/totalMinionKills:125/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:213794/totalQuadraKills:0/totalSessionsPlayed:2/totalSessionsLost:1/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:3/cmp:/champ:Zyra/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:6/largestCrit:0/largestKillingSpree:0/maxDeaths:15/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:6/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:72/totalKills:18/totalDmgDealt:243615/totalDmgTaken:96872/doubleKills:0/totalFirstBlood:0/totalGold:48988/totalHeal:0/totalMagicDmgDealt:206972/totalMinionKills:159/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:32250/totalQuadraKills:0/totalSessionsPlayed:4/totalSessionsLost:2/totalSessionsWon:2/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Ivern/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:2/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:2/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:16/totalKills:2/totalDmgDealt:243445/totalDmgTaken:35870/doubleKills:0/totalFirstBlood:0/totalGold:11760/totalHeal:0/totalMagicDmgDealt:15533/totalMinionKills:33/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:9249/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Kalista/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:15/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:9/totalKills:3/totalDmgDealt:96805/totalDmgTaken:28444/doubleKills:0/totalFirstBlood:0/totalGold:10648/totalHeal:0/totalMagicDmgDealt:1112/totalMinionKills:161/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:95208/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Cassiopeia/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:12/largestCrit:0/largestKillingSpree:0/maxDeaths:12/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:12/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:24/totalKills:32/totalDmgDealt:638674/totalDmgTaken:111522/doubleKills:5/totalFirstBlood:0/totalGold:57632/totalHeal:0/totalMagicDmgDealt:597495/totalMinionKills:866/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:38756/totalQuadraKills:0/totalSessionsPlayed:4/totalSessionsLost:3/totalSessionsWon:1/totalTripleKills:1/totalTurretKills:6/cmp:/champ:Ezreal/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:8/largestCrit:0/largestKillingSpree:0/maxDeaths:10/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:8/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:38/totalKills:17/totalDmgDealt:574599/totalDmgTaken:91931/doubleKills:2/totalFirstBlood:0/totalGold:46850/totalHeal:0/totalMagicDmgDealt:125486/totalMinionKills:641/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:439162/totalQuadraKills:0/totalSessionsPlayed:3/totalSessionsLost:1/totalSessionsWon:2/totalTripleKills:0/totalTurretKills:7/cmp:/champ:Rek'Sai/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:13/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:12/totalKills:3/totalDmgDealt:164401/totalDmgTaken:62238/doubleKills:1/totalFirstBlood:0/totalGold:13821/totalHeal:0/totalMagicDmgDealt:33983/totalMinionKills:45/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:117380/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:1/cmp:/champ:Tristana/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:19/largestCrit:0/largestKillingSpree:0/maxDeaths:12/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:19/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:84/totalKills:98/totalDmgDealt:1793581/totalDmgTaken:281757/doubleKills:17/totalFirstBlood:0/totalGold:161818/totalHeal:0/totalMagicDmgDealt:531195/totalMinionKills:2356/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:1238607/totalQuadraKills:0/totalSessionsPlayed:14/totalSessionsLost:6/totalSessionsWon:8/totalTripleKills:4/totalTurretKills:35/cmp:/champ:Sivir/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:7/largestCrit:0/largestKillingSpree:0/maxDeaths:6/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:7/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:16/totalKills:7/totalDmgDealt:149182/totalDmgTaken:19253/doubleKills:0/totalFirstBlood:0/totalGold:13840/totalHeal:0/totalMagicDmgDealt:22487/totalMinionKills:201/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:126233/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:2/cmp:/champ:Braum/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:3/largestCrit:0/largestKillingSpree:0/maxDeaths:7/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:3/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:5/totalKills:3/totalDmgDealt:20788/totalDmgTaken:27071/doubleKills:0/totalFirstBlood:0/totalGold:7580/totalHeal:0/totalMagicDmgDealt:6311/totalMinionKills:39/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:7950/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Soraka/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:2/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:2/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:22/totalKills:2/totalDmgDealt:23637/totalDmgTaken:23879/doubleKills:0/totalFirstBlood:0/totalGold:13535/totalHeal:0/totalMagicDmgDealt:17046/totalMinionKills:18/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:4223/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Vel'Koz/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:1/largestCrit:0/largestKillingSpree:0/maxDeaths:13/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:1/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:15/totalKills:1/totalDmgDealt:93333/totalDmgTaken:25252/doubleKills:0/totalFirstBlood:0/totalGold:15083/totalHeal:0/totalMagicDmgDealt:68692/totalMinionKills:98/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:6466/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Nami/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:1/largestCrit:0/largestKillingSpree:0/maxDeaths:2/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:1/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:7/totalKills:1/totalDmgDealt:14972/totalDmgTaken:7692/doubleKills:0/totalFirstBlood:0/totalGold:7476/totalHeal:0/totalMagicDmgDealt:7780/totalMinionKills:11/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:6736/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:0/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:1/cmp:/champ:Leona/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:11/largestCrit:0/largestKillingSpree:0/maxDeaths:14/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:11/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:158/totalKills:26/totalDmgDealt:342817/totalDmgTaken:276805/doubleKills:2/totalFirstBlood:0/totalGold:99720/totalHeal:0/totalMagicDmgDealt:153475/totalMinionKills:527/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:114274/totalQuadraKills:0/totalSessionsPlayed:8/totalSessionsLost:2/totalSessionsWon:6/totalTripleKills:1/totalTurretKills:3/cmp:/champ:Thresh/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:4/largestCrit:0/largestKillingSpree:0/maxDeaths:11/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:4/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:98/totalKills:15/totalDmgDealt:123958/totalDmgTaken:162424/doubleKills:1/totalFirstBlood:0/totalGold:58539/totalHeal:0/totalMagicDmgDealt:81217/totalMinionKills:208/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:39398/totalQuadraKills:0/totalSessionsPlayed:6/totalSessionsLost:2/totalSessionsWon:4/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Tahm Kench/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:1/largestCrit:0/largestKillingSpree:0/maxDeaths:5/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:1/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:6/totalKills:1/totalDmgDealt:38153/totalDmgTaken:33895/doubleKills:0/totalFirstBlood:0/totalGold:8919/totalHeal:0/totalMagicDmgDealt:17935/totalMinionKills:59/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:11803/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Evelynn/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:10/largestCrit:0/largestKillingSpree:0/maxDeaths:9/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:10/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:20/totalKills:21/totalDmgDealt:337655/totalDmgTaken:89876/doubleKills:2/totalFirstBlood:0/totalGold:30482/totalHeal:0/totalMagicDmgDealt:188420/totalMinionKills:90/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:126450/totalQuadraKills:0/totalSessionsPlayed:3/totalSessionsLost:3/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Lux/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:10/largestCrit:0/largestKillingSpree:0/maxDeaths:11/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:10/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:45/totalKills:21/totalDmgDealt:263609/totalDmgTaken:77672/doubleKills:2/totalFirstBlood:0/totalGold:48930/totalHeal:0/totalMagicDmgDealt:224859/totalMinionKills:398/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:35469/totalQuadraKills:0/totalSessionsPlayed:4/totalSessionsLost:3/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:0/cmp:/champ:Jinx/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:17/largestCrit:0/largestKillingSpree:0/maxDeaths:12/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:17/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:33/totalKills:26/totalDmgDealt:539471/totalDmgTaken:68173/doubleKills:4/totalFirstBlood:0/totalGold:43235/totalHeal:0/totalMagicDmgDealt:2857/totalMinionKills:719/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:532657/totalQuadraKills:0/totalSessionsPlayed:3/totalSessionsLost:2/totalSessionsWon:1/totalTripleKills:0/totalTurretKills:5/cmp:/champ:Rammus/avgAssists:0/avgKills:0/avgPlayerScore:0/avgDeaths:0/avgTeamObj:0/botGamesPlayed:0/killingSpree:0/maxAssists:0/maxKills:0/largestCrit:0/largestKillingSpree:0/maxDeaths:8/maxTeamObj:0/maxTime:0/maxTimeLive:0/mostChampKillSession:0/mostSpellsCast:0/normalGames:0/rankedPremade:0/rankedSolo:0/totalAssists:8/totalKills:0/totalDmgDealt:107683/totalDmgTaken:37342/doubleKills:0/totalFirstBlood:0/totalGold:8801/totalHeal:0/totalMagicDmgDealt:60741/totalMinionKills:39/totalNeutralKills:0/totalPentaKills:0/totalPhysicalDmgDealt:39784/totalQuadraKills:0/totalSessionsPlayed:1/totalSessionsLost:1/totalSessionsWon:0/totalTripleKills:0/totalTurretKills:0";
                    responses.add(debug_response);
               } else{
                   this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                   //////////very important for net
                   this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                   this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                   out.writeUTF(input);
                   out.flush();

                   String rsp = in.readUTF();

                   publishProgress(50);

                   responses.add(rsp);

                   in.close();
                   out.close();
                   s.close();
               }
            } catch(ConnectException e){
                e.printStackTrace();
                errorMessage = e.getMessage();
                return -2;
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
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
                championStatResponse = responses.get(0);
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
}

