import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Database.Album;
import Database.Database;
import Database.Person;
import download.WebService;
import parsers.ParseResultsForWS;
import parsers.WebServiceDescription;

public class QueryEngine {
	/**
	 * a function to check the form of query
	 * @param query : complex query entered by people
	 * @throws Exception when the form of the query is not good
	 */
	public static void checkQuery(String query) throws Exception {
		if(!query.contains("<-")) {
			throw new Exception("the form of query is not good");
		}
		String[] splits = query.split("<-");
		String[] queries = parseQuery(query);
		boolean check = false;
		for(String q : queries) {
			String head = getHead(q);
			if(head.equals("getArtistInfoByName") || head.equals("getAlbumByArtistId") || head.equals("getSongByAlbumId")) {
				check = true;
			}
		}
		
		if(splits.length != 2 || queries.length > 3 || check==false) {
			throw new Exception("the form of query is not good");
		}
	}
	
	/**
	 * a function that permits to check the output entered by people 
	 * and store the index of outputs in a list
	 * for the convenience of the show of results
	 * @param query : complex query entered by people
	 * @return : a list containing index of outputs in the database
	 * @throws Exception when the output entered by people is not a parameter of query, throw exception
	 */
	public static ArrayList<Integer> getOutput(String query) throws Exception {
		ArrayList<Integer> outputIndex = new ArrayList<Integer>();
		String[] splits = query.split("<-");
		String[] queries = splits[1].split("#");
		String output = splits[0];		
		String outputs = output.split("[()]")[1];
		String[] otpList = outputs.split(",");
		String res = "";
		
		HashSet<String> parameters = new HashSet<String>();
		for(String s : queries) {
			String[] params = getParameters(s);
			for(String p : params) {
				String pp = p.split("\\?")[1];
				parameters.add(pp);
			}
		}

		for(String s : otpList) {
			res = s.split("\\?")[1];
			if (!res.equals("name") && !res.equals("id") && !res.equals("b") && !res.equals("e") 
					&& !res.equals("aid") && !res.equals("albumName") && !res.equals("title") && !res.equals("duration")) {
				throw new Exception("There is no output that you enter in the parameter of query");
			}
			
			if(!parameters.contains(res)) {
				throw new Exception("There is no output that you enter in the parameter of query");
			}
			
			if(res.equals("name")) {
				outputIndex.add(0);
			}
			if(res.equals("id")) {
				outputIndex.add(1);
			}
			if(res.equals("b")) {
				outputIndex.add(2);
			}
			if(res.equals("e")) {
				outputIndex.add(3);
			}
			if(res.equals("aid")) {
				outputIndex.add(4);
			}
			if(res.equals("albumName")) {
				outputIndex.add(5);
			}
			if(res.equals("title")) {
				outputIndex.add(6);
			}
			if(res.equals("duration")) {
				outputIndex.add(7);
			}
		}
		return outputIndex;
	}
	
	/**
	 * a function who returns every single query in a list
	 * @param query : composed query who contains some queries combining by #
	 * @return a list of String that contains single queries
	 */
	public static String[] parseQuery(String query) {
		String[] splits = query.split("<-");
		String queries = splits[1];
		return queries.split("#");
	}

	/**
	 * a function who returns the head of the query
	 * @param parsedQueryPart : single query obtained from the complex queries
	 * @return obtain the head of the every single query
	 */
	public static String getHead(String parsedQueryPart) {
		return parsedQueryPart.split("[()]")[0];
	}

	/**
	 * a function who returns the parameters of every single query
	 * @param parsedQueryPart : single query obtained from the complex queries
	 * @return obtain the parameters of the every single query
	 */
	public static String[] getParameters(String parsedQueryPart) {
		String[] param = parsedQueryPart.split("[()]")[1].split(",");
		Pattern pat = Pattern.compile("=.*", Pattern.CASE_INSENSITIVE);
		for (int i = 0; i < param.length; i++) {
			Matcher m = pat.matcher(param[i]);
			param[i] = m.replaceAll("");
		}
		return param;
	}

