package com.bettypower.betChecker;

/**
 * Created by giuliopettenuzzo on 18/03/18.
 * Return always true if win, false if lost
 */

public class ConcreteChecker {

    private int homeResult;
    private int awayResult;
    private int firstTimeHomeResult;
    private int firstTimeAwayResult;
    private int secondTimeHomeResult;
    private int secondTimeAwayResult;



    public ConcreteChecker(int homeResult, int awayResult, int firstTimeHomeResult, int firstTimeAwayResult, int secondTimeHomeResult, int secondTimeAwayResult){
        this.homeResult = homeResult;
        this.awayResult = awayResult;
        this.firstTimeHomeResult = firstTimeHomeResult;
        this.firstTimeAwayResult = firstTimeAwayResult;
        this.secondTimeHomeResult = secondTimeHomeResult;
        this.secondTimeAwayResult = secondTimeAwayResult;
    }

    public boolean uno(){
        return homeResult > awayResult;
    }

    public boolean due(){
        return awayResult > homeResult;
    }

    public boolean x(){
        return homeResult == awayResult;
    }

    public boolean unoX(){
        return homeResult >= awayResult;
    }

    public boolean dueX(){
        return homeResult <= awayResult;
    }

    public boolean unoDue(){
        return homeResult != awayResult;
    }

    public boolean goal(){
        return homeResult > 0 && awayResult > 0;
    }

    public boolean noGoal(){
        return homeResult == 0 || awayResult == 0;
    }

    public boolean under05(){
        return (homeResult + awayResult) == 0;
    }

    public boolean over05(){
        return (homeResult + awayResult) > 0;
    }

    public boolean under15(){
        return (homeResult + awayResult) < 2;
    }

    public boolean over15(){
        return (homeResult + awayResult) >= 2;
    }

    public boolean under25(){
        return (homeResult + awayResult) < 3;
    }

    public boolean over25(){
        return (homeResult + awayResult) >= 3;
    }

    public boolean under35(){
        return (homeResult + awayResult) < 4;
    }

    public boolean over35(){
        return (homeResult + awayResult) >= 4;
    }

    public boolean under45(){
        return (homeResult + awayResult) < 5;
    }

    public boolean over45(){
        return (homeResult + awayResult) >= 5;
    }

    public boolean under55(){
        return (homeResult + awayResult) < 6;
    }

    public boolean over55(){
        return (homeResult + awayResult) >= 6;
    }

    public boolean under65(){
        return (homeResult + awayResult) < 7;
    }

    public boolean over65(){
        return (homeResult + awayResult) >= 7;
    }

    public boolean under75(){
        return (homeResult + awayResult) < 8;
    }

    public boolean over75(){
        return (homeResult + awayResult) >= 8;
    }

    public boolean handicap101() {
        awayResult = awayResult + 1;
        return uno();
    }

    public boolean handicapX01(){
        awayResult = awayResult + 1;
        return x();
    }

    public boolean handicap201(){
        awayResult = awayResult + 1;
        return due();
    }

    public boolean handicap110(){
        homeResult = homeResult + 1;
        return uno();
    }

    public boolean handicapX10(){
        homeResult = homeResult + 1;
        return x();
    }

    public boolean handicap210(){
        homeResult = homeResult + 1;
        return due();
    }

    public boolean handicap102() {
        awayResult = awayResult + 2;
        return uno();
    }

    public boolean handicapX02(){
        awayResult = awayResult + 2;
        return x();
    }

    public boolean handicap202(){
        awayResult = awayResult + 2;
        return due();
    }

    public boolean handicap120(){
        homeResult = homeResult + 2;
        return uno();
    }

    public boolean handicapX20(){
        homeResult = homeResult + 2;
        return x();
    }

    public boolean handicap220(){
        homeResult = homeResult + 2;
        return due();
    }

    public boolean dispari(){
        return (homeResult + awayResult) % 2 != 0;
    }

    public boolean pari(){
        return (homeResult + awayResult) % 2 == 0;
    }

    public boolean final10() {
        return homeResult == 1 && awayResult == 0;
    }

    public boolean final20() {
        return homeResult == 2 && awayResult == 0;
    }

    public boolean final21() {
        return homeResult == 2 && awayResult == 1;
    }

    public boolean final30() {
        return homeResult == 3 && awayResult == 0;
    }

    public boolean final31() {
        return homeResult == 3 && awayResult == 1;
    }

    public boolean final32() {
        return homeResult == 3 && awayResult == 2;
    }

    public boolean final40() {
        return homeResult == 4 && awayResult == 0;
    }

    public boolean final41() {
        return homeResult == 4 && awayResult == 1;
    }

    public boolean final42() {
        return homeResult == 4 && awayResult == 2;
    }

    public boolean final43() {
        return homeResult == 4 && awayResult == 3;
    }

    public boolean final00() {
        return homeResult == 0 && awayResult == 0;
    }

    public boolean final11() {
        return homeResult == 1 && awayResult == 1;
    }

    public boolean final22() {
        return homeResult == 2 && awayResult == 2;
    }

    public boolean final33() {
        return homeResult == 3 && awayResult == 3;
    }

    public boolean final44() {
        return homeResult == 4 && awayResult == 4;
    }

    public boolean final01() {
        return homeResult == 0 && awayResult == 1;
    }

    public boolean final02() {
        return homeResult == 0 && awayResult == 2;
    }

    public boolean final12() {
        return homeResult == 1 && awayResult == 2;
    }

    public boolean final03() {
        return homeResult == 0 && awayResult == 3;
    }

    public boolean final13() {
        return homeResult == 1 && awayResult == 3;
    }

    public boolean final23() {
        return homeResult == 2 && awayResult == 3;
    }

    public boolean final04() {
        return homeResult == 0 && awayResult == 4;
    }

    public boolean final14() {
        return homeResult == 1 && awayResult == 4;
    }

    public boolean final24() {
        return homeResult == 2 && awayResult == 4;
    }

    public boolean final34() {
        return homeResult == 3 && awayResult == 4;
    }

    public boolean tot0() {
        return homeResult + awayResult == 0;
    }

    public boolean tot1() {
        return homeResult + awayResult == 1;
    }

    public boolean tot2() {
        return homeResult + awayResult == 2;
    }

    public boolean tot3() {
        return homeResult + awayResult == 3;
    }

    public boolean tot4() {
        return homeResult + awayResult == 4;
    }

    public boolean tot5() {
        return homeResult + awayResult == 5;
    }

    public boolean tot5plus() {
        return homeResult + awayResult >= 5;
    }

    public boolean tot6() {
        return homeResult + awayResult == 6;
    }

    public boolean tot7() {
        return homeResult + awayResult == 7;
    }

    public boolean tot7plus() {
        return homeResult + awayResult >= 7;
    }

    public boolean segnaSquadraCasaSI() {
        return homeResult > 0;
    }

    public boolean segnaSquadraCasaNO() {
        return homeResult == 0;
    }

    public boolean segaSquadraOspiteSI() {
        return awayResult > 0;
    }

    public boolean segnaSquadraOspiteNO() {
        return awayResult == 0;
    }

    public boolean casaUnder15() {
        return homeResult < 2;
    }

    public boolean casaOver15() {
        return homeResult >1;
    }

    public boolean casaUnder25() {
        return homeResult < 3;
    }

    public boolean casaOver25() {
        return homeResult > 2;
    }

    public boolean casaUnder35() {
        return homeResult < 4;
    }

    public boolean casaOver35() {
        return homeResult > 3;
    }

    public boolean casaUnder45() {
        return homeResult < 5;
    }

    public boolean casaOver45() {
        return homeResult > 4;
    }

    public boolean casaUnder55() {
        return homeResult < 6;
    }

    public boolean casaOver55() {
        return homeResult > 5;
    }

    public boolean ospiteUnder15() {
        return awayResult < 2;
    }

    public boolean ospiteOver15() {
        return awayResult > 1;
    }

    public boolean ospiteUnder25() {
        return awayResult < 3;
    }

    public boolean ospiteOver25() {
        return awayResult > 2;
    }

