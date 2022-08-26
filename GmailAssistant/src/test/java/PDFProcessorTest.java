package com.justin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.File;


public class PDFProcessorTest {

    @Test
    void testProcessor(){
        File[] files;
        String resourcePath = "./src/test/java/resources";
        File dir = new File(resourcePath);
        files = dir.listFiles();

        for (File file: files){
            String ansString = file.toString().split("£")[1].replace(".pdf", "");
            Double ansDouble = Double.parseDouble(ansString.replace(",", ""));
    
            try {
                PDFProcessor.PDFSummary summary = PDFProcessor.ExtractText(file.getAbsolutePath());
                assertEquals(summary.netIncome, ansDouble, 0.5, file.toString() + " was incorrect.");

            } catch (IOException e) {
                System.out.println("Failed to read file: " + files);
            }
        }



    }
    
}
