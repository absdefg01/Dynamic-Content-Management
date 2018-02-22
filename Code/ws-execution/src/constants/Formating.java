package constants;

public class Formating {

	
	public static final String transformStringForURL(String input){
		return input.trim().replaceAll("\\s+","+");
	}
	
	//string... 是一个表格
	public static final String getFileNameForInputs(String...inputs){
		StringBuffer buff=new StringBuffer();
		boolean first=true;
		//这里得到的结果就相当于字符_字符_字符
		for(String input: inputs){
			if(!first) buff.append("_");
			else first=false;
			buff.append(transformStringForURL(input));
		}
		//最终得到文件名，字符之间是由下划线连接起来的
		buff.append(".xml");
		return buff.toString();
	}
	
}
