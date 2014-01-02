package utils;

public class utils {
	
	// define some global constancts
	private utils() {}
	public static final int MAX_LABEL_LEN = 10; //max length of labels (max num of labeled 
											  //logic facts in label of a sentence)
	public static final int MAX_HIERARCHY_NUM = 6; // max layer numbers
	public static final double MAX_ACC_CRI = 1.0;
	public static final double MAX_INACC_CRI = 0.0;
	public static final int BOOSTING_TURNS = 20; // max boosting turns
	public static final double BOOSTING_SAMPLING_POR = 0.6; // sampling proportion
	
	public static boolean isNumeric(String str) {
		for (int i = str.length();--i>=0;) {   
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
