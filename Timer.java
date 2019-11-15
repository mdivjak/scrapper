package scraping;

public class Timer {
	private long startTime;
	public void  start() {
		startTime = System.currentTimeMillis();
	}
	
	public long measure() {
		return System.currentTimeMillis() - startTime;
	}
}
