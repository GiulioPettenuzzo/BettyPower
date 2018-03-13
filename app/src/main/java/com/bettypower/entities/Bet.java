package com.bettypower.entities;

import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 13/10/17.
 */

public interface Bet extends Parcelable{
    ArrayList<PalimpsestMatch> getArrayMatch();
    void setArrayMatch(ArrayList<PalimpsestMatch> allMatches);

    String getBookMaker();
    void setBookMaker(String bookMaker);

    String getDate();
    void setDate(String date);

    String getPuntata();
    void setPuntata(String puntata);

    String getVincita();
    void setVincita(String vincita);

    String getErrors();
    void setErrors(String errors);

    boolean isSistema();
    void setSistema(boolean isSistema);

    boolean areMatchesFinished();
    void setAreMatchFinished(boolean value);
}
