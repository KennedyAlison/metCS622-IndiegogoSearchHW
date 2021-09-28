import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.*;


/**
 * This class is the main search class that searches a file for a specified word from the users,
 * prints out data realted to that keywords, and keeps track of each search done by the user
 * 
 * @author Alison
 *
 */

public class IndiegogoSearch {
	
	public static void main(String[] args) throws IOException {
		
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<String> zipFiles = new ArrayList<String>();
		ArrayList<SearchObject> searches = new ArrayList<SearchObject>();
		Set<String> searchWords = new HashSet<String>();
		
		// Using DateFormat format time stamps of searches 
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		// Zip files paths to be merged - REPLACE with absolute path to local file names 
		String zipFile1 = "/Users/Alison/eclipse-workspace/CS622/src/Indiegogo_2021-05-14T20_40_43_677Z.zip";
		String zipFile2 = "/Users/Alison/eclipse-workspace/CS622/src/Indiegogo_2021-04-16T20_40_39_964Z.zip";
		// Uncomment out the below code to add additional zip files
		// String zipFile3 = "";
		
		// Destination path for unzipped files - REPLACE with absolute path to desired file destination 
		String zipDest = "/Users/Alison/eclipse-workspace/CS622/src/";
		
		// Destination path for merged file data - REPLACE with absolute path to desired file destination 
		String mergedFile = "/Users/Alison/eclipse-workspace/CS622/src/Indiegogo_Merged.csv";
		
		// Add zip file names to array list for zip files to be unzipped and merged
		zipFiles.add(zipFile1);
		zipFiles.add(zipFile2);
		// Uncomment out the below code to add additional zip files
		// zipFiles.add(zipFile3);
		
		// Unzip all zipped files 
		ZipFile.unzip(files, zipFiles, zipDest);
			
		// Merge contents of all desired files 
		MergeFiles.mergeFiles(files, mergedFile);	
		
		// Use scanner to get user input for keywords to be searched 
		Scanner scanner = new Scanner(System.in);
		
		// Keep prompting the user to enter keywords until they specify they are done searching
		String keyword = "";
		do {
			System.out.println("Enter a keyword one at a time to search for then enter 'done' when finished: ");

			keyword = scanner.nextLine();
			
			// Get the current time
			Calendar cal = Calendar.getInstance();
			
			if(!keyword.equalsIgnoreCase("done")) {
				
				// Add the search word to the set of search words
				if(searchWords.contains(keyword.toLowerCase())){ // The word has already been searched, increase the frequency count and add the time searched
					int i = 0;
					while(i < searches.size()) {
						if(searches.get(i).getWord().equals(keyword.toLowerCase())) {
							searches.get(i).increaseFreq();
							searches.get(i).addTime(dateFormat.format(cal.getTime()));
							break;
						}
						i++;
					}
				}else {	// Search word has not previously been searched, add it to the set of search words and ArrayList of Search Objects
					searchWords.add(keyword.toLowerCase());
					SearchObject searchWord = new SearchObject(keyword.toLowerCase(), dateFormat.format(cal.getTime()));
					searches.add(searchWord);
				}
				
				// Keep count for the number of matched returned by the keyword
				int matchCount = 0;
				
				// Search for the keyword and get the number of matches 
				matchCount = searchKeyword(keyword, mergedFile);
				
				// Keep prompting the user for keywords until they enter a keyword that returns at least 1 match 
				while(matchCount < 1) {
					System.out.println(keyword + " returned 0 matches, please enter another keyword:");
					keyword = scanner.nextLine();
					matchCount = searchKeyword(keyword, mergedFile);
				}
				
				System.out.println(matchCount + " total number of matched entries found for " + keyword);
			}
		} while (!keyword.equalsIgnoreCase("done"));
		
		scanner.close();
		
		// Print out search history 
		printSearches(searches);
		
		
	}

	/**
	 * This method prints all of the SearchObject search words, including the search terms, their timestamps, and their frequency
	 * @param searches is an ArrayList of SearchObjects 
	 */
	private static void printSearches(ArrayList<SearchObject> searches) {
		System.out.println("Search History");
		// Iterate through ArrayList of search objects and print each one 
		for(int i=0; i < searches.size(); i++) {
			System.out.println(searches.get(i));
		}
		
	}


	/**
	 * This method takes in a string keyword and a file and searches that file for any lines containing that keyword 
	 * and prints desired data (fund_raised_percent and close_date) to the console
	 * @param keyword is the word to be searched in the contents of the file 
	 * @param mergedFile is the name of the file to be searched 
	 * @return the number of matched entries found 
	 * @throws FileNotFoundException if the file to be searched does not exist 
	 */
	private static int searchKeyword(String keyword, String mergedFile) throws FileNotFoundException {
		// Local variable to track the number of matched entried found 
		int count = 0;
		
		BufferedReader br =	null;
		String line	= "";
		
		System.out.println("\nSearch results matching keyword: " + keyword + "\n");
		
		try {
			br	= new BufferedReader(new FileReader(mergedFile));
			// Iterate through the contents of the file 
			while ((line = br.readLine()) != null) {
				// Search for the keyword using regex pattern 
				Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(line);
				boolean matchFound = matcher.find();
				// If the keyword is found, use regex expression to search for desired data within the line entry found 
				if(matchFound) {
					// Increment the count as a matched entry was found 
					count++;
					// Use a regex pattern to find and group the close_date, fund_raised_percent, and the projectID
					Pattern dataPattern = Pattern.compile("(\\d{4}-[01]\\d-[0-3]\\dT\\d{2}:\\d{2}:\\d{2}-\\d{2}:\\d{2})\",\"[A-Z]{3}\",\"[0-9]*\",\"(\\d+\\.?\\d*).*(\"[0-9]+)\",\"[a-zA-Z]*");
					Matcher dataMatcher = dataPattern.matcher(line);
					// When the data is found, print it out 
					while(dataMatcher.find()) {
						System.out.println(String.format("%s %-15.3f %s %-30s  %s %-35s", "fund_raised_percent:", Float.parseFloat(dataMatcher.group(2)), "close_date:", dataMatcher.group(1), "projectID:", dataMatcher.group(3).substring(1)));
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Merged File was not found: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading the merged file: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (br	!=	null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Error closing the BufferReader stream: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return count;
	} 
} 



