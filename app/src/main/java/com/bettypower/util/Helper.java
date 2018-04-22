package com.bettypower.util;

import android.content.Context;

import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.Team;
import com.renard.betty.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class Helper {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private Context context;

    public Helper(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        this.allPalimpsestMatch = allPalimpsestMatch;
    }

    public Helper(Context context){
        this.context = context;
    }

    public Helper(){

    }


    /**
     * check if the word given in param is a number or not
     * @param word to examinate
     * @return true if word is a number, false otherwise
     */
    public boolean isNumber(String word){
        try{
            long wordToLong = Long.parseLong(word);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean binarySearch(ArrayList<Long> allNumbers,long palimpsest , int low, int high) {
        if (high < low)
            return false; // not found
        int mid = low + ((high - low) / 2);
        if (allNumbers.get(mid) > palimpsest)
            return binarySearch(allNumbers,palimpsest, low, mid-1);
        else if (allNumbers.get(mid) < palimpsest)
            return binarySearch(allNumbers,palimpsest, mid+1, high);
        else
            return true; // found
    }

    /**
     * @return all teams present in the palimpsest match data set
     */
    public ArrayList<Team> getAllTeams(){
        ArrayList<Team> result = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
             ) {
            Team homeTeam = currentPalimpsestMatch.getHomeTeam();
            Team awayTeam = currentPalimpsestMatch.getAwayTeam();
            if(!isTeamInList(result,homeTeam)){
                result.add(homeTeam);
            }
            if(!isTeamInList(result,awayTeam)){
                result.add(awayTeam);
            }
        }
        return result;
    }

    /**
     * check if the team given in param is already present in the list, the decision is taken with name string matching
     * @param allTeams the list we consider
     * @param team the team we consider
     * @return true if the team is present in the list, false otherwise
     */
    public boolean isTeamInList(ArrayList<Team> allTeams,Team team){
        for (Team currentTeam:allTeams
             ) {
            if(team.getName().equalsIgnoreCase(currentTeam.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * this method make the join of the two list given in param,
     * to use this method you have to be sure that the two list haven't any element in common
     */
    public ArrayList<PalimpsestMatch> joinTwoPalimpsestLists(ArrayList<PalimpsestMatch> listOne,ArrayList<PalimpsestMatch> listTwo){
        for (PalimpsestMatch current:listTwo
             ) {
            listOne.add(current);
        }
        return listOne;
    }

    public int getNumberMatchNotFinished(ArrayList<PalimpsestMatch> allPalimpsestMatch) {
        int numberMatchRemained = 0;
        for (PalimpsestMatch currentMatch:allPalimpsestMatch
                ) {
            if (!isMatchFinish(currentMatch)) {
                numberMatchRemained++;
            }
        }
        return numberMatchRemained;
    }

    public boolean isMatchFinish(PalimpsestMatch palimpsestMatch){
        if(palimpsestMatch.getResultTime()!=null && (palimpsestMatch.getResultTime().equalsIgnoreCase(context.getResources().getString(R.string.match_terminated)) ||
                palimpsestMatch.getResultTime().equalsIgnoreCase(context.getResources().getString(R.string.match_annullato)) ||
                        palimpsestMatch.getResultTime().equalsIgnoreCase(context.getResources().getString(R.string.match_posticipated)))){
            return true;
        }
        String One = giveDate(); //one is current date
        String Two = palimpsestMatch.getTime();
        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM HH:mm",Locale.getDefault());
        java.util.Date dateOne = null;
        java.util.Date dateTwo = null;
        boolean result = false;
        try {
            dateOne = formatter1.parse(One);
            dateTwo = formatter1.parse(Two);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int monthOne = dateOne.getMonth();
        int monthTwo = dateTwo.getMonth();

        if(dateOne.getMonth()==11 && (dateTwo.getMonth()>=0 && dateTwo.getMonth()<9)){
            result = false;
        }
        else if(dateOne.getMonth() == 0 && (dateTwo.getMonth() <= 11 && dateTwo.getMonth()>= 9)){
            result = true;
        }
        else {
            final long HOUR = 3600*1000;
            dateTwo = new java.util.Date(dateTwo.getTime() + 2 * HOUR);
            if (dateOne.after(dateTwo)) {
                result = true;
            } else {
                result = false;
            }
        }

        return result;

    }

    private String giveDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String getCurrentTime = sdfh.format(c.getTime());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String date = sdf.format(cal.getTime());

	/*	if (getCurrentTime.compareTo(midnight) >= 0 && getCurrentTime.compareTo(oneOclock) <= 0)
		{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
			cal.add(Calendar.DATE, -1);
			date = sdf.format(cal.getTime());
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
			date = sdf.format(cal.getTime());
		}*/
        return date + " " + getCurrentTime;
    }

}
