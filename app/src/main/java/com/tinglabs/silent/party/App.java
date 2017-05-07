package com.tinglabs.silent.party;

import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * Created by Talal on 1/5/2017.
 */
public class App extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }
}