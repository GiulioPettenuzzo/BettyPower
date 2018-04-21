package com.bettypower.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bettypower.util.ImageHelper;
import com.bettypower.util.urlManagmentForLogos.ImageUrlUnpacker;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 19/02/18.
 * Load logo given the team's name
 */

public class LoadLogoThread extends Thread {

    private String teamName;
    private LoadLogoListener loadLogoListener;
    private Context context;

    private static final String IMAGE_URL = "https://www.bettypower.it/team_logos/all_logos.html";
    private static final String INITIAL_YAHOO_URL = "https://it.images.search.yahoo.com/search/images;_ylt=A2KLn0VkFotaOFIAZgQdDQx.?p=";
    private static final String FINAL_YAHOO_URL = "soccer+logo&fr=yfp-t-909&fr2=sb-top-it.images.search.yahoo.com&ei=UTF-8&n=60&x=wrt&imgty=clipart";
    private String url;

    private RequestQueue imageQueue;
    private RequestQueue yahooQueue;



    public LoadLogoThread(String teamname, LoadLogoListener loadLogoListener, Context context){
        this.teamName = teamname;
        this.loadLogoListener = loadLogoListener;
        this.context = context;
        StringTokenizer token = new StringTokenizer(teamname);
        while(token.hasMoreTokens()) {
            String word = token.nextToken();
            url = INITIAL_YAHOO_URL+"+"+word;
        }
        url = url+"+"+FINAL_YAHOO_URL;
    }

    @Override
    public void run() {
        super.run();

        if (imageQueue == null) {
            imageQueue = Volley.newRequestQueue(context);
        }
        StringRequest imageRequest = new StringRequest(Request.Method.GET, IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Map<String,String> serverLogoMap = getAllServerUrl(response);
                        if(serverLogoMap.containsKey(teamName)){
                            new DownloadImageTask()
                                    .execute(serverLogoMap.get(teamName));
                        }else{
                            loadLogoFromYahoo();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errore", error.toString());
            }
        });
        imageRequest.setShouldCache(false);
        imageQueue.add(imageRequest);
    }

    /**
     * inner class that get the image from url string
     */
    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        //ImageView bmImage;

        private DownloadImageTask(){}

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            ImageHelper imageHelper = new ImageHelper(context);
            imageHelper.
                    setFileName(teamName + ".png").
                    setDirectoryName("logo_images").setExternal(false).
                    save(result);
            loadLogoListener.onLoadLogoFinish(result);
        }
    }

    private void loadLogoFromYahoo(){
        final ImageUrlUnpacker imageUrlUnpacker = new ImageUrlUnpacker();

        if (yahooQueue == null) {
            yahooQueue = Volley.newRequestQueue(context);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //saving response for pircing
                        imageUrlUnpacker.setResponce(response);
                        String image_url = imageUrlUnpacker.getMyString();
                        new DownloadImageTask()
                                .execute(image_url);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        yahooQueue.add(stringRequest);
    }

    private Map<String,String> getAllServerUrl(String response){
        Map<String,String> result = new HashMap<>();
        StringTokenizer token = new StringTokenizer(response);
        while (token.hasMoreTokens()){
            String word = token.nextToken();
            String url;
            StringBuilder teamName;
            if (word.equals("<img")){
                word = token.nextToken();
                if(word.startsWith("src=\"")){
                    url = word.substring(5,word.length()-1);
                    word = token.nextToken();
                    if(word.startsWith("alt=\"")){
                        if(word.endsWith("\">")){
                            teamName = new StringBuilder(word.substring(5, word.length() - 2));
                        }else {
                            teamName = new StringBuilder(word.substring(5));
                            while (!word.endsWith("\">")) {
                                word = token.nextToken();
                                if (word.endsWith("\">")) {
                                    teamName.append(" ").append(word.substring(0, word.length() - 2));
                                }else {
                                    teamName.append(" ").append(word);
                                }
                            }
                        }
                        result.put(teamName.toString().toUpperCase(), url);

                    }
                }
            }
        }
        return result;
    }

    public interface LoadLogoListener{
        void onLoadLogoFinish(Bitmap bitmap);
    }
}