	/**
	 * a function to obtain the value of input
	 * @param parsedQueryPart : single query
	 * @return input value entered by people
	 */
	public static String getInputValue(String parsedQueryPart) {
		Pattern pattern = Pattern.compile("=(.*?),", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(parsedQueryPart);
		return (matcher.find()) ? matcher.group(1): null;
	}
	
	/**
	 * a function realize the web service getArtistInfoByName
	 * @param singleQuery : single query
	 * @param artists : list to store the properties of artist
	 * @param dataList : list to store the data obtained by the call of web service
	 * @param db : class database which aims to store the the data obtained
	 * @param resultList : store all data obtained from the web service to generate the final result
	 * @throws Exception 
	 */
	public static void ws_getArtistInfoByName(String singleQuery, HashMap<String, String> artists, LinkedList<Person> dataList, Database db, ArrayList<ArrayList<String>> resultList) throws Exception{
		String headWS1 = getHead(singleQuery);
		String input = getInputValue(singleQuery);
		if(input != null) {
			Thread.sleep(500);
			WebService ws = WebServiceDescription.loadDescription("mb_" + headWS1);
			String fileWithCallResult = ws.getCallResult(input);
			System.out.println("The call is   **" + fileWithCallResult + "**");
			String fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
			ArrayList<String[]> listOfTupleResult = ParseResultsForWS.showResults(fileWithTransfResults, ws);
			System.out.println();
			System.out.println("WebService : " + headWS1);
			System.out.println("Single Query executed : " + singleQuery);
			System.out.println("According to the first single query, we know the output composed by the artistId, the beginDate and the endDate");
			System.out.println("The tuple results : ");
			System.out.println("artistName" + "				" + "    artistId" + "				" + "		beginDate" + "				" + "endDate");
			for (String[] tuple : listOfTupleResult) {
				String artistName = tuple[0];
				String artistId = tuple[1];
				String beginDate = tuple[2];
				String endDate = tuple[3];
				System.out.println(artistName + "				" + artistId + "				" + beginDate + "				" + endDate);
				artists.put(artistId, artistName);
				Person d = new Person(artistName, artistId, beginDate, endDate);
				dataList.add(d);
				db = new Database(artistName, artistId, beginDate, endDate, null,null,null,null);
				db.resultMaker();
				resultList.add(db.getResult());
			}
		}else {
			// if there is no input
			// we need to use the value of name that we obtained by another query
			System.out.println("There is no input for this single query : " + singleQuery + ", \nthere're no other queries who offer an input for this query, so there are no results.");
		}	
	}

	/**
	 * a function realize the web service getAlbumByArtistId
	 * @param singleQuery : single query
	 * @param artists : list to store the properties of artist
	 * @param dataList : list to store the data obtained by the call of web service
	 * @param albumList : list containing data of album : albumId, albumName
	 * @param db : class database which aims to store the the data obtained
	 * @param resultList : store all data obtained from the web service to generate the final result
	 * @throws Exception 
	 */
	public static void ws_getAlbumByArtistId(String singleQuery, HashMap<String, String> artists, LinkedList<Person> dataList, LinkedList<Album> albumList, Database db, ArrayList<ArrayList<String>> resultList) throws Exception {
		String headWS2 = getHead(singleQuery);
		String input = getInputValue(singleQuery);
		HashSet<Integer> deleteIndex = new HashSet<Integer>(); 

		// verify if there is input
		// if there is input, we need to execute the second query according to the value of the input (artistId) : id
		if (input != null) {
			Thread.sleep(500);
			String key = getInputValue(singleQuery);
			WebService ws = WebServiceDescription.loadDescription("mb_" + headWS2);
			String fileWithCallResult = ws.getCallResult(key);
			System.out.println("The call is   **" + fileWithCallResult + "**");
			String fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
			ArrayList<String[]> listOfTupleResult2 = ParseResultsForWS.showResults(fileWithTransfResults, ws);
			System.out.println("The tuple results are obtained by using the input value of the second single query");
			if (listOfTupleResult2.size() == 0) {
				System.out.println("There is no result for the query : " + singleQuery);
				System.out.println();
				System.out.println();
			}else {
				System.out.println("              artistId" + "                              " + "				albumId"
						+ "                              " + "			albumName");
				for(String[] tuple : listOfTupleResult2) {
					String artistId = tuple[0];
					String albumId = tuple[1];
					String albumName = tuple[2];
					System.out.println(artistId + "				" + albumId + "				" + albumName);	
					Album a = new Album(artistId, albumId, albumName);
					a.setAlbumList(albumId, albumName);
					a.setResList(artistId, a.getAlbumList() );
					albumList.add(a);
					String artistName = null;
					String beginDate = null;
					String endDate = null;
					for(int i = 0; i < resultList.size(); i++) {
						if(resultList.get(i).get(1).equals(artistId)) {
							if(artistName != null){artistName = resultList.get(i).get(0);}
							if(beginDate != null){beginDate = resultList.get(i).get(2);}
							if(endDate != null){endDate =  resultList.get(i).get(3);}
							deleteIndex.add(i);
							break;
						}
					}
					db = new Database(artistName, artistId, beginDate, endDate, albumId, albumName, null, null);
					db.resultMaker();
					resultList.add(db.getResult());
				}
				System.out.println();
				System.out.println();
			}
			for(int ind : deleteIndex) {
				resultList.remove(ind);
			}
		} else {
			/*
			if there is no input itself,we need to use the value of id that we obtained by the other query
			or there is no input offered by other queries,
			because in our case, there is just the first single query who can offer the input for this single query,
			so we need just to verify if there is the input that we need in the datalist generated by the first single query.
			 */
			if(dataList.size() == 0) {
				System.out.println("There is no input for this single query : " + singleQuery + 
						", \nthere're no other queries who offer an input for this query, so there are no results.");
			}else {
				ArrayList<String> keyList = new ArrayList<String>();
				for(int i = 0; i < dataList.size(); i++) {
					String key = dataList.get(i).getId();
					keyList.add(key);
				}
				for(int i = 0; i < dataList.size(); i++) {
					Thread.sleep(500);
					String key = dataList.get(i).getId();
					WebService ws = WebServiceDescription.loadDescription("mb_" + headWS2);
					String fileWithCallResult = ws.getCallResult(key);
					if(fileWithCallResult==null) {
						continue;
					}
					Thread.sleep(500);
					System.out.println("The call is   **" + fileWithCallResult + "**");
					String fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
					ArrayList<String[]> listOfTupleResult2 = ParseResultsForWS.showResults(fileWithTransfResults, ws);
					System.out.println("The tuple results are obtained by using the result of the first web service ");
					if (listOfTupleResult2.size() == 0) {
						System.out.println("There is no album for : " + dataList.get(i).getName() );
						System.out.println();
						System.out.println();
					}else {
						System.out.println("              artistId" + "                              " + "				albumId"
								+ "                              " + "			albumName");
						for(String[] tuple : listOfTupleResult2) {
							String artistId = tuple[0];
							if(!keyList.contains(artistId)) {
								continue;
							}
							String albumId = tuple[1];
							String albumName = tuple[2];
							System.out.println(artistId + "				" + albumId + "				" + albumName);
							dataList.get(i).getAlbums().put(albumId, albumName);
							Album a = new Album(artistId, albumId, albumName);
							a.setAlbumList(albumId, albumName);
							a.setResList(artistId, a.getAlbumList() );
							albumList.add(a);
							String artistName = null;
							String beginDate = null;
							String endDate = null;
							for(int j = 0; j < resultList.size(); j++) {
								if(resultList.get(j).get(1).equals(artistId)) {
									artistName = resultList.get(j).get(0);								
									beginDate = resultList.get(j).get(2);									
									endDate = resultList.get(j).get(3);
									deleteIndex.add(j);
									break;
								}
							}

							db = new Database(artistName, artistId, beginDate, endDate, albumId, albumName, null, null);
							db.resultMaker();
							resultList.add(db.getResult());
						}
						System.out.println();
						System.out.println();
					}
				}
			}
			for(int ind : deleteIndex) {
				resultList.remove(ind);
			}

		}
	}

	/**
	 * a function realize the web service getSongByAlbumId
	 * @param singleQuery : single query
	 * @param artists : list to store the properties of artist
	 * @param dataList : list to store the data obtained by the call of web service
	 * @param albumList : list containing data of album : albumId, albumName
	 * @param db : class database which aims to store the the data obtained
	 * @param resultList : store all data obtained from the web service to generate the final result
	 * @throws Exception 
	 */
	public static void ws_getSongByAlbumId(String singleQuery, HashMap<String, String> artists,
			LinkedList<Person> dataList, LinkedList<Album> albumList, Database db, ArrayList<ArrayList<String>> resultList) throws Exception {
		String headWS3 = getHead(singleQuery);
		String input = getInputValue(singleQuery);
		HashSet<Integer> deleteIndex = new HashSet<Integer>();

		// verify if there is input
		// if there is input, we need to execute the second query according to the value of the input (artistId) : id
		if (input != null) {
			Thread.sleep(500);
			WebService ws = WebServiceDescription.loadDescription("mb_" + headWS3);
			String fileWithCallResult = ws.getCallResult(input);
			System.out.println("The call is   **" + fileWithCallResult + "**");
			String fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
			ArrayList<String[]> listOfTupleResult3 = ParseResultsForWS.showResults(fileWithTransfResults, ws);
			System.out.println("The tuple results are obtained by using the result of the input of this single query : " + singleQuery);
			System.out.println("its input is : " + input);
			if (listOfTupleResult3.size() == 0) {	
				System.out.println("There is no results for : " + singleQuery);
				System.out.println();
				System.out.println();
			}else {
				System.out.println("              albumId" + "                              " + "		title"
						+ "                              " + "		duration");
				for(String[] tuple : listOfTupleResult3) {
					String albumId = tuple[0];
					String title = tuple[1];
					String duration = tuple[2];
					System.out.println(albumId + "				" + title + "					" + duration);	
					String artistName = null;
					String artistId = null;
					String beginDate = null;
					String endDate = null;
					String albumName = null;
					for(int i = 0; i < resultList.size(); i++) {
						if(resultList.get(i).get(4).equals(albumId)) {
							artistId = resultList.get(i).get(1);
							artistName = resultList.get(i).get(0);
							beginDate = resultList.get(i).get(2);
							endDate =  resultList.get(i).get(3);
							albumName =  resultList.get(i).get(5);
							deleteIndex.add(i);
							break;
						}
					}
					db = new Database(artistName, artistId, beginDate, endDate, albumId, albumName, title, duration);
					db.resultMaker();
					resultList.add(db.getResult());
				}
				System.out.println();
				System.out.println();
			}

		} else {
			/*
			if there is no input itself,we need to use the value of id that we obtained by the other query
			or there is no input offered by other queries,
			because in our case, there is just the first single query who can offer the input for this single query,
			so we need just to verify if there is the input that we need in the datalist generated by the first single query.
			 */
			if (dataList.size()==0) {
				if(albumList.size() == 0) {
					System.out.println("There is no input for this single query : " + singleQuery + 
							", \nthere're no other queries who offer an input for this query, so there are no results.");
				}else {
					for(int i = 0; i < albumList.size(); i++) {
						Thread.sleep(500);
						HashMap<String,String> album = albumList.get(i).getAlbumList();
						Set<String> albumIdSet = album.keySet();
						for(String aid : albumIdSet) {
							WebService ws = WebServiceDescription.loadDescription("mb_" + headWS3);
							String fileWithCallResult = ws.getCallResult(aid.toString());
							if(fileWithCallResult != null) {
								System.out.println("The call is   **" + fileWithCallResult + "**");
								String fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
								ArrayList<String[]> listOfTupleResult3 = ParseResultsForWS.showResults(fileWithTransfResults, ws);
								if (listOfTupleResult3.size() == 0) {	
									System.out.println("There is no results for : " + singleQuery);
									System.out.println();
									System.out.println();
								}else {
									System.out.println("              albumId" + "                              " + "		title"
											+ "                              " + "		duration");
									for(String[] tuple : listOfTupleResult3) {
										String albumId = tuple[0];
										String title = tuple[1];
										String duration = tuple[2];
										System.out.println(albumId + "				" + title + "					" + duration);			

										String artistName = null;
										String artistId = null;
										String beginDate = null;
										String endDate = null;
										String albumName = null;

										for(int j = 0; j < resultList.size(); j++) {
											if(resultList.get(j).get(4).equals(albumId)) {
												artistId = resultList.get(j).get(1);
												artistName = resultList.get(j).get(0);
												beginDate = resultList.get(j).get(2);
												endDate =  resultList.get(j).get(3);
												albumName =  resultList.get(j).get(5);
												deleteIndex.add(j);
												break;
											}
										}
										db = new Database(artistName, artistId, beginDate, endDate, albumId, albumName, title, duration);
										db.resultMaker();
										resultList.add(db.getResult());
									}
									System.out.println();
									System.out.println();
								}
							}

						}
					}

				}

			}else {
				for(Person p : dataList) {
					Set<String> keys = p.ablums.keySet();
					for(String key : keys) {
						System.out.println(key);
					}
				}
				WebService ws = WebServiceDescription.loadDescription("mb_" + headWS3);
				for(Person p : dataList) {
					Set<String> keys = p.ablums.keySet();
					for(String key : keys) {
						String fileWithCallResult = null;
						try {
							fileWithCallResult = ws.getCallResult(key);
						}catch(Exception e) {
							e.printStackTrace();
						}
						if(fileWithCallResult == null) {
							continue;
						}else {
							Thread.sleep(500);
							System.out.println("The call is   **" + fileWithCallResult + "**");
							String fileWithTransfResults = null;
							fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);

							ArrayList<String[]> listOfTupleResult3 = ParseResultsForWS.showResults(fileWithTransfResults, ws);
							System.out.println("The tuple results are obtained by using the result of the first web service ");
							if (listOfTupleResult3.size() == 0) {	
								System.out.println("There is no results for : " + singleQuery);
								System.out.println();
								System.out.println();
							}else {
								System.out.println("              albumId" + "                              " + "		title"
										+ "                              " + "		duration");
								//for(String[] tuple : listOfTupleResult3) {
								for(int x = 0; x < listOfTupleResult3.size(); x++) {	
									String albumId = listOfTupleResult3.get(x)[0];
									if(!keys.contains(albumId)) {
										listOfTupleResult3.remove(x);
										continue;
									}
									String title =listOfTupleResult3.get(x)[1];
									String duration = listOfTupleResult3.get(x)[2];
									System.out.println(albumId + "				" + title + "					" + duration);			
									String artistName = null;
									String artistId = null;
									String beginDate = null;
									String endDate = null;
									String albumName = null;
									for(int j = 0; j < resultList.size(); j++) {
										if(resultList.get(j).get(4) != null) {
											if(resultList.get(j).get(4).equals(albumId)) {
												
												artistId = resultList.get(j).get(1);
												artistName = resultList.get(j).get(0);
												beginDate = resultList.get(j).get(2);
												endDate =  resultList.get(j).get(3);
												albumName =  resultList.get(j).get(5);
												deleteIndex.add(j);
												break;
											}
										}
									}
									db = new Database(artistName, artistId, beginDate, endDate, albumId, albumName, title, duration);

									//db = new Database(null, null, null, null, albumId, null, title, duration);

									db.resultMaker();
									resultList.add(db.getResult());
								}
								System.out.println();
								System.out.println();
							}
						}
						
					}
				}
			}
			for(int i = 0; i < resultList.size(); i++) {
				if(resultList.get(i).get(0)==null || resultList.get(i).get(1)==null) {
					resultList.remove(resultList.get(i));
				}
			}
		}
	}
	/**
	 * execute web services to obtain the result of the complex query
	 * @param args : complex query entered by people
	 * @throws Exception
	 */
	public static final void main(String[] args) throws Exception {
		//complex query
		//String arg = "P(?name,?id,?b)<-getArtistInfoByName(?name, ?id, ?b, ?e)";
		//String arg = "P(?name,?id,?b)<-getArtistInfoByName(?name=Enya, ?id, ?b, ?e)";

		//String arg = "P(?albumName)<-getAlbumByArtistId(?id, ?aid, ?albumName)";
		//String arg = "P(?albumName)<-getAlbumByArtistId(?id=4967c0a1-b9f3-465e-8440-4598fd9fc33c, ?aid, ?albumName)";

		//String arg = "P(?title, ?duration)<-getSongByAlbumId(?aid, ?title, ?duration)";
		//String arg = "P(?title, ?duration)<-getSongByAlbumId(?aid=59b05591-7998-485d-8ab6-214c2dfe63b5, ?title, ?duration)";

		//String arg = "P(?aid, ?albumName)<-getArtistInfoByName(?name, ?id, ?b, ?e)#getAlbumByArtistId(?id, ?aid, ?albumName)";
		//String arg = "P(?aid, ?albumName)<-getArtistInfoByName(?name=Enya, ?id, ?b, ?e)#getAlbumByArtistId(?id, ?aid, ?albumName)";
		//String arg = "P(?aid, ?albumName)<-getArtistInfoByName(?name, ?id, ?b, ?e)#getAlbumByArtistId(?id=4967c0a1-b9f3-465e-8440-4598fd9fc33c, ?aid, ?albumName)";

		//String arg = "P(?aid, ?albumName)<-getAlbumByArtistId(?id, ?aid, ?albumName)#getSongByAlbumId(?aid, ?title, ?duration)";
		//String arg = "P(?aid, ?albumName)<-getAlbumByArtistId(?id=4967c0a1-b9f3-465e-8440-4598fd9fc33c, ?aid, ?albumName)#getSongByAlbumId(?aid, ?title, ?duration)";
		//String arg = "P(?aid, ?albumName)<-getAlbumByArtistId(?id, ?aid, ?albumName)#getSongByAlbumId(?aid=59b05591-7998-485d-8ab6-214c2dfe63b5, ?title, ?duration)";

		String arg = "P(?title,?duration)<-getArtistInfoByName(?name=Jackson, ?id, ?b, ?e)#getAlbumByArtistId(?id, ?aid, ?albumName)#getSongByAlbumId(?aid, ?title, ?duration)";
		checkQuery(arg);
		//a hashmap : key is the artistId, value is the artistName
		HashMap<String, String> artists = new HashMap<String, String>();	
		//list to stock the data together
		LinkedList<Person> dataList = new LinkedList<Person>();	

		//list to stock the album data that we obtain from single query
		LinkedList<Album> albumList = new LinkedList<Album>();	

		//database
		Database db = new Database(null,null,null,null,null,null,null,null);
		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();

		//list of web services
		String[] queries = parseQuery(arg);	
		//number of web services
		int nbWS = queries.length;	

		ArrayList<Integer> outputIndex = getOutput(arg);
		
		//verify the number of web services that we have
		if (nbWS == 0) {		
			throw new Exception("something is wrong, there is no web service");
		} else {
			/*
			 according to the structure of this complex query
			 the second query needs the value of id that we can obtain from the first query if the second query doesn't have the input itself
			 the third query needs the value of aid that we can obtain from the second query if the third query doesn't have the input itself
			 so we need to obtain the result of the first query at first
			 */
			
			for(String singleQuery : queries) {
				if(getHead(singleQuery).equals("getArtistInfoByName")) {
					ws_getArtistInfoByName(singleQuery, artists, dataList, db, resultList);
				}
				if(getHead(singleQuery).equals("getAlbumByArtistId")) {
					ws_getAlbumByArtistId(singleQuery, artists, dataList, albumList, db, resultList);
				}
				if(getHead(singleQuery).equals("getSongByAlbumId")) {
					ws_getSongByAlbumId(singleQuery, artists, dataList, albumList, db, resultList);
				}
			}
		}
		/*
		this.artistName = artistName;
		this.artistId = artistId;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.albumId = albumId;
		this.albumName = albumName;
		this.title = title;
		this.duration = duration;
		 */
		System.out.println();
		System.out.println();
		
		for(int i = 0; i < resultList.size(); i++) {
			ArrayList<String> line = resultList.get(i);
			if(line.get(5) != null) {
				System.out.println(line);
			}
		}
		
		System.out.println("The result of the query : ");
		for(int i = 0; i < resultList.size(); i++) {
			ArrayList<String> line = resultList.get(i);
			for(int j : outputIndex) {
				if(line.get(j) != null) {
					System.out.print(line.get(j) + ", ");
				}
			}
			System.out.println();
		}
	}


}
