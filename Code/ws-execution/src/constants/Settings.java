package constants;

public class Settings {

	public static final String rootProject="../WS-Evaluation/";
	public static final String dirWithDef=rootProject+"ws-definitions/";
	
	//找到callresult文件夹
	public static final String getDirForCallResults(String ws){
		return rootProject+ws+"/call_results/";
	}
	
	//找到transfresult文件夹
	public static final String getDirForTransformationResults(String ws){
		return rootProject+ws+"/transf_results/";
	}
	
	
	
}
