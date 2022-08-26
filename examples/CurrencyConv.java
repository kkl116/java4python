import java.util.Scanner;

public class CurrencyConv {
    public static void main(String[] args){
        /*
        Declaring varaibles here - note that Scanner is a class so functons 
        as a type during declaration, so we're saying in's type = Scanner
        */
        Double hkd;
        Double gbp;
        Scanner in;

        in = new Scanner(System.in);
        System.out.println("Enter HKD amount to convert to GBP: ");
        hkd = in.nextDouble();

        gbp = hkd/10;
        System.out.println("The GBP amount is: £" + gbp);
    }
}
