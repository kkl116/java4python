package com.justin;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PDFProcessor {

    public static PDFSummary generateSummary(String pdfPath) throws IOException, Exception {
        try (PDDocument doc = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String extractedText = textStripper.getText(doc);
            String[] lines = extractedText.split("\\r?\\n");

            PDFSummary summary = ProcessText(lines);
            //validate
            if (summary.validate(pdfPath)){
                return summary;
            } else {
                throw new Exception("Error in processing PDF: " + pdfPath);
            }

        } catch (IOException e) { 
            System.err.println("Failed to read PDF document: " + pdfPath);
            return null;
        }

    }

    public static PDFSummary ProcessText(String[] extractedText){
        
        ArrayList<String> income = new ArrayList<String>();
        ArrayList<String> expense = new ArrayList<String>();
        double incomeTotal = 0d;
        double expenseTotal = 0d;
        String currCategory = "";
        String incomeKeyword = "Income";
        String expenseKeyword = "Expenditure";
        String dateString = extractedText[extractedText.length - 1].split("\\s+")[0];

        for (String line: extractedText){
            if (line.contains(incomeKeyword)){
                currCategory = incomeKeyword;
            } else if (line.contains(expenseKeyword)){
                currCategory = expenseKeyword;
            }

            if (isItem(line) && currCategory.length() > 0){
                String curr = "";
                String[] chunks = line.split("£");
                String grossString = chunks[chunks.length - 1];
                String itemString = chunks[0];

                if (itemString.contains("Management")){
                    itemString = itemString + chunks[1];
                }

                double itemGross = Double.parseDouble(grossString.replace(",", ""));

                curr = curr + itemString + "\t\t" + "£" + grossString;

                if (currCategory.equals(incomeKeyword)){
                    incomeTotal = incomeTotal + itemGross;
                    income.add(curr);
                } else {
                    expenseTotal = expenseTotal + itemGross;
                    expense.add(curr);
                }
            }

            if (line.contains("Invoice")){
                break;
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);

        PDFSummary summary = new PDFSummary(date, income, expense, incomeTotal, expenseTotal);

        return summary;
    }

    private static boolean isItem(String line){
        /*
         * .chars() creates a stream from a string (with each char being represented as an int)
         * .filter accepts a lambda expression (parameter -> expression) much like python's map? 
         * forgot which function
         * .count() returns the number of elements within the stream.
         */
        long poundSignCount = line.chars().filter(ch -> ch == '£').count();
        char startChar = line.charAt(0);
        return (poundSignCount >= 3) && (startChar != '£');
    }

    private static String processDate(String dateString){
        //Use HashMap for <String, String> Map
        Map<String, String> monthMap = new HashMap<String, String>() {{
            put("January", "01");
            put("February", "02");
            put("March", "03");
            put("April", "04");
            put("May", "05");
            put("June", "06");
            put("July", "07");
            put("August", "08");
            put("September", "09");
            put("October", "10");
            put("November", "11");
            put("December", "12");
        }};
        
        String[] components = dateString.split("\\s+");
        //regex to remove th and st suffixes
        String dd = components[0].replaceAll("[^0-9.]", "");
        String mm = monthMap.get(components[1]);
        String yy = components[components.length - 1];
        return dd + "/" + mm + "/" + yy;
        
    }

    public static void generateGlobalSummary(ArrayList<PDFSummary> processed, String outFilePath){

        Double incomeTotalGlobal = processed.stream().mapToDouble(pdf -> pdf.incomeTotal).sum();
        Double expenseTotalGlobal = processed.stream().mapToDouble(pdf -> pdf.expenseTotal).sum();
        Double netIncomeGlobal = processed.stream().mapToDouble(pdf -> pdf.netIncome).sum();

        //write to a pdf 

    }

    //implement comparator method here 
    static class PDFSummary implements Comparable<PDFSummary>{
        public LocalDate date;
        public ArrayList<String> income; 
        public ArrayList<String> expense;
        public Double incomeTotal; 
        public Double expenseTotal; 
        public Double netIncome;

        public PDFSummary(LocalDate _date, ArrayList<String> _income, ArrayList<String> _expense,
            Double _incomeTotal,Double _expenseTotal){

                date = _date;
                income = _income; 
                expense = _expense;
                incomeTotal = _incomeTotal;
                expenseTotal = _expenseTotal;
                netIncome = incomeTotal - expenseTotal;
            }

        public ArrayList<String> generateStringArray(){
            String formatString = "%.2f";
            ArrayList<String> summary = new ArrayList<String>();

            summary.add("Statement: " + date);
            summary.add("\n");
            summary.add("Income: ");
            for (String line: income){
                summary.add(line);
            }
            summary.add("\n");
            summary.add("\t Total Income: \t\t" + "£" + String.format(formatString, incomeTotal));

            summary.add("\n");
            summary.add("Expense: ");
            for (String line: expense){
                summary.add(line);
            }
            summary.add("\n");
            summary.add("\t Total Expense: \t\t" + "£" + String.format(formatString, expenseTotal));

            summary.add("\n");
            summary.add("\t Net Income: \t\t\t" + "£" + String.format(formatString, netIncome));
            summary.add("\n");

            return summary;
        }

        public Boolean validate(String fileName){
            String ansString = fileName.toString().split("£")[1].replace(".pdf", "");
            Double ansDouble = Double.parseDouble(ansString.replace(",", ""));

            return equalEnough(ansDouble, this.netIncome, 0.5);
        }

        public static boolean equalEnough(Double a, Double b, Double eps){
            return Math.abs(a-b)<eps;
        }

        @Override
        public int compareTo(PDFSummary other){
            return this.date.compareTo(other.date);
        }

    }
}
