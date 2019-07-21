package scraping;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SlovakianScrapper extends Scrapper {	
	private static final String LAST_UPDATE_SELECTOR = "#p_p_id_enactmentSearchChronologic_WAR_portletsez_ > div > div > div.predpisFullWidth > div.span8 > div.ucinnost_header";
	private static final String TITLE_SELECTOR = "div.predpisTyp";
	private static final String THEME_SELECTOR = ".predpisNadpis";
	private static final String DATE_ATTRIBUTE = "data-from";
	
	public static class Util {
		public static String getLawName(Document doc) {
			Elements title = doc.select(TITLE_SELECTOR);
			Elements theme = doc.select(THEME_SELECTOR);
			String titleText = title.text().toLowerCase();
			String fullText = "";
			if(titleText != null && titleText.length() > 1)
				fullText = String.valueOf(titleText.charAt(0)).toUpperCase() + titleText.substring(1);
			return fullText + " " + theme.text();
		}
		
		public static String getLastUpdateDate(Document doc) {
			Elements dates = doc.select(LAST_UPDATE_SELECTOR);
			return dates.attr(DATE_ATTRIBUTE);
		}
	}

	public SlovakianScrapper(LinkQueue links, ResultQueue results) {
		super(links, results);
		start();
	}
	
	@Override
	public Result scrapeData(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		
		String lawName = Util.getLawName(doc);
		String lastUpdateDate = Util.getLastUpdateDate(doc);
		return new Result(lawName.trim(), lastUpdateDate.trim());
	}

}
