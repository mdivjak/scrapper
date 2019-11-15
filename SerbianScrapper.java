package scraping;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scraping.Errors.BadDataException;
import scraping.Errors.NoDateException;
import scraping.Errors.NoJsonException;
import scraping.Errors.NoNameException;
import scraping.Errors.NoUrlException;

public class SerbianScrapper extends Scrapper {
//	.reference-list.expanded
	public static final String LAST_REFERENCE_SELECTOR = "#collapseReferences > ul > div > ul > li > a"; 

	public SerbianScrapper(LinkQueue links, ResultQueue results) {
		super(links, results);
		start();
	}
	
	public static class Util {
		public static JsonObject getJsonObject(Document doc) throws NoJsonException {
			Elements scripts = doc.select("script");
			for(Element e : scripts) {
				String s = e.toString();
				boolean status = s.contains("actData");
				if(status) {
					String jsonString = s.substring(s.indexOf("{"), s.lastIndexOf(";"));
					JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
					return json;
				}
			}
			throw new Errors.NoJsonException();
		}
	
		public static String getUrl(Document doc) throws NoUrlException {
//			http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/viewdoc?actid=920142&doctype=og&abc=cba&injectEliUri=true
			Elements els = doc.select("h5");
			for(Element e : els) {
				if(e.text().contains("Основни текст")) {
					Element a = e.nextElementSibling().child(0).child(0);
					String link = a.attr("ng-click");
					Pattern p = Pattern.compile("\\('(.*)'\\)");
					Matcher m = p.matcher(link);
					if(m.find()) {
						return "http://www.pravno-informacioni-sistem.rs" + m.group(1);
					}
				}
			}
			
			throw new Errors.NoUrlException();
		}
		
		public static String extractLastUpdateDate(Document doc) throws NoDateException {
			Elements els = doc.select(".potpis");
			for(Element e : els) {
				if(e.text().matches(".*[0-9]*.*[0-9]{4}.*")) {
					Pattern p = Pattern.compile("([0-9]*)\\.(.*)([0-9]{4})");
					Matcher m = p.matcher(e.text());
					if (m.find()) {
						String date = m.group(1) + ".";
					    String month = "0";
					    switch(m.group(2).trim().substring(0, 3)) {
					    case "јан": month = "1"; break;
					    case "феб": month = "2"; break;
					    case "мар": month = "3"; break;
					    case "апр": month = "4"; break;
					    case "мај": month = "5"; break;
					    case "јун": month = "6"; break;
					    case "јул": month = "7"; break;
					    case "авг": month = "8"; break;
					    case "сеп": month = "9"; break;
					    case "окт": month = "10"; break;
					    case "нов": month = "11"; break;
					    case "дец": month = "12"; break;
					    }
					    date = date + month + "." + m.group(3) + ".";
					    return date;
					}
				}
			}
			throw new Errors.NoDateException();
		}
		
		public static String getLastUpdateDate(Document doc) throws NoJsonException, NoUrlException, IOException, NoDateException {
			JsonObject jsonObject = Util.getJsonObject(doc);
			JsonArray array = jsonObject.get("htmlLinks").getAsJsonArray();
			String date;
			if(array.size() == 0 || array.size() == 1) {
				date = Util.getMetaDate(doc);
			} else {
				String link = Util.getUrl(doc);
				Document dateDoc = Jsoup.connect(link).get();
				date = Util.extractLastUpdateDate(dateDoc);
			}
			return date;
		}
		
		public static String getMetaDate(Document doc) throws NoDateException {
			Elements els = doc.select("meta[property=\"eli:date_document\"]");
			if(els.size() == 0) throw new NoDateException();
			String[] content = els.get(0).attr("content").split("-");
			if(content.length != 3) throw new NoDateException();
			String date = content[2] + "." + content[1] + "." + content[0] + ".";
			return date;
		}
		
		public static String getLawName(Document doc) throws NoNameException {
			JsonObject jsonObject;
			try {
				jsonObject = getJsonObject(doc);
				String name = jsonObject.get("baseTitle").getAsString();
				return name;
			} catch (NoJsonException e) {
				throw new Errors.NoNameException();
			}
			
		}
	}
	
	@Override
	public Result scrapeData(String url) throws IOException, NoNameException, NoDateException, BadDataException {
		Document doc = Jsoup.connect(url).get();
		
		try {
			JsonObject jsonObject = Util.getJsonObject(doc);
			String name = jsonObject.get("baseTitle").getAsString();
			JsonArray array = jsonObject.get("htmlLinks").getAsJsonArray();
			String date;
			if(array.size() == 0 || array.size() == 1) {
				date = Util.getMetaDate(doc);
			} else {
				String link = Util.getUrl(doc);
				Document dateDoc = Jsoup.connect(link).get();
				date = Util.extractLastUpdateDate(dateDoc);
			}
			return new Result(name, date);
		} catch (NoJsonException | NoUrlException e) {
			throw new Errors.BadDataException();
		}
	}

}
