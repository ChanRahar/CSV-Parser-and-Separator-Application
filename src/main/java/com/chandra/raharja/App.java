package com.chandra.raharja;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Source CSV File");

        //Enter "ms3data.csv", however, if the source csv file is moved to another folder, relative pathing can be entered
        //for example, the same source csv file is located in the java folder, and it can be accessed through entering
        // "src/main/java/com/chandra/raharja/ms3data.csv"
        String csvFile = sc.nextLine();

        ArrayList<String[]> incompleteRows = new ArrayList<>();
        ArrayList<String[]> completeRows = new ArrayList<>();
        int numIncompleteRows = 0;
        int numCompleteRows = 0;

        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String[] row;

            //parsing through the csv source file to update the column with a ",", and separate bad rows with complete rows
            while ((row = reader.readNext()) != null) {

                if (row != null && row.length > 1) {
                    for (int i = 0; i < row.length; i++) {

                        if (row[i].indexOf(",") >= 0) {
                            row[i] = '"' + row[i] + '"';
                            continue;
                        }
                        String empty = new String();
                        if (row[i].equals(empty)) {
                            numIncompleteRows++;
                            incompleteRows.add(row);
                            break;
                        } else if (row.length == i + 1) {
                            numCompleteRows++;
                            completeRows.add(row);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        //if source csv file contain at least one row with a complete data, add to in memory database
        if (numCompleteRows != 0) {
            System.out.println("Enter a Name for Table");

            //name for the table in the in-memory database
            String tableName = sc.nextLine();

            saveToDatabase(tableName, completeRows);
        }

        System.out.println("Enter a prefix name for the bad data CSV file");

        //prefix name of the bad data file. Bad data is created in output folder
        String badDataName = sc.nextLine();
        printBadData(badDataName, incompleteRows);

        System.out.println("Enter a Name for the log file");

        //name of the log file. Log file is created in the output folder
        String logName = sc.nextLine();

        printLog(logName, numIncompleteRows, numCompleteRows);

        System.out.println("CSV Parsing Completed");
    }

    public static void saveToDatabase(String table, ArrayList<String[]> completeRows) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement statement = conn.createStatement();

            String columnHeader = new String();

            for (String[] line : completeRows) {

                for (int i = 0; i < completeRows.get(0).length; i++) {
                    if (i == 0) {
                        columnHeader = columnHeader.concat(line[i] + " Text");
                    } else {
                        columnHeader = columnHeader.concat(", " + line[i] + " Text");
                    }
                }
                break;
            }

            statement.execute("CREATE TABLE IF NOT EXISTS " + table + " (" + columnHeader + ")");


            String columnHolder = new String();

            for (int i = 1; i <= completeRows.get(0).length; i++) {
                if (i == 1) {
                    columnHolder = columnHolder.concat("?");
                } else {
                    columnHolder = columnHolder.concat(",?");
                }
            }

            PreparedStatement myStmt = conn.prepareStatement("INSERT INTO " + table + " VALUES (" + columnHolder + ")");

            for (String[] line : completeRows) {

                for (int i = 1; i <= completeRows.get(0).length; i++) {
                    myStmt.setString(i, line[i - 1]);
                }
                myStmt.executeUpdate();
            }

            myStmt.close();

            statement.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Something went wrong : " + e.getMessage());
        }
    }

    public static void printBadData(String badDataName, ArrayList<String[]> incompleteRows) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH.mm").format(new Date());

            FileWriter badData = new FileWriter("output/" + badDataName + "-bad-data-" + timestamp + ".csv");
            CSVWriter writer = new CSVWriter(badData);
            writer.writeAll(incompleteRows);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void printLog(String logName, int numIncompleteRows, int numCompleteRows) {
        try {
            int docSize = numIncompleteRows + numCompleteRows;

            Log statisticLog = new Log("output/" + logName + ".txt");

            statisticLog.logger.setLevel(Level.ALL);

            //Number of records received is assumed as info level
            statisticLog.logger.info("Number of Records Received: " + docSize);

            //Number of records successful is assumed as info level
            statisticLog.logger.info("Number of Records Successful: " + numCompleteRows);

            //Number of records failed is assumed as severe level
            statisticLog.logger.severe("Number of Records Failed: " + numIncompleteRows);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
