package com.bettypower.betMatchFinder.labelSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * return all the date format allowed to be read
 * Created by giuliopettenuzzo on 26/10/17.
 */

public class DateLabelSet {

    //TODO mesi con 30 e 31 giorni e settembre
    public Map<String,ArrayList<String>> getAllDay(){
        HashMap<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> one = new ArrayList<>();
        one.add("1");
        one.add("01");
        map.put("01",one);

        ArrayList<String> two = new ArrayList<>();
        two.add("2");
        two.add("02");
        map.put("02",two);

        ArrayList<String> three = new ArrayList<>();
        three.add("3");
        three.add("03");
        map.put("03",three);

        ArrayList<String> four = new ArrayList<>();
        four.add("4");
        four.add("04");
        map.put("04",four);

        ArrayList<String> five = new ArrayList<>();
        five.add("5");
        five.add("05");
        map.put("05",five);

        ArrayList<String> six = new ArrayList<>();
        six.add("6");
        six.add("06");
        map.put("06",six);

        ArrayList<String> seven = new ArrayList<>();
        seven.add("7");
        seven.add("07");
        map.put("07",seven);

        ArrayList<String> eight = new ArrayList<>();
        eight.add("8");
        eight.add("08");
        map.put("08",eight);

        ArrayList<String> nine = new ArrayList<>();
        nine.add("9");
        nine.add("09");
        map.put("09",nine);

        ArrayList<String> ten = new ArrayList<>();
        ten.add("10");
        map.put("10",ten);

        ArrayList<String> eleven = new ArrayList<>();
        eleven.add("11");
        map.put("11",eleven);

        ArrayList<String> twelve = new ArrayList<>();
        twelve.add("12");
        map.put("12",twelve);

        ArrayList<String> thirteen = new ArrayList<>();
        thirteen.add("13");
        map.put("13",thirteen);

        ArrayList<String> fourteen = new ArrayList<>();
        fourteen.add("14");
        map.put("14",fourteen);

        ArrayList<String> fifteen = new ArrayList<>();
        fifteen.add("15");
        map.put("15",fifteen);

        ArrayList<String> sixteen = new ArrayList<>();
        sixteen.add("16");
        map.put("16",sixteen);

        ArrayList<String> seventeen = new ArrayList<>();
        seventeen.add("17");
        map.put("17",seventeen);

        ArrayList<String> eighteen = new ArrayList<>();
        eighteen.add("18");
        map.put("18",eighteen);

        ArrayList<String> nineteen = new ArrayList<>();
        nineteen.add("19");
        map.put("19",nineteen);

        ArrayList<String> twenty = new ArrayList<>();
        twenty.add("20");
        map.put("20",twenty);

        ArrayList<String> twentyOne = new ArrayList<>();
        twentyOne.add("21");
        map.put("21",twentyOne);

        ArrayList<String> twentyTwo = new ArrayList<>();
        twentyTwo.add("22");
        map.put("22",twentyTwo);

        ArrayList<String> twentyThree = new ArrayList<>();
        twentyThree.add("23");
        map.put("23",twentyThree);

        ArrayList<String> twentyFour = new ArrayList<>();
        twentyFour.add("24");
        map.put("24",twentyFour);

        ArrayList<String> twentyFive = new ArrayList<>();
        twentyFive.add("25");
        map.put("25",twentyFive);

        ArrayList<String> twentySix = new ArrayList<>();
        twentySix.add("26");
        map.put("26",twentySix);

        ArrayList<String> twentySeven = new ArrayList<>();
        twentySeven.add("27");
        map.put("27",twentySeven);

        ArrayList<String> twentyEight = new ArrayList<>();
        twentyEight.add("28");
        map.put("28",twentyEight);

        ArrayList<String> twentyNine = new ArrayList<>();
        twentyNine.add("29");
        map.put("29",twentyNine);

        ArrayList<String> thirty = new ArrayList<>();
        thirty.add("30");
        map.put("30",thirty);

        ArrayList<String> thirtyOne = new ArrayList<>();
        thirtyOne.add("31");
        map.put("31",thirtyOne);

        return map;
    }

    //TODO se i mesi a parole li scrivo in string.xml??
    public Map<String,ArrayList<String>> getAllMonth(){
        HashMap<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> genn = new ArrayList<>();
        genn.add("1");
        genn.add("01");
        genn.add("gennaio");
        map.put("01",genn);

        ArrayList<String> febb = new ArrayList<>();
        febb.add("2");
        febb.add("02");
        febb.add("febbraio");
        map.put("02",febb);

        ArrayList<String> march = new ArrayList<>();
        march.add("3");
        march.add("03");
        march.add("marzo");
        map.put("03",march);

        ArrayList<String> april = new ArrayList<>();
        april.add("4");
        april.add("04");
        april.add("aprile");
        map.put("04",april);

        ArrayList<String> may = new ArrayList<>();
        may.add("5");
        may.add("05");
        may.add("maggio");
        map.put("05",may);

        ArrayList<String> june = new ArrayList<>();
        june.add("6");
        june.add("06");
        june.add("giugno");
        map.put("06",june);

        ArrayList<String> july = new ArrayList<>();
        july.add("7");
        july.add("07");
        july.add("luglio");
        map.put("07",july);

        ArrayList<String> august = new ArrayList<>();
        august.add("8");
        august.add("08");
        august.add("august");
        map.put("08",august);

        ArrayList<String> september = new ArrayList<>();
        september.add("9");
        september.add("09");
        september.add("settembre");
        map.put("09",september);

        ArrayList<String> october = new ArrayList<>();
        october.add("10");
        october.add("ottobre");
        map.put("10",october);

        ArrayList<String> november = new ArrayList<>();
        november.add("11");
        november.add("novembre");
        map.put("11",november);

        ArrayList<String> dicember = new ArrayList<>();
        dicember.add("12");
        dicember.add("dicembre");
        map.put("12",dicember);

        return map;
    }

    //TODO in questo modo è da aggiornare ogni anno
    public Map<String,ArrayList<String>> getAllYear(){
        HashMap<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> thisYear = new ArrayList<>();
        thisYear.add("17");
        thisYear.add("2017");
        map.put("17",thisYear);

        ArrayList<String> nextYear = new ArrayList<>();
        nextYear.add("18");
        nextYear.add("2018");
        map.put("18",nextYear);

        return map;
    }

    public String getCurrentYear(){
        return "17";
    }
}
