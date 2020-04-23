package com.mikedogg.jsoupcb;



import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


public class ParseGame {
	
	static TreeMap<String,Hitter> getHitterPlayerStats (String gameUrl) throws IOException {
		
		TreeMap<String,Hitter> hitMap = new TreeMap<String,Hitter>();
		
	
// Retrieve doc from URL
		
		Document doc = Jsoup.connect(gameUrl).get();
		
		int loopVal=1;
		
		do {
		
// Get team hitter stats 
		
			Element batStatTbl = doc.selectFirst("#all_stat-table-"+String.valueOf(loopVal));
		
// get team hitter tbl
		
			String teamFullName = batStatTbl.child(0).child(0).ownText();
//			//System.out.println("visitTeamName= "+teamFullName);
		
// parse html table to get player stats
			String playerName="";
			int abs=0;
			int runs=0;
			int hits=0;
			int rbis=0;
			Element htmlVisitBatPlayers = batStatTbl.child(1).child(0).child(0).getElementsByTag("tbody").first();
			for (int i=0; i<htmlVisitBatPlayers.childrenSize();i++) {
//				//System.out.println(htmlVisitBatPlayers.child(i).childNodeSize()+" "+htmlVisitBatPlayers.child(i).childrenSize());
				for (int j=0; j<htmlVisitBatPlayers.child(i).childrenSize(); j++ ) {
//					//System.out.println("html tbl rows = "+htmlVisitBatPlayers.child(i).child(j).toString());
					Element playerRow = htmlVisitBatPlayers.child(i).child(j);
					switch (j) {
					case 0:
						playerName=playerRow.getElementsByTag("a").first().text();
						//System.out.println("Player= "+playerName);
					break;
					case 1:
						abs=Integer.parseInt(playerRow.ownText());
						//System.out.println("Abs= "+abs);
					break;
					case 2:
						runs=Integer.parseInt(playerRow.ownText());
						//System.out.println("Runs= "+runs);
					break;
					case 3:
						hits=Integer.parseInt(playerRow.ownText());
						//System.out.println("Hits="+hits);
					break;
					case 4:
						rbis=Integer.parseInt(playerRow.ownText());
						//System.out.println("RBIs="+rbis);
					break;
					}
				}
				hitMap.put(teamFullName+":"+playerName,new Hitter(playerName,teamFullName,abs,runs,hits,rbis,0,0,0,0,0) );

			}
			
			// The section immediately after a teams batting stats tables contains the
			// DOUBLES, TRIPLES, SB, and ERROR stats. These stats are enclosed in
			// a pair (hierchical) of <div> tags. the approach is to read each element
			// of the inner <div> tag and look at each child element. the 4 stats are
			// are BOLDED. 

			Element startPattern = doc.selectFirst("#all_stat-table-"+String.valueOf(loopVal)).nextElementSibling();
			Node currNode;
			Element currElement;
			String batCategory = "";
			String[] arrayOfTokens;

			startPattern = doc.selectFirst("#all_stat-table-"+String.valueOf(loopVal)).nextElementSibling();
			for (int i=0; i<startPattern.child(0).childrenSize(); i++)  {
//				//System.out.println("line= "+i+" "+startPattern.child(0).child(i).toString());
				Element tagName;
				tagName = startPattern.child(0).child(i);
//				//System.out.println("child= "+tagName.toString());
				// Check for Stats BOLD test
				if (tagName.tagName().equalsIgnoreCase("b") && tagName.ownText().equalsIgnoreCase("Doubles:")) {
					batCategory = "DOUBLES";
				}
				else if (tagName.tagName().equalsIgnoreCase("b") && tagName.ownText().equalsIgnoreCase("Triples:")) {
					batCategory = "TRIPLES";
				}
				else if (tagName.tagName().equalsIgnoreCase("b") && tagName.ownText().equalsIgnoreCase("Home Runs:")) {
					batCategory = "HOMERUNS";
				}
				else if (tagName.tagName().equalsIgnoreCase("b") && tagName.ownText().equalsIgnoreCase("SB:")) {
					batCategory = "SBS";
				}
				else if (tagName.tagName().equalsIgnoreCase("b") && tagName.ownText().equalsIgnoreCase("Errors:")) {
					batCategory = "ERRORS";
				}
				else if (tagName.tagName().equalsIgnoreCase("a") && ! batCategory.isEmpty()) {
					Hitter hit = hitMap.get(teamFullName+":"+tagName.ownText());
					// if hit returns null, create hitter stat for player (AL pitcher with sb or error)
					if (hit == null) {
						playerName=tagName.ownText();
						hitMap.put(teamFullName+":"+playerName,new Hitter(playerName,teamFullName,0,0,0,0,0,0,0,0,0) );
						hit = hitMap.get(teamFullName+":"+playerName);
//						System.out.println(teamFullName+":"+playerName);
					}

	// read the next node to get # of stats (blank if = 1)
					
					arrayOfTokens = startPattern.child(0).child(i).nextSibling().toString().trim().split("\\(");
					int valStat = 0;
					if ( arrayOfTokens[0].matches(" ?") ) {
						//System.out.println("Next line after <a> = 1");
						valStat = 1;
					}
					else {
						//System.out.println("Next line after <a> = "+arrayOfTokens[0]);
						valStat = Integer.parseInt(arrayOfTokens[0].trim());
					}
					switch(batCategory) {
					case "DOUBLES":
						hit.setDoubles(valStat);
					break;
					case "TRIPLES":
						hit.setTriples(valStat);
					break;
					case "HOMERUNS":
						hit.setHr(valStat);
					break;
					case "SBS":
						hit.setSb(valStat);
					break;
					case "ERRORS":
						hit.setErr(valStat);
					break;					
					}
					hitMap.replace(teamFullName+":"+tagName.ownText(), hit);
				}
				else
					batCategory = "";
				
			}
			
			loopVal++;

		} while (loopVal < 3);
/*
		Hitter hitter;
	      for(Map.Entry m: hitMap.entrySet()){
	    	  hitter = (Hitter) m.getValue();
	          System.out.println(hitter.getTeam()+" "
	    	  +hitter.getPlayer()+" "
	    	  +hitter.getAb()+" "
	    	  +hitter.getRuns()+" "
	    	  +hitter.getHits()+" "
	    	  +hitter.getRbi()+" "
	    	  +hitter.getDoubles()+" "
	    	  +hitter.getTriples()+" "
	    	  +hitter.getHr()+" "
	    	  +hitter.getSb()+" "
	    	  +hitter.getErr()+" "
	    	  );    
	         } 
 */
	      return hitMap;
	}

