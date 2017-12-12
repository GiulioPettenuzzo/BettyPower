package com.bettypower.entities;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 01/08/17.
 */

public interface PalimpsestMatch extends Parcelable,Match{

    String getPalimpsest();
    void setPalimpsest(String palimpsest);

    String getEventNumber();
    void setEventNumber(String eventNumber);

    String getCompletePalimpsest();
    void setCompletePalimpsest(String completePalimpsest);

    Map<String,String> getAllOdds();
    void setAllOdds(Map<String,String> allOdds);

    String getBookMaker();
    void setBookMaker(String bookMaker);

    boolean compareToByName(PalimpsestMatch palimpsestMatch);
}