    public boolean ospiteUnder35() {
        return awayResult < 4;
    }

    public boolean ospiteOver35() {
        return awayResult > 3;
    }

    public boolean ospiteUnder45() {
        return awayResult < 5;
    }

    public boolean ospiteOver45() {
        return awayResult > 4;
    }

    public boolean ospiteUnder55() {
        return awayResult < 6;
    }

    public boolean ospiteOver55() {
        return awayResult > 5;
    }

    public boolean casaVittoriaZeroSI() {
        return homeResult > 0 && awayResult == 0;
    }

    public boolean casaVittoriaZeroNO() {
        return uno() && awayResult > 0;
    }

    public boolean ospiteVittoriaZeroSI() {
        return awayResult > 0 && homeResult == 0;
    }

    public boolean ospiteVittoriaZeroNO() {
        return due() && homeResult > 0;
    }

    public boolean unoAndUnder15() {
        return uno() && under15();
    }

    public boolean unoAndOver15() {
        return uno() && over15();
    }

    public boolean xAndUnder15() {
        return x() && under15();
    }

    public boolean xAndOver15() {
        return x() && over15();
    }

    public boolean dueAndUnder15() {
        return due() && under15();
    }

    public boolean dueAndOver15() {
        return due() && over15();
    }

    public boolean unoAndUnder25() {
        return uno() && under25();
    }

    public boolean unoAndOver25() {
        return uno() && over25();
    }

    public boolean xAndUnder25() {
        return x() && under25();
    }

    public boolean xAndOver25() {
        return x() && over25();
    }

    public boolean dueAndUnder25() {
        return due() && under25();
    }

    public boolean dueAndOver25() {
        return due() && over25();
    }

    public boolean unoAndUnder35() {
        return uno() && under35();
    }

    public boolean unoAndOver35() {
        return uno() && over35();
    }

    public boolean xAndUnder35() {
        return x() && under35();
    }

    public boolean xAndOver35() {
        return x() && over35();
    }

    public boolean dueAndUnder35() {
        return due() && under35();
    }

    public boolean dueAndOver35() {
        return due() && over35();
    }

    public boolean unoAndUnder45() {
        return uno() && under45();
    }

    public boolean unoAndOver45() {
        return uno() && over45();
    }

    public boolean xAndUnder45() {
        return x() && under45();
    }

    public boolean xAndOver45() {
        return x() && over45();
    }

    public boolean dueAndUnder45() {
        return due() && under45();
    }

    public boolean dueAndOver45() {
        return due() && over45();
    }

    public boolean unoAndGoal() {
        return uno() && goal();
    }

    public boolean unoAndNoGoal() {
        return uno() && noGoal();
    }

    public boolean xAndGoal() {
        return x() && goal();
    }

    public boolean xAndNoGoal() {
        return x() && noGoal();
    }

    public boolean dueAndGoal() {
        return due() && goal();
    }

    public boolean dueAndNoGoal() {
        return due() && noGoal();
    }

    public boolean unoXAndGoal() {
        return unoX() && goal();
    }

    public boolean unoXAndNoGoal() {
        return unoX() && noGoal();
    }

    public boolean xDueAndGoal() {
        return dueX() && goal();
    }

    public boolean xDueAndNoGoal() {
        return dueX() && noGoal();
    }

    public boolean unoDueAndGoal() {
        return unoDue() && goal();
    }

    public boolean unoDueAndNoGoal() {
        return unoDue() && noGoal();
    }

    public boolean unoXAndUnder15() {
        return unoX() && under15();
    }

    public boolean unoXAndOver15() {
        return unoX() && over15();
    }

    public boolean xDueAndUnder15() {
        return dueX() && under15();
    }

    public boolean xDueAndOver15() {
        return dueX() && over15();
    }

    public boolean unoDueAndUnder15() {
        return unoDue() && under15();
    }

    public boolean unoDueAndOver15() {
        return unoDue() && over15();
    }

    public boolean unoXAndUnder25() {
        return unoX() && under25();
    }

