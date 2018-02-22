package parsers;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import download.WebService;

public class ParseResultsForWS  {
	 static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
     static final DocumentBuilder builder = getBuilder();
     static final XPath xPath =  XPathFactory.newInstance().newXPath();

     public static final  DocumentBuilder getBuilder(){
    	     try{
    	    	 	return builderFactory.newDocumentBuilder();
    	     }catch(Exception e ){ return null;}
     }
     
     /**
      * 
      * @param fileWithWithTransfResults
      * @param ws
      * @return the list of tuples; each tuples respects the order of head variables as defined in the description of the WS
      * @throws Exception
      */
	public static ArrayList<String[]> showResults(String fileWithWithTransfResults, WebService ws) throws Exception{
		ArrayList<String[]> listOfTupleResults= new ArrayList<String[]>();
		
		 Document xmlDocument = builder.parse(fileWithWithTransfResults);
		 System.out.println("Parse document "+fileWithWithTransfResults);
		 
		 String record = "/RESULT/RECORD";
         NodeList nodeList = (NodeList) xPath.compile(record).evaluate(xmlDocument, XPathConstants.NODESET);
         //对于每个record点，我都建立一个vector
         for (int i = 0; i < nodeList.getLength(); i++) {
        	 	// init the new tuple vector 
        	 	//初始化一个vector，目的是来装head里面的variable
        	 	//headVariable里有：[?artistName, ?artistId, ?beginDate, ?endDate]
        	 	String [] tuple = new String[ws.headVariables.size()]; 
        	 	for(int k=0;k<tuple.length;k++){
        	 		tuple[k]=null;
        	 	}
        			 
        	 	
        	    //read each item (value of a variable)
        	 	//在transfresult文件中
        	 	//每一个record中包含相同数量的item
        	 	//每个item是为了装变量的值
        	 	//ex 
        	 	/*
        	 	 * <RECORD>
				  <ITEM ANGIE-VAR="?artistName">Frank Sinatra</ITEM>
				  <ITEM ANGIE-VAR="?artistId">197450cd-0124-4164-b723-3c22dd16494d</ITEM>
				  <ITEM ANGIE-VAR="?beginDate">1915-12-12</ITEM>
				  <ITEM ANGIE-VAR="?endDate">1998-05-14</ITEM></RECORD>
				<RECORD>
        	 	 */
        	 	String item_expr= "./ITEM";
        	 	NodeList listItem = (NodeList) xPath.compile(item_expr).evaluate(nodeList.item(i), XPathConstants.NODESET);
        	 	for (int j = 0; j < listItem.getLength(); j++) {
            		//value中包含了每行item所包含的value
        	 		String value=listItem.item(j).getTextContent();
            	 	//System.out.println("aaaaa " + value);

        	 		//variable是ANGIE-VAR=后面的变量名称
        	 		//<ITEM ANGIE-VAR="?artistName">Leonard Cohen</ITEM>
        	 		//ex ?artistName
        	 		String exprVarible="./@ANGIE-VAR";
            		String variable=((Node) xPath.compile(exprVarible).evaluate(listItem.item(j), XPathConstants.NODE)).getNodeValue();
            		//System.out.println("aaaaa " + variable);
            		
            		//variable的position值
            		//若posVariable不为空
            		//说明这个variable是存在的
            		//那就在tuple中的posVariable的位置放上对应的value
            		Integer posVariable=ws.headVariableToPosition.get(variable.trim());
            		if(posVariable==null) System.err.println("Incorrect script: variable unknown ");
            		tuple[posVariable]=value.trim();
            }
        	 	//一个tuple里有一个record中，每行item包含的 值
        	 	//例如
        	 	//Leonard Cohen
        	 	//65314b12-0e08-43fa-ba33-baaa7b874c15
        	 	//1934-09-21
        	 	//所以listOfTupleResults包含所有tuple，即transfresult xml的所有值
        	 	listOfTupleResults.add(tuple);
         }
		 return listOfTupleResults;
	}
	
}
