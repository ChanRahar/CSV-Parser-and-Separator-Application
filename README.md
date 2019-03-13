# CSV Parser and Separator Application

A simple Maven Java application that parse data from a CSV file, and manipulate the data to ensure that only rows with complete data are saved into the in-memory sqlite database. 

All elements with commas are also double quoted to ensure that those elements are saved correctly. 

This application separate data by checking if each column of each row has a value. If a row has a column that has no value, the row is incomplete,and that row will be inserted into the "bad-data" csv file, which is located in the output folder. 

A log text file is also generate to show how many rows of records are in the CSV file, how many rows of records are complete data, and how many rows of records are incomplete data. 

## How to run the application
* This Maven Java application can be run on a Java IDE, with Maven installed. The user will be first promted with the source csv file. A source file "ms3data.csv" is located in the main project folder and can be enter as is (by typing "ms3data.csv"). However, if the source csv file is moved to another folder, relative pathing can be entered. For example, the same source csv file is located in the java folders, and it can be accessed through entering "src/main/java/com/chandra/raharja/ms3data.csv"

* After the csv file is parsed, the user will be promted to enter the name of the table in the In-Memory Sqlite database.

* Once that is completed, user then will be promted to enter the prefix to the the "bad-data" csv file that includes all rows with incomplete data. 

* Once the "bad-data" file is generated, the user will be promted again to enter the name of the generated log file that will show how many records received, complete, and incomplete is generated.

* Both the "bad-data" file and log txt file can be reviewed in the output folder. 

## How I approach the project
I approached creating application by going through the following steps:
* Firstly, I added Opencsv library as a dependency to parse and write csv file.
* I used CSVReader and FileReader to read through the csv file, then I used both the while and for loops to try to to add the quotations for the elements with commas, and validate each row and column.
* If a row contains a blank value, that row is added into the incompleteRows ArrayList, and a counter for numIncompleteRows is incremented. If a row does not contains blank, that row is added into the completeRows ArrayList, and numCompleteRows is incremented. 
* The numIncompleteRows and numCompleteRows is incremented for the statistic log later on.
* Once the data are separated, I created a method to create the in-memory sqlite database and table, and insert the completeRows into the database using the sqlite jdbc. 
* The incomplete rows included in incompleteRows ArrayList then is outputted into a "bad-data" csv file.
* I generate the "bad-data" csv file using CSVWriter and FileWriter.
* Lastly, I created a Log class to create the Log txt file that output total number of records as info level log, total number of successful records as info level log, and total number of records failed as severe level log.
* At the end, I added Scanner so user can dynamically enter where the source file is located, and name the in-memory table, the "bad-data" file prefix, and the name for the log file.