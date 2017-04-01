package com.raingoddess.lapclient.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
public class TabAnalysis extends Fragment implements View.OnClickListener{
    public final static String EXTRA_MESSAGE = "com.raingoddess.lapclient.MESSAGE";
    protected ImageView temp_image;
    protected TextView temp_view;
    protected ImageButton temp_button;
    protected Bitmap temp_bMap;
    protected Bitmap swap_bMap;

    private static List<RankedChampionStat> temp_list;

    public int num = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tab_analysis, container, false);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.analysis1);
        temp_list = new ArrayList<>();
        temp_list.clear();

    //takes the ranked champ stats from SendInputToHost and processes them
        temp_list = SendInputToHost.getChampionStatResponse();

    //setup first page
        if(temp_list.size()!=0){
            setupChampPool(v);
        } else{
            TextView errorText = new TextView(getContext());
            String errorMsg = "No Ranked Games Played This Season!";
            errorText.setText(errorMsg);
            errorText.setTextColor(getResources().getColor(R.color.black));
            layout.addView(errorText, 0);
        }
        return v;
    }

    public static List<RankedChampionStat> getTempList(){ return temp_list; }

    @Override
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), ViewChampionStat.class);

        intent.putExtra(EXTRA_MESSAGE, view.getContentDescription());
        startActivity(intent);
    }

    private boolean setupChampionPortals(int id, int offset, int text, View root){
        int height = (int)getResources().getDimension(R.dimen.image_height2);
        int width = height;
        temp_button = (ImageButton) root.findViewById(getStringIdentifier(getContext(), "i" + id, "id"));
        //System.out.println("Champ " + (temp_list.size()-offset) + " " + temp_list.get(temp_list.size()-offset));
        String name = temp_list.get(temp_list.size()-offset).getStatAtIndex(1).replace("champ:","").toLowerCase().replace("'","").replace(" ","");
        temp_button.setImageResource(                               getStringIdentifier(getContext(), name, "drawable"));
        temp_bMap = BitmapFactory.decodeResource(getResources(),    getStringIdentifier(getContext(), name, "drawable"));
        swap_bMap = Bitmap.createScaledBitmap(temp_bMap, width, height, true);
        temp_button.getLayoutParams().height =(int) getResources().getDimension(R.dimen.image_height2);
        temp_button.getLayoutParams().width =(int) getResources().getDimension(R.dimen.image_height2);
        temp_button.setImageBitmap(swap_bMap);
        temp_button.setContentDescription("" + (temp_list.size()-offset));
        temp_button.setOnClickListener(this);

        temp_view = (TextView) root.findViewById(getStringIdentifier(getContext(), "t" + text, "id"));
        temp_view.setText(temp_list.get(temp_list.size()-offset).getStat("champ"));

        setupChampTextFields(id, offset, text, root);

        return true;
    }

    private void setupChampTextFields(int id, int offset, int text, View root){
        setupChampGamesPlayed(id, offset, root);
        setupChampWinrate(id, offset, root);
    }

    private void setupChampGamesPlayed(int id, int offset, View root){
        String gamesPlayed = temp_list.get(temp_list.size()-offset).getStat("totalSessionsPlayed")+"G";
        TextView gp = (TextView) root.findViewById(getStringIdentifier(getContext(), "tab_analysis_games_" + id, "id"));
        gp.setText(gamesPlayed);
    }

    private void setupChampWinrate(int id, int offset, View root){
        String winrate = String.format(Locale.US, "%.1f%%W", getWinRate(offset));
        TextView wr = (TextView) root.findViewById(getStringIdentifier(getContext(), "tab_analysis_winrate_" + id,"id"));
        wr.setText(winrate);
        wr.setBackgroundColor(getWinRateColor(getWinRate(offset)));
    }

    private double getWinRate(int offset){
        int gamesPlayed = Integer.parseInt(temp_list.get(temp_list.size()-offset).getStat("totalSessionsPlayed"));
        int gamesWon = Integer.parseInt(temp_list.get(temp_list.size()-offset).getStat("totalSessionsWon"));
        return (((double)gamesWon / (double)gamesPlayed) * 100.0);
    }

    private int getWinRateColor(double winrate){
        if(winrate>80)
            return getResources().getColor(R.color.goldkda);
        else if(winrate>60)
            return getResources().getColor(R.color.bluekda);
        else if(winrate>=50)
            return getResources().getColor(R.color.good_green);
        else if(winrate>40)
            return getResources().getColor(R.color.okay_orange);
        else return getResources().getColor(R.color.regular_red);
    }

    private int getKdaColor(double kdaNumber){
        if(kdaNumber!=Double.POSITIVE_INFINITY){
            if (kdaNumber < 1) return getResources().getColor(R.color.regular_red);
            else if (1 <= kdaNumber && kdaNumber <= 2.5) return getResources().getColor(R.color.okay_orange);
            else if (2.5 < kdaNumber && kdaNumber < 4.0) return getResources().getColor(R.color.good_green);
            else if (4.0 <= kdaNumber && kdaNumber < 5.0) return getResources().getColor(R.color.bluekda);
            else if (5.0 <= kdaNumber ) return getResources().getColor(R.color.goldkda);
        } else{
            return getResources().getColor(R.color.goldkda);
        }
        return 0;
    }

    private double getKda(int offset){
        int kills = Integer.parseInt(temp_list.get(temp_list.size()-offset).getStat("totalKills"));
        int assists = Integer.parseInt(temp_list.get(temp_list.size()-offset).getStat("totalAssists"));
        int deaths = Integer.parseInt(temp_list.get(temp_list.size()-offset).getStat("totalDeaths"));
        if(deaths!=0)
            return (((double)kills + (double)assists)/(double)deaths);
        else return Double.POSITIVE_INFINITY;

    }

    private boolean setupChampPool(View v){
        //champ buttons

        if(temp_list.size() >= 10) {
            for (int i = 1, j = 2, k = 3; j < 11; j++, i++, k++) {
                setupChampionPortals(i, j, k, v);
            }
        } else{
            for(int i = 1, j = 2, k = 3; j < temp_list.size(); j++, i++, k++){
                setupChampionPortals(i,j,k,v);
            }
        } return true;
    }

    private void sort(){
        temp_list = SendInputToHost.ranked_summoner_stats;

        //System.err.println("length " + temp_length);
        //System.err.println("list: " + temp_list);
        try{
            quickSort(0, temp_list.size()-1);
        } catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            for(int i = 0; i<temp_list.size(); i++){
                if(temp_list.get(i).getStats().size() < 35){
                    temp_list.remove(i);
                }
            } sort();
        }
    }

    private void quickSort(int low, int high){
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
                exchangePos(i,j); //move index to next position on both sides
                i++;
                j--;
            }
        }
        //call quickSort Recursively
        if(low<j)
            quickSort(low,j);
        if(i<high)
            quickSort(i, high);
    }

    private void exchangePos(int i, int j){
        RankedChampionStat temp = temp_list.get(i);
        temp_list.add(i, temp_list.get(j));
        temp_list.remove(i+1);

        temp_list.add(j, temp);
        temp_list.remove(j+1);
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }

    protected boolean parseOutputString(String output){
        if((!output.contains("Exception"))) {
            SendInputToHost.ranked_summoner_stats.clear();
            String[] temp_storage = output.split("/cmp:");
            RankedChampionStat temp_stat;
            for (int i = 0; i < temp_storage.length; i++) {
                temp_stat = new RankedChampionStat(temp_storage[i]);
                SendInputToHost.ranked_summoner_stats.add(temp_stat);
                //System.out.println(temp_stat.toString());
            } //SendInputToHost.ranked_summoner_stats.remove(SendInputToHost.ranked_summoner_stats.size()-1);
            //SendInputToHost.ranked_summoner_stats.remove(0);
            sort();
            //System.out.println("Sorted:\n");
            for(int i = 0; i< temp_list.size(); i++){
                //System.out.println(temp_list.get(i));
            }
        } else {
            return false;
        }   return true;
    }

    private class ChampionStatRetrieveTask extends AsyncTask<String, Integer, String> { //do in Background thread
        protected DataInputStream in;
        protected DataOutputStream out;
        protected Socket s;

        private String input;
        private ArrayList<String> responses;
        private Context context;
        private ProgressDialog mProgressDialog;

        public ChampionStatRetrieveTask(String input) { //, Context context
            try {
                //this.context = context;
                //dialog = new ProgressDialog(context);
                this.input = input;
                responses = new ArrayList<String>(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
            //dialog.setMessage("Working:... ");
            //dialog.show();
            //getWindowManager().addView(pB, windowParam);
            //mProgressDialog = ProgressDialog.show(SendInputToHost.this, "Retrieving Data...", "Please Wait", true);
        }

        protected String doInBackground(String... strings) {
            String temp_string = "";
            try {
                this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                //////////very important for net
                this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                out.writeUTF(input);
                out.flush();

                String rsp = in.readUTF();

                //System.out.println("RSP: " + rsp);
                responses.add(rsp);

                in.close();
                out.close();
                s.close();

                temp_string =  responses.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                //return temp_string;
            }
             return temp_string;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            //System.out.println(result);
            //championStatResponce = result;

        }

        protected boolean parseOutputString(String output){
            if((!output.contains("Exception"))) {
                String[] temp_storage = output.split("/cmp:");
                RankedChampionStat temp_stat;
                for (int i = 0; i < temp_storage.length; i++) {
                    temp_stat = new RankedChampionStat(temp_storage[i]);
                    SendInputToHost.ranked_summoner_stats.add(temp_stat);
                    //System.out.println(temp_stat.toString());
                } SendInputToHost.ranked_summoner_stats.remove(0);
                //sort();
            } else {
                return false;
            }   return true;
        }
    }
}
