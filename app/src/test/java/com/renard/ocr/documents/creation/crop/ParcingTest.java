package com.renard.ocr.documents.creation.crop;

import com.bettypower.betMatchFinder.Assembler;
import com.bettypower.betMatchFinder.Divider;
import com.bettypower.betMatchFinder.Finder;
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

import java.util.ArrayList;

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
        paliOne.setEventNumber("1037");
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
        paliOne.setEventNumber("1111111");
        paliTwo.setEventNumber("2222222");
        paliThree.setEventNumber("3333333");
        paliFour.setEventNumber("4444444");
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
        paliOne.setEventNumber("1111111");
        paliTwo.setEventNumber("2222222");
        paliThree.setEventNumber("3333333");
        paliFour.setEventNumber("4444444");
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

        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("HOFFENHEIM"),new ParcelableTeam("HERTHA BERLINO"),"273711","529","17/09 13:30");
        ArrayList<PalimpsestMatch> allPali = new ArrayList<>();
        allPali.add(paliOne);
        matchToFindOne.setPalimpsestMatch(allPali);
        matchToFindTwo.setPalimpsestMatch(allPali);
        allMatchToFind.add(matchToFindOne);
        allMatchToFind.add(matchToFindTwo);
        Assembler assembler = new Assembler(allMatchToFind);

        assertEquals("",assembler.getAllMatchReassembled().get(0).getPalimpsest() + " " + assembler.getAllMatchReassembled().get(0).getHomeName() + " " +
                assembler.getAllMatchReassembled().get(0).getAwayName() + " " + assembler.getAllMatchReassembled().get(0).getUnknownTeamName() + " " +
                assembler.getAllMatchReassembled().get(0).getDate() + " " + assembler.getAllMatchReassembled().get(0).getHour() + " " +
                assembler.getAllMatchReassembled().get(0).getBet() + " " + assembler.getAllMatchReassembled().get(0).getBetKind());
    }

    private ArrayList<PalimpsestMatch> addReplatz(ArrayList<PalimpsestMatch> allPalimpsestMatch){
        PalimpsestMatch paliOne = new ParcelablePalimpsestMatch(new ParcelableTeam("JUVENTUS"),new ParcelableTeam("CAGLIARI"),"27331","4682","19/08 18:00");
        PalimpsestMatch paliTwo = new ParcelablePalimpsestMatch(new ParcelableTeam("HELLAS VERONA"),new ParcelableTeam("NAPOLI"),"27331","4680","19/08 20:45");
        PalimpsestMatch paliTheree = new ParcelablePalimpsestMatch(new ParcelableTeam("BOLOGNA"),new ParcelableTeam("TORINO"),"27331","4678","20/08 20:45");
        PalimpsestMatch paliFour = new ParcelablePalimpsestMatch(new ParcelableTeam("INTER"),new ParcelableTeam("ACF FIORENTINA"),"27331","4681","20/08 20:45");
        PalimpsestMatch paliFive = new ParcelablePalimpsestMatch(new ParcelableTeam("SASSUOLO"),new ParcelableTeam("GENOA"),"27331","4685","20/08 20:45");
        PalimpsestMatch paliSix = new ParcelablePalimpsestMatch(new ParcelableTeam("UDINESE"),new ParcelableTeam("CHIEVO VERONA"),"27331","4686","20/08 20:45");
        PalimpsestMatch paliSeven = new ParcelablePalimpsestMatch(new ParcelableTeam("SAMPDORIA"),new ParcelableTeam("BENEVENTO"),"27331","4712","20/08 20:45");
        PalimpsestMatch paliEight = new ParcelablePalimpsestMatch(new ParcelableTeam("LAZIO"),new ParcelableTeam("SPAL"),"27331","4713","20/08 20:45");
        PalimpsestMatch paliNine = new ParcelablePalimpsestMatch(new ParcelableTeam("ATALANTA"),new ParcelableTeam("ROMA"),"27331","4677","20/08 18:00");
        PalimpsestMatch paliTen = new ParcelablePalimpsestMatch(new ParcelableTeam("CROTONE"),new ParcelableTeam("MILAN"),"27331","4679","21/08 20:45");
        PalimpsestMatch paliEleven = new ParcelablePalimpsestMatch(new ParcelableTeam("BAYERN MONACO"),new ParcelableTeam("BAYERN LEVERKUSEN"),"27331","1760","18/08 20:30");
        PalimpsestMatch paliTwelve = new ParcelablePalimpsestMatch(new ParcelableTeam("HOFFENHEIM"),new ParcelableTeam("WARDER BREMA"),"27331","1751","19/08 15:30");
        PalimpsestMatch pailThirteen = new ParcelablePalimpsestMatch(new ParcelableTeam("AMBURGO"),new ParcelableTeam("AUGSBURG"),"27331","1769","19/08 15:30");
        PalimpsestMatch paliFourteen = new ParcelablePalimpsestMatch(new ParcelableTeam("MAINZ 05"),new ParcelableTeam("HANNOVER"),"27331","1761","19/08 15:30");
        PalimpsestMatch pailFifteen = new ParcelablePalimpsestMatch(new ParcelableTeam("WOLFSBURG"),new ParcelableTeam("BORUSSIA DORTMUND"),"27331","1763","19/08 15:30");


        allPalimpsestMatch.add(paliOne);
        allPalimpsestMatch.add(paliTwo);
        allPalimpsestMatch.add(paliTheree);
        allPalimpsestMatch.add(paliFour);
        allPalimpsestMatch.add(paliFive);
        allPalimpsestMatch.add(paliSix);
        allPalimpsestMatch.add(paliSeven);
        allPalimpsestMatch.add(paliEight);
        allPalimpsestMatch.add(paliNine);
        allPalimpsestMatch.add(paliTen);
        allPalimpsestMatch.add(paliEleven);
        allPalimpsestMatch.add(paliTwelve);
        allPalimpsestMatch.add(pailThirteen);
        allPalimpsestMatch.add(paliFourteen);
        allPalimpsestMatch.add(pailFifteen);

        return allPalimpsestMatch;
    }

}