package com.bettypower.threads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.bettypower.adapters.SingleBetAdapter;
import com.bettypower.entities.Match;
import com.bettypower.unpacker.LogoUnpacker;
import com.bettypower.adapters.SingleBetAdapter;
import com.bettypower.entities.Match;
import com.bettypower.unpacker.LogoUnpacker;
import com.bettypower.util.ImageHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by giuliopettenuzzo on 23/07/17.
 */

public class LoadImageThread extends Thread {

    Context context;
    String response;
    ArrayList<Match> allMatch;
    ArrayList<String> allLogosURL;
    LoadLogoListener loadLogoListener;

    public LoadImageThread(String response, ArrayList<Match> allMatch,ArrayList<String> allLogosURL,Context context){
        this.response = response;
        this.allMatch = allMatch;
        this.allLogosURL = allLogosURL;
        this.context = context;
    }

    public void setOnLoadLogoListener(LoadLogoListener loadLogoListener){
        this.loadLogoListener = loadLogoListener;
    }

    @Override
    public void run() {
        super.run();
        LogoUnpacker logoUnpacker = new LogoUnpacker(response);
        allLogosURL = logoUnpacker.getAllLogos();
        for (final Match match:allMatch) {
            Bitmap homeBitmap = null;
            Bitmap awayBitmap = null;
            //TODO quando avrai inserito tutti i loghi la parte qui sotto li associerà a ciascuna squadra se ci sono altrimenti restano null
            /**
             for (String logos:allLogosURL
             ) {
             if(logos.contains(match.getHomeTeam().getName())){
             try {
             homeBitmap = new DownloadImageTask()
             .execute("http://fishtagram.it/football_logos/barcellona.png").get();
             match.getHomeTeam().setBitmapLogo(homeBitmap);
             runOnUiThread(new Runnable() {
            @Override public void run() {
            // singleBetAdapter.notifyDataSetChanged();
            singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(match));
            Log.i("item",String.valueOf(allMatch.lastIndexOf(match)));
            }
            });
             } catch (InterruptedException e) {
             e.printStackTrace();
             } catch (ExecutionException e) {
             e.printStackTrace();
             }

             }
             if(logos.contains(match.getAwayTeam().getName())){
             try {
             awayBitmap = new DownloadImageTask()
             .execute("http://fishtagram.it/football_logos/realmadrid.png").get();
             match.getAwayTeam().setBitmapLogo(awayBitmap);
             runOnUiThread(new Runnable() {
            @Override public void run() {
            // singleBetAdapter.notifyDataSetChanged();
            singleBetAdapter.notifyItemChanged(allMatch.lastIndexOf(match));
            Log.i("item",String.valueOf(allMatch.lastIndexOf(match)));
            }
            });
             } catch (InterruptedException e) {
             e.printStackTrace();
             } catch (ExecutionException e) {
             e.printStackTrace();
             }
             }
             }*/

            try {
                ImageHelper imageHelper = new ImageHelper(context);
                homeBitmap = new DownloadImageTask()
                        .execute("http://fishtagram.it/football_logos/barcellona.png").get();
                imageHelper.
                        setFileName(match.getHomeTeam().getName() + ".png").
                        setDirectoryName("logo_images").setExternal(false).
                        save(homeBitmap);

                awayBitmap = new DownloadImageTask()
                        .execute("http://fishtagram.it/football_logos/realmadrid.png").get();
                imageHelper.
                        setFileName(match.getAwayTeam().getName() + ".png").
                        setDirectoryName("logo_images").setExternal(false).
                        save(awayBitmap);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            loadLogoListener.onLoadLogoFinish(match);
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageTask(){
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                mIcon11 = BitmapFactory.decodeStream(in,null,options);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                loadLogoListener.onAllLogosUploaded();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

        }

    }

    public interface LoadLogoListener{
        void onLoadLogoFinish(Match match);
        void onAllLogosUploaded();
    }

}


