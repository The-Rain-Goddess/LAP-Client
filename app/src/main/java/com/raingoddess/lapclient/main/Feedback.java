package com.raingoddess.lapclient.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.raingoddess.lapclient.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rain on 3/11/2017.
 */

public class Feedback extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Intent intent = getIntent();

        setupToolbar();
    }

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    public void sendFeedback(View view){
        List<String> data = Arrays.asList(getUserFeedback(view).split("\n"));
        FeedbackDispatchTask fbct = new FeedbackDispatchTask("theraingoddess::send_feedback::0::0::0", data);
        fbct.execute();
    }

    private String getUserFeedback(View view){
        EditText userTextInput = (EditText) findViewById(R.id.action_feedback_edittext);
        String feed = userTextInput.getText().toString();
        userTextInput.setText("");
        return feed;
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

    //Data Send
    private class FeedbackDispatchTask extends AsyncTask<String, Integer, Integer> {
        protected DataInputStream in;
        protected DataOutputStream out;
        protected Socket s;

        private List<String> data;
        private String errorMessage;
        private String serverCommand;

        public FeedbackDispatchTask(String commandForServer, List<String> data){
            serverCommand = commandForServer;
            this.data = data;
        }

        protected void onPreExecute(){

        }

        protected Integer doInBackground(String... strings){
            try {
                    this.s = new Socket(Main.getServerIp(), 48869); //  "71.94.133.203"
                    //////////very important for net
                    this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                    this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                    out.writeUTF(serverCommand);
                    out.flush();

                    out.writeUTF(data.size()+"");
                    for(int i = 0; i<data.size(); i++){
                        out.writeUTF(data.get(i));
                        out.flush();
                    }

                    in.close();
                    out.close();
                    s.close();
            } catch(ConnectException e){
                e.printStackTrace();
                errorMessage = "Could not connect to server";
                return -2;
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "IOException occurred data dispatch.";
                return -2;
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                errorMessage = e.getMessage();
                return -2;
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress){

        }

        protected void onPostExecute(Integer responseCode){
            if(responseCode==0){
                System.out.println("Feedback Dispatched Successfully");

            } else if(responseCode==-1){ //Server-side Exception

                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } //System.exit(0);
            } else if(responseCode==-2){ //Client-side exception

                try{
                    Thread.sleep(3000L);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } //System.exit(0);
            }
        }
    }
}


