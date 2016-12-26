package com.uitstu.party;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by duy tung dao on 12/26/2016.
 */

public class ApplicationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //int i=0;
        /*Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/
    }
}
