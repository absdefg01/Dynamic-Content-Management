package Database;

import java.util.HashMap;

public class Album {
	private String artistId;
	private String albumId;
	private String albumName;
	private HashMap<String, String> albumList = new HashMap<String, String>();
	private HashMap<String, HashMap<String,String>> resList = new HashMap<String,HashMap<String,String>>();
	
	public Album(String artistId, String albumId, String albumName) {
		this.artistId = artistId;
		this.albumId = albumId;
		this.albumName = albumName;
	}
	
	public String getArtistId() {
		return this.artistId;
	}
	
	public String getAlbumId() {
		return this.albumId;
	}
	
	public String getAlbumName() {
		return this.albumName;
	}
	
	public HashMap<String,String> getAlbumList(){
		return this.albumList;
	}
	
	public HashMap<String, HashMap<String,String>> getResList(){
		return this.resList;
	}
	
	public void setAlbumList(String albumId, String albumName) {
		albumList.put(albumId, albumName);
	}
	
	public void setResList(String artistId, HashMap<String,String> albumList) {
		resList.put(artistId, albumList);
	}
}
