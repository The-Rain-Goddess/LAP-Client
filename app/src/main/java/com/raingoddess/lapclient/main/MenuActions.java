package com.raingoddess.lapclient.main;

import android.content.Context;
import android.content.Intent;

import com.raingoddess.lapclient.R;

import static com.raingoddess.lapclient.main.Main.EXTRA_MESSAGE;

/**
 * Created by Rain on 3/11/2017.
 */

public class MenuActions {

    public static void activateMenuItem(int id, Context context){
        if(id == R.id.action_search)
            search(context);
        else if(id == R.id.action_reload)
            reload(context);
        else if(id == R.id.action_feedback)
            sendFeedbackToServer(context);
        else if(id == R.id.action_settings)
            loadSettingsPage(context);

    }

    public static boolean sendFeedbackToServer(Context context){
        Intent feedbackInput = new Intent(context, Feedback.class);
        context.startActivity(feedbackInput);
        return true;
    }

    public static boolean loadSettingsPage(Context context){

        return true;
    }

    public static boolean search(Context context){
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage( context.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        return true;
    }

    public static boolean reload(Context context){
        Intent intent = new Intent(context, SendInputToHost.class);
        intent.putExtra(EXTRA_MESSAGE, Main.getSummonerName());
        context.startActivity(intent);
        return true;
    }
}
