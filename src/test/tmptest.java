package test;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;
import ILP.*;

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
		testEvaluation(doc, prolog);
		testPathFind(doc);
	}
	
	public static void testEvaluation(Document doc, Prolog prolog) {
		
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
	
	public static void testPathFind(Document doc) {	    
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
            	System.out.format("num of paths: %d\n", pf.Search(visitedEdges).size());
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
}