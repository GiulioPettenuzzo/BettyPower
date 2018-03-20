package com.renard.ocr.documents.creation.crop;

import android.text.format.DateUtils;
import android.util.Log;

import com.bettypower.betChecker.ConcreteChecker;
import com.bettypower.betMatchFinder.Assembler;
import com.bettypower.betMatchFinder.Divider;
import com.bettypower.betMatchFinder.Finder;
import com.bettypower.betMatchFinder.entities.Date;
import com.bettypower.util.Helper;
import com.bettypower.betMatchFinder.entities.ConcreteMatchToFind;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.fixers.PalimpsestFixer;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by giuliopettenuzzo on 12/08/17.
 */
@RunWith(JUnit4.class)
public class ParcingTest {

    @Test
    public void resultThread(){
        int home = 3;
        int away = 1;
        ConcreteChecker concreteChecker = new ConcreteChecker(home,away);
        boolean result = concreteChecker.handicap101();
        assertEquals(true,result);
    }

    @Test
    public void existNumber(){
        String prova = "ciao";
        boolean result = false;
        if(prova.matches(".*\\d+.*")){
            result = true;
        }
        else{
            result = false;
        }
        assertEquals(true,result);
    }

    @Test
    public void mustUpdateDate(){
        String One = "00:30"; //one is current date
        String Two = "03:00";
        String lastHourUpdate = "03:59";

        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm",Locale.getDefault());
        java.util.Date dateOne = null;
        java.util.Date dateTwo = null;
        java.util.Date lastUpdateDate = null;

        java.util.Date now = Calendar.getInstance().getTime();
        String nowHour = String.valueOf(now.getHours());
        String nowminute = String.valueOf(now.getMinutes());
        String thisTime = nowHour+":"+nowminute;
        java.util.Date dateNow = null;


        try {
            dateOne = formatter1.parse(One); //ora minore
            dateTwo = formatter1.parse(Two); //or maggiore
            dateNow = formatter1.parse(thisTime); //ora corrente
            lastUpdateDate = formatter1.parse(lastHourUpdate);//ultimo aggiornamento
            String thisHour = String.valueOf(lastUpdateDate.getHours());
            String thisminute = String.valueOf(lastUpdateDate.getMinutes());
            lastHourUpdate = thisHour+":"+thisminute;
            lastUpdateDate = formatter1.parse(lastHourUpdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean resultdate = false;

        if(lastUpdateDate.after(dateOne)&&dateTwo.after(lastUpdateDate)&&dateNow.after(dateTwo)){
            resultdate = true;
        }
        else if(lastUpdateDate.after(dateOne)&&dateTwo.after(lastUpdateDate)&&dateTwo.after(dateNow)){
            resultdate = false;
        }
        assertEquals(true,resultdate);
    }


    @Test
    public void afterThreeOclock(){
        String One = "11:00";
        java.util.Date now = Calendar.getInstance().getTime();
        String thisHour = String.valueOf(now.getHours());
        String thisminute = String.valueOf(now.getMinutes());
        String thisTime = thisHour+":"+thisminute;
        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm");
        java.util.Date dateOne = null;
        java.util.Date dateNow = null;
        try {
            dateOne = formatter1.parse(One);
            dateNow = formatter1.parse(thisTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean resultdate = false;

        if(dateNow.after(dateOne)){
            resultdate = true;
        }

        assertEquals(true,resultdate);
    }
    @Test
    public void updateDateTest(){
        String One = "10:30"; //one is current date
        String Two = "16:30";
        java.util.Date now = Calendar.getInstance().getTime();
        String thisHour = String.valueOf(now.getHours());
        String thisminute = String.valueOf(now.getMinutes());
        String thisTime = thisHour+":"+thisminute;
        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm");
        java.util.Date dateOne = null;
        java.util.Date dateTwo = null;
        java.util.Date dateNow = null;
        try {
            dateOne = formatter1.parse(One);
            dateTwo = formatter1.parse(Two);
            dateNow = formatter1.parse(thisTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean resultdate = false;
        if(dateNow.after(dateOne) && dateTwo.after(dateNow)){
            resultdate = true;
        }
        assertEquals(true,resultdate);
    }


    @Test
    public void dateHourTest(){
        Calendar c = Calendar.getInstance();
        String One = "01:00"; //one is current date
        String Two = "03:00";
        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM HH:mm");
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

        assertEquals(true, result);
    }

    @Test
    public void fixPaliTest() {
        PalimpsestFixer fixer = new PalimpsestFixer();
        fixer.setWord("27371-2516,");
        String word = fixer.getFixedWord();
        assertEquals(word, "273712516");
    }

    @Test
    public void palimpsestTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("real madrid"),new ParcelableTeam("athletico madrid"));
        paliOne.setPalimpsest("21371");
        allPali.add(paliOne);
        Finder finder = new Finder(allPali);
        finder.isPalimpsest();
        finder.setWord("~1037484848484848484848484848484848484848484848484848484848484,");
        assertEquals("273421432",finder.isPalimpsest());
    }

    @Test
    public void dateTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Finder finder = new Finder(allPali);
        //assertEquals("month_day",finder.isDate().getInitialFormat());
        assertEquals("17/09/17",finder.isDate().toString());
    }

    @Test
    public void hourTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Finder finder = new Finder(allPali);
        assertEquals("09:30",finder.isHour());

    }

    @Test
    public void bookMakerTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Finder finder = new Finder(allPali);
        assertEquals("william hill",finder.isBookMaker());
    }

    @Test
    public void quoteText(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Finder finder = new Finder(allPali);
        assertEquals(true,finder.isQuote());

    }

    @Test
    public void betTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Finder finder = new Finder(allPali);
        assertEquals("1",finder.isBet());

    }