    public boolean unoXAndOver25() {
        return unoX() && over25();
    }

    public boolean xDueAndUnder25() {
        return dueX() && under25();
    }

    public boolean xDueAndOver25() {
        return dueX() && over25();
    }

    public boolean unoDueAndUnder25() {
        return unoDue() && under25();
    }

    public boolean unoDueAndOver25() {
        return unoDue() && over25();
    }

    public boolean unoXAndUnder35() {
        return unoX() && under35();
    }

    public boolean unoXAndOver35() {
        return unoX() && over35();
    }

    public boolean xDueAndUnder35() {
        return dueX() && under35();
    }

    public boolean xDueAndOver35() {
        return dueX() && over35();
    }

    public boolean unoDueAndUnder35() {
        return unoDue() && under35();
    }

    public boolean unoDueAndOver35() {
        return unoDue() && over35();
    }

    public boolean unoXAndUnder45() {
        return unoX() && under45();
    }

    public boolean unoXAndOver45() {
        return unoX() && over45();
    }

    public boolean xDueAndUnder45() {
        return dueX() && under45();
    }

    public boolean xDueAndOver45() {
        return dueX() && over45();
    }

    public boolean unoDueAndUnder45() {
        return unoDue() && under45();
    }

    public boolean unoDueAndOver45() {
        return unoDue() && over45();
    }

    public boolean goalAndOver25() {
        return goal() && over25();
    }

    public boolean goalAndUnder25() {
        return goal() && under25();
    }

    public boolean noGoalAndOver25() {
        return noGoal() && over25();
    }

    public boolean noGoalAndUnder25() {
        return noGoal() && under25();
    }

    public boolean goal12() {
        return over05() && under25();
    }

    public boolean goal13() {
        return over05() && under35();
    }

    public boolean goal14() {
        return over05() && under45();
    }

    public boolean goal15() {
        return over05() && under55();
    }

    public boolean goal16() {
        return over05() && under65();
    }

    public boolean goal23() {
        return over15() && under35();
    }

    public boolean goal24() {
        return over15() && under45();
    }

    public boolean goal25() {
        return over15() && under55();
    }

    public boolean goal26() {
        return over15() && under65();
    }

    public boolean goal34() {
        return over25() && under45();
    }

    public boolean goal35() {
        return over25() && under55();
    }

    public boolean goal36() {
        return over25() && under65();
    }

    public boolean goal45() {
        return over35() && under55();
    }

    public boolean goal46() {
        return over35() && under65();
    }

    public boolean goal56() {
        return over45() && under65();
    }

    public boolean goal7plus() {
        return over65();
    }

    public boolean goal12casa() {
        return homeResult>=1 && homeResult<=2;
    }

    public boolean goal13casa() {
        return homeResult>=1 && homeResult<=3;
    }

    public boolean goal14casa() {
        return homeResult>=1 && homeResult<=4;
    }

    public boolean goal15casa() {
        return homeResult>=1 && homeResult<=5;
    }

    public boolean goal23casa() {
        return homeResult>=2 && homeResult<=3;
    }

    public boolean goal24casa() {
        return homeResult>=2 && homeResult<=4;
    }

    public boolean goal25casa() {
        return homeResult>=2 && homeResult<=5;
    }

    public boolean goal12trasferta() {
        return awayResult>=1 && awayResult<=2;
    }

    public boolean goal13trasferta() {
        return awayResult>=1 && awayResult<=3;
    }

    public boolean goal14trasferta() {
        return awayResult>=1 && awayResult<=4;
    }

    public boolean goal15trasferta() {
        return awayResult>=1 && awayResult<=5;
    }

    public boolean goal23trasferta() {
        return awayResult>=2 && awayResult<=3;
    }

    public boolean goal24trasferta() {
        return awayResult>=2 && awayResult<=4;
    }

    public boolean goal25trasferta() {
        return awayResult>=2 && awayResult<=5;
    }

    public boolean dispari1time() {
        return (firstTimeHomeResult + firstTimeAwayResult) % 2 != 0;
    }

    public boolean pari1time() {
        return (firstTimeHomeResult + firstTimeAwayResult) % 2 == 0;
    }

