/**
 * 
 */
package Tree;

import java.util.ArrayList;

import Logic.*;
/**
 * @author Wang-Zhou
 *
 */
public class RuleTree {

	/**
	 * translate sentence and formula into decision tree and vice versa.
	 * 
	 * in logic domain, adding one term may not affect the classification
	 * distribution, so if any term cannot affect the distribution, just add
	 * the default term (e.g., contains certain variable or just in turn).
	 * 
	 * [optimization] if the variable was not introduced in before terms, its feature cannot
	 * be added
	 * 
	 * [optimization] must contain variable in head, so first add these two terms
	 */
	
	TreeNode root; // tree root - the first splitting node;
	myTerm head; // logical term - head:-body., must have variable
	private int maxHeight;
	
	public RuleTree() {
		root = new TreeNode();
		head = new myTerm();
	}
	
	public RuleTree(myTerm h) {
		root = new TreeNode();
		root.setFather(null);
		root.setHierarchy(1);
		head = h;
	}
	
	public myTerm getHead() {
		return head;
	}
	
	public void setHead(myTerm h) {
		head = h;
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode r) {
		root = r;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public void setMaxHeight(int h) {
		maxHeight = h;
	}
	
	/**
	 * main procedure for building a rule tree
	 * @data is the data for training a tree
	 * @candidateTerms are the candidate terms for a tree 
	 */
	public TreeNode buildTree(ArrayList<Sentence> data, ArrayList<myTerm> candidateTerms) {
		TreeNode node = new TreeNode();
		// TODO if no coverage, no different clvbasses, return node as a label
		// TODO else split current node
		ArrayList<myTerm> availTerms = getAvailTerms(node, candidateTerms); // get available terms to add
		// TODO use ILP coverage
		
		return node;
	}
	
	/**
	 * compute available terms
	 * @node the node to build
	 * @candidateTerms a candidate set for choosing
	 */
	private ArrayList<myTerm> getAvailTerms(TreeNode node, ArrayList<myTerm> candidateTerms) {
		// TODO debug
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<String> appVars = new ArrayList<String>();
		ArrayList<myTerm> historyTerms = node.getHistoryNodes();
		for (myTerm t : historyTerms) {
			for (myWord w : t.getArgs()) {
				if ((w.isVar()) && !appVars.contains(w.toPrologString())) {
					appVars.add(w.toPrologString());					
				}
			}
		}
		for (myTerm t : candidateTerms) {
			boolean banned = false;
			for (myWord w : t.getArgs()) {
				for (String s : appVars) {
					if (s.equals(w.toPrologString()))
						banned = true;
				}
			}
			if (!banned)
				re.add(t);
		}
		return re;
	}
}
