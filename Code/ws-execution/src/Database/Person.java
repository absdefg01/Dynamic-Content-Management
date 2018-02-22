package Database;

import java.util.ArrayList;
import java.util.HashMap;

public class Person {
	private String name;
	private String id;
	private String beginDate;
	private String endDate;
	private String albumId;
	private String albumName;
	public HashMap<String, String> ablums = new HashMap<String,String>();
	
	// constructor
	public Person(String name, String id, String beginDate, String endDate) {
		this.name = name;
		this.id = id;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	// getters
	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	public String getBeginDate() {
		return this.beginDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public HashMap<String,String> getAlbums() {
		return this.ablums;
	}
	
	// mutators
	public void setAblums(String albumId, String ablumName) {
		this.ablums.put(albumId, ablumName);
	}
}