    public boolean uno1tempo() {
        return firstTimeHomeResult > firstTimeAwayResult;
    }

    public boolean x1tempo() {
        return firstTimeHomeResult == firstTimeAwayResult;
    }

    public boolean due1tempo() {
        return firstTimeHomeResult < firstTimeAwayResult;
    }

    public boolean unoUno() {
        return firstTimeHomeResult > firstTimeAwayResult && secondTimeHomeResult > secondTimeAwayResult;
    }

    public boolean unoAndX() {
        return firstTimeHomeResult > firstTimeAwayResult && secondTimeHomeResult == secondTimeAwayResult;
    }

    public boolean unoAndDue() {
        return firstTimeHomeResult > firstTimeAwayResult && secondTimeHomeResult < secondTimeAwayResult;
    }

    public boolean xUno() {
        return firstTimeHomeResult == firstTimeAwayResult && secondTimeHomeResult > secondTimeAwayResult;
    }

    public boolean xX() {
        return firstTimeHomeResult == firstTimeAwayResult && secondTimeHomeResult == secondTimeAwayResult;
    }

    public boolean xDue() {
        return firstTimeHomeResult == firstTimeAwayResult && secondTimeHomeResult < secondTimeAwayResult;
    }

    public boolean dueUno() {
        return firstTimeHomeResult < firstTimeAwayResult && secondTimeHomeResult > secondTimeAwayResult;
    }

    public boolean dueandX() {
        return firstTimeHomeResult < firstTimeAwayResult && secondTimeHomeResult == secondTimeAwayResult;
    }

    public boolean dueDue() {
        return firstTimeHomeResult < firstTimeAwayResult && secondTimeHomeResult < secondTimeAwayResult;
    }

    public boolean under05primo() {
        return firstTimeHomeResult + firstTimeAwayResult < 1;
    }

    public boolean over05primo() {
        return firstTimeHomeResult + firstTimeAwayResult > 0;
    }

    public boolean under15primo() {
        return firstTimeHomeResult + firstTimeAwayResult < 2;
    }

    public boolean over15primo() {
        return firstTimeHomeResult + firstTimeAwayResult > 1;
    }

    public boolean under25primo() {
        return firstTimeHomeResult + firstTimeAwayResult < 3;
    }

    public boolean over25primo() {
        return firstTimeHomeResult + firstTimeAwayResult > 2;
    }

    public boolean under35primo() {
        return firstTimeHomeResult + firstTimeAwayResult < 4;
    }

    public boolean over35primo() {
        return firstTimeHomeResult + firstTimeAwayResult > 3;
    }

    public boolean under45primo() {
        return firstTimeHomeResult + firstTimeAwayResult < 5;
    }

    public boolean over45primo() {
        return firstTimeHomeResult + firstTimeAwayResult > 4;
    }

    public boolean under55primo() {
        return firstTimeHomeResult + firstTimeAwayResult < 6;
    }

    public boolean over55primo() {
        return firstTimeHomeResult + firstTimeAwayResult > 5;
    }

