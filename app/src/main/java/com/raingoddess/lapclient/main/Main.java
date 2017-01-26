package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.EditText;
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
        for(int i = 0; i<favorites.size() && i<3; i++){
            setFavorite(favorites.get(i), i);
        }
    }

    private void setFavorite(String summoner, int n){
        final TextView favoriteSummoner = (TextView) findViewById(getStringIdentifier(this.getApplicationContext(), "favorite" + n, "id"));
        if(favoriteSummoner!=null){
            favoriteSummoner.setText(summoner);
            Drawable favoriteIcon = getResources().getDrawable(R.drawable.favorite_icon);
            favoriteSummoner.setCompoundDrawables(favoriteIcon, null, null, null);
            favoriteSummoner.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, null, null, null);
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
        Intent intent = new Intent(this, SendInputToHost.class);
        EditText inputText = (EditText) findViewById(R.id.inputID);
        String input = inputText.getText().toString();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(input, input);
        editor.apply();

        intent.putExtra(EXTRA_MESSAGE, input);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
