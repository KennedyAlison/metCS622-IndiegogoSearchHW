# Indiegogo Search - CS 622 Homework 2

**Overview**: This project contains one class that takes in multiple data files, merges the data within each of the files (getting rid of duplicate data), then searches the data on a given keyword and returns specified data for each matched entry. The objective of this project is to highlight the concepts of file I/O, exception handling, and the use of regular expressions. The class is written to perform the following actions: 
* **Unzip the Files:** The class begins by taking in multiple zip files of csv data from [Indiegogo-datasets](https://webrobots.io/indiegogo-dataset) and unzip the contents to a specified location. All of the unzipped files are also added to an ArrayList so the contents can next be merged.
* **Merge the File Content:** Once all of the files have been unzipped, a File Interator is used to iterate through all of the files and merge the contents. A projectIDs is checked here and only unique projectID are added to the merged file, therefore getting rid of any duplicates.
* **Search for Keyword Data:** Once the files have been merged into a single file, the user is asked to input a keyword to search within the file. If the given keyword returns 0 matches, then the user is prompted to input a new keyword. When a match is found, the data for columns "fund_raised_percent" and "close_date" are returned along with the projectID identifier.
<br />

**Concepts Used**
<br />
<br />
**File I/O:**
* I leveraged the java.io packages in multiple places within my code 
* I utilized a FileInputStream and FileOutputStream with a ZipInputStream to read in files from multiple zip files and write out those files to a specific directory 
* I utilized a BufferReader and a BufferWriter to read the contents of each individual file and write out the content to a new, merged file containing all the data 
* I also used a BufferReader to search for a keyword within the merged file dataset and print out desired column data to the console 

**Exception Handling:**
* The main class throws an IOException as both the *mergedFiles* and *searchKeywords* methods required an external file as input and throw an IOException and FileNotFoundException respectively
* The *unzip* method also uses a try catch when getting the Files from the zip file and outputting them to a new destination as a IOException can occur 
* The *mergeFiles* method catches an IOException when attempting to create a new file. This method also catches a FileNotFoundException, IOExcpetion, and general Exception when iterating through the file contents to be merged
* The *searchKeyword* method also utilizes try catch statements to catch both a FileNotFoundException and an IOException when reading through the merged file searching for the keyword and desired column data

**Regular expressions:**
* A regular expression is first used within the *mergedFiles* method to locate the projectID associated to each line item. Only if this projectID is unique, will that entry be added into the merged file
* Two regular expressions are used within the *searchKeyword* method. The first pattern searches for the keyword, this search for all instances of the keyword (not an exact match) within each entry. If the keyword is found, another pattern is used to search for the fund_raised_percent, close_date, and projectID column data. Groups are leveraged within this pattern to pull out the data in those 3 specific columns

##Assumptions
* The assumption is made that the files in each of the zip files are csv files 
* The assumption is made that projectIDs are unique and therefore duplicate IDs are filtered out when the files are merged 
* The assumption is made that one keyword is searched at a time and the keyword is searched throughout all columns in the csv as well as a part of any word, meaning it does not have to be an exact match. For example, when searching "robot", any valid string containing the characters "robot" will be returned, e.g. "mrrobot" or "robotics". 


## Instructions 

1. Navigate to [Indiegogo-datasets](https://webrobots.io/indiegogo-dataset) and download the latest two CSV files from 2021: 2021-05-17 and 2021-04-16
2. Open the *IndiegogoSearch.java* class and update the *zipFile1* and *zipFile2* strings with the absolute path to the two zip files just downloaded (*NOTE:* More than two zipfiles can be added here by creating additional String zip file variables with the absolute path location and then adding those zip files the the zipFiles ArrayList)
3. Update the *zipDest*  string with the destination directory for the files to be unzipped 
4. Update the *mergedFile* string with the destination directory and file name for the file to store all of the merged content 
5. Run the program and when prompted, enter the first keyword to search for - type in "robot" and hit enter to see matched results 
6. When prompted to enter another keyword, type in "fitness" and hit enter to see the matched results
7. When prompted to enter another keyword, type in "wearable" and hit enter to see the matched results 
8. When prompted to enter another keyword, type in "done" to end the program 

### Prerequisites

Ensure you are running Java Standard Edition Development Kit v.8 (JDK 8). If you are running from command line, you can check you version of java by running the following:

```
java -version 
```

Ensure you have downloaded sample datasets from [Indiegogo-datasets](https://webrobots.io/indiegogo-dataset)

## Deployment

N/A

## Contributing

N/A

## Versioning

N/A

## Authors

* **Alison Kennedy** 


## License


N/A

