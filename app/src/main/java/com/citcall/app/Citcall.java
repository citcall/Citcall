package com.citcall.app;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

/**
 * Created by Agustya on 9/19/16.
 */
public class Citcall extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).setEncryptionMethod(HawkBuilder.EncryptionMethod.HIGHEST).
                setStorage(HawkBuilder.newSharedPrefStorage(this)).setLogLevel(LogLevel.FULL).setPassword("Citcall123").build();
    }
}
