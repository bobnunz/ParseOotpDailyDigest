package com.mikedogg.jsoupcb;

public class Hitter {
	
	private String player;
	private String team;
	private int ab;
	private int runs;
	private int hits;
	private int rbi;
	private int doubles;
	private int triples;
	private int hr;
	private int sb;
	private int err;
	
	public Hitter(String player, String team, int ab, int runs, int hits, int rbi, int doubles, int triples, int hr,
			int sb, int err) {
		super();
		this.player = player;
		this.team = team;
		this.ab = ab;
		this.runs = runs;
		this.hits = hits;
		this.rbi = rbi;
		this.doubles = doubles;
		this.triples = triples;
		this.hr = hr;
		this.sb = sb;
		this.err = err;
	}

	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public int getAb() {
		return ab;
	}
	public void setAb(int ab) {
		this.ab = ab;
	}
	public int getRuns() {
		return runs;
	}
	public void setRuns(int runs) {
		this.runs = runs;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public int getRbi() {
		return rbi;
	}
	public void setRbi(int rbi) {
		this.rbi = rbi;
	}
	public int getDoubles() {
		return doubles;
	}
	public void setDoubles(int doubles) {
		this.doubles = doubles;
	}
	public int getTriples() {
		return triples;
	}
	public void setTriples(int triples) {
		this.triples = triples;
	}
	public int getHr() {
		return hr;
	}
	public void setHr(int hr) {
		this.hr = hr;
	}
	public int getSb() {
		return sb;
	}
	public void setSb(int sb) {
		this.sb = sb;
	}
	public int getErr() {
		return err;
	}
	public void setErr(int err) {
		this.err = err;
	}

}
