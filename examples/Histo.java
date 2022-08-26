import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

public class Histo {
    public static void main(String[] args){
        Scanner data = null;
        ArrayList<Integer> count; 
        /*
         * another implementation with integer arrays: 
         * Integer[] count = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
         * with integer arrays in the later for loop we can directly index 
         * as in python e.g. count[idx] = count[idx] + 1 
         */
        Integer idx; 

        try {
            data = new Scanner(new File("test.dat"));
        }
        catch (IOException e) {
            System.out.println("Unable to open data file.");
            e.printStackTrace();
            System.exit(0);
        }

        count = new ArrayList<Integer>(10);
        for (Integer i = 0; i < 10; i++){
            count.add(i, 0);
        }
        /*
         * rewriting different python for loops:
         * for i in range(2,101,2) -> for (ArrayList i = 2; i < 101, i+=2)
         * for i in range(1, 100) -> for (ArrayList i = 1; i < 100, i++)
         * for i in range(100, 0, -1) -> for (ArrayList i = 100; i > 0; i--)
         * for x,y in zip(range(10), range(0, 20,2)) -> 
         * for ArrayList(x = 0, y = 0; x < 10, y < 20; x++, y += 2) - separate clauses with ,
         */

        while(data.hasNextInt()){
            idx = data.nextInt();
            count.set(idx, count.get(idx)+1);
        }

        idx = 0;
        for(Integer i: count){ 
            System.out.println(idx + " occurred " + i + " times.");
            idx++; 
        }
    }
}
