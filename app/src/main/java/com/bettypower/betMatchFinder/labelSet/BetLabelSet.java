package com.bettypower.betMatchFinder.labelSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 25/10/17.
 */

public class BetLabelSet {

    public Map<String,ArrayList<String>> getAllBet(){
        Map<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> homeWinner = new ArrayList<>();
        homeWinner.add("1");
        map.put("1",homeWinner);

        ArrayList<String> awayWinner = new ArrayList<>();
        awayWinner.add("2");
        map.put("2",awayWinner);

        ArrayList<String> draw = new ArrayList<>();
        draw.add("x");
        map.put("X",draw);

        ArrayList<String> unoX = new ArrayList<>();
        unoX.add("1x");
        map.put("1X",unoX);

        ArrayList<String> dueX = new ArrayList<>();
        dueX.add("2x");
        map.put("2x",dueX);

        ArrayList<String> unoDue = new ArrayList<>();
        unoDue.add("12");
        map.put("12",unoDue);

        ArrayList<String> goal = new ArrayList<>();
        goal.add("goal");
        map.put("goal",goal);

        ArrayList<String> noGoal = new ArrayList<>();
        noGoal.add("nogoal");
        map.put("nogoal",noGoal);

        ArrayList<String> under = new ArrayList<>();
        under.add("under");
        map.put("under",under);

        ArrayList<String> over = new ArrayList<>();
        over.add("over");
        map.put("over",over);

        ArrayList<String> unoPiuOverUnoEMezzo = new ArrayList<>();
        unoPiuOverUnoEMezzo.add("1 + over 1,5");
        map.put("1 + over 1.5",unoPiuOverUnoEMezzo);

        ArrayList<String> unoPiuHendicap = new ArrayList<>();
        unoPiuOverUnoEMezzo.add("1 + hendicap");
        map.put("1 hendicap",unoPiuOverUnoEMezzo);

        return map;
    }

    public Map<String,ArrayList<String>> getAllBetKind(){
        Map<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> one = new ArrayList<>();
        one.add("1x2");
        one.add("1X2");
        map.put("1x2",one);

        ArrayList<String> two = new ArrayList<>();
        two.add("goal/no goal");
        map.put("goal/nogoal",two);

        ArrayList<String> underOver = new ArrayList<>();
        underOver.add("under / over");
        map.put("under/over",underOver);

        ArrayList<String> three = new ArrayList<>();
        three.add("esito finale 1x2");
        map.put("esito finale 1x2",three);

        return map;
    }

