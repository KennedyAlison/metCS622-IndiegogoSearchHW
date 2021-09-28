import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * This class handles zip files with a method to unzip all the contents of the zip files to a specified destination 
 * @author Alison
 *
 */

public class ZipFile {

	/**
	 * This method utilizes the ZipEntry and ZipInputStream packages to take in a list of zip files and unzip them to a specified location 
	 * @param files is an ArrayList of files to keep track of what files are to be merged later on
	 * @param zipFiles is an ArrayList of absolute path names for zip files to be unzipped 
	 * @param zipDest is a String for the absolute path for the destination of the files to be unzipped 
	 */
	public static void unzip(ArrayList<File> files, ArrayList<String> zipFiles, String zipDest) {
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
					System.out.println("Error with input streams reading the zip files: " + e.getMessage());
					e.printStackTrace();
			}
		}
		
		
	}

} 



