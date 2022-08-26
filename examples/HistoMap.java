import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class HistoMap {

    public static void main(String[] args) {

        /*
         * task: ignore punctuation
         */
        Scanner data = null;
        TreeMap<String,Integer> count;
        String word;
        Integer wordCount;

        try {
                data = new Scanner(new File("alice30.txt"));
        }
        catch ( IOException e) {
            System.out.println("Unable to open data file");
            e.printStackTrace();
            System.exit(0);
        }

        count = new TreeMap<String,Integer>();

        while(data.hasNext()) {
            word = data.next().toLowerCase();
            /*
             * nicest solution without a weird for loop is to use regex... (I have no idea
             * what the regex is saying just found it on stackoverflow)
             */
            word = word.replaceFirst("^[^a-zA-Z]+", "");
            word = word.replaceAll("[^a-zA-Z]+$", "");
            wordCount = count.get(word);
            if (wordCount == null) {
                wordCount = 0;
            }
            count.put(word,++wordCount);
        }

        for(String i : count.keySet()) {
            System.out.printf("%-20s occured %5d times\n", i, count.get(i) );
        }
    }
}
