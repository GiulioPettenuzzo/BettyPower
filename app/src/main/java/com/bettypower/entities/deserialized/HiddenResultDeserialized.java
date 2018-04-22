package com.bettypower.entities.deserialized;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.ParcelableHiddenResult;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by giuliopettenuzzo on 12/03/18.
 */

public class HiddenResultDeserialized implements JsonDeserializer<HiddenResult> {
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
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public HiddenResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = json.getAsJsonObject();
        String action = jobject.get("action").getAsString();
        int actionTeam = jobject.get("actionTeam").getAsInt();
        String playerName = jobject.get("playerName").getAsString();
        String result = jobject.get("result").getAsString();
        String time = jobject.get("time").getAsString();
        ParcelableHiddenResult hiddenResult = new ParcelableHiddenResult(playerName,time,action,actionTeam);
        hiddenResult.setResult(result);
        return hiddenResult;
    }
}
