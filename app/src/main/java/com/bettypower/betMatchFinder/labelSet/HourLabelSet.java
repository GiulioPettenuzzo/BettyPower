package com.bettypower.betMatchFinder.labelSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * rappresent all the possible hour and minute configuration
 * the minute is took only 5 minutes distance
 * Created by giuliopettenuzzo on 29/10/17.
 */

public class HourLabelSet {


    public Map<String,ArrayList<String>> getAllHour(){
        Map<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> zero = new ArrayList<>();
        zero.add("0");
        zero.add("00");
        map.put("00",zero);

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

        return map;
    }

    public Map<String,ArrayList<String>> getAllMinutes(){
        Map<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> zero = new ArrayList<>();
        zero.add("0");
        zero.add("00");
        map.put("00",zero);

        ArrayList<String> five = new ArrayList<>();
        five.add("5");
        five.add("05");
        map.put("05",five);

        ArrayList<String> ten = new ArrayList<>();
        ten.add("10");
        map.put("10",ten);

        ArrayList<String> fifteen = new ArrayList<>();
        fifteen.add("15");
        map.put("15",fifteen);

        ArrayList<String> twenty = new ArrayList<>();
        twenty.add("20");
        map.put("20",twenty);

        ArrayList<String> twentyFive = new ArrayList<>();
        twentyFive.add("25");
        map.put("25",twentyFive);

        ArrayList<String> thirty = new ArrayList<>();
        thirty.add("30");
        map.put("30",thirty);

        ArrayList<String> thirtyFive = new ArrayList<>();
        thirtyFive.add("35");
        map.put("35",thirtyFive);

        ArrayList<String> forty = new ArrayList<>();
        forty.add("40");
        map.put("40",forty);

        ArrayList<String> fortyFive = new ArrayList<>();
        fortyFive.add("45");
        map.put("45",fortyFive);

        ArrayList<String> fifty = new ArrayList<>();
        fifty.add("50");
        map.put("50",fifty);

        ArrayList<String> fiftyFive = new ArrayList<>();
        fiftyFive.add("55");
        map.put("55",fiftyFive);

        ArrayList<String> sixty = new ArrayList<>();
        sixty.add("60");
        map.put("60",sixty);

        return map;
    }

}
