package Tree;

import java.util.ArrayList;

public class PathSat {
	/*
	 * Like sent sat, but stores path satisfication data
	 */
	ArrayList<PathData> negative = new ArrayList<PathData>();
	ArrayList<PathData> positive = new ArrayList<PathData>();
	ArrayList<PathData> total = new ArrayList<PathData>();
	
	int pos_num;
	int neg_num;
	
	public PathSat() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPath(PathData pd) {
		total.add(pd);
		if (pd.cls()) {
			pos_num++;
			positive.add(pd);
		} else {
			neg_num++;
			negative.add(pd);
		}
	}
	
	public double getAccuracy() {
		return (double) ((double) pos_num)/((double) pos_num + neg_num);
	}
	
	public ArrayList<PathData> getNegative() {
		return negative;
	}
	
	public ArrayList<PathData> getPositive() {
		return positive;
	}
	
	public ArrayList<PathData> getAll() {
		return total;
	}
}
