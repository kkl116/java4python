package com.justin;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.File;


public class PDFProcessorTest {

    @Test
    void testProcessor(){
        File[] files;
        String resourcePath = "./src/test/resources";
        File dir = new File(resourcePath);
        files = dir.listFiles();

        for (File file: files){
            try {
                String pdfPath = file.getAbsolutePath();
                PDFProcessor.PDFSummary summary = PDFProcessor.generateSummary(pdfPath);
                assertTrue(summary.validate(pdfPath));

            } catch (IOException e) {
                System.out.println("Failed to read file: " + files);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