	static TreeMap<String,Pitcher> getPitcherPlayerStats (String gameUrl) throws IOException {
		
		TreeMap<String,Hitter> hitMap = new TreeMap<String,Hitter>();
		TreeMap<String, Pitcher> pitMap = new TreeMap<String, Pitcher>();
		
	
// Retrieve doc from URL
		
		Document doc = Jsoup.connect(gameUrl).get();
		
		Elements elementRet = doc.getElementsByAttributeValueMatching("id", "all_stat-table-[1]");

		
		Elements batStatTbls = doc.getElementsByAttributeValueMatching("id", "all_stat-table-[12]");
		
		int loopVal=3;
		
		do {


// Get team pitcher stats 
		
			Element pitStatTbl = doc.selectFirst("#all_stat-table-"+String.valueOf(loopVal));
		
// get team pitcher tbl
		
			String teamFullName = pitStatTbl.child(0).child(0).ownText();
			//System.out.println("visitTeamName= "+teamFullName);
		
// parse html table to get player stats
		
			Element htmlVisitBatPlayers = pitStatTbl.child(1).child(0).child(0).getElementsByTag("tbody").first();
			for (int i=0; i<htmlVisitBatPlayers.childrenSize();i++) {
//				//System.out.println(htmlVisitBatPlayers.child(i).childNodeSize()+" "+htmlVisitBatPlayers.child(i).childrenSize());
				String playerName="";
				float ip=0;
				int ers=0;
				int hits=0;
				int bbs=0;
				int ks = 0;
				int wins=0;
				int saves=0;
				int holds=0;
				for (int j=0; j<htmlVisitBatPlayers.child(i).childrenSize(); j++ ) {
//					//System.out.println("html tbl rows = "+htmlVisitBatPlayers.child(i).child(j).toString());
					Element playerRow = htmlVisitBatPlayers.child(i).child(j);
					switch (j) {
					case 0:
						playerName=playerRow.getElementsByTag("a").first().text();
						//System.out.println("Player= "+playerName);
						String[] arrayOfTokens = playerRow.child(0).nextSibling().toString().split("\\(");
						if ( arrayOfTokens[0].contains("W")) {
							//System.out.println("Win= 1");
							wins=1;
						}
						else if (arrayOfTokens[0].contains("SV")) {
							//System.out.println("Save= 1");
							saves=1;
						}
						else if (arrayOfTokens[0].contains("H")) {
							//System.out.println("Hold= 1");
							holds=1;
						}
					break;
					case 1:
						ip=Float.parseFloat(playerRow.ownText());
						//System.out.println("IP= "+ip);
					break;
					case 2:
						hits=Integer.parseInt(playerRow.ownText());
						//System.out.println("Hits= "+hits);
					break;
					case 4:
						ers=Integer.parseInt(playerRow.ownText());
						//System.out.println("ERs="+ers);
					break;
					case 5:
						bbs=Integer.parseInt(playerRow.ownText());
						//System.out.println("BBs="+bbs);
					break;
					case 6:
						ks=Integer.parseInt(playerRow.ownText());
						//System.out.println("Ks="+ks);
					break;
					}
				}
				pitMap.put(teamFullName+":"+playerName,new Pitcher(playerName,teamFullName,ip,hits,ers,bbs,ks,wins,saves,holds) );
			}

			loopVal++;
		} while (loopVal < 5);
		/*
    	Pitcher pitcher;
        for(Map.Entry m: pitMap.entrySet()){
	    	  pitcher = (Pitcher) m.getValue();
	          System.out.println(pitcher.getTeam()+" "
	    	  +pitcher.getPlayer()+" "
	    	  +pitcher.getIp()+" "
	    	  +pitcher.getHits()+" "
	    	  +pitcher.getEr()+" "
	    	  +pitcher.getBb()+" "
	    	  +pitcher.getK()+" "
	    	  +pitcher.getWin()+" "
	    	  +pitcher.getSave()+" "
	    	  +pitcher.getHold()+" "
	    	  );    
	       } 
	       */ 
        return pitMap;
		//System.out.println(startPattern.child(0).ownText());
		
	}
}
