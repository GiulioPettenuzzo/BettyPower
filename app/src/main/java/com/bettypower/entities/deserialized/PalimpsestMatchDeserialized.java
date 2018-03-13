package com.bettypower.entities.deserialized;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.Team;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by giuliopettenuzzo on 12/03/18.
 */

public class PalimpsestMatchDeserialized implements JsonDeserializer<PalimpsestMatch> {
    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public PalimpsestMatch deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = json.getAsJsonObject();
        JsonObject Jteam = jobject.get("homeTeam").getAsJsonObject();
        Team homeTeam = new ParcelableTeam(Jteam.get("name").getAsString());
        JsonObject JAwayteam = jobject.get("awayTeam").getAsJsonObject();
        Team awayTeam = new ParcelableTeam(JAwayteam.get("name").getAsString());
        PalimpsestMatch palimpsestMatch = new ParcelablePalimpsestMatch(homeTeam,awayTeam);

        String time = "";
        if(jobject.get("matchTime")!=null) {
            time = jobject.get("matchTime").getAsString();
            palimpsestMatch.setTime(time);
        }

        String palimpsest = "";
        if(jobject.get("palimpsest")!=null) {
            palimpsest = jobject.get("palimpsest").getAsString();
            palimpsestMatch.setPalimpsest(palimpsest);
        }

        String bet = "";
        if(jobject.get("bet")!=null) {
            bet = jobject.get("bet").getAsString();
            palimpsestMatch.setBet(bet);
        }

        String betKind = "";
        if(jobject.get("betKind")!=null) {
            betKind = jobject.get("betKind").getAsString();
            palimpsestMatch.setBetKind(betKind);
        }

        String quote = "";
        if(jobject.get("finalQuote")!=null) {
            quote = jobject.get("finalQuote").getAsString();
            palimpsestMatch.setQuote(quote);
        }

        boolean fissa = false;
        if(jobject.get("fissa")!=null) {
            fissa = jobject.get("fissa").getAsBoolean();
            palimpsestMatch.setFissa(fissa);
        }

        String resultHomeTeam = "";
        if(jobject.get("resultHomeTeam")!=null) {
            resultHomeTeam = jobject.get("resultHomeTeam").getAsString();
            palimpsestMatch.setHomeResult(resultHomeTeam);
        }

        String resultAwayTeam = "";
        if(jobject.get("resultAwayTeam")!=null) {
            resultAwayTeam = jobject.get("resultAwayTeam").getAsString();
            palimpsestMatch.setAwayResult(resultAwayTeam);
        }

        String resultTime = "";
        if(jobject.get("resultTime")!=null) {
            resultTime = jobject.get("resultTime").getAsString();
            palimpsestMatch.setResultTime(resultTime);
        }

        JsonArray array = jobject.get("allHiddenResult").getAsJsonArray();
        ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();
        for(int i = 0;i<array.size();i++){
            JsonObject jSon = (JsonObject) array.get(i);
            String action = "";
            if(jSon.get("action")!=null)
                action = jSon.get("action").getAsString();
            int actionTeam = -10;
            if(jSon.get("actionTeam")!=null)
                actionTeam = jSon.get("actionTeam").getAsInt();
            String playerName = "";
            if(jSon.get("playerName")!=null)
                playerName = jSon.get("playerName").getAsString();
            String result = "";
            if(jSon.get("result")!=null)
                result = jSon.get("result").getAsString();
            String hiddenTime = "";
            if(jSon.get("time") !=null)
                hiddenTime = jSon.get("time").getAsString();

            ParcelableHiddenResult hiddenResult = new ParcelableHiddenResult(playerName,hiddenTime,action,actionTeam);
            if(!result.isEmpty())
                hiddenResult.setResult(result);
            allHiddenResult.add(hiddenResult);
        }
        palimpsestMatch.setAllHiddenResult(allHiddenResult);
        return palimpsestMatch;

        //TODO l'unica cosa rimasta sarebbe allOdds ma non ha molto senso!
    }
}