    public boolean under05secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) < 1;
    }

    public boolean over05secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) > 0;
    }

    public boolean under15secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) < 2;
    }

    public boolean over15secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) > 1;
    }

    public boolean under25secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) < 3;
    }

    public boolean over25secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) > 2;
    }

    public boolean under35secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) < 4;
    }

    public boolean over35secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) > 3;
    }

    public boolean under45secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) < 5;
    }

    public boolean over45secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) > 4;
    }

    public boolean under55secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) < 6;
    }

    public boolean over55secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) > 5;
    }

    public boolean goalPrimo() {
        return firstTimeHomeResult > 0 && firstTimeAwayResult > 0;
    }

    public boolean noGoalPrimo() {
        return firstTimeHomeResult == 0 || firstTimeAwayResult == 0;
    }

    public boolean goalSecondo() {
        return (secondTimeHomeResult - firstTimeHomeResult) > 0 && (secondTimeAwayResult - firstTimeAwayResult) > 0;
    }

    public boolean noGoalSecondo() {
        return (secondTimeHomeResult - firstTimeHomeResult) == 0 || (secondTimeAwayResult - firstTimeAwayResult) == 0;
    }

    public boolean primo() {
        return (firstTimeHomeResult + firstTimeAwayResult) > (secondTimeHomeResult + secondTimeAwayResult);
    }

    public boolean secondo() {
        return (firstTimeHomeResult + firstTimeAwayResult) < (secondTimeHomeResult + secondTimeAwayResult);
    }

    public boolean entrambiPari() {
        return (firstTimeHomeResult + firstTimeAwayResult) == (secondTimeHomeResult + secondTimeAwayResult);
    }

    public boolean goal12primo() {
        return (firstTimeHomeResult + firstTimeAwayResult) >= 1 && (firstTimeHomeResult + firstTimeAwayResult) <= 2;
    }

    public boolean goal13primo() {
        return (firstTimeHomeResult + firstTimeAwayResult) >= 1 && (firstTimeHomeResult + firstTimeAwayResult) <= 3;
    }

    public boolean goal23primo() {
        return (firstTimeHomeResult + firstTimeAwayResult) >= 2 && (firstTimeHomeResult + firstTimeAwayResult) <= 3;
    }

    public boolean goal12secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) >= 1 && ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) <= 2;
    }

    public boolean goal13secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) >= 1 && ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) <= 3;
    }

    public boolean goal23secondo() {
        return ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) >= 2 && ((secondTimeHomeResult - firstTimeHomeResult) + (secondTimeAwayResult - firstTimeAwayResult)) <= 3;
    }

    public boolean unoXprimo() {
        return firstTimeHomeResult >= firstTimeAwayResult;
    }

    public boolean unoDueprimo() {
        return firstTimeHomeResult != firstTimeAwayResult;
    }

    public boolean dueXprimo() {
        return firstTimeHomeResult <= firstTimeAwayResult;
    }

    public boolean unoXsecondo() {
        return (secondTimeHomeResult - firstTimeHomeResult) >= (secondTimeAwayResult - firstTimeAwayResult);
    }

    public boolean unoDuesecondo() {
        return (secondTimeHomeResult - firstTimeHomeResult) != (secondTimeAwayResult - firstTimeAwayResult);
    }

    public boolean dueXsecondo() {
        return (secondTimeHomeResult - firstTimeHomeResult) <= (secondTimeAwayResult - firstTimeAwayResult);
    }

    public boolean casaSi() {
        return firstTimeHomeResult > 0 && (secondTimeHomeResult - firstTimeHomeResult) > 0;
    }

    public boolean casaNo() {
        return !(firstTimeHomeResult > 0 && (secondTimeHomeResult - firstTimeHomeResult) > 0);
    }

    public boolean ospiteSi() {
        return firstTimeAwayResult > 0 && (secondTimeAwayResult - firstTimeAwayResult) > 0;
    }

    public boolean ospiteNo() {
        return !(firstTimeAwayResult > 0 && (secondTimeAwayResult - firstTimeAwayResult) > 0);
    }

    public boolean vinceCasaSi() {
        return (firstTimeHomeResult > firstTimeAwayResult) && ((secondTimeHomeResult - firstTimeHomeResult) > (secondTimeAwayResult - firstTimeAwayResult));
    }

    public boolean vinceCasaNo() {
        return !((firstTimeHomeResult > firstTimeAwayResult) && ((secondTimeHomeResult - firstTimeHomeResult) > (secondTimeAwayResult - firstTimeAwayResult)));
    }


    public boolean vinceOspiteSi() {
        return (firstTimeHomeResult < firstTimeAwayResult) && ((secondTimeHomeResult - firstTimeHomeResult) < (secondTimeAwayResult - firstTimeAwayResult));
    }

    public boolean vinceOspiteNo() {
        return !((firstTimeHomeResult < firstTimeAwayResult) && ((secondTimeHomeResult - firstTimeHomeResult) < (secondTimeAwayResult - firstTimeAwayResult)));
    }
}
