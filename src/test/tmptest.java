package test;

import java.util.LinkedList;
import Logic.*;
import ILP.*;

public class tmptest {


	public static void main(String[] args) {
		Document doc = new Document("data/questions/questions.pred", 
				"data/questions/test/query_v2.dep.bak", false);
		
		for (int i = 0; i < doc.length(); i++) {
			System.out.println(doc.getSent(i).toString());
		}
	}
	
	public void testPathFind(myWord s, myWord e) {	    
        // this graph is directional
        HyperGraph graph = new HyperGraph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "A");
        graph.addEdge("B", "D");
        graph.addEdge("B", "E"); // this is the only one-way connection
        graph.addEdge("B", "F");
        graph.addEdge("C", "A");
        graph.addEdge("C", "E");
        graph.addEdge("C", "F");
        graph.addEdge("D", "B");
        graph.addEdge("E", "C");
        graph.addEdge("E", "F");
        graph.addEdge("F", "B");
        graph.addEdge("F", "C");
        graph.addEdge("F", "E");
        LinkedList<String> visited = new LinkedList();
        visited.add(START);
        new PathFind().breadthFirst(graph, visited);
	}
}