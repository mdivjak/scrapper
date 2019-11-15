package scraping;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import scraping.Errors.NoDateException;
import scraping.Errors.NoJsonException;
import scraping.Errors.NoNameException;
import scraping.Errors.NoUrlException;

public class Test {
	
	public static void main(String[] args) {
		String[] links = {"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/skupstina/zakon/2015/112/19/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/uredba/2018/30/2/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/ministarstva/pravilnik/2019/35/7/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/skupstina/zakon/2006/62/7/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/uredba/2019/18/2/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/uredba/2018/30/4/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/odluka/2016/93/2/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/odluka/2011/5/1/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/odluka/2010/87/5/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/vlada/odluka/2010/3/2/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/ministarstva/pravilnik/2017/48/2/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/ministarstva/pravilnik/2017/16/7/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/ministarstva/pravilnik/1997/60/1/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/ministarstva/pravilnik/1994/23/1/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgrs/ministarstva/pravilnik/1993/33/1/reg",
				"http://www.pravno-informacioni-sistem.rs/SlGlasnikPortal/eli/rep/sgsrs/drugidrzavniorganiorganizacije/uputstvo/1977/3/1/reg"};
		Timer timer = new Timer(); timer.start();
/* 0 odakle je izvukao datum 29.12.2015., treba 30.12.2015.
 * 4 netacno je izvukao datum 14.03.2019., treba 23.05.2019.
 * 6 get document +
 * 8 datum + nije uvek radio selektor pa sam pretrazivao po tagu h5 koji sadrzi tekst Osnovni tekst i unuce njegovog brata sadrzi datum
 * 9 get document | datum
 * 10 datum + nije radio regex za vadjenje datuma jer je jednocifren broj dana u mesecu
 * 12 datum atribut je href, sadrzi los link
 * 13 datum +
 * 14 datum +
 * 15 datum +
 * */
		try {
			Logger.log("log\\results.csv", "Rezultati");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i = 0; i < links.length; i++) {
			Document doc; int j = 0;
			while(true) {
				System.out.println("Try " + (++j));
				try {
					doc = Jsoup.connect(links[i]).get();
					break;
				} catch(IOException e) {}
			}
				try {
					Logger.log("log\\site.html", doc.toString());
				} catch (IOException e) {}
				String name, date;
				try {
					name = SerbianScrapper.Util.getLawName(doc);
				} catch(NoNameException err) {
					name = "FAILED";
				}
				try {
					date = SerbianScrapper.Util.getLastUpdateDate(doc);
				} catch(NoJsonException | NoUrlException | IOException | NoDateException err) {
					date = "FAILED";
				}
				System.out.println(i + ". " + name + "\t "+ date);
				try {
					Logger.loga("log\\results.csv", links[i] + ",\"" + name + "\"," + date);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		System.out.println("done");
	}
}
