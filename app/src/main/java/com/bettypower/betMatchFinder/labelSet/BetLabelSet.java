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

        ArrayList<String> bet0 = new ArrayList<>();
        bet0.add("Si");
        map.put("Si" , bet0);

        ArrayList<String> bet1 = new ArrayList<>();
        bet1.add("No");
        map.put("No" , bet1);

        ArrayList<String> bet2 = new ArrayList<>();
        bet2.add("1-0");
        map.put("1-0" , bet2);

        ArrayList<String> bet3 = new ArrayList<>();
        bet3.add("2-0");
        map.put("2-0" , bet3);

        ArrayList<String> bet4 = new ArrayList<>();
        bet4.add("2-1");
        map.put("2-1" , bet4);

        ArrayList<String> bet5 = new ArrayList<>();
        bet5.add("3-0");
        map.put("3-0" , bet5);

        ArrayList<String> bet6 = new ArrayList<>();
        bet6.add("3-1");
        map.put("3-1" , bet6);

        ArrayList<String> bet7 = new ArrayList<>();
        bet7.add("3-2");
        map.put("3-2" , bet7);

        ArrayList<String> bet8 = new ArrayList<>();
        bet8.add("4-0");
        map.put("4-0" , bet8);

        ArrayList<String> bet9 = new ArrayList<>();
        bet9.add("4-1");
        map.put("4-1" , bet9);

        ArrayList<String> bet10 = new ArrayList<>();
        bet10.add("4-2");
        map.put("4-2" , bet10);

        ArrayList<String> bet11 = new ArrayList<>();
        bet11.add("4-3");
        map.put("4-3" , bet11);

        ArrayList<String> bet12 = new ArrayList<>();
        bet12.add("0-0");
        map.put("0-0" , bet12);

        ArrayList<String> bet13 = new ArrayList<>();
        bet13.add("1-1");
        map.put("1-1" , bet13);

        ArrayList<String> bet14 = new ArrayList<>();
        bet14.add("2-2");
        map.put("2-2" , bet14);

        ArrayList<String> bet15 = new ArrayList<>();
        bet15.add("3-3");
        map.put("3-3" , bet15);

        ArrayList<String> bet16 = new ArrayList<>();
        bet16.add("4-4");
        map.put("4-4" , bet16);

        ArrayList<String> bet17 = new ArrayList<>();
        bet17.add("0-1");
        map.put("0-1" , bet17);

        ArrayList<String> bet18 = new ArrayList<>();
        bet18.add("0-2");
        map.put("0-2" , bet18);

        ArrayList<String> bet19 = new ArrayList<>();
        bet19.add("1-2");
        map.put("1-2" , bet19);

        ArrayList<String> bet20 = new ArrayList<>();
        bet20.add("0-3");
        map.put("0-3" , bet20);

        ArrayList<String> bet21 = new ArrayList<>();
        bet21.add("1-3");
        map.put("1-3" , bet21);

        ArrayList<String> bet22 = new ArrayList<>();
        bet22.add("2-3");
        map.put("2-3" , bet22);

        ArrayList<String> bet23 = new ArrayList<>();
        bet23.add("0-4");
        map.put("0-4" , bet23);

        ArrayList<String> bet24 = new ArrayList<>();
        bet24.add("1-4");
        map.put("1-4" , bet24);

        ArrayList<String> bet25 = new ArrayList<>();
        bet25.add("2-4");
        map.put("2-4" , bet25);

        ArrayList<String> bet26 = new ArrayList<>();
        bet26.add("3-4");
        map.put("3-4" , bet26);

        ArrayList<String> bet27 = new ArrayList<>();
        bet27.add("1-2 Gol");
        map.put("1-2 Gol" , bet27);

        ArrayList<String> bet28 = new ArrayList<>();
        bet28.add("1-3 Gol");
        map.put("1-3 Gol" , bet28);

        ArrayList<String> bet29 = new ArrayList<>();
        bet29.add("1-4 Gol");
        map.put("1-4 Gol" , bet29);

        ArrayList<String> bet30 = new ArrayList<>();
        bet30.add("1-5 Gol");
        map.put("1-5 Gol" , bet30);

        ArrayList<String> bet31 = new ArrayList<>();
        bet31.add("1-6 Gol");
        map.put("1-6 Gol" , bet31);

        ArrayList<String> bet32 = new ArrayList<>();
        bet32.add("2-3 Gol");
        map.put("2-3 Gol" , bet32);

        ArrayList<String> bet33 = new ArrayList<>();
        bet33.add("2-4 Gol");
        map.put("2-4 Gol" , bet33);

        ArrayList<String> bet34 = new ArrayList<>();
        bet34.add("2-5 Gol");
        map.put("2-5 Gol" , bet34);

        ArrayList<String> bet35 = new ArrayList<>();
        bet35.add("2-6 Gol");
        map.put("2-6 Gol" , bet35);

        ArrayList<String> bet36 = new ArrayList<>();
        bet36.add("3-4 Gol");
        map.put("3-4 Gol" , bet36);

        ArrayList<String> bet37 = new ArrayList<>();
        bet37.add("3-5 Gol");
        map.put("3-5 Gol" , bet37);

        ArrayList<String> bet38 = new ArrayList<>();
        bet38.add("3-6 Gol");
        map.put("3-6 Gol" , bet38);

        ArrayList<String> bet39 = new ArrayList<>();
        bet39.add("4-5 Gol");
        map.put("4-5 Gol" , bet39);

        ArrayList<String> bet40 = new ArrayList<>();
        bet40.add("4-6 Gol");
        map.put("4-6 Gol" , bet40);

        ArrayList<String> bet41 = new ArrayList<>();
        bet41.add("5-6 Gol");
        map.put("5-6 Gol" , bet41);

        ArrayList<String> bet42 = new ArrayList<>();
        bet42.add("7+ Gol");
        map.put("7+ Gol" , bet42);

        ArrayList<String> bet43 = new ArrayList<>();
        bet43.add("Dispari");
        map.put("Dispari" , bet43);

        ArrayList<String> bet44 = new ArrayList<>();
        bet44.add("Pari");
        map.put("Pari" , bet44);

        ArrayList<String> bet45 = new ArrayList<>();
        bet45.add("Dispari 1T");
        map.put("Dispari 1T" , bet45);

        ArrayList<String> bet46 = new ArrayList<>();
        bet46.add("Pari 1T");
        map.put("Pari 1T" , bet46);

        ArrayList<String> bet47 = new ArrayList<>();
        bet47.add("Goal");
        map.put("Goal" , bet47);

        ArrayList<String> bet48 = new ArrayList<>();
        bet48.add("No Goal");
        map.put("No Goal" , bet48);

        ArrayList<String> bet49 = new ArrayList<>();
        bet49.add("Primo Tempo");
        map.put("Primo Tempo" , bet49);

        ArrayList<String> bet50 = new ArrayList<>();
        bet50.add("Secondo Tempo");
        map.put("Secondo Tempo" , bet50);

        ArrayList<String> bet51 = new ArrayList<>();
        bet51.add("1-2 Gol Casa");
        map.put("1-2 Gol Casa" , bet51);

        ArrayList<String> bet52 = new ArrayList<>();
        bet52.add("1-3 Gol Casa");
        map.put("1-3 Gol Casa" , bet52);

        ArrayList<String> bet53 = new ArrayList<>();
        bet53.add("1-4 Gol Casa");
        map.put("1-4 Gol Casa" , bet53);

        ArrayList<String> bet54 = new ArrayList<>();
        bet54.add("1-5 Gol Casa");
        map.put("1-5 Gol Casa" , bet54);

        ArrayList<String> bet55 = new ArrayList<>();
        bet55.add("2-3 Gol Casa");
        map.put("2-3 Gol Casa" , bet55);

        ArrayList<String> bet56 = new ArrayList<>();
        bet56.add("2-4 Gol Casa");
        map.put("2-4 Gol Casa" , bet56);

        ArrayList<String> bet57 = new ArrayList<>();
        bet57.add("2-5 Gol Casa");
        map.put("2-5 Gol Casa" , bet57);

        ArrayList<String> bet58 = new ArrayList<>();
        bet58.add("1-2 Gol Trasferta");
        map.put("1-2 Gol Trasferta" , bet58);

        ArrayList<String> bet59 = new ArrayList<>();
        bet59.add("1-3 Gol Trasferta");
        map.put("1-3 Gol Trasferta" , bet59);

        ArrayList<String> bet60 = new ArrayList<>();
        bet60.add("1-4 Gol Trasferta");
        map.put("1-4 Gol Trasferta" , bet60);

        ArrayList<String> bet61 = new ArrayList<>();
        bet61.add("1-5 Gol Trasferta");
        map.put("1-5 Gol Trasferta" , bet61);

        ArrayList<String> bet62 = new ArrayList<>();
        bet62.add("2-3 Gol Trasferta");
        map.put("2-3 Gol Trasferta" , bet62);

        ArrayList<String> bet63 = new ArrayList<>();
        bet63.add("2-4 Gol Trasferta");
        map.put("2-4 Gol Trasferta" , bet63);

        ArrayList<String> bet64 = new ArrayList<>();
        bet64.add("2-5 Gol Trasferta");
        map.put("2-5 Gol Trasferta" , bet64);

        ArrayList<String> bet65 = new ArrayList<>();
        bet65.add("1-2 Gol Primo Tempo");
        map.put("1-2 Gol Primo Tempo" , bet65);

        ArrayList<String> bet66 = new ArrayList<>();
        bet66.add("1-3 Gol Primo Tempo");
        map.put("1-3 Gol Primo Tempo" , bet66);

        ArrayList<String> bet67 = new ArrayList<>();
        bet67.add("2-3 Gol Primo Tempo");
        map.put("2-3 Gol Primo Tempo" , bet67);

        ArrayList<String> bet68 = new ArrayList<>();
        bet68.add("1-2 Gol Secondo Tempo");
        map.put("1-2 Gol Secondo Tempo" , bet68);

        ArrayList<String> bet69 = new ArrayList<>();
        bet69.add("1-3 Gol Secondo Tempo");
        map.put("1-3 Gol Secondo Tempo" , bet69);

        ArrayList<String> bet70 = new ArrayList<>();
        bet70.add("2-3 Gol Secondo Tempo");
        map.put("2-3 Gol Secondo Tempo" , bet70);

        ArrayList<String> bet71 = new ArrayList<>();
        bet71.add("Gol");
        map.put("Gol" , bet71);

        ArrayList<String> bet72 = new ArrayList<>();
        bet72.add("NoGol");
        map.put("NoGol" , bet72);

        ArrayList<String> bet73 = new ArrayList<>();
        bet73.add("Gol & Over 2,5");
        map.put("Gol & Over 2,5" , bet73);

        ArrayList<String> bet74 = new ArrayList<>();
        bet74.add("Gol & Under 2,5");
        map.put("Gol & Under 2,5" , bet74);

        ArrayList<String> bet75 = new ArrayList<>();
        bet75.add("NoGol & Over 2,5");
        map.put("NoGol & Over 2,5" , bet75);

        ArrayList<String> bet76 = new ArrayList<>();
        bet76.add("NoGol & Under 2,5");
        map.put("NoGol & Under 2,5" , bet76);

        ArrayList<String> bet77 = new ArrayList<>();
        bet77.add("1 / 1");
        map.put("1 / 1" , bet77);

        ArrayList<String> bet78 = new ArrayList<>();
        bet78.add("1 / X");
        map.put("1 / X" , bet78);

        ArrayList<String> bet79 = new ArrayList<>();
        bet79.add("1 / 2");
        map.put("1 / 2" , bet79);

        ArrayList<String> bet80 = new ArrayList<>();
        bet80.add("X / 1");
        map.put("X / 1" , bet80);

        ArrayList<String> bet81 = new ArrayList<>();
        bet81.add("X / X");
        map.put("X / X" , bet81);

        ArrayList<String> bet82 = new ArrayList<>();
        bet82.add("X / 2");
        map.put("X / 2" , bet82);

        ArrayList<String> bet83 = new ArrayList<>();
        bet83.add("2 / 1");
        map.put("2 / 1" , bet83);

        ArrayList<String> bet84 = new ArrayList<>();
        bet84.add("2 / X");
        map.put("2 / X" , bet84);

        ArrayList<String> bet85 = new ArrayList<>();
        bet85.add("2 / 2");
        map.put("2 / 2" , bet85);

        ArrayList<String> bet86 = new ArrayList<>();
        bet86.add("1 & Under 1.5");
        map.put("1 & Under 1.5" , bet86);

        ArrayList<String> bet87 = new ArrayList<>();
        bet87.add("1 & Over 1.5");
        map.put("1 & Over 1.5" , bet87);

        ArrayList<String> bet88 = new ArrayList<>();
        bet88.add("X & Under 1.5");
        map.put("X & Under 1.5" , bet88);

        ArrayList<String> bet89 = new ArrayList<>();
        bet89.add("X & Over 1.5");
        map.put("X & Over 1.5" , bet89);

        ArrayList<String> bet90 = new ArrayList<>();
        bet90.add("2 & Under 1.5");
        map.put("2 & Under 1.5" , bet90);

        ArrayList<String> bet91 = new ArrayList<>();
        bet91.add("2 & Over 1.5");
        map.put("2 & Over 1.5" , bet91);

        ArrayList<String> bet92 = new ArrayList<>();
        bet92.add("1 & Under 2.5");
        map.put("1 & Under 2.5" , bet92);

        ArrayList<String> bet93 = new ArrayList<>();
        bet93.add("1 & Over 2.5");
        map.put("1 & Over 2.5" , bet93);

        ArrayList<String> bet94 = new ArrayList<>();
        bet94.add("X & Under 2.5");
        map.put("X & Under 2.5" , bet94);

        ArrayList<String> bet95 = new ArrayList<>();
        bet95.add("X & Over 2.5");
        map.put("X & Over 2.5" , bet95);

        ArrayList<String> bet96 = new ArrayList<>();
        bet96.add("2 & Under 2.5");
        map.put("2 & Under 2.5" , bet96);

        ArrayList<String> bet97 = new ArrayList<>();
        bet97.add("2 & Over 2.5");
        map.put("2 & Over 2.5" , bet97);

        ArrayList<String> bet98 = new ArrayList<>();
        bet98.add("1 & Under 3.5");
        map.put("1 & Under 3.5" , bet98);

        ArrayList<String> bet99 = new ArrayList<>();
        bet99.add("1 & Over 3.5");
        map.put("1 & Over 3.5" , bet99);

        ArrayList<String> bet100 = new ArrayList<>();
        bet100.add("X & Under 3.5");
        map.put("X & Under 3.5" , bet100);

        ArrayList<String> bet101 = new ArrayList<>();
        bet101.add("X & Over 3.5");
        map.put("X & Over 3.5" , bet101);

        ArrayList<String> bet102 = new ArrayList<>();
        bet102.add("2 & Under 3.5");
        map.put("2 & Under 3.5" , bet102);

        ArrayList<String> bet103 = new ArrayList<>();
        bet103.add("2 & Over 3.5");
        map.put("2 & Over 3.5" , bet103);

        ArrayList<String> bet104 = new ArrayList<>();
        bet104.add("1 & Under 4.5");
        map.put("1 & Under 4.5" , bet104);

        ArrayList<String> bet105 = new ArrayList<>();
        bet105.add("1 & Over 4.5");
        map.put("1 & Over 4.5" , bet105);

        ArrayList<String> bet106 = new ArrayList<>();
        bet106.add("X & Under 4.5");
        map.put("X & Under 4.5" , bet106);

        ArrayList<String> bet107 = new ArrayList<>();
        bet107.add("X & Over 4.5");
        map.put("X & Over 4.5" , bet107);

        ArrayList<String> bet108 = new ArrayList<>();
        bet108.add("2 & Under 4.5");
        map.put("2 & Under 4.5" , bet108);

        ArrayList<String> bet109 = new ArrayList<>();
        bet109.add("2 & Over 4.5");
        map.put("2 & Over 4.5" , bet109);

        ArrayList<String> bet110 = new ArrayList<>();
        bet110.add("1X");
        map.put("1X" , bet110);

        ArrayList<String> bet111 = new ArrayList<>();
        bet111.add("12");
        map.put("12" , bet111);

        ArrayList<String> bet112 = new ArrayList<>();
        bet112.add("X2");
        map.put("X2" , bet112);

        ArrayList<String> bet113 = new ArrayList<>();
        bet113.add("1 & Gol");
        map.put("1 & Gol" , bet113);

        ArrayList<String> bet114 = new ArrayList<>();
        bet114.add("1 & NoGol");
        map.put("1 & NoGol" , bet114);

        ArrayList<String> bet115 = new ArrayList<>();
        bet115.add("X & Gol");
        map.put("X & Gol" , bet115);

        ArrayList<String> bet116 = new ArrayList<>();
        bet116.add("X & NoGol");
        map.put("X & NoGol" , bet116);

        ArrayList<String> bet117 = new ArrayList<>();
        bet117.add("2 & Gol");
        map.put("2 & Gol" , bet117);

        ArrayList<String> bet118 = new ArrayList<>();
        bet118.add("2 & NoGol");
        map.put("2 & NoGol" , bet118);

        ArrayList<String> bet119 = new ArrayList<>();
        bet119.add("1X & Under 1.5");
        map.put("1X & Under 1.5" , bet119);

        ArrayList<String> bet120 = new ArrayList<>();
        bet120.add("1X & Over 1.5");
        map.put("1X & Over 1.5" , bet120);

        ArrayList<String> bet121 = new ArrayList<>();
        bet121.add("X2 & Under 1.5");
        map.put("X2 & Under 1.5" , bet121);

        ArrayList<String> bet122 = new ArrayList<>();
        bet122.add("X2 & Over 1.5");
        map.put("X2 & Over 1.5" , bet122);

        ArrayList<String> bet123 = new ArrayList<>();
        bet123.add("12 & Under 1.5");
        map.put("12 & Under 1.5" , bet123);

        ArrayList<String> bet124 = new ArrayList<>();
        bet124.add("12 & Over 1.5");
        map.put("12 & Over 1.5" , bet124);

        ArrayList<String> bet125 = new ArrayList<>();
        bet125.add("1X & Under 2.5");
        map.put("1X & Under 2.5" , bet125);

        ArrayList<String> bet126 = new ArrayList<>();
        bet126.add("1X & Over 2.5");
        map.put("1X & Over 2.5" , bet126);

        ArrayList<String> bet127 = new ArrayList<>();
        bet127.add("X2 & Under 2.5");
        map.put("X2 & Under 2.5" , bet127);

        ArrayList<String> bet128 = new ArrayList<>();
        bet128.add("X2 & Over 2.5");
        map.put("X2 & Over 2.5" , bet128);

        ArrayList<String> bet129 = new ArrayList<>();
        bet129.add("12 & Under 2.5");
        map.put("12 & Under 2.5" , bet129);

        ArrayList<String> bet130 = new ArrayList<>();
        bet130.add("12 & Over 2.5");
        map.put("12 & Over 2.5" , bet130);

        ArrayList<String> bet131 = new ArrayList<>();
        bet131.add("1X & Under 3.5");
        map.put("1X & Under 3.5" , bet131);

        ArrayList<String> bet132 = new ArrayList<>();
        bet132.add("1X & Over 3.5");
        map.put("1X & Over 3.5" , bet132);

        ArrayList<String> bet133 = new ArrayList<>();
        bet133.add("X2 & Under 3.5");
        map.put("X2 & Under 3.5" , bet133);

        ArrayList<String> bet134 = new ArrayList<>();
        bet134.add("X2 & Over 3.5");
        map.put("X2 & Over 3.5" , bet134);

        ArrayList<String> bet135 = new ArrayList<>();
        bet135.add("12 & Under 3.5");
        map.put("12 & Under 3.5" , bet135);

        ArrayList<String> bet136 = new ArrayList<>();
        bet136.add("12 & Over 3.5");
        map.put("12 & Over 3.5" , bet136);

        ArrayList<String> bet137 = new ArrayList<>();
        bet137.add("1X & Under 4.5");
        map.put("1X & Under 4.5" , bet137);

        ArrayList<String> bet138 = new ArrayList<>();
        bet138.add("1X & Over 4.5");
        map.put("1X & Over 4.5" , bet138);

        ArrayList<String> bet139 = new ArrayList<>();
        bet139.add("X2 & Under 4.5");
        map.put("X2 & Under 4.5" , bet139);

        ArrayList<String> bet140 = new ArrayList<>();
        bet140.add("X2 & Over 4.5");
        map.put("X2 & Over 4.5" , bet140);

        ArrayList<String> bet141 = new ArrayList<>();
        bet141.add("12 & Under 4.5");
        map.put("12 & Under 4.5" , bet141);

        ArrayList<String> bet142 = new ArrayList<>();
        bet142.add("12 & Over 4.5");
        map.put("12 & Over 4.5" , bet142);

        ArrayList<String> bet143 = new ArrayList<>();
        bet143.add("Under 0,5");
        map.put("Under 0,5" , bet143);

        ArrayList<String> bet144 = new ArrayList<>();
        bet144.add("Over 0,5");
        map.put("Over 0,5" , bet144);

        ArrayList<String> bet145 = new ArrayList<>();
        bet145.add("Under 1,5");
        map.put("Under 1,5" , bet145);

        ArrayList<String> bet146 = new ArrayList<>();
        bet146.add("Over 1,5");
        map.put("Over 1,5" , bet146);

        ArrayList<String> bet147 = new ArrayList<>();
        bet147.add("Under 2,5");
        map.put("Under 2,5" , bet147);

        ArrayList<String> bet148 = new ArrayList<>();
        bet148.add("Over 2,5");
        map.put("Over 2,5" , bet148);

        ArrayList<String> bet149 = new ArrayList<>();
        bet149.add("Under 3,5");
        map.put("Under 3,5" , bet149);

        ArrayList<String> bet150 = new ArrayList<>();
        bet150.add("Over 3,5");
        map.put("Over 3,5" , bet150);

        ArrayList<String> bet151 = new ArrayList<>();
        bet151.add("Under 4,5");
        map.put("Under 4,5" , bet151);

        ArrayList<String> bet152 = new ArrayList<>();
        bet152.add("Over 4,5");
        map.put("Over 4,5" , bet152);

        ArrayList<String> bet153 = new ArrayList<>();
        bet153.add("Under 5,5");
        map.put("Under 5,5" , bet153);

        ArrayList<String> bet154 = new ArrayList<>();
        bet154.add("Over 5,5");
        map.put("Over 5,5" , bet154);

        ArrayList<String> bet155 = new ArrayList<>();
        bet155.add("Under 6,5");
        map.put("Under 6,5" , bet155);

        ArrayList<String> bet156 = new ArrayList<>();
        bet156.add("Over 6,5");
        map.put("Over 6,5" , bet156);

        ArrayList<String> bet157 = new ArrayList<>();
        bet157.add("Under 7,5");
        map.put("Under 7,5" , bet157);

        ArrayList<String> bet158 = new ArrayList<>();
        bet158.add("Over 7,5");
        map.put("Over 7,5" , bet158);

        ArrayList<String> bet159 = new ArrayList<>();
        bet159.add("1");
        map.put("1" , bet159);

        ArrayList<String> bet160 = new ArrayList<>();
        bet160.add("X");
        map.put("X" , bet160);

        ArrayList<String> bet161 = new ArrayList<>();
        bet161.add("2");
        map.put("2" , bet161);

        ArrayList<String> bet162 = new ArrayList<>();
        bet162.add("1 (0:1)");
        map.put("1 (0:1)" , bet162);

        ArrayList<String> bet163 = new ArrayList<>();
        bet163.add("X (0:1)");
        map.put("X (0:1)" , bet163);

        ArrayList<String> bet164 = new ArrayList<>();
        bet164.add("2 (0:1)");
        map.put("2 (0:1)" , bet164);

        ArrayList<String> bet165 = new ArrayList<>();
        bet165.add("1 (1:0)");
        map.put("1 (1:0)" , bet165);

        ArrayList<String> bet166 = new ArrayList<>();
        bet166.add("X (1:0)");
        map.put("X (1:0)" , bet166);

        ArrayList<String> bet167 = new ArrayList<>();
        bet167.add("2 (1:0)");
        map.put("2 (1:0)" , bet167);

        ArrayList<String> bet168 = new ArrayList<>();
        bet168.add("1 (0:2)");
        map.put("1 (0:2)" , bet168);

        ArrayList<String> bet169 = new ArrayList<>();
        bet169.add("X (0:2)");
        map.put("X (0:2)" , bet169);

        ArrayList<String> bet170 = new ArrayList<>();
        bet170.add("2 (0:2)");
        map.put("2 (0:2)" , bet170);

        ArrayList<String> bet171 = new ArrayList<>();
        bet171.add("1 (2:0)");
        map.put("1 (2:0)" , bet171);

        ArrayList<String> bet172 = new ArrayList<>();
        bet172.add("X (2:0)");
        map.put("X (2:0)" , bet172);

        ArrayList<String> bet173 = new ArrayList<>();
        bet173.add("2 (2:0)");
        map.put("2 (2:0)" , bet173);

        ArrayList<String> bet174 = new ArrayList<>();
        bet174.add("1X & Gol");
        map.put("1X & Gol" , bet174);

        ArrayList<String> bet175 = new ArrayList<>();
        bet175.add("1X & NoGol");
        map.put("1X & NoGol" , bet175);

        ArrayList<String> bet176 = new ArrayList<>();
        bet176.add("X2 & Gol");
        map.put("X2 & Gol" , bet176);

        ArrayList<String> bet177 = new ArrayList<>();
        bet177.add("X2 & NoGol");
        map.put("X2 & NoGol" , bet177);

        ArrayList<String> bet178 = new ArrayList<>();
        bet178.add("12 & Gol");
        map.put("12 & Gol" , bet178);

        ArrayList<String> bet179 = new ArrayList<>();
        bet179.add("12 & NoGol");
        map.put("12 & NoGol" , bet179);

        ArrayList<String> bet180 = new ArrayList<>();
        bet180.add("0");
        map.put("0" , bet180);

        ArrayList<String> bet181 = new ArrayList<>();
        bet181.add("3");
        map.put("3" , bet181);

        ArrayList<String> bet182 = new ArrayList<>();
        bet182.add("4");
        map.put("4" , bet182);

        ArrayList<String> bet183 = new ArrayList<>();
        bet183.add("5");
        map.put("5" , bet183);

        ArrayList<String> bet184 = new ArrayList<>();
        bet184.add("5+");
        map.put("5+" , bet184);

        ArrayList<String> bet185 = new ArrayList<>();
        bet185.add("6");
        map.put("6" , bet185);

        ArrayList<String> bet186 = new ArrayList<>();
        bet186.add("7");
        map.put("7" , bet186);

        ArrayList<String> bet187 = new ArrayList<>();
        bet187.add("7+");
        map.put("7+" , bet187);

        return map;
    }

    public Map<String,ArrayList<String>> getAllBetKind(){
        Map<String,ArrayList<String>> map = new HashMap<>();

        ArrayList<String> one = new ArrayList<>();
        one.add("1x2");
        one.add("1X2");
        one.add("esito finale 1x2");
        map.put("1x2",one);

        ArrayList<String> arrayListTwo = new ArrayList<>();
        arrayListTwo.add("Doppia Chance");
        map.put("Doppia Chance",arrayListTwo);

        ArrayList<String> two = new ArrayList<>();
        two.add("goal/no goal");
        map.put("goal/nogoal",two);

        ArrayList<String> underOver = new ArrayList<>();
        underOver.add("under / over");
        map.put("under/over",underOver);

        ArrayList<String> handicap = new ArrayList<>();
        handicap.add("Handicap");
        map.put("Handicap",handicap);

        ArrayList<String> arrayListSix = new ArrayList<>();
        arrayListSix.add("Pari / Dispari");
        map.put("Pari / Dispari",arrayListSix);

        ArrayList<String> arrayListSeven = new ArrayList<>();
        arrayListSeven.add("Primo Tempo");
        map.put("Primo Tempo",arrayListSeven);

        ArrayList<String> arrayListEight = new ArrayList<>();
        arrayListEight.add("Primo Tempo / Secondo Tempo");
        map.put("Primo Tempo / Secondo Tempo",arrayListEight);

        ArrayList<String> arrayListNine = new ArrayList<>();
        arrayListNine.add("Risultato Finale");
        map.put("Risultato Finale",arrayListNine);

        ArrayList<String> arrayListTen = new ArrayList<>();
        arrayListTen.add("Gol Totali");
        map.put("Gol Totali",arrayListTen);

        ArrayList<String> arrayListEleven = new ArrayList<>();
        arrayListEleven.add("Segna Squadra In Casa");
        map.put("Segna Squadra In Casa",arrayListEleven);

        ArrayList<String> arrayListTwelve = new ArrayList<>();
        arrayListTwelve.add("Segna squadra fuori casa");
        map.put("Segna squadra fuori casa",arrayListTwelve);

        ArrayList<String> arrayListThirteen = new ArrayList<>();
        arrayListThirteen.add("Under / Over primo tempo");
        map.put("Under / Over primo tempo",arrayListThirteen);

        ArrayList<String> arrayListFourteen = new ArrayList<>();
        arrayListFourteen.add("Under / Over secondo tempo");
        map.put("Under / Over secondo tempo",arrayListFourteen);

        ArrayList<String> arrayListSixteen = new ArrayList<>();
        arrayListSixteen.add("Goal totali squadra in casa");
        map.put("Goal totali squadra in casa",arrayListSixteen);

        ArrayList<String> arrayListSeventeen = new ArrayList<>();
        arrayListSeventeen.add("Goal totali squadra fuori casa");
        map.put("Goal totali squadra fuori casa",arrayListSeventeen);

        ArrayList<String> arrayListEighteen = new ArrayList<>();
        arrayListEighteen.add("Squadra in casa vittoria a zero");
        map.put("Squadra in casa vittoria a zero",arrayListEighteen);

        ArrayList<String> arrayListNineteen = new ArrayList<>();
        arrayListNineteen.add("Squadra fuori casa vittoria a zero");
        map.put("Squadra fuori casa vittoria a zero",arrayListNineteen);

        ArrayList<String> arrayListTwenty = new ArrayList<>();
        arrayListTwenty.add("Goal / NoGoal primo tempo");
        map.put("Goal / NoGoal primo tempo",arrayListTwenty);

        ArrayList<String> arrayListTwentyOne = new ArrayList<>();
        arrayListTwentyOne.add("Goal / NoGoal secondo tempo");
        map.put("Goal / NoGoal secondo tempo",arrayListTwentyOne);

        ArrayList<String> arrayListTwentyTwo = new ArrayList<>();
        arrayListTwentyTwo.add("Tempo con piu gol");
        map.put("Tempo con piu gol",arrayListTwentyTwo);

        ArrayList<String> arrayListTwentyThree = new ArrayList<>();
        arrayListTwentyThree.add("Esito finale + Under / Over");
        map.put("Esito finale + Under / Over",arrayListTwentyThree);

        ArrayList<String> arrayListTwentyFour = new ArrayList<>();
        arrayListTwentyFour.add("Esito finale + Gol / NoGol");
        map.put("Esito finale + Gol / NoGol",arrayListTwentyFour);

        ArrayList<String> arrayListTwentyFive = new ArrayList<>();
        arrayListTwentyFive.add("Doppia Chance + Gol / NoGol");
        map.put("Doppia Chance + Gol / NoGol",arrayListTwentyFive);

        ArrayList<String> arrayListTwentySix = new ArrayList<>();
        arrayListTwentySix.add("Doppia chance + Under / Over");
        map.put("Doppia chance + Under / Over",arrayListTwentySix);

        ArrayList<String> arrayListTwentySeven = new ArrayList<>();
        arrayListTwentySeven.add("Gol / NoGol + Under / Over");
        map.put("Gol / NoGol + Under / Over",arrayListTwentySeven);

        ArrayList<String> arrayListTwentyEight = new ArrayList<>();
        arrayListTwentyEight.add("Multi Gol");
        map.put("Multi Gol",arrayListTwentyEight);

        ArrayList<String> arrayListTwentyNine = new ArrayList<>();
        arrayListTwentyNine.add("Multi Gol 1/2");
        map.put("Multi Gol 1/2",arrayListTwentyNine);

        ArrayList<String> arrayListThirty = new ArrayList<>();
        arrayListThirty.add("Multi Gol C/T");
        map.put("Multi Gol C/T",arrayListThirty);

        ArrayList<String> arrayListThirtyOne = new ArrayList<>();
        arrayListThirtyOne.add("Doppia Chance primo tempo");
        map.put("Doppia Chance primo tempo",arrayListThirtyOne);

        ArrayList<String> arrayListThirtyTwo = new ArrayList<>();
        arrayListThirtyTwo.add("Doppia Chance secondo tempo");
        map.put("Doppia Chance secondo tempo",arrayListThirtyTwo);

        ArrayList<String> arrayListThirtyThree = new ArrayList<>();
        arrayListThirtyThree.add("Squadra in casa segna entrambi i tempi");
        map.put("Squadra in casa segna entrambi i tempi",arrayListThirtyThree);

        ArrayList<String> arrayListThirtyFour = new ArrayList<>();
        arrayListThirtyFour.add("Squadra ospite segna entrambi i tempi");
        map.put("Squadra ospite segna entrambi i tempi",arrayListThirtyFour);

        ArrayList<String> arrayListThirtyFive = new ArrayList<>();
        arrayListThirtyFive.add("Rimborso in caso di pareggio");
        map.put("Rimborso in caso di pareggio",arrayListThirtyFive);

        ArrayList<String> arrayListThirtySix = new ArrayList<>();
        arrayListThirtySix.add("Squadra in casa vince entrambi i tempi");
        map.put("Squadra in casa vince entrambi i tempi",arrayListThirtySix);

        ArrayList<String> arrayListThirtySeven = new ArrayList<>();
        arrayListThirtySeven.add("Squadra ospite vince entrambi i tempi");
        map.put("Squadra ospite vince entrambi i tempi",arrayListThirtySeven);

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
        arrayListThree.add("Goal");
        arrayListThree.add("No Goal");
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
        arrayListSix.add("Pari 1T");
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
        map.put("Doppia Chance + Gol / NoGol",arrayListTwentyFive);

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
        map.put("Multi Gol",arrayListTwentyEight);

        ArrayList<String> arrayListTwentyNine = new ArrayList<>();
        arrayListTwentyNine.add("1-2 Gol Primo Tempo");
        arrayListTwentyNine.add("1-3 Gol Primo Tempo");
        arrayListTwentyNine.add("2-3 Gol Primo Tempo");
        arrayListTwentyNine.add("1-2 Gol Secondo Tempo");
        arrayListTwentyNine.add("1-3 Gol Secondo Tempo");
        arrayListTwentyNine.add("2-3 Gol Secondo Tempo");
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
}
