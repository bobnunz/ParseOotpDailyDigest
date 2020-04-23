package com.mikedogg.jsoupcb;

public class Pitcher {
	
	private String player;
	private String team;
	private float ip;
	private int hits;
	private int er;
	private int bb;
	private int k;
	private int win;
	private int save;
	private int hold;

	public Pitcher(String player, String team, float ip, int hits, int er, int bb, int k, int win, int save,
			int hold) {
		super();
		this.player = player;
		this.team = team;
		this.ip = ip;
		this.hits = hits;
		this.er = er;
		this.bb = bb;
		this.k = k;
		this.win = win;
		this.save = save;
		this.hold = hold;
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
	public float getIp() {
		return ip;
	}
	public void setIp(float ip) {
		this.ip = ip;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public int getEr() {
		return er;
	}
	public void setEr(int er) {
		this.er = er;
	}
	public int getBb() {
		return bb;
	}
	public void setBb(int bb) {
		this.bb = bb;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	public int getSave() {
		return save;
	}
	public void setSave(int save) {
		this.save = save;
	}
	public int getHold() {
		return hold;
	}
	public void setHold(int hold) {
		this.hold = hold;
	}

}
