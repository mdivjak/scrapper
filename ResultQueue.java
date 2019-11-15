package scraping;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultQueue {
	private HashMap<String, Result> results = new HashMap<>();
	private long firstPutTime;
	private long lastMilestone;
	
	public synchronized void put(String key, Result result) {
		if(results.size() == 0) firstPutTime = lastMilestone = System.currentTimeMillis();
		results.put(key, result);
//		System.out.println("" + results.size() + ". " + (System.currentTimeMillis() - Test.startTime) + " " + result.toString());
		if(results.size() % 50 == 0) {
			long currentTime = System.currentTimeMillis();
			System.out.println("Time: " + (currentTime - firstPutTime) + "\t Lap: "
					+ (currentTime - lastMilestone) + "\t Done: " + results.size());
			lastMilestone = currentTime;
		}
	}
	
	public synchronized Object get(String key) {
		Object result = results.get(key);
		results.remove(key);
		return result;
	}
	
	public synchronized int size() { return results.size(); }
	
	public synchronized void saveToFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		for(Object result: results.values()) {
			writer.println(result.toString());
		}
		writer.close();
	}
}
