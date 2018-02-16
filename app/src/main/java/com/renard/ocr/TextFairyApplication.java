/*
 * Copyright (C) 2012,2013 Renard Wellnitz.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.renard.ocr;

import com.bettypower.betMatchFinder.Resolver;
import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.renard.ocr.analytics.Analytics;
import com.renard.ocr.analytics.AnalyticsFactory;
import com.renard.ocr.main_menu.language.OcrLanguage;
import com.renard.ocr.main_menu.language.OcrLanguageDataStore;
import com.renard.ocr.util.PreferencesUtils;
import com.renard.ocr.util.ResourceUtils;
import com.squareup.leakcanary.LeakCanary;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class TextFairyApplication extends Application {

    private Analytics mAnalytics;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private  ArrayList<Match> allMatches = new ArrayList<>();
    public boolean isPalimpsestMatchLoaded = false;
    public AllMatchLoadListener allMatchLoadListener;
    public Resolver resolver;

    public void onCreate() {
        super.onCreate();
        trackCrashes();
        createAnalytics();
        initTextPreferences();
        enableStrictMode();
        alwaysShowOverflowButton();
        startLeakCanary();
        checkLanguages();
    }

    private static final String TAG = "TextFairyApplication";


    public void setRealTimeOcrResult(final String realTimeOcrResult){
        resolver = new Resolver(allPalimpsestMatch,this);
        final Thread x = new Thread(new Runnable() {
            @Override
            public void run() {
                resolver.setRealTimeResponse(realTimeOcrResult);
            }
        });
        x.start();
    }

    public void setAllMatchLoadListener(AllMatchLoadListener allMatchLoadListener){
        this.allMatchLoadListener = allMatchLoadListener;
    }

    public ArrayList<PalimpsestMatch> getAllPalimpsestMatch(){
        return allPalimpsestMatch;
    }

    public void setAllPalimpsestMatch(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        this.allPalimpsestMatch = allPalimpsestMatch;
    }


    private void checkLanguages() {
        if (BuildConfig.DEBUG) {
            Map<String, String> hashMapResource = ResourceUtils.getHashMapResource(this, R.xml.iso_639_mapping);
            final List<OcrLanguage> availableOcrLanguages = OcrLanguageDataStore.getAvailableOcrLanguages(this);
            final Iterator<OcrLanguage> iterator = availableOcrLanguages.iterator();
            while (iterator.hasNext()) {
                OcrLanguage language = iterator.next();
                if (hashMapResource.remove(language.getValue()) != null) {
                    iterator.remove();
                }
            }
            if (!availableOcrLanguages.isEmpty()) {
                Log.w(TAG, "Some OCR languages don't have a mapping to iso 636");
                for (OcrLanguage language : availableOcrLanguages) {
                    Log.w(TAG, language.getDisplayText());
                }
            }

            if (!hashMapResource.isEmpty()) {
                Log.w(TAG, "Some iso 636 mappings don't have a corresponding OCR language");
                for (String key : hashMapResource.keySet()) {
                    Log.w(TAG, key);
                }
            }
        }

    }




    private void startLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void initTextPreferences() {
        PreferencesUtils.initPreferencesWithDefaultsIfEmpty(getApplicationContext());
    }

    private void createAnalytics() {
        mAnalytics = AnalyticsFactory.createAnalytics(this);
    }

    private void trackCrashes() {
        if (BuildConfig.FLAVOR.contains("playstore")) {
            final Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics(), new CrashlyticsNdk()).debuggable(BuildConfig.DEBUG).build();
            Fabric.with(fabric);
            TessBaseAPI.initCrashlytics();
        }
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void alwaysShowOverflowButton() {
        // force overflow button for actionbar for devices with hardware option
        // button
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    public Analytics getAnalytics() {
        return mAnalytics;
    }


    public static boolean isRelease() {
        return com.renard.ocr.BuildConfig.FLAVOR.contains("playstore");
    }

    //TODO mettere questo listener in una classe a parte
    public interface AllMatchLoadListener{
        void onMatchLoaded(ArrayList<PalimpsestMatch> allPalimpsestMatch);
    }
}
