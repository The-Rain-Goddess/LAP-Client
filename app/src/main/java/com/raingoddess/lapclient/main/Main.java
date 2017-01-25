package com.raingoddess.lapclient.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.EditText;

import com.raingoddess.lapclient.R;

/**
 * Created by Black Lotus on 7/1/2016.
 */

public class Main extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.raingoddess.lapclient.MESSAGE";
    private final static String SERVER_IP = "71.94.133.203";
    private Toolbar toolbar;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.rain);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public static String getServerIp(){ return SERVER_IP; }

    public void sendInput(View view){


        Intent intent = new Intent(this, SendInputToHost.class);
        EditText inputText = (EditText) findViewById(R.id.inputID);
        String input = inputText.getText().toString();
        input.toLowerCase().replaceAll(" ", "");
        intent.putExtra(EXTRA_MESSAGE, input);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
