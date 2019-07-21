package scraping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class LinkQueue {
	private ArrayList<String> links = new ArrayList<>();
	
	
	public synchronized void put(String link) {
		links.add(link);
		notifyAll();
	}
	
	public synchronized String get() throws InterruptedException {
		while(links.size() == 0) wait();
		String result = links.get(0);
		links.remove(0);
		notifyAll();
		return result;
	}
	
	public synchronized void loadFromFile(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Stream<String> stringStream = reader.lines();
		stringStream.forEach(line -> { links.add(line); });
		reader.close();
	}
	
	public synchronized int size() { return links.size(); }
}
