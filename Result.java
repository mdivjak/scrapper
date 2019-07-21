package scraping;

public class Result {
	private String lawName;
	private String lastUpdateDate;
	
	public Result(String lawName, String lastUpdateDate) {
		this.lawName = lawName;
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public String getLawName() { return lawName; }
	public String getLastUpdateDate() { return lastUpdateDate; }
	
	public String toString() {
		return "\"" + lawName + "\",\"" + lastUpdateDate + "\"";
	}
}
