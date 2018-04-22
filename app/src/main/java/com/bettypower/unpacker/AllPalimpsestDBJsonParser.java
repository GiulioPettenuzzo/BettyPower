package com.bettypower.unpacker;

import android.os.AsyncTask;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.util.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllPalimpsestDBJsonParser extends AsyncTask<String,String,ArrayList<PalimpsestMatch>> {

        private JSONParser jParser = new JSONParser();
        private static final String url_all_products = "https://www.bettypower.it/provider/all_result.php";

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         * */
        protected ArrayList<PalimpsestMatch> doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            // getting JSON string from URL
            JSONArray json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON reponse

            ArrayList<PalimpsestMatch> allPalimpsestMatch = new ArrayList<>();
            try {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonobject = json.getJSONObject(i);
                    String palimpsest = jsonobject.getString("palimpsest").trim();
                    String homeTeam = jsonobject.getString("home_team").trim();
                    String awayTeam = jsonobject.getString("away_team").trim();
                    String data = jsonobject.getString("data").trim();
                    String ora = jsonobject.getString("ora").trim();
                    String homeResult = jsonobject.getString("home_result").trim();
                    String awayResult = jsonobject.getString("away_result").trim();
                    String resultTime = jsonobject.getString("result_time").trim();
                    PalimpsestMatch currentPalimpsestMatch = new ParcelablePalimpsestMatch(new ParcelableTeam(homeTeam),new ParcelableTeam(awayTeam),palimpsest,data + " " + ora);
                    if(homeResult.equalsIgnoreCase("null"))
                        currentPalimpsestMatch.setHomeResult("-");
                    else{
                        currentPalimpsestMatch.setHomeResult(homeResult);
                    }
                    if(awayResult.equalsIgnoreCase("null"))
                        currentPalimpsestMatch.setAwayResult("-");
                    else{
                        currentPalimpsestMatch.setAwayResult(awayResult);
                    }
                    if(!resultTime.equalsIgnoreCase("null")) {
                        if(resultTime.equalsIgnoreCase("ht")){
                            resultTime = "Intervallo";
                        }
                        if(resultTime.equalsIgnoreCase("ft")){
                            resultTime = "Terminata";
                        }
                        if(resultTime.equalsIgnoreCase("postp.")){
                            resultTime = "Posticipata";
                        }
                        if(resultTime.equalsIgnoreCase("pen.")){
                            resultTime = "Rigori";
                        }
                        currentPalimpsestMatch.setResultTime(resultTime);
                    }
                    try {
                        String jsonHiddenResult = jsonobject.getString("hidden_result");
                        JSONArray jsonArray = new JSONArray(jsonHiddenResult);
                        ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                            int actionTeam = jsonObject.getInt("action_team");
                            String player = jsonObject.getString("player").trim();
                            String actionTime = jsonObject.getString("action_time").trim();
                            String action = jsonObject.getString("action").trim();
                            String result = jsonObject.getString("result").trim();
                            HiddenResult hiddenResult = new ParcelableHiddenResult(player, actionTime, action, actionTeam);
                            if (action.equalsIgnoreCase("Goal")) {
                                hiddenResult.setResult(result);
                            }
                            allHiddenResult.add(hiddenResult);
                        }
                        currentPalimpsestMatch.setAllHiddenResult(allHiddenResult);
                    }catch(JSONException e){
                        currentPalimpsestMatch.setAllHiddenResult(new ArrayList<HiddenResult>());
                    }
                    allPalimpsestMatch.add(currentPalimpsestMatch);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return allPalimpsestMatch;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute() {
            // dismiss the dialog after getting all products

        }


}
