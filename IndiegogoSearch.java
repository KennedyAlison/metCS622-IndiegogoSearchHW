import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IndiegogoSearch {
	
	public static void main(String[] args) throws IOException {
		
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<String> zipFiles = new ArrayList<String>();
		
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
		unzip(files, zipFiles, zipDest);
			
		// Merge contents of all desired files 
		mergeFiles(files, mergedFile);	
		
		// Use scanner to get user input for keywords to be searched 
		Scanner scanner = new Scanner(System.in);
		
		// Keep prompting the user to enter keywords until they specify they are done searching
		String keyword = "";
		do {
			System.out.println("Enter a keyword one at a time to search for then enter 'done' when finished: ");

			keyword = scanner.nextLine();
			
			if(!keyword.equalsIgnoreCase("done")) {
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
		
	}

	/**
	 * This method utilizes the ZipEntry and ZipInputStream packages to take in a list of zip files and unzip them to a specified location 
	 * @param files is an ArrayList of files to keep track of what files are to be merged later on
	 * @param zipFiles is an ArrayList of absolute path names for zip files to be unzipped 
	 * @param zipDest is a String for the absolute path for the destination of the files to be unzipped 
	 */
	private static void unzip(ArrayList<File> files, ArrayList<String> zipFiles, String zipDest) {
		// Location for unzipped files to go 
		File dir = new File(zipDest);
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		FileInputStream fis;
		
		// Create buffer for reading and writing to file 
		byte[] buffer = new byte[1024];
		
		// Loop through all zip files to be unzipped 
		for(int i=0; i < zipFiles.size(); i++) {
			try {
				// Create input streams for the files and zip files 
				fis = new FileInputStream(zipFiles.get(i));
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry ze = zis.getNextEntry();
				// Loop through all the files in the zip file 
				while(ze != null) {
					// Create a name for the file 
					String fileName = zipFiles.get(i).substring(52,76)+ "_" + ze.getName();
					// Create the file 
					File newFile = new File(zipDest + File.separator + fileName);
					// Add the file to the ArrayList of files to be merged later on 
					files.add(newFile);
					
					// Write the files to the specified output directory 
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while((len = zis.read(buffer)) > 0){
						fos.write(buffer, 0 , len);
					}
					fos.close();
					zis.closeEntry();
					ze = zis.getNextEntry();
					}
				}catch (IOException e) {
					e.printStackTrace();
			}
		}
		
		
	}

	/**
	 * This method takes in an ArrayList of Files and a desired output file name then loops through the files to be merged, 
	 * removes any duplicated based on a unique projectID, and merges all remaining file contents to the specified output file location 
	 * @param files is an ArrayList of Files contained all files to be merged 
	 * @param mergedFile if the name of the absolute path, including the file name, to where the merged file will go 
	 * @throws IOException can occur whenever an input or output operation fails 
	 */
	private static void mergeFiles(ArrayList<File> files, String mergedFile) throws IOException {
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br	!=	null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	} 
} 



