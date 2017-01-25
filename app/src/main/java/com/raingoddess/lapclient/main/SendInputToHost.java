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
    private boolean matchDataCollected = false, champDataCollected = false;
    private ProgressBar pB;
    private Toolbar toolbar;
    private WindowManager.LayoutParams windowParam;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Match History", "Analysis"};
    int NumOfTabs = 2;
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

//creates the background task for retrieving match data
        DataRetrieveTask drt = new DataRetrieveTask(summoner_name + "::get_match_history::8::0::8"); //second part of string hardcoded
        drt.execute();
//creates the background task for retrieving ranked champ stats
        ChampionStatRetrieveTask csrt = new ChampionStatRetrieveTask(summoner_name + "::get_analysis::0::0::0");
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
                        System.out.println("RSP: " + rsp);
                        responses.add(rsp);
                        publishProgress((int)Math.ceil(75/responseCode));
                    } in.close(); out.close(); s.close();
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
                this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                //////////very important for net
                this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                out.writeUTF(input);
                out.flush();

                String rsp = in.readUTF();

                publishProgress(25);

                responses.add(rsp);

                in.close();
                out.close();
                s.close();
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

        protected boolean parseOutputString(String output){
            if((!output.contains("Exception"))) {
                String[] temp_storage = output.split("/cmp:");
                RankedChampionStat temp_stat;
                for (int i = 0; i < temp_storage.length; i++) {
                    temp_stat = new RankedChampionStat(temp_storage[i]);
                    SendInputToHost.ranked_summoner_stats.add(temp_stat);
                    System.out.println(temp_stat.toString());
                } SendInputToHost.ranked_summoner_stats.remove(0);
                //sort();
            } else {
                return false;
            }   return true;
        }
    }

    private class RetrieveTaskListener implements Runnable{
        private boolean keepRunning = true;
        private long startTime = System.currentTimeMillis();

        @Override
        public void run(){
            while(keepRunning){
                if((System.currentTimeMillis()-startTime)%1000==0)
                    System.out.println(System.currentTimeMillis()-startTime);

                if(champDataCollected && matchDataCollected){
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
                    keepRunning = false;
                }
            }
        }
    }
}