    @Test
    public void betKindTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Finder finder = new Finder(allPali);
        finder.setWord("1");
        finder.getWordKinds();
        finder.setWord("1X2");
        //finder.setWord("finale");
        //finder.setWord("+");
        //finder.setWord("u/o");
        //finder.setWord("1,5");
        assertEquals("1X2",finder.isBetKind());
    }

    @Test
    public void isNameTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("real madrid"),new ParcelableTeam("athletico madrid"));
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("atalanta"),new ParcelableTeam("chievo verona"));
        PalimpsestMatch paliThree = new ParcelablePalimpsestMatch(new ParcelableTeam("real sociedad"),new ParcelableTeam("athletico bilbao"));
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("real sociedad yankie"),new ParcelableTeam("athletico bilbao alloffame"));
        paliOne.setPalimpsest("1111111");
        paliTwo.setPalimpsest("2222222");
        paliThree.setPalimpsest("3333333");
        paliFour.setPalimpsest("4444444");
        allPali.add(paliFour);
        allPali.add(paliThree);
        allPali.add(paliTwo);
        allPali.add(paliOne);


        Finder finder = new Finder(allPali);
        finder.isName("");

        assertEquals("",finder.isName(""));
    }

    @Test
    public void dividerTest(){
        Divider divider = new Divider();
        divider.setWord("MATCHPOINT");
        assertEquals("",divider.getPossibleFields());
    }

    @Test
    public void helperNameContainsTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Helper helper = new Helper(allPali);
        assertEquals(true,helper.isWordContainedInTeamNameWithoutOcrError("real madrid","madrld"));
    }

    @Test
    public void helperNameComapreTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        Helper helper = new Helper(allPali);
        assertEquals(true,helper.compareNamesWithoutOCRError("milan","mllan"));
    }

    @Test
    public void findeTest(){
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();

        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("Hoffenhelm"),new ParcelableTeam("Hertha Berlino"));
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("atalanta"),new ParcelableTeam("inter"));
        PalimpsestMatch paliThree = new ParcelablePalimpsestMatch(new ParcelableTeam("real sociedad"),new ParcelableTeam("athletico bilbao"));
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("real sociedad yankie"),new ParcelableTeam("athletico bilbao alloffame"));

        paliOne.setPalimpsest("1111111");
        paliTwo.setPalimpsest("2222222");
        paliThree.setPalimpsest("3333333");
        paliFour.setPalimpsest("4444444");
        allPali.add(paliFour);
        allPali.add(paliThree);
        allPali.add(paliTwo);
        allPali.add(paliOne);


        Finder finder = new Finder(allPali);



        assertEquals("",finder.getWordKinds());
    }

    @Test
    public void assemblerTest(){
        ArrayList<MatchToFind> allMatchToFind = new ArrayList<>();
        MatchToFind matchToFindOne = new ConcreteMatchToFind();
        matchToFindOne.setPalimpsest("273711529");
        matchToFindOne.setHomeTeamName("HOFFENHEIM");
        matchToFindOne.setAwayTeamName("HERTHA BERLINO");
        matchToFindOne.setDate("17/09");
        matchToFindOne.setBetKind("1X2");

        MatchToFind matchToFindTwo = new ConcreteMatchToFind();
        matchToFindTwo.setBet("1");
        matchToFindTwo.setUnknownTeamName("HERTHA BERLINO");
        matchToFindTwo.setHomeTeamName("HOFFENHEIM");
        matchToFindTwo.setHour("13:30");

        allMatchToFind.add(matchToFindOne);
        allMatchToFind.add(matchToFindTwo);
        Assembler assembler = new Assembler(allMatchToFind);

        assertEquals("",assembler.getAllMatchReassembled().get(0).getPalimpsest() + " " + assembler.getAllMatchReassembled().get(0).getHomeName() + " " +
                assembler.getAllMatchReassembled().get(0).getAwayName() + " " + assembler.getAllMatchReassembled().get(0).getUnknownTeamName() + " " +
                assembler.getAllMatchReassembled().get(0).getDate() + " " + assembler.getAllMatchReassembled().get(0).getHour() + " " +
                assembler.getAllMatchReassembled().get(0).getBet() + " " + assembler.getAllMatchReassembled().get(0).getBetKind());
    }



}