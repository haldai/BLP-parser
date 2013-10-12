package test;

import java.util.LinkedList;
import Logic.*;
import ILP.*;

public class tmptest {


	public static void main(String[] args) {
		Document doc = new Document("data/questions/questions.pred", 
				"data/questions/test/query_v2.dep.bak", false);
		
//		for (int i = 0; i < doc.length(); i++) {
//			System.out.println(doc.getSent(i).toString());
//		}
		
		testPathFind(doc);
	}
	
	public static void testPathFind(Document doc) {	    
        Sentence[] sentences = doc.getSentences();
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
        	HyperPathFind pf = new HyperPathFind(graph, graph.getVertex(0), graph.getVertex(graph.getVertexLen() - 1));
        	LinkedList<String> visited = new LinkedList();
        	visited.add(graph.getVertex(0).toMyWord().toString());
        	LinkedList<HyperEdge> visitedEdges = new LinkedList();
        	pf.Search(graph, visitedEdges, visited);
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
//        graph.addEdge("A", "B");
//        graph.addEdge("A", "C");
//        graph.addEdge("B", "A");
//        graph.addEdge("B", "D");
//        graph.addEdge("B", "E"); // this is the only one-way connection
//        graph.addEdge("B", "F");
//        graph.addEdge("C", "A");
//        graph.addEdge("C", "E");
//        graph.addEdge("C", "F");
//        graph.addEdge("D", "B");
//        graph.addEdge("E", "C");
//        graph.addEdge("E", "F");
//        graph.addEdge("F", "B");
//        graph.addEdge("F", "C");
//        graph.addEdge("F", "E");
//        LinkedList<String> visited = new LinkedList();
//        visited.add(START);
//        HyperPathFind pf = new HyperPathFind(graph, null, null);
//        .breadthFirst(graph, visited)
	}
}