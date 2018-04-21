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

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.ViewConfiguration;

import com.bettypower.betMatchFinder.Resolver;
import com.bettypower.entities.Bet;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.SingleBet;
import com.bettypower.entities.Team;
import com.bettypower.entities.deserialized.HiddenResultDeserialized;
import com.bettypower.entities.deserialized.PalimpsestMatchDeserialized;
import com.bettypower.entities.deserialized.TeamDeserialized;
import com.bettypower.unpacker.AllPalimpsestDBJsonParser;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.renard.ocr.analytics.Analytics;
import com.renard.ocr.analytics.AnalyticsFactory;
import com.renard.ocr.main_menu.language.OcrLanguage;
import com.renard.ocr.main_menu.language.OcrLanguageDataStore;
import com.renard.ocr.util.PreferencesUtils;
import com.renard.ocr.util.ResourceUtils;
import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class TextFairyApplication extends Application {

    private Analytics mAnalytics;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    public boolean isPalimpsestMatchLoaded = false;
    public AllMatchLoadListener allMatchLoadListener;
    public Resolver resolver;
    public Uri allPalimpsestURI;

    public void onCreate() {
        super.onCreate();
        trackCrashes();
        createAnalytics();
        initTextPreferences();
        enableStrictMode();
        alwaysShowOverflowButton();
        //startLeakCanary();
        checkLanguages();
    }

    private static final String TAG = "TextFairyApplication";

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

    public interface AllMatchLoadListener{
        void onMatchLoaded(ArrayList<PalimpsestMatch> allPalimpsestMatch);
    }

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

    public ArrayList<PalimpsestMatch> getAllMatchFromMemory(){
        String text = readFromFile(getApplicationContext(),"all_palimpsest.txt");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(HiddenResult.class, new HiddenResultDeserialized());
        gsonBuilder.registerTypeAdapter(Team.class, new TeamDeserialized());
        gsonBuilder.registerTypeAdapter(PalimpsestMatch.class, new PalimpsestMatchDeserialized());
        Gson gsonGetter = gsonBuilder.create();

        Bet bet = gsonGetter.fromJson(text, SingleBet.class);
        if(bet!=null) {
            allPalimpsestMatch = bet.getArrayMatch();
        }

        return allPalimpsestMatch;
    }

    public boolean isLessThanMounthAgo(String date){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Date dateOne = null;
        try {
            dateOne = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date now = cal.getTime();

        String si = "";
        if(dateOne!=null) {
            return !now.after(dateOne);
        }
        else{
            return false;
        }
    }


    public void setPalimpsest(){
        String lastPalimpsestUpdate = readFromFile(getApplicationContext(),"palimpsest_date.txt");
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        String today = df.format(c);
        String lastUpdate = "";
        String lastHour = "";
        if(!lastPalimpsestUpdate.isEmpty()) {
            StringTokenizer token = new StringTokenizer(lastPalimpsestUpdate);
            lastUpdate = token.nextToken();
            lastHour = token.nextToken();
        }
        if(isLessThanMounthAgo(lastUpdate)) {
            Thread loadPalimpsestFromMemory = new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<PalimpsestMatch> allPali = getAllMatchFromMemory();
                    if (allMatchLoadListener != null)
                        allMatchLoadListener.onMatchLoaded(allPali);
                }
            });
            loadPalimpsestFromMemory.start();
        }
        if(!(today.equalsIgnoreCase(lastUpdate) && !isMustDateUpdate(lastHour))){
           if(isInternetConnected(this)) {
               Thread loadPalimpsest = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       AllPalimpsestDBJsonParser allPalimpsestDBJsonParser = new AllPalimpsestDBJsonParser();
                       allPalimpsestDBJsonParser.execute();
                       try {
                           ArrayList<PalimpsestMatch> allPalimpsestMatches = allPalimpsestDBJsonParser.get();
                           setAllPalimpsestMatch(allPalimpsestMatches);
                           if (allMatchLoadListener != null)
                               allMatchLoadListener.onMatchLoaded(allPalimpsestMatches);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       } catch (ExecutionException e) {
                           e.printStackTrace();
                       }
                   }
               });
               loadPalimpsest.start();
           }
        }
    }

    public boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setAllPalimpsestMatch(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        this.allPalimpsestMatch = allPalimpsestMatch;
        Gson gson = new Gson();
        Bet bet = new SingleBet(allPalimpsestMatch);
        String jsonBet = gson.toJson(bet);
        writeToFile(jsonBet,getApplicationContext(),"all_palimpsest.txt");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        String currentDateTimeString = dateFormat.format(new Date());
        writeToFile(currentDateTimeString,getApplicationContext(),"palimpsest_date.txt");
    }

    private void writeToFile(String data,Context context,String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context,String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private boolean isMustDateUpdate(String lastHourUpdate){

        String One = "00:30"; //one is current date
        String Two = "04:00";
        //String lastHourUpdate = "02:59";

        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm",Locale.getDefault());
        java.util.Date dateOne = null;
        java.util.Date dateTwo = null;
        java.util.Date lastUpdateDate = null;

        java.util.Date now = Calendar.getInstance().getTime();
        String nowHour = String.valueOf(now.getHours());
        String nowminute = String.valueOf(now.getMinutes());
        String thisTime = nowHour+":"+nowminute;
        java.util.Date dateNow = null;
        try {
            dateOne = formatter1.parse(One); //ora minore
            dateTwo = formatter1.parse(Two); //or maggiore
            dateNow = formatter1.parse(thisTime); //ora corrente
            lastUpdateDate = formatter1.parse(lastHourUpdate);//ultimo aggiornamento
            String thisHour = String.valueOf(lastUpdateDate.getHours());
            String thisminute = String.valueOf(lastUpdateDate.getMinutes());
            lastHourUpdate = thisHour+":"+thisminute;
            lastUpdateDate = formatter1.parse(lastHourUpdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean resultdate = false;
        if(lastUpdateDate!=null) {
            if (lastUpdateDate.after(dateOne) && dateTwo.after(lastUpdateDate) && dateNow.after(dateTwo)) {
                resultdate = true;
            } else if (lastUpdateDate.after(dateOne) && dateTwo.after(lastUpdateDate) && dateTwo.after(dateNow)) {
                resultdate = false;
            }
        }
        return resultdate;
    }
}