    public Map<String,ArrayList<String>> hashBetAndBetKind(){
        Map<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> arrayListOne = new ArrayList<>();
        arrayListOne.add("1");
        arrayListOne.add("X");
        arrayListOne.add("2");
        map.put("1X2",arrayListOne);

        ArrayList<String> arrayListTwo = new ArrayList<>();
        arrayListTwo.add("1X");
        arrayListTwo.add("12");
        arrayListTwo.add("X2");
        map.put("Doppia Chance",arrayListTwo);

        ArrayList<String> arrayListThree = new ArrayList<>();
        arrayListThree.add("goal");
        arrayListThree.add("nogoal");
        map.put("Goal / NoGoal",arrayListThree);

        ArrayList<String> arrayListFour = new ArrayList<>();
        arrayListFour.add("Under 0,5");
        arrayListFour.add("Over 0,5");
        arrayListFour.add("Under 1,5");
        arrayListFour.add("Over 1,5");
        arrayListFour.add("Under 2,5");
        arrayListFour.add("Over 2,5");
        arrayListFour.add("Under 3,5");
        arrayListFour.add("Over 3,5");
        arrayListFour.add("Under 4,5");
        arrayListFour.add("Over 4,5");
        arrayListFour.add("Under 5,5");
        arrayListFour.add("Over 5,5");
        arrayListFour.add("Under 6,5");
        arrayListFour.add("Over 6,5");
        arrayListFour.add("Under 7,5");
        arrayListFour.add("Over 7,5");
        map.put("Under / Over",arrayListFour);

        ArrayList<String> arrayListFive = new ArrayList<>();
        arrayListFive.add("1 (0:1)");
        arrayListFive.add("X (0:1)");
        arrayListFive.add("2 (0:1)");
        arrayListFive.add("1 (1:0)");
        arrayListFive.add("X (1:0)");
        arrayListFive.add("2 (1:0)");
        arrayListFive.add("1 (0:2)");
        arrayListFive.add("X (0:2)");
        arrayListFive.add("2 (0:2)");
        arrayListFive.add("1 (2:0)");
        arrayListFive.add("X (2:0)");
        arrayListFive.add("2 (2:0)");
        map.put("Handicap",arrayListFive);

        ArrayList<String> arrayListSix = new ArrayList<>();
        arrayListSix.add("Dispari");
        arrayListSix.add("Pari");
        arrayListSix.add("Dispari 1T");
        arrayListSix.add("Pari 2T");
        map.put("Pari / Dispari",arrayListSix);

        ArrayList<String> arrayListSeven = new ArrayList<>();
        arrayListSeven.add("1");
        arrayListSeven.add("X");
        arrayListSeven.add("2");
        map.put("Primo Tempo",arrayListSeven);

        ArrayList<String> arrayListEight = new ArrayList<>();
        arrayListEight.add("1 / 1");
        arrayListEight.add("1 / X");
        arrayListEight.add("1 / 2");
        arrayListEight.add("X / 1");
        arrayListEight.add("X / X");
        arrayListEight.add("X / 2");
        arrayListEight.add("2 / 1");
        arrayListEight.add("2 / X");
        arrayListEight.add("2 / 2");
        map.put("Primo Tempo / Secondo Tempo",arrayListEight);

        ArrayList<String> arrayListNine = new ArrayList<>();
        arrayListNine.add("1-0");
        arrayListNine.add("2-0");
        arrayListNine.add("2-1");
        arrayListNine.add("3-0");
        arrayListNine.add("3-1");
        arrayListNine.add("3-2");
        arrayListNine.add("4-0");
        arrayListNine.add("4-1");
        arrayListNine.add("4-2");
        arrayListNine.add("4-3");
        arrayListNine.add("0-0");
        arrayListNine.add("1-1");
        arrayListNine.add("2-2");
        arrayListNine.add("3-3");
        arrayListNine.add("4-4");
        arrayListNine.add("0-1");
        arrayListNine.add("0-2");
        arrayListNine.add("1-2");
        arrayListNine.add("0-3");
        arrayListNine.add("1-3");
        arrayListNine.add("2-3");
        arrayListNine.add("0-4");
        arrayListNine.add("1-4");
        arrayListNine.add("2-4");
        arrayListNine.add("3-4");
        map.put("Risultato Finale",arrayListNine);

        ArrayList<String> arrayListTen = new ArrayList<>();
        arrayListTen.add("0");
        arrayListTen.add("1");
        arrayListTen.add("2");
        arrayListTen.add("3");
        arrayListTen.add("4");
        arrayListTen.add("5");
        arrayListTen.add("5+");
        arrayListTen.add("6");
        arrayListTen.add("7");
        arrayListTen.add("7+");
        map.put("Gol Totali",arrayListTen);

        ArrayList<String> arrayListEleven = new ArrayList<>();
        arrayListEleven.add("Si");
        arrayListEleven.add("No");
        map.put("Segna Squadra In Casa",arrayListEleven);

        ArrayList<String> arrayListTwelve = new ArrayList<>();
        arrayListTwelve.add("Si");
        arrayListTwelve.add("No");
        map.put("Segna squadra fuori casa",arrayListTwelve);

        ArrayList<String> arrayListThirteen = new ArrayList<>();
        arrayListThirteen.add("Under 0,5");
        arrayListThirteen.add("Over 0,5");
        arrayListThirteen.add("Under 1,5");
        arrayListThirteen.add("Over 1,5");
        arrayListThirteen.add("Under 2,5");
        arrayListThirteen.add("Over 2,5");
        arrayListThirteen.add("Under 3,5");
        arrayListThirteen.add("Over 3,5");
        arrayListThirteen.add("Under 4,5");
        arrayListThirteen.add("Over 4,5");
        arrayListThirteen.add("Under 5,5");
        arrayListThirteen.add("Over 5,5");
        map.put("Under / Over primo tempo",arrayListThirteen);

        ArrayList<String> arrayListFourteen = new ArrayList<>();
        arrayListFourteen.add("Under 0,5");
        arrayListFourteen.add("Over 0,5");
        arrayListFourteen.add("Under 1,5");
        arrayListFourteen.add("Over 1,5");
        arrayListFourteen.add("Under 2,5");
        arrayListFourteen.add("Over 2,5");
        arrayListFourteen.add("Under 3,5");
        arrayListFourteen.add("Over 3,5");
        arrayListFourteen.add("Under 4,5");
        arrayListFourteen.add("Over 4,5");
        arrayListFourteen.add("Under 5,5");
        arrayListFourteen.add("Over 5,5");
        map.put("Under / Over secondo tempo",arrayListFourteen);

        ArrayList<String> arrayListSixteen = new ArrayList<>();
        arrayListSixteen.add("Under 1,5");
        arrayListSixteen.add("Over 1,5");
        arrayListSixteen.add("Under 2,5");
        arrayListSixteen.add("Over 2,5");
        arrayListSixteen.add("Under 3,5");
        arrayListSixteen.add("Over 3,5");
        arrayListSixteen.add("Under 4,5");
        arrayListSixteen.add("Over 4,5");
        arrayListSixteen.add("Under 5,5");
        arrayListSixteen.add("Over 5,5");
        map.put("Goal totali squadra in casa",arrayListSixteen);

        ArrayList<String> arrayListSeventeen = new ArrayList<>();
        arrayListSeventeen.add("Under 1,5");
        arrayListSeventeen.add("Over 1,5");
        arrayListSeventeen.add("Under 2,5");
        arrayListSeventeen.add("Over 2,5");
        arrayListSeventeen.add("Under 3,5");
        arrayListSeventeen.add("Over 3,5");
        arrayListSeventeen.add("Under 4,5");
        arrayListSeventeen.add("Over 4,5");
        arrayListSeventeen.add("Under 5,5");
        arrayListSeventeen.add("Over 5,5");
        map.put("Goal totali squadra fuori casa",arrayListSeventeen);

        ArrayList<String> arrayListEighteen = new ArrayList<>();
        arrayListEighteen.add("Si");
        arrayListEighteen.add("No");
        map.put("Squadra in casa vittoria a zero",arrayListEighteen);

        ArrayList<String> arrayListNineteen = new ArrayList<>();
        arrayListNineteen.add("Si");
        arrayListNineteen.add("No");
        map.put("Squadra fuori casa vittoria a zero",arrayListNineteen);

        ArrayList<String> arrayListTwenty = new ArrayList<>();
        arrayListTwenty.add("Gol");
        arrayListTwenty.add("NoGol");
        map.put("Goal / NoGoal primo tempo",arrayListTwenty);

        ArrayList<String> arrayListTwentyOne = new ArrayList<>();
        arrayListTwentyOne.add("Gol");
        arrayListTwentyOne.add("NoGol");
        map.put("Goal / NoGoal secondo tempo",arrayListTwentyOne);

        ArrayList<String> arrayListTwentyTwo = new ArrayList<>();
        arrayListTwentyTwo.add("Primo Tempo");
        arrayListTwentyTwo.add("Secondo Tempo");
        arrayListTwentyTwo.add("Pari");
        map.put("Tempo con piu gol",arrayListTwentyTwo);

        ArrayList<String> arrayListTwentyThree = new ArrayList<>();
        arrayListTwentyThree.add("1 & Under 1.5");
        arrayListTwentyThree.add("1 & Over 1.5");
        arrayListTwentyThree.add("X & Under 1.5");
        arrayListTwentyThree.add("X & Over 1.5");
        arrayListTwentyThree.add("2 & Under 1.5");
        arrayListTwentyThree.add("2 & Over 1.5");
        arrayListTwentyThree.add("1 & Under 2.5");
        arrayListTwentyThree.add("1 & Over 2.5");
        arrayListTwentyThree.add("X & Under 2.5");
        arrayListTwentyThree.add("X & Over 2.5");
        arrayListTwentyThree.add("2 & Under 2.5");
        arrayListTwentyThree.add("2 & Over 2.5");
        arrayListTwentyThree.add("1 & Under 3.5");
        arrayListTwentyThree.add("1 & Over 3.5");
        arrayListTwentyThree.add("X & Under 3.5");
        arrayListTwentyThree.add("X & Over 3.5");
        arrayListTwentyThree.add("2 & Under 3.5");
        arrayListTwentyThree.add("2 & Over 3.5");
        arrayListTwentyThree.add("1 & Under 4.5");
        arrayListTwentyThree.add("1 & Over 4.5");
        arrayListTwentyThree.add("X & Under 4.5");
        arrayListTwentyThree.add("X & Over 4.5");
        arrayListTwentyThree.add("2 & Under 4.5");
        arrayListTwentyThree.add("2 & Over 4.5");
        map.put("Esito finale + Under / Over",arrayListTwentyThree);

        ArrayList<String> arrayListTwentyFour = new ArrayList<>();
        arrayListTwentyFour.add("1 & Gol");
        arrayListTwentyFour.add("1 & NoGol");
        arrayListTwentyFour.add("X & Gol");
        arrayListTwentyFour.add("X & NoGol");
        arrayListTwentyFour.add("2 & Gol");
        arrayListTwentyFour.add("2 & NoGol");
        map.put("Esito finale + Gol / NoGol",arrayListTwentyFour);

        ArrayList<String> arrayListTwentyFive = new ArrayList<>();
        arrayListTwentyFive.add("1X & Gol");
        arrayListTwentyFive.add("1X & NoGol");
        arrayListTwentyFive.add("X2 & Gol");
        arrayListTwentyFive.add("X2 & NoGol");
        arrayListTwentyFive.add("12 & Gol");
        arrayListTwentyFive.add("12 & NoGol");
        map.put("Esito finale + Gol / NoGol",arrayListTwentyFive);

        ArrayList<String> arrayListTwentySix = new ArrayList<>();
        arrayListTwentySix.add("1X & Under 1.5");
        arrayListTwentySix.add("1X & Over 1.5");
        arrayListTwentySix.add("X2 & Under 1.5");
        arrayListTwentySix.add("X2 & Over 1.5");
        arrayListTwentySix.add("12 & Under 1.5");
        arrayListTwentySix.add("12 & Over 1.5");
        arrayListTwentySix.add("1X & Under 2.5");
        arrayListTwentySix.add("1X & Over 2.5");
        arrayListTwentySix.add("X2 & Under 2.5");
        arrayListTwentySix.add("X2 & Over 2.5");
        arrayListTwentySix.add("12 & Under 2.5");
        arrayListTwentySix.add("12 & Over 2.5");
        arrayListTwentySix.add("1X & Under 3.5");
        arrayListTwentySix.add("1X & Over 3.5");
        arrayListTwentySix.add("X2 & Under 3.5");
        arrayListTwentySix.add("X2 & Over 3.5");
        arrayListTwentySix.add("12 & Under 3.5");
        arrayListTwentySix.add("12 & Over 3.5");
        arrayListTwentySix.add("1X & Under 4.5");
        arrayListTwentySix.add("1X & Over 4.5");
        arrayListTwentySix.add("X2 & Under 4.5");
        arrayListTwentySix.add("X2 & Over 4.5");
        arrayListTwentySix.add("12 & Under 4.5");
        arrayListTwentySix.add("12 & Over 4.5");
        map.put("Doppia chance + Under / Over",arrayListTwentySix);

        ArrayList<String> arrayListTwentySeven = new ArrayList<>();
        arrayListTwentySeven.add("Gol & Over 2,5");
        arrayListTwentySeven.add("Gol & Under 2,5");
        arrayListTwentySeven.add("NoGol & Over 2,5");
        arrayListTwentySeven.add("NoGol & Under 2,5");
        map.put("Gol / NoGol + Under / Over",arrayListTwentySeven);

        ArrayList<String> arrayListTwentyEight = new ArrayList<>();
        arrayListTwentyEight.add("1-2 Gol");
        arrayListTwentyEight.add("1-3 Gol");
        arrayListTwentyEight.add("1-4 Gol");
        arrayListTwentyEight.add("1-5 Gol");
        arrayListTwentyEight.add("1-6 Gol");
        arrayListTwentyEight.add("2-3 Gol");
        arrayListTwentyEight.add("2-4 Gol");
        arrayListTwentyEight.add("2-5 Gol");
        arrayListTwentyEight.add("2-6 Gol");
        arrayListTwentyEight.add("3-4 Gol");
        arrayListTwentyEight.add("3-5 Gol");
        arrayListTwentyEight.add("3-6 Gol");
        arrayListTwentyEight.add("4-5 Gol");
        arrayListTwentyEight.add("4-6 Gol");
        arrayListTwentyEight.add("5-6 Gol");
        arrayListTwentyEight.add("7+ Gol");
        map.put("Multi Gol",arrayListTwentySeven);

        ArrayList<String> arrayListTwentyNine = new ArrayList<>();
        arrayListTwentyNine.add("1-2 Gol Primo Tempo");
        arrayListTwentyNine.add("1-3 Gol Primo Tempo");
        arrayListTwentyNine.add("2-3 Gol Primo Tempo");
        arrayListTwentyNine.add("1-2 Gol Secondo Tempo");
        arrayListTwentyNine.add("1-3 Gol Primo Tempo");
        arrayListTwentyNine.add("2-3 Gol Primo Tempo");
        map.put("Multi Gol 1/2",arrayListTwentyNine);

        ArrayList<String> arrayListThirty = new ArrayList<>();
        arrayListThirty.add("1-2 Gol Casa");
        arrayListThirty.add("1-3 Gol Casa");
        arrayListThirty.add("1-4 Gol Casa");
        arrayListThirty.add("1-5 Gol Casa");
        arrayListThirty.add("2-3 Gol Casa");
        arrayListThirty.add("2-4 Gol Casa");
        arrayListThirty.add("2-5 Gol Casa");

        arrayListThirty.add("1-2 Gol Trasferta");
        arrayListThirty.add("1-3 Gol Trasferta");
        arrayListThirty.add("1-4 Gol Trasferta");
        arrayListThirty.add("1-5 Gol Trasferta");
        arrayListThirty.add("2-3 Gol Trasferta");
        arrayListThirty.add("2-4 Gol Trasferta");
        arrayListThirty.add("2-5 Gol Trasferta");
        map.put("Multi Gol C/T",arrayListThirty);


        ArrayList<String> arrayListThirtyOne = new ArrayList<>();
        arrayListThirtyOne.add("1X");
        arrayListThirtyOne.add("12");
        arrayListThirtyOne.add("X2");
        map.put("Doppia Chance primo tempo",arrayListThirtyOne);

        ArrayList<String> arrayListThirtyTwo = new ArrayList<>();
        arrayListThirtyTwo.add("1X");
        arrayListThirtyTwo.add("12");
        arrayListThirtyTwo.add("X2");
        map.put("Doppia Chance secondo tempo",arrayListThirtyTwo);

        ArrayList<String> arrayListThirtyThree = new ArrayList<>();
        arrayListThirtyThree.add("Si");
        arrayListThirtyThree.add("No");
        map.put("Squadra in casa segna entrambi i tempi",arrayListThirtyThree);

        ArrayList<String> arrayListThirtyFour = new ArrayList<>();
        arrayListThirtyFour.add("Si");
        arrayListThirtyFour.add("No");
        map.put("Squadra ospite segna entrambi i tempi",arrayListThirtyFour);

        ArrayList<String> arrayListThirtyFive = new ArrayList<>();
        arrayListThirtyFive.add("1");
        arrayListThirtyFive.add("2");
        map.put("Rimborso in caso di pareggio",arrayListThirtyFive);

        ArrayList<String> arrayListThirtySix = new ArrayList<>();
        arrayListThirtySix.add("Si");
        arrayListThirtySix.add("No");
        map.put("Squadra in casa vince entrambi i tempi",arrayListThirtySix);

        ArrayList<String> arrayListThirtySeven = new ArrayList<>();
        arrayListThirtySeven.add("Si");
        arrayListThirtySeven.add("No");
        map.put("Squadra ospite vince entrambi i tempi",arrayListThirtySeven);
        return map;
    }
    //TODO tornera molto utile un metodo che mappi i betkind in bet per vedere tutte le scommesse a quale gruppo appartengno
}
