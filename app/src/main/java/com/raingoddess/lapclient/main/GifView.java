package com.raingoddess.lapclient.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.view.View;

import com.raingoddess.lapclient.R;

import java.io.InputStream;

/**
 * Created by Black Lotus on 8/3/2016.
 */
public class GifView extends View {
    Movie movie, movie1;
    InputStream is = null, is1 = null;
    long movieStart;

//public constructor
    public GifView(Context context){
        super(context);
        is = context.getResources().openRawResource( + R.drawable.rain_gif2);
        movie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.WHITE);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        System.out.println("now=" + now);
        if (movieStart == 0) { // first time
            movieStart = now;
        }

        int relTime = (int) ((now - movieStart) % movie.duration());
        movie.setTime(relTime);
        movie.draw(canvas, this.getWidth() / 2 - 20, this.getHeight() / 2 - 40);
        this.invalidate();
    }
}
