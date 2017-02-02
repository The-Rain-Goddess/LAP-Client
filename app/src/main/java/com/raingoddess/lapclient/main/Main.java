package com.raingoddess.lapclient.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Black Lotus on 7/1/2016.
 */

public class Main extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.raingoddess.lapclient.MESSAGE";
    private final static String SERVER_IP = "71.94.133.203";
    private Toolbar toolbar;
    private SharedPreferences mPreferences;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForUserFavorites();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setLogo(R.drawable.rain);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void checkForUserFavorites(){
        mPreferences = getSharedPreferences("User", MODE_PRIVATE);
        List<String> listOfUserFavorites = getUserFavorites();
        setFavoritesToShow(listOfUserFavorites);
    }

    private void setFavoritesToShow(List<String> favorites){
        for(int i = favorites.size()-1, j = 0; i>=0 && j<3; i--, j++){
            setFavorite(favorites.get(i), j);
        }
    }

    private void setFavorite(String summoner, int n){
        final TextView favoriteSummoner = (TextView) findViewById(getStringIdentifier(this.getApplicationContext(), "favorite" + n, "id"));
        if(favoriteSummoner!=null){
            favoriteSummoner.setText(summoner);
            Drawable favoriteIcon = getResources().getDrawable(R.drawable.favorite_icon);
            Drawable background = getResources().getDrawable(R.drawable.back);
            favoriteSummoner.setCompoundDrawables(favoriteIcon, null, null, null);
            favoriteSummoner.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, null, null, null);
            favoriteSummoner.setBackground(background);
            favoriteSummoner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SendInputToHost.class);
                    String summoner_name = favoriteSummoner.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, summoner_name);
                    startActivity(intent);
                }
            });
        }
    }

    private int getStringIdentifier(Context context, String in, String con){
        return context.getResources().getIdentifier(in, con, context.getPackageName());
    }

    private List<String> getUserFavorites(){
        Map<String, ?> userFavoriteSummoners = mPreferences.getAll();
        List<String> listOfUserFavorites = new ArrayList<>(10);
        for(Map.Entry<String, ?> entry : userFavoriteSummoners.entrySet()){
            listOfUserFavorites.add(entry.getValue().toString());
        }
        return listOfUserFavorites;
    }

    public static String getServerIp(){ return SERVER_IP; }

    public void sendInput(View view){
        EditText inputText = (EditText) findViewById(R.id.inputID);
        String input = inputText.getText().toString();

        if(!input.equals("")){
            Intent intent = new Intent(this, SendInputToHost.class);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(input, input);
            editor.apply();
            intent.putExtra(EXTRA_MESSAGE, input);
            startActivity(intent);
        } else{
            /*Dialog inputErrorDialog = new Dialog(view.getContext());
            TextView errorMessage = new TextView(inputErrorDialog.getContext());
            String message = "Invalid Summoner Name!\nPlease retry:";
            errorMessage.setText(message);
            RelativeLayout.LayoutParams messageLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            messageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            inputErrorDialog.setContentView(errorMessage, messageLayoutParams);
            inputErrorDialog.show();
            inputText.setText(""); */
            Dialog inputErrorDialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Invalid Summoner Name!")
                    .setMessage("Please Re-enter Summoner Name!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            inputErrorDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
