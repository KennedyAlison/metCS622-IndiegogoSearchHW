import java.util.ArrayList;

/**
 * This class creates Search objects that keeps track of each word that is searched,
 * the frequency of times that word was searched, and the timestamps for each search 
 * @author Alison
 *
 */


public class SearchObject {
	String word;
	int freq;
	String time;
	ArrayList<String> times;

	// Constructor with given inputs 
	public SearchObject(String word, String time) {
		this.word = word;
		this.freq = 1;
		this.time = time;
		this.times = new ArrayList<String>();
		this.times.add(time);
	}

	// Set the time 
	public void setTime(String time) {
		this.time = time;
	}
	
	// Get the time 
	public String getTime() {
		return time;
	}

	// Get the search word 
	public String getWord() {
		return word;
	}

	// Get the frequency for how many times the word was searched 
	public int getFreq() {
		return freq;
	}

	// Set the frequency for how many times the word was searched 
	public void setFreq(int freq) {
		this.freq = freq;
	}

	// Get the time 
	public ArrayList<String> getTimes() {
		return times;
	}
	
	// Increase the frequency by 1
	public void increaseFreq() {
		this.freq++;
	}
	
	public void addTime(String t) {
		this.times.add(t);
	}
	
	@Override
	/**
	 * @return a specified format string representation of each search object 
	 */
   public String toString() {
	   String p =
				"Search Word: " + word +
	            "\n\tFrequency: " + freq +
	            "\n\tTimes: ";
	   for(int i=0; i< times.size(); i++) {
		   p += times.get(i) + ", ";
	   }
	   return p.substring(0, p.length() - 2);
   }	

}