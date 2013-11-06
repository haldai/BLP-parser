package utils;

public class utils {
	
	// define some global constancts
	private utils() {}
	public static final int MAX_LABEL_LEN = 5; //max length of labels (max num of labeled 
											  //logic facts in label of a sentence)
	public static boolean isNumeric(String str) {
		for (int i = str.length();--i>=0;) {   
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
