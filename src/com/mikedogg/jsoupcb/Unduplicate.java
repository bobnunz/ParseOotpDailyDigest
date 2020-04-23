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
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.opencsv.CSVReader;

public class Unduplicate {
	
	static TreeMap<String, String> UnDupRawPlayers() throws IOException {
		
		TreeMap<String, String> unDupAllPlayers = new TreeMap<String, String>();
		String allOotpPlayersFile="D://baseball//2020//allOotpPlayersORIG.csv";
		Document playerDoc = Jsoup.connect("https://www.baseball-reference.com/sim/leagues/MLB/2020-batting.shtml").get();
//		Document pitDoc = Jsoup.connect("https://www.baseball-reference.com/sim/leagues/MLB/2020-pitching.shtml").get();
        int idx=0;
		do {
			idx++;
			if (idx == 2)
				playerDoc = Jsoup.connect("https://www.baseball-reference.com/sim/leagues/MLB/2020-pitching.shtml").get();
				
		    Elements tableElements=playerDoc.getElementsByTag("tr");
//		    		getElementsByAttribute("data-append-csv");
		    for(Element e:tableElements) {
		    	if (e.child(1).hasAttr("data-append-csv") ) {	
			    	String[] playerFullName=e.child(1).attr("csk").split(",");
//			    	String playerLastName = playerFullName[0];
			    	String playerFirstInit = playerFullName[1].charAt(0)+".";
			    	
			    	String playerLink=e.child(1).attr("data-append-csv");
			    	String playerURL=e.child(1).getElementsByTag("a").first().attr("href");
			    	playerFullName = e.child(1).getElementsByTag("a").first().text().split(" ");
			    	String playerLastName;
			    	if (playerFullName.length == 2)
			    		playerLastName = playerFullName[1];
			    	else
			    		playerLastName = playerFullName[1]+" "+playerFullName[2];
			    	String playerTeam=e.child(2).child(0).text();
//			    	unDupAllPlayers.put(playerURL, playerFirstInit+" "+playerLastName+","+playerTeam);
			    	unDupAllPlayers.put(playerFirstInit+" "+playerLastName,playerURL);
//			    	System.out.println(playerFirstInit+" "+playerLastName+" "+playerLink+" "+playerURL+" "+playerTeam);
		    	}
		    } 
		} while (idx < 2);
	    
	    return unDupAllPlayers;
		
	}

}
