package test;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;
import ILP.*;
import Tree.*;
import utils.*;

public class tmptest {
	static Prolog prolog;
	public static void main(String[] args) {
		Document doc = new Document("data/questions/questions.pred", 
				"data/test_new/test.dep", true);
		
		for (int i = 0; i < doc.length(); i++) {
			System.out.println(doc.getSent(i).toString());
			System.out.println(doc.getLabel(i).toString());
		}
		
		prolog = new Prolog();
		
//		testEvaluation(doc, prolog);
		testPathFind(doc);
		testRuleTree(doc, prolog);
		testTuple();

	}
	
	public static void testTuple() {
		System.out.println("Test Tuple!");
		Tuple<Integer, Integer> tup1 = new Tuple<Integer, Integer>(0, 0);
		Tuple<Integer, Integer> tup2 = new Tuple<Integer, Integer>(0, 2);
		System.out.println(tup1.equals(tup2));
	}
	
	public static void testEvaluation(Document doc, Prolog prolog) {
		System.out.println("Test Evaluation!");
		Formula f = new Formula("sem(X_1_var,X_2_var):-att(X_2_var,X_3_var);de(X_3_var,X_1_var)."); 
		LogicProgram p = new LogicProgram();
		p.addRule(f);
		f = new Formula("sem(X_2_var,X_1_var):-att(X_1_var,X_2_var);\\==(X_2_var,的_0_u)."); // do not use \=/2(unification)
		// TODO in prolog, the value of myWord position is not important, can be removed ?
		p.addRule(f);
		Eval eval = new Eval(prolog, p, doc);
		ArrayList<LinkedList<myTerm>> sems = eval.evalAll();
		int cnt = 0;
		for (LinkedList<myTerm> l : sems) {
			if (l == null) 
				continue;
			else {
				for (myTerm t : l) {
					cnt++;
					System.out.println(t.toString());
				}
				System.out.println("==============");
			}
		}
		System.out.println("num of semantics: " + cnt);
	}
	
	private static ArrayList<LinkedList<myTerm>> findPath(myTerm label, Sentence sent) {
		HyperGraph graph = new HyperGraph();
		myTerm[] terms = sent.getTerms();
		myWord[] words = sent.getWords();
		for (myWord word : words) {
			graph.addHyperVertex(word);
		}
		
		for (myTerm term : terms) {
			graph.addHyperEdge(term);
		}
		
		HyperVertex start = new HyperVertex(label.getArg(0));
		HyperVertex end = new HyperVertex(label.getArg(1));
		HyperPathFind pf = new HyperPathFind(graph, start, end);
		LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
		pf.Search(visitedEdges);
		
		return pf.getPaths();
	}
	
	public static void testPathFind(Document doc) {
		System.out.println("Test PathFind!");
		Sentence[] sentences = doc.getSentences();
		int cnt = 0;
       for (Sentence sent : sentences) {
    	   HyperGraph graph = new HyperGraph();
    	   myTerm[] terms = sent.getTerms();
    	   myWord[] words = sent.getWords();
    	   for (myWord word : words) {
    		   graph.addHyperVertex(word);
    	   }
    	   
    	   for (myTerm term : terms) {
    		   graph.addHyperEdge(term);
    	   }
    	   
    	   for (myTerm label : doc.getLabel(cnt)) {
    		   HyperVertex start = new HyperVertex(label.getArg(0));
    		   HyperVertex end = new HyperVertex(label.getArg(1));
    		   HyperPathFind pf = new HyperPathFind(graph, start, end);
    		   LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
    		   pf.Search(visitedEdges);
//           	 	System.out.format("num of paths: %d\n", pf.Search(visitedEdges).size());
        		// test substitution
    		   for (LinkedList<myTerm> path : pf.getPaths()) {
    			   Substitute sub = new Substitute(new ArrayList<myTerm>(path));
    			   for (myWord w : sub.getWordList()) {
    				   System.out.print(String.format("%s ", w.toString()));
    			   }
    			   System.out.println();
    			   for (myWord w : sub.getVarList()) {
    				   System.out.print(String.format("%s ", w.toString()));
    			   }
    			   System.out.println();
    			   for (myTerm t : sub.getOriginTerms()) {
    				   System.out.print(String.format("%s ", t.toString()));
    			   }
    			   System.out.println();
    			   for (myTerm t : sub.getSubTerms()) {
    				   System.out.print(String.format("%s ", t.toString()));
    			   }
    			   System.out.println();
    		   }
    	   }
    	   cnt++;
          	
//        	System.out.format("edge len %d, vertex len %d\n", graph.getEdgeLen(), graph.getVertexLen());
//        	if (graph.getEdgeLen() - graph.getVertexLen() != -1)
//        		System.out.println("ERROR!!");
//        	for (HyperEdge edge : graph.getEdges()) {
//        		System.out.println(edge.toMyTerm().toString());
//        	}
//        	for (HyperVertex vertex : graph.getVertices()) {
//        		System.out.println(vertex.toMyWord().toString());
//        	}
        	graph = null;
        }
	}
	public static void testRuleTree(Document doc, Prolog prolog) {
		System.out.println("Test RuleTree!");
		// find all paths;
		for (int i = 0; i < doc.length(); i++) {
			ArrayList<myTerm> labels = doc.getLabel(i);
			Sentence sent = doc.getSent(i);
			for (myTerm label : labels) {
				ArrayList<LinkedList<myTerm>> paths = findPath(label, sent);
				for (LinkedList<myTerm> path : paths) {
					RuleTree tree = new RuleTree(prolog);
					tree.buildTree(doc, label, path);
				}
			}
		}
	}
}
