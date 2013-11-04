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
	private ArrayList<myTerm> termNodes;	// name of current node (splitting criteria)
	private ArrayList<Sentence> instances; // instances had been assigned to current node
	private ArrayList<myTerm> candTerms; // candidate terms for splitting
	private int hierarchy;
	private TreeNode father;
	private TreeNode trueChild;
	private TreeNode falseChild;
	private boolean isLeaf;
	private boolean isPositiveBranch;
	
	public TreeNode() {
		// TODO Auto-generated constructor stub
		termNodes = new ArrayList<myTerm>();
		instances = new ArrayList<Sentence>();
		candTerms = new ArrayList<myTerm>();
		trueChild = null;
		falseChild = null;
		hierarchy = 0;
		father = null;
		isPositiveBranch = true;
		isLeaf = true;
	}
	
	public TreeNode getTrueChild() {
		return trueChild;
	}
	
	public TreeNode getFalseChild() {
		return falseChild;
	}
	
	public void setTrueChild(TreeNode t) {
		trueChild = t;
		isLeaf = false;
	}
	
	public void setFalseChild(TreeNode t) {
		falseChild = t;
		isLeaf = false;
	}
	
	public void removeTrueChild() {
		trueChild = null; 
		isLeaf = true;
	}
	
	public void removeFalseChild() {
		falseChild = null;
		isLeaf = true;
	}
	
	public void setBranchPositive() {
		isPositiveBranch = true;
	}
	
	public void setBranchNegative() {
		isPositiveBranch = false;
	}
	
	public ArrayList<myTerm> getTermNodes() {
		return termNodes;
	}
	
	public void setTermNodes(ArrayList<myTerm> n) {
		termNodes = n;
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
	
	public ArrayList<myTerm> getAncestorNodes() {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		TreeNode f = this.getFather();
		while (f != null) {
			for (myTerm t:f.getTermNodes()) {
				re.add(t);
				f = f.getFather();
			}
		}
		return re;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
	public boolean isPositiveBranch() {
		return isPositiveBranch;
	}
	
	public LinkedList<myTerm> toTerms() {
		LinkedList<myTerm> re = new LinkedList<myTerm>();
		for (myTerm t : termNodes) {
			re.add(t);
		}
		return re;
	}
	
}
