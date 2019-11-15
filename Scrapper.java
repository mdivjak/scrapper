package scraping;

import java.io.IOException;

import scraping.Errors.BadDataException;
import scraping.Errors.NoDateException;
import scraping.Errors.NoNameException;

abstract public class Scrapper extends Thread {
	private static int ID = 0;
	private int id = ++ID;
	private LinkQueue links;
	private ResultQueue results;
	private boolean running = false;
	
	public Scrapper(LinkQueue links, ResultQueue results) {
		this.links = links;
		this.results = results;
	}
	
	public abstract Result scrapeData(String url) throws IOException, NoNameException, NoDateException, BadDataException;
	
	public void run() {
		try {
			while(!interrupted()) {
				while(!running) synchronized(this) { wait(); }
				String link = links.get();
				if("quit".equals(link)) {
					links.put("quit");
					break;
				}
				try {
					Result result = scrapeData(link);
					results.put(link, result);
				} catch(IOException e) {}
				catch (NoNameException e) {}
				catch (NoDateException e) {}
				catch (BadDataException e) {}
			}
		} catch(InterruptedException e) {}
	}
	
	public synchronized void work() { running = true; notify(); }
	public synchronized void pause() { running = false; }
	public synchronized void halt() { interrupt(); }
}
