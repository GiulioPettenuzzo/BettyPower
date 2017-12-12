package com.bettypower.unpacker;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 31/07/17.
 */
//TODO potrebbe sbagliare qualcosa, se lo provi due volte di seguito a volte ne legge due in piu altre due in meno
    //TODO cambiare tutti i comapre to con equals
public class AllMatchesByPalimpsestUnpacker {

    Context context;
    ArrayList<String> allURL = new ArrayList<>();
    ArrayList<PalimpsestMatch> allPalimpsestMatch = new ArrayList();
    private String currentURL;
    private RequestQueue queue;
    ResponseLoaderListener responseLoaderListener;

    private final String beginUrl = "http://sys.betcalcio.it/scommesse.asp?sid={73DF4270-5F69-4975-9D51-B2B890D547B5}&tpg=palinsesto&pal=";

    public AllMatchesByPalimpsestUnpacker(Context context,ArrayList<String> allURL, ResponseLoaderListener responseLoaderListener) {
        this.context = context;
        this.allURL = allURL;
        this.responseLoaderListener = responseLoaderListener;
        this.queue =  Volley.newRequestQueue(context);

        sendSingleVolleyRequest(allURL.get(allURL.size()-1));
    }

        public void sendVolleyRequest(){
            currentURL = "http://www.betcalcio.it/home/home.asp?sid=%7B4E30F9C3-9337-47A3-A5EC-494D83C32471%7D&basket=pagef&title=PALINSESTI+MATCHPOINT&description=Tutti+i+palinsesti+MATCHPOINT&keywords=palinsesti%2CMATCHPOINT";
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, currentURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getCurrentPalimpsestMatch(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("errore", error.toString());
                }
            });
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
    }


    private void sendSingleVolleyRequest(final String currentUrl){
        String completeUrl = beginUrl + currentUrl;
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, completeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("primo volley", currentUrl);
                        getCurrentPalimpsestMatch(response);
                        allURL.remove(currentUrl);
                        if(allURL.isEmpty()){
                            responseLoaderListener.onFinishResponseLoading(allPalimpsestMatch);
                        }else{
                                sendSingleVolleyRequest(allURL.get(allURL.size() - 1));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errore", error.toString());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    public void getCurrentPalimpsestMatch(String response){
        ArrayList<String> allTeams = findTeams(response);
        ArrayList<String> palimpsests = findAllPalimpsest(response);
        ArrayList<String> allTime = findMatchTime(response);
        for (String current:allTeams
                ) {
            Team homeTeam = new ParcelableTeam(findHomeTeam(current));
            Team awayTeam = new ParcelableTeam(findAwayTeam(current));
            String pali = findPalimpsestCode(palimpsests.get(allTeams.lastIndexOf(current)));
            String event = findEventCode(palimpsests.get(allTeams.lastIndexOf(current)));
            String time = allTime.get(allTeams.lastIndexOf(current));
            PalimpsestMatch palimpsestMatch = new ParcelablePalimpsestMatch(homeTeam,awayTeam,pali,event,time);
            allPalimpsestMatch.add(palimpsestMatch);
        }
    }

    public ArrayList<PalimpsestMatch> getAllPalimpsestMatch(){
        return allPalimpsestMatch;
    }

    private ArrayList<String> findTeams(String response){
        //title="SCOMMESSE FINN HARPS FC - GALWAY UNITED">
        ArrayList<String> allTeams = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.endsWith("SCOMMESSE")){
                String team = "";
                while(!word.contains("\">")){
                    word = token.nextToken();
                    team = team + " " + word;
                    if(word.contains("\">")){
                        allTeams.add(team);
                    }
                }
            }
        }
        return allTeams;
    }
    private String findHomeTeam(String singleResponse){
        // FINN HARPS FC - GALWAY UNITED"><strong>FINN
        String homeTeam = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        String word = token.nextToken();
        homeTeam = homeTeam + word;
        while(!word.equals("-")){
            word = token.nextToken();
            if(word.startsWith("(")&&word.endsWith(")")){
                word = "";
            }
            if(!word.equals("-")){
                homeTeam = homeTeam + " " + word;
            }
        }

        return homeTeam;
    }

    private String findAwayTeam(String singleResponse){
        // FINN HARPS FC - GALWAY UNITED"><strong>FINN
        String awayTeam = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.startsWith("(")&&word.endsWith(")")){
                word = "";
            }
            if(word.equals("-")){
                awayTeam = token.nextToken("\">");
            }
        }
        return awayTeam;
    }

    private String findPalimpsestCode(String singleResponse){
        //SANTOS</strong></a></td><td>27301</td><td>254</td><td
        String palimsest = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        while(token.hasMoreTokens()){
            String word = token.nextToken(">");
            String numberOnly= word.replaceAll("[^0-9]", "");
            if(!numberOnly.equals("")){
                palimsest = numberOnly;
                break;
            }

        }
        return palimsest;
    }

    private String findEventCode(String singleResponse){
        //SANTOS</strong></a></td><td>27301</td><td>254</td><td
        String eventCode = "";
        StringTokenizer token = new StringTokenizer(singleResponse);
        while(token.hasMoreTokens()){
            String word = token.nextToken(">");
            String numberOnly= word.replaceAll("[^0-9]", "");
            if(!numberOnly.equals("")){
                eventCode = numberOnly;
            }
        }
        return eventCode;
    }

    private ArrayList<String> findAllPalimpsest(String response){
       // </strong></a></td><td>27331</td><td>1587</td><td nowrap>19/08 15:30</td><td
        ArrayList<String> allPalimpsest = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.contains("</strong></a></td><td>")){
                allPalimpsest.add(word);
            }
        }
        return allPalimpsest;
    }

    private ArrayList<String> findMatchTime(String response){
        //</td><td nowrap>19/08 15:30</td><td
        ArrayList<String> allMatchTime = new ArrayList<>();
        StringTokenizer token = new StringTokenizer(response);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.endsWith("</td><td")){
                word = token.nextToken();
                if(word.startsWith("nowrap>")){
                    String time = "";
                    word = word.substring(7);
                    time = time + word;
                    while(!word.endsWith("</td><td")){
                        word = token.nextToken();
                        if(word.endsWith("</td><td")){
                            String parseWord = word.substring(0,word.length()-8);
                            time = time + " " + parseWord;
                            allMatchTime.add(time);
                        }
                        else{
                            time = time + " " + word;
                            allMatchTime.add(time);
                        }
                    }
                }
            }
        }
        return allMatchTime;
    }

    public interface ResponseLoaderListener{
        /**
         * listener in order to know when an all the palimpsest match are loaded
         */
        void onFinishResponseLoading(ArrayList<PalimpsestMatch> allPalimpsestMatches);
    }

}
