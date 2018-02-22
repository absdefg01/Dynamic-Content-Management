package Database;

import java.util.ArrayList;

public class Database {

	private String artistName;
	private String artistId;
	private String beginDate;
	private String endDate;
	private String albumId;
	private String albumName;
	private String title;
	private String duration;
	private ArrayList<String> result = new ArrayList<String>();
	
	public Database(String artistName, String artistId, String beginDate, String endDate, String albumId, String albumName, String title, String duration) {
		this.artistName = artistName;
		this.artistId = artistId;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.albumId = albumId;
		this.albumName = albumName;
		this.title = title;
		this.duration = duration;
	}
	
	public String toString() {
		return (this.artistName + "        " + this.artistId + "        " + this.beginDate + "        "
				+ this.endDate + "        " + this.albumId + "        " + this.albumName + "        " 
				+ this.title + "        " + this.duration);
	}
	
	public void resultMaker2(String line) {
		result.add(line);
	}
	
	public ArrayList<String> getResult(){
		return result;
	}
	
	public void resultMaker() {
		result.add(this.artistName);
		result.add(this.artistId);
		result.add(this.beginDate);
		result.add(this.endDate);
		result.add(this.albumId);
		result.add(this.albumName);
		result.add(this.title);
		result.add(this.duration);
	}
}
