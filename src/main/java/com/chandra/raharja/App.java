package com.chandra.raharja;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

public class App {
    public static void main(String[] args) {
        String basePath = new File("").getAbsolutePath();
        System.out.println(basePath);

        String path = new File("src/main/java/com/chandra/raharja/ms3data.csv")
                .getAbsolutePath();

        String csvFile = path;

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            ArrayList<String[]> allDocs = new ArrayList<>();
            ArrayList<String[]> badDocs = new ArrayList<>();
            ArrayList<String[]> goodDocs = new ArrayList<>();
            int numFailedData = 0;
            int numCompleteData = 0;
            while ((line = reader.readNext()) != null) {

                if (line != null && line.length > 1) {
                    for (int i = 0; i < line.length; i++) {

                        if (line[i].indexOf(",") >= 0) {
                            line[i] = '"' + line[i] + '"';
                            continue;
                        }
                        String empty = new String();
                        if (line[i].equals(empty)) {
                            numFailedData++;
                            badDocs.add(line);
                            break;
                        } else if (line.length == i + 1) {
                            numCompleteData++;
                            goodDocs.add(line);
                            break;
                        }
                    }
                    allDocs.add(line);
                }
            }

            int docSize = numFailedData + numCompleteData;

            System.out.println(numCompleteData);

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/java/com/chandra/raharja/test.java.db");
                Statement statement = conn.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS data " +
                        "(A Text, B Text, C Text, D Text, E Text, F Text, G Text, H Text, I Text, J Text )");

//                for(String[] row: goodDocs){
//                    PreparedStatement myStmt = conn.prepareStatement("INSERT INTO data VALUES (?,?,?,?,?,?,?,?,?,?)");
//                    for (int i = 1; i <= 10; i++) {
//                        myStmt.setString(i, row[i-1]);
//                    }
//                    myStmt.executeUpdate();
//                    myStmt.close();
//                }

//                PreparedStatement myStmt = conn.prepareStatement("INSERT INTO data VALUES (?,?,?,?,?,?,?,?,?,?)");
//
//                for (int i = 1; i <= 10; i++) {
//                    myStmt.setString(i, Integer.toString(i));
//                    System.out.println(i);
//                }
//
//                myStmt.executeUpdate();
//                myStmt.close();

                statement.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("Something went wrong : " + e.getMessage());
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            FileWriter badData = new FileWriter("bad-data-" + timestamp + ".csv");
            CSVWriter writer = new CSVWriter(badData);
            writer.writeAll(badDocs);
            writer.flush();

            FileWriter goodData = new FileWriter("good_data.csv");
            CSVWriter newWriter = new CSVWriter(goodData);
            newWriter.writeAll(goodDocs);
            newWriter.flush();
            newWriter.close();


            try {
                Log statisticLog = new Log("statisticLog.txt");

                statisticLog.logger.setLevel(Level.ALL);

                statisticLog.logger.info("Number of Records Received: " + docSize);
                statisticLog.logger.info("Number of Records Successful: " + numCompleteData);
                statisticLog.logger.severe("Number of Records Failed: " + numFailedData);
                ;


            } catch (Exception e) {
//                System.out.println(e);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("CSV Read Complete");

    }
}
