package scraping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class SheetsQuickstart {
	private static final String APPLICATION_NAME = "Google Sheets API Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new BufferedReader(new FileReader(CREDENTIALS_FILE_PATH)));
    	GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
    			.setAccessType("offline")
    			.build();
    	LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    	return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
    private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
    	final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    	return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    			.setApplicationName(APPLICATION_NAME)
    			.build();
    }
    
    public static void main(String... args) throws IOException, GeneralSecurityException {
        final String spreadsheetId = "1Fo5gedlD4rfBd0peBq7VJIUuRmbWho_ByEShT9JIaRE";
        final int NUM_CORES = Runtime.getRuntime().availableProcessors();
        long startTime = System.currentTimeMillis();
        long lastMeasureTime = startTime;
        Sheets service = getSheetsService();
        
        String range = "Testiranje programa!B2:B";
        ValueRange result = service.spreadsheets().values().get(spreadsheetId, range).execute();
        int numRows = result.getValues() != null ? result.getValues().size() : 0;
        System.out.printf("%d rows retrieved in %d ms\n", numRows, System.currentTimeMillis() - startTime);
        lastMeasureTime = System.currentTimeMillis();
        
        List<List<Object>> cells = result.getValues();
        LinkQueue links = new LinkQueue();
        for(List<Object> row : cells)
			links.put((String) row.get(0));
        links.put("quit");
        
        System.out.println("Loaded links...");
		Scrapper[] workers = new Scrapper[NUM_CORES];
		lastMeasureTime = System.currentTimeMillis();
		ResultQueue results = new ResultQueue();
		for(int i = 0; i < workers.length; i++) {
			workers[i] = new SlovakianScrapper(links, results);
			workers[i].work();
		}
		System.out.println("Workers started working...");
		for(int i = 0; i < workers.length; i++) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long workTime = System.currentTimeMillis() - lastMeasureTime;
		System.out.println("Worked for: " + workTime + " ms");
		
//		for(List<Object> row : cells) {
//			String key = (String) row.get(0);
//			System.out.println(results.get(key).toString());
//		}
		
		lastMeasureTime = System.currentTimeMillis();
		List<List<Object>> updateValues = new ArrayList<List<Object>>();
		for(List<Object> row : cells) {
			List<Object> thisRow = new ArrayList<Object>();
			Result entry = (Result) results.get((String) row.get(0));
			thisRow.add(entry.getLawName());
			thisRow.add(entry.getLastUpdateDate());
			updateValues.add(thisRow);
		}
		
		ValueRange body = new ValueRange().setValues(updateValues);
		UpdateValuesResponse updateResponse =
		        service.spreadsheets().values().update(spreadsheetId, "Testiranje programa!C2:D", body)
		                .setValueInputOption("RAW")
		                .execute();
		System.out.printf("%d cells updated in %d ms\n", updateResponse.getUpdatedCells(), System.currentTimeMillis() - lastMeasureTime);
        
        System.out.println("Total Time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}