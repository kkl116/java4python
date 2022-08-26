import java.util.ArrayList; 
import java.util.Collections;
import java.util.Random;

public class Fraction extends Number implements Comparable<Fraction> {
    private Integer numerator;
    private Integer denominator;

    /*
     * Method overloading: each Fraction constructor method and add method takes 
     * different parameter types, and during runtime the compiler will decide
     * which one to use! 
     */
    public Fraction(Integer num, Integer den){
        numerator = num;
        denominator = den;
    }

    public Fraction(Integer num){
        numerator = num;
        denominator = 1;
    }

    public Integer getNumerator() {
        return numerator;
    }

    public void setNumerator(Integer num) {
        numerator = num;
    }

    public Integer getDenominator() {
        return denominator;
    }

    public void setDenominator(Integer den){
        denominator = den;
    }

    public Fraction add(Fraction otherFrac){
        Integer newNum = otherFrac.getDenominator() * numerator + 
                        denominator * otherFrac.getNumerator();
        Integer newDen = denominator * otherFrac.getDenominator(); 
        Integer common = gcd(newNum, newDen);

        return new Fraction(newNum/common, newDen/common);
    }

    public Fraction add(Integer other){
        return add(new Fraction(other));
    }

    private static Integer gcd(Integer a, Integer b){ 
        if (b == 0){
            return a;
        } else{
            return gcd(b, a%b);
        }
    }

    public String toString(){
        return numerator.toString() + '/' + denominator.toString();
    }

    public double doubleValue() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    public float floatValue() {
        return numerator.floatValue() / denominator.floatValue();
    }

    public int intValue() {
        return numerator.intValue() / denominator.intValue();
    }

    public long longValue() {
        return numerator.longValue() / denominator.longValue();
    }

    public boolean equals(Fraction other){
        Integer num1 = this.numerator * other.getDenominator();
        Integer num2 = this.denominator * other.getNumerator();
        return num1 == num2;
    }

    public int compareTo(Fraction other){
        Integer num1 = this.numerator * other.getDenominator();
        Integer num2 = this.denominator * other.getNumerator();
        return num1-num2;
    }

    public static void main(String[] args) {

        Fraction f1 = new Fraction(1,2);
        Fraction f2 = new Fraction(3, 2);

        System.out.println(f1.add(f2));
        System.out.println(f1.equals(f2));

        ArrayList<Fraction> lst; 
        Integer randNum; 
        Integer randDen;
        Random ran = new Random();

        lst = new ArrayList<Fraction>(10);
        for (Integer i = 0; i < 10; i++){
            randNum = ran.nextInt(50);
            randDen = ran.nextInt(50);
            lst.add(i, new Fraction(randNum, randDen));
        }

        Collections.sort(lst);
        for (Fraction frac: lst){
            System.out.println(frac);
        }
    }

}