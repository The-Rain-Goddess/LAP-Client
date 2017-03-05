package com.raingoddess.lapclient.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raingoddess.lapclient.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
        setupPageElements();
    }

    private void setupPageElements(){
        setupBackgroundImage();
        checkForUserFavorites();
        setupToolBar();

        //needed for older Android who dont have automatic Input prompts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void setupToolBar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setLogo(R.drawable.rain);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    private void setupBackgroundImage(){
        RelativeLayout mainScreen = (RelativeLayout) findViewById(R.id.main_container);
        mainScreen.setBackgroundResource(R.mipmap.main_back);
    }

    private void checkForUserFavorites(){
        mPreferences = getSharedPreferences("User", MODE_PRIVATE);
        List<String> listOfUserFavorites = getUserFavorites();
        Collections.sort(listOfUserFavorites, Collator.getInstance(Locale.US));
        setFavoritesToShow(listOfUserFavorites);
    }

    private List<String> getUserFavorites(){
        Map<String, ?> userFavoriteSummoners = mPreferences.getAll();
        List<String> listOfUserFavorites = new ArrayList<>(10);

        for(Map.Entry<String, ?> entry : userFavoriteSummoners.entrySet()){
            listOfUserFavorites.add(entry.getValue() +":"+ entry.getKey());
        } return listOfUserFavorites;
    }

    private void setFavoritesToShow(List<String> favorites){
        for(int i = favorites.size()-1, numberOfFavorites = 0; i>=0 && numberOfFavorites<3; i--, numberOfFavorites++){
            setFavorite(favorites.get(i), numberOfFavorites);
            setRemoveFavoriteIcon(favorites.get(i), numberOfFavorites);
        }
    }

    private void setFavorite(String summoner, int n){
        final TextView favoriteSummoner = (TextView) findViewById(getStringIdentifier(this.getApplicationContext(), "favorite" + n, "id"));
        if(favoriteSummoner!=null){
            String splitSummoner[] = summoner.split(":");
            favoriteSummoner.setText(splitSummoner[1]); //removes the sorting character at index 0.
            Drawable favoriteIcon = getResources().getDrawable(R.drawable.favorite_icon);

            favoriteSummoner.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, null, null, null);
            favoriteSummoner.setBackground(getBackgroundDrawable(R.drawable.back));
            favoriteSummoner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SendInputToHost.class);
                    String summoner_name = favoriteSummoner.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, summoner_name);
                    startActivity(intent);
                }
            });
            favoriteSummoner.setVisibility(View.VISIBLE);
        }
    }

    private Drawable getBackgroundDrawable(int resourceId){
        return getResources().getDrawable(resourceId);
    }

    private void setRemoveFavoriteIcon(final String summoner, int n){
        final TextView favoriteSummoner = (TextView) findViewById(getStringIdentifier(this.getApplicationContext(), "favorite" + n, "id"));
        final ImageButton removeFavorite = new ImageButton(getApplicationContext());
        final String splitSummoner[] = summoner.split(":");

        removeFavorite.setImageDrawable(getRescaledDrawable(android.R.drawable.ic_delete));
        removeFavorite.setBackground(getBackgroundDrawable(R.drawable.back));
        removeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove(splitSummoner[1]);
                editor.apply();
                System.out.println("Remove button Pressed.");
                favoriteSummoner.setVisibility(View.GONE);
                removeFavorite.setVisibility(View.GONE);
            }
        });
        getMainContainer().addView(removeFavorite, getRelativeLayoutParams(n));
    }

    private Drawable getRescaledDrawable(int resourceId){
        Drawable removeFavoriteIcon = getResources().getDrawable(resourceId);
        Bitmap removeIconBitmap = ((BitmapDrawable) removeFavoriteIcon).getBitmap();
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(removeIconBitmap, 50,50, true));
    }

    private RelativeLayout.LayoutParams getRelativeLayoutParams(int n){
        RelativeLayout.LayoutParams rfParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rfParams.addRule(RelativeLayout.END_OF, getStringIdentifier(getApplicationContext(), "favorite" + n, "id"));
        rfParams.addRule(RelativeLayout.RIGHT_OF,  getStringIdentifier(getApplicationContext(), "favorite" + n, "id"));
        rfParams.addRule(RelativeLayout.ALIGN_TOP, getStringIdentifier(getApplicationContext(), "favorite" + n, "id"));
        rfParams.addRule(RelativeLayout.ALIGN_BOTTOM, getStringIdentifier(getApplicationContext(), "favorite" + n, "id"));
        return rfParams;
    }

    private RelativeLayout getMainContainer(){
        return (RelativeLayout) findViewById(R.id.main_container);
    }

    private int getStringIdentifier(Context context, String resourceName, String resourceContext){
        return context.getResources().getIdentifier(resourceName, resourceContext, context.getPackageName());
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return ( o1.getValue() ).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    public static String getServerIp(){ return SERVER_IP; }

    public void sendInput(View view){
        EditText inputText = (EditText) findViewById(R.id.inputID);
        String userName = inputText.getText().toString().trim();
        userName = userName.replaceAll("([@#$%^&*|])", "@");
        if(userName.equals(" ")){
            showUserInputErrorDialog(view);
        } else if((userName.matches("([^A-Za-z\\s\\d])") || userName.contains("@"))){ //^.*?(@|#|$|%|^|&|!).*$
            showUserInputErrorDialog(view);
        } else{
            addUserToFavorites(userName);
            startActivity(setupIntent(userName));
        }

    }

    private void addUserToFavorites(String userName){
        SharedPreferences.Editor editor = mPreferences.edit();
        if(mPreferences.contains(userName)){
            int timeEntered = mPreferences.getInt(userName, 0);
            editor.putInt(userName, ++timeEntered);
            editor.apply();
        } else{
            editor.putInt(userName, 1);
            editor.apply();
        }
    }

    private Intent setupIntent(String userName){
        Intent intent = new Intent(this, SendInputToHost.class);
        intent.putExtra(EXTRA_MESSAGE, userName);
        return intent;
    }

    private void showUserInputErrorDialog(View view){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
