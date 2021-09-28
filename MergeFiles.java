import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.*;


/**
 * This class has one method to take in multiple csv files and merge the contents within each file to create a single, merged file
 * 
 * @author Alison
 *
 */

public class MergeFiles {

	
	/**
	 * This method takes in an ArrayList of Files and a desired output file name then loops through the files to be merged, 
	 * removes any duplicated based on a unique projectID, and merges all remaining file contents to the specified output file location 
	 * @param files is an ArrayList of Files contained all files to be merged 
	 * @param mergedFile if the name of the absolute path, including the file name, to where the merged file will go 
	 * @throws IOException can occur whenever an input or output operation fails 
	 */
	public static void mergeFiles(ArrayList<File> files, String mergedFile) throws IOException {
		// Create an ArrayList to keep track of unique projectIDs
		ArrayList<String> projectIDs = new ArrayList<String>();
		// Keep track of the headers for the csv files 
		String[] headers = null; 
		BufferedReader br =	null;
		
		
		// Create output file for merged data, if file already exists, delete existing file 
		File fileOutput = new File(mergedFile);
		if(fileOutput.exists()) {
			fileOutput.delete();
		}
		try {
			fileOutput.createNewFile();
		} catch(IOException e) {
			System.out.println("Error in creating new merged file: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Create an iterator to iterate over the files to be merged 
		Iterator<File> fileIter = files.iterator();
		
		BufferedWriter wr = new BufferedWriter(new FileWriter(mergedFile, true));
		
		// Create a scanner item to get the headers of the first file 
		Scanner scanner = new Scanner(files.get(0));
	
		if(scanner.hasNextLine()) {
			headers = scanner.nextLine().split(",");
		}
		
		scanner.close();
	
		// Write the headers to the new file 
		for(String s: headers) {
			wr.write(s);
			wr.write(",");
		}
		wr.newLine();
		
		// Iterate through entire ArrayList of files 
		while(fileIter.hasNext()) {
			try {
				String line	= "";
				String[] firstLine = null;
				
				File nextFile = fileIter.next();
				br = new BufferedReader(new FileReader(nextFile));
				
				// Get the first line of the file to compare headers across files 
				if((line = br.readLine()) != null) {
					firstLine = line.split(",");
				}
				// If the headers are different, throw an exception 
				if(!Arrays.equals(headers, firstLine)) {
					throw new Exception("Header mismatch between files: " + files.get(0) + " and " + nextFile.getAbsolutePath());
				}
				// Iterate through the contents of the file, only adding unique projectIDs to the merged file 
				while ((line = br.readLine()) != null) {
					// Pattern to identify projectID
					Pattern pattern = Pattern.compile("(\"[0-9]+)\",\"([a-zA-Z]*)\"", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(line);
					boolean matchFound = matcher.find();
					if(matchFound) {
						// Add project ID to ArrayList if it is not already there 
						if(!projectIDs.contains(matcher.group(1).substring(1))) {
							projectIDs.add(matcher.group(1).substring(1));
							// Only add unique project IDs
							wr.write(line);
							wr.newLine();
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Unable to find input file: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error while reading input file and writing to merged file: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Error while merging files: " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		wr.close();
		
	}

	
}