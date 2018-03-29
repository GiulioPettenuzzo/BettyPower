package com.bettypower.entities;

import android.graphics.Bitmap;
import android.os.Parcel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 13/10/17.
 */

public class SingleBet implements Bet {

    private ArrayList<PalimpsestMatch> allMatches;
    private String bookmaker;
    private String date;
    private String puntata;
    private String vincita;
    private String errors;
    private boolean isSistema;
    private boolean areMatchesFinished;  // in order to know if all matches are finished

    public SingleBet(ArrayList<PalimpsestMatch> allMatches){
        this.allMatches = allMatches;
    }

    @Override
    public ArrayList<PalimpsestMatch> getArrayMatch() {
        return allMatches;
    }

    @Override
    public void setArrayMatch(ArrayList<PalimpsestMatch> allMatches) {
        this.allMatches = allMatches;
    }

    @Override
    public String getBookMaker() {
        return bookmaker;
    }

    @Override
    public void setBookMaker(String bookMaker) {
        this.bookmaker = bookMaker;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getPuntata() {
        return puntata;
    }

    @Override
    public void setPuntata(String puntata) {
        this.puntata = puntata;
    }

    @Override
    public String getVincita() {
        return vincita;
    }

    @Override
    public void setVincita(String vincita) {
        this.vincita = vincita;
    }

    @Override
    public String getErrors() {
        return errors;
    }

    @Override
    public void setErrors(String errors) {
        this.errors = errors;
    }

    @Override
    public boolean isSistema() {
        return isSistema;
    }

    @Override
    public void setSistema(boolean isSistema) {
        this.isSistema = isSistema;
    }

    @Override
    public boolean areMatchesFinished() {
        return areMatchesFinished;
    }

    @Override
    public void setAreMatchFinished(boolean value) {
        areMatchesFinished = value;
    }

    @Override
    public boolean areThereMatchToday() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Date date = new Date();
        String dateString = dateFormat.format(date);
        for (PalimpsestMatch currentPalimpsest:allMatches
             ) {
            StringTokenizer token = new StringTokenizer(currentPalimpsest.getDate());
            String monthDay = token.nextToken();
            if (dateString.equalsIgnoreCase(monthDay)) {
                return true;
            }
        }
        return false;
    }

    public static final Creator<SingleBet> CREATOR = new Creator<SingleBet>(){

        @Override
        public SingleBet createFromParcel(Parcel source) {
            return new SingleBet(source);
        }

        @Override
        public SingleBet[] newArray(int size) {
            return new SingleBet[size];
        }
    };

    public SingleBet(Parcel source){
        this( source.readArrayList(ParcelablePalimpsestMatch.class.getClassLoader()));
        //allMatches = new ArrayList<>();
       // this.allMatches = source.readArrayList(List.class.getClassLoader());
        this.bookmaker = source.readString();
        this.puntata = source.readString();
        this.vincita = source.readString();
        this.errors = source.readString();
        this.date = source.readString();
        this.isSistema = (Boolean) source.readValue(getClass().getClassLoader());
        this.areMatchesFinished = (Boolean) source.readValue(getClass().getClassLoader());
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(allMatches);
        dest.writeString(bookmaker);
        dest.writeString(puntata);
        dest.writeString(vincita);
        dest.writeString(errors);
        dest.writeString(date);
        dest.writeValue(isSistema);
        dest.writeValue(areMatchesFinished);
    }
}
