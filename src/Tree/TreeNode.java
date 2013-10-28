/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;

/**
 * @author Wang-Zhou
 *
 */
public class TreeNode {

	/**
	 * TreeNode. It may be a term or a word feature.
	 */
	private myTerm termNode;	// name of current node (splitting criteria)
	private ArrayList<Sentence> instances; // instances had been assigned to current node
	private ArrayList<myTerm> candTerms; // candidate terms for splitting
	private int hierarchy;
	private TreeNode father;
	private TreeNode trueChild;
	private TreeNode falseChild;
	private boolean isLeaf;
	
	public TreeNode() {
		// TODO Auto-generated constructor stub
		termNode = new myTerm();
		instances = new ArrayList<Sentence>();
		candTerms = new ArrayList<myTerm>();
		trueChild = null;
		falseChild = null;
		hierarchy = 0;
		father = null;
		isLeaf = false;
	}
	
	public TreeNode getTrueChild() {
		return trueChild;
	}
	
	public TreeNode getFalseChild() {
		return falseChild;
	}
	
	public void setTrueChild(TreeNode t) {
		trueChild = t; 
	}
	
	public void setFalseChild(TreeNode t) {
		falseChild = t; 
	}
	
	public myTerm getTermNode() {
		return termNode;
	}
	
	public void setTermNode(myTerm n) {
		termNode = n;
	}
	
	public ArrayList<Sentence> getInstances() {  
        return instances;  
	}  
	
	public void setInstances(ArrayList<Sentence> ins) {  
	    this.instances = ins;  
	}  
	
	public ArrayList<myTerm> getCandTerms() {  
		return candTerms;  
	}  
	
	public void setCandTerms(ArrayList<myTerm> ct) {  
		this.candTerms = ct;
	}  
	
	public TreeNode getFather() {
		return father;
	}
	
	public void setFather(TreeNode f) {
		father = f;
	}
	
	public int getHierarchy() {
		return hierarchy;
	}
	
	public void setHierarchy(int h) {
		hierarchy = h;
	}
	
	public ArrayList<myTerm> getHistoryNodes() {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		TreeNode f = this.getFather();
		while (f != null) {
			re.add(f.getTermNode());
			f = f.getFather();
		}
		return re;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
}
