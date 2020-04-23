package com.mikedogg.jsoupcb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.opencsv.CSVReader;

public class ParseScoreBoard {

	static void ParseAllGames (String month, String day) throws IOException, URISyntaxException {
		
		String ownedPlayerFile="D://baseball//2020//OOTP//RefFiles//majors_players_matched_bxsc_to_ootp_v2.csv";
		String teamNames="D://baseball//2020//OOTP/RefFiles//Team_Names.csv";
		Document document = Jsoup.connect("https://www.baseball-reference.com/sim/daily.fcgi?month="+month+"&day="+day+"&year=2020").get();
		String title;
		Elements elements = document.select("a:contains(final)");
		TreeMap<String, Hitter> allHitMap = new TreeMap<String, Hitter>();
		TreeMap<String, Pitcher> allPitMap = new TreeMap<String, Pitcher>();
		
// get owned players
		
	    Reader reader = Files.newBufferedReader(Paths.get(ownedPlayerFile));
	    CSVReader csvReader = new CSVReader(reader);
	    List<String[]> list = new ArrayList<>();
	    list = csvReader.readAll();
	    reader.close();
	    csvReader.close();
	    
	    TreeMap<String, String> ownedHit = new TreeMap<String, String>();
	    TreeMap<String, String> ownedPit = new TreeMap<String, String>();
	    TreeMap<String, String> ownedAll = new TreeMap<String, String>();
	    for (String[] i:list) {
	    	ownedAll.put(i[2]+":"+i[4], i[0]);
	    	if ( i[0].equalsIgnoreCase("owner") ) 
	    		continue;
	    	else if ( i[1].equalsIgnoreCase("H")) {
	    		ownedHit.put(i[2]+":"+i[4], i[0]);
	    	}
	    	else if ( i[1].equalsIgnoreCase("P"))
	    		ownedPit.put(i[2]+":"+i[4], i[0]);
	    }

//System.out.println(ownedAll.size()+" "+ownedHit.size()+" "+ownedPit.size());
/*
for(String k:ownedHit.keySet()) {
//  	if (ownedHit.get(k).matches("MIN:J. Polanco"))
  			System.out.println(k);
}
*/
	    
// get full team names with abbrevitions
	    
	    reader = Files.newBufferedReader(Paths.get(teamNames));
	    csvReader = new CSVReader(reader);
	    list = new ArrayList<>();
	    list = csvReader.readAll();
	    reader.close();
	    csvReader.close();

	    TreeMap<String, String> teamsAbbrev = new TreeMap<String, String>();
	    for (String[] i:list) {
	    	teamsAbbrev.put(i[0], i[1]);
	    }
	    
	    // get manager results
	    
	    TreeMap<String, String> mgrResults = ParseResults.getMgrResults(month, day);
	    
	    // get all games and append player stats
		
		int j = 0;
		for (Element i:elements) {
			j++;
			allHitMap.putAll(ParseGame.getHitterPlayerStats(i.absUrl("href")));
			allPitMap.putAll(ParseGame.getPitcherPlayerStats(i.absUrl("href")));
/*
for(String k:allHitMap.keySet()) {
	String[] t=k.split(":");
	if (t[1].matches("J. Polanco"))
			System.out.println(k);
}
*/

//			myWriter.write(j+" hitsize= "+allHitMap.size()+" "+allPitMap.size());
		}
		
		Hitter hitter;
		TreeMap<String, String> ownedPlayerIdx = new TreeMap<String,String> ();
		String owner;
		String ownedPlayerIdxKey;
	    for(String  m: allHitMap.keySet()){
	    	  hitter = allHitMap.get(m);
//	    	  owner=ownedHit.get(teamsAbbrev.get(hitter.getTeam())+":"+hitter.getPlayer());
	    	  owner=ownedAll.get(teamsAbbrev.get(hitter.getTeam())+":"+hitter.getPlayer());
	    	  if ( owner != null) {
	    		  ownedPlayerIdxKey = owner+":H:"+teamsAbbrev.get(hitter.getTeam())+":"+hitter.getPlayer();
	    		  ownedPlayerIdx.put(ownedPlayerIdxKey, m);
	    	  }
	         } 

    	Pitcher pitcher;
        for(String m: allPitMap.keySet()) {
	    	  pitcher = allPitMap.get(m);
//	    	  owner=ownedPit.get(teamsAbbrev.get(pitcher.getTeam())+":"+pitcher.getPlayer());
	    	  owner=ownedAll.get(teamsAbbrev.get(pitcher.getTeam())+":"+pitcher.getPlayer());
	    	  if ( owner != null)  {
	    		  ownedPlayerIdxKey = owner+":P:"+teamsAbbrev.get(pitcher.getTeam())+":"+pitcher.getPlayer();
	    		  ownedPlayerIdx.put(ownedPlayerIdxKey, m);
	    	  }

	       } 
        
//  process by owner
        String prevOwner = " ";
        int[] ownerTotals = new int[10];
        float[] ipTotals = new float[3];
        int loopIdx = 0;
        FileWriter myWriter = new FileWriter("d:\\baseball\\2020\\OOTP\\DailyFiles\\dailyScores_"+month+"_"+day+"_2020.csv");
        DecimalFormat df = new DecimalFormat("##.#");
        for (String i: ownedPlayerIdx.keySet() ) {
      
        	loopIdx++;

        	String[] tokens=i.split(":");
        	if (!tokens[0].equalsIgnoreCase(prevOwner)) {
        		if ( !prevOwner.equals(" ")) {
        			String[] mgrInning = mgrResults.get(prevOwner).split(":");
        			myWriter.write("Totals ("+mgrInning[0]+"),,,"
        					+ownerTotals[0]+","
        					+ownerTotals[1]+","
        					+ownerTotals[2]+","
        					+ownerTotals[3]+","
        					+ownerTotals[4]+","
        					+ownerTotals[5]+","
        					+ownerTotals[6]+","
        					+ownerTotals[7]+","
        					+df.format(ipTotals[0])+","
        					+df.format(ipTotals[1])+","
        					+mgrInning[1]+","
        					+df.format(ipTotals[2])+","
        					+ownerTotals[8]+","
        					+ownerTotals[9]+","
        					+"\n\n");       			
        		}
        		myWriter.write("Owner,Player,Team,AB,NR,Hits,HRs,RBIs,SBs,ERRs,WHS,nIP,nKB,(MGR),PHits,2Bs,3Bs\n");
        		prevOwner=tokens[0];
        		Arrays.fill(ownerTotals,0);
        		Arrays.fill(ipTotals, 0);
        	}
        	if (tokens[1].equalsIgnoreCase("H")) {
        		String AllHitMapKey=ownedPlayerIdx.get(i);
        		hitter=allHitMap.get(AllHitMapKey);
        		myWriter.write(tokens[0]+","
		    	  +hitter.getPlayer()+","
        		  +tokens[2]+","
		    	  +hitter.getAb()+","
		    	  +Integer.toString(hitter.getRuns()-hitter.getHr())+","
		    	  +hitter.getHits()+","
		    	  +hitter.getHr()+","
		    	  +hitter.getRbi()+","
		    	  +hitter.getSb()+","
		    	  +hitter.getErr()+","
		    	  +"0,0,0,0,0,"
		    	  +hitter.getDoubles()+","
		    	  +hitter.getTriples()+"\n"
        		);
		    	ownerTotals[0] += hitter.getAb();
        		ownerTotals[1] += (hitter.getRuns()-hitter.getHr());
		    	ownerTotals[2] += hitter.getHits();
		    	ownerTotals[4] += hitter.getRbi();
		    	ownerTotals[3] += hitter.getHr();
		    	ownerTotals[5] += hitter.getSb();
		    	ownerTotals[6] += hitter.getErr();
        		if ( ownedPlayerIdx.size() == loopIdx ) {
        			String[] mgrInning = mgrResults.get(prevOwner).split(":");
        			myWriter.write("Totals ("+mgrInning[0]+"),,,"
        					+ownerTotals[0]+","
        					+ownerTotals[1]+","
        					+ownerTotals[2]+","
        					+ownerTotals[3]+","
        					+ownerTotals[4]+","
        					+ownerTotals[5]+","
        					+ownerTotals[6]+","
        					+ownerTotals[7]+","
        					+df.format(ipTotals[0])+","
        					+df.format(ipTotals[1])+","
        					+mgrInning[1]+","
        					+df.format(ipTotals[2])+","
        					+ownerTotals[8]+","
        					+ownerTotals[9]+","
        					+"\n\n");       			
        		}
       		
        	}
        	else if (tokens[1].equalsIgnoreCase("P")) {
        		String AllPitMapKey=ownedPlayerIdx.get(i);
        		pitcher=allPitMap.get(AllPitMapKey);
        		String convToStr = Float.toString(pitcher.getIp());
        		if (! convToStr.endsWith("0")) {
        			float testIP=(int) pitcher.getIp();
        			if (convToStr.endsWith("1"))
        				pitcher.setIp((float) (testIP+.3));
        			else
        				pitcher.setIp((float) (testIP+.7));
         		}

        		myWriter.write(tokens[0]+","
		    	  +pitcher.getPlayer()+","
        		  +tokens[2]+","
        		  +"0,0,0,0,0,0,0,"
		    	  +Integer.toString(pitcher.getWin()+pitcher.getSave()+pitcher.getHold())+","
		    	  +df.format(pitcher.getIp()-pitcher.getEr())+","
		    	  +Integer.toString(pitcher.getK()-pitcher.getBb())+","
		    	  +"0,"
		    	  +df.format(pitcher.getIp()-pitcher.getHits())+","
		    	  +"0,0\n"
        		);

		    	ownerTotals[7] += (pitcher.getWin()+pitcher.getSave()+pitcher.getHold());
		    	ipTotals[0] += (pitcher.getIp()-pitcher.getEr());
		    	ipTotals[1] += (pitcher.getK()-pitcher.getBb());
		    	ipTotals[2] += (pitcher.getIp()-pitcher.getHits());

        		if ( ownedPlayerIdx.size() == loopIdx ) {
        			String[] mgrInning = mgrResults.get(prevOwner).split(":");
        			myWriter.write("Totals ("+mgrInning[0]+"),,,"
        					+ownerTotals[0]+","
        					+ownerTotals[1]+","
        					+ownerTotals[2]+","
        					+ownerTotals[3]+","
        					+ownerTotals[4]+","
        					+ownerTotals[5]+","
        					+ownerTotals[6]+","
        					+ownerTotals[7]+","
        					+df.format(ipTotals[0])+","
        					+df.format(ipTotals[1])+","
        					+mgrInning[1]+","
        					+df.format(ipTotals[2])+","
        					+ownerTotals[8]+","+
        					+ownerTotals[9]+","+
        					"\n\n");       			
        		}
       		
        	}

        }	

        myWriter.close();
	    TreeMap<String, String> rawPlayers = Unduplicate.UnDupRawPlayers();
	    int idx=0;
	    for (String s:rawPlayers.keySet()) {
//	    	System.out.println(s+","+rawPlayers.get(s));
	    }
	    ownedAll.clear();
	    reader = Files.newBufferedReader(Paths.get(ownedPlayerFile));
	    csvReader = new CSVReader(reader);
	    list = new ArrayList<>();
	    list = csvReader.readAll();
	    reader.close();
	    csvReader.close();

	    for (String[] i:list) {
//	    	System.out.println(i[0]+","+i[2]+","+i[3]+","+i[4]+",https://www.baseball-reference.com"+rawPlayers.get(i[4]));
//	    	ownedAll.put(i[4], i[0]);
//	    	if (rawPlayers.get(i))
	    }

	}
		
}