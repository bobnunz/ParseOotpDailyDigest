package com.mikedogg.jsoupcb;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.opencsv.CSVReader;

public class ParseResults {
	
	public static TreeMap<String,String> getMgrResults (String month, String day) throws IOException {
		String ownedMgrFile="D://baseball//2020//OOTP//RefFiles//Owned_mgrs.csv";
		Document document = Jsoup.connect("https://www.baseball-reference.com/sim/daily.fcgi?month="+month+"&day="+day+"&year=2020").get();
		
		// read in file containing owners with thier team mgr
		
	    Reader reader = Files.newBufferedReader(Paths.get(ownedMgrFile));
	    CSVReader csvReader = new CSVReader(reader);
	    List<String[]> list = new ArrayList<>();
	    list = csvReader.readAll();
	    reader.close();
	    csvReader.close();
	    
	    //insert mgrs into TreeMap for later lookup
	    
	    TreeMap<String, String> keyTeamValOwner = new TreeMap<String, String>();
	    TreeMap<String, String> keyOwnerValTeam = new TreeMap<String, String>();
	    for (String[] i:list) {
	    	if (i[2] != null && !i[2].isEmpty() && !i[2].contentEquals(" "))
	    		keyTeamValOwner.put(i[0], i[2]);
    			keyOwnerValTeam.put(i[2], i[0]+":0");
	    }

	    // every table has a class called winner - select to get winning teams
	    
	    Elements winnerElements=document.getElementsByAttributeValue("class", "winner");
	    
	    // loop through all winners, set keyOwnerValTeam with status (1=win, default=0)
	    
	    for (Element e: winnerElements) {
	    	String winningTeam = e.child(0).child(0).ownText();
	    	String winningOwner = keyTeamValOwner.get(winningTeam);
	    	if ( winningOwner != null) {
	            String[] ownerValue=keyOwnerValTeam.get(winningOwner).split(":");
	    		int ownerWins=Integer.parseInt(ownerValue[1]);
	    		keyOwnerValTeam.replace(winningOwner,winningTeam+":"+(++ownerWins) );	
	    	}
	    }
	    	    
	    return keyOwnerValTeam;
	}

}
