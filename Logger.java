package scraping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Logger {
	public static void log(String fileName, String text) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName), StandardCharsets.UTF_8));
		bw.write(text);
		bw.newLine();
		bw.close();
	}
	
	public static void loga(String fileName, String text) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName), StandardCharsets.UTF_8, true));
		bw.write(text);
		bw.newLine();
		bw.close();
	}
}
