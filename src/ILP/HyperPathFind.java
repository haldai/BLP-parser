/**
 * 
 */
package ILP;

import Logic.*;

import java.util.ArrayList;
/**
 * @author daiwz
 *
 */
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * @author Wang-Zhou
 *
 */

public class HyperPathFind {
	/**
	 * HyperPathFind finds a hyper path in HyperGraph. Different 
	 * from path finding in simple graph, the hyper graph path finding 
	 * caches EDGES rather than VERTICES. 
	 * 
	 * 标记一个diff，每个节点只能被触发两次
	 */
    String start = null;
    String end = null;
    HyperGraph graph = null;
    
    public HyperPathFind(HyperGraph g, String s, String e) {
    	graph = g;
    	start = s;
    	end = e;
    }
    
    public HyperPathFind(HyperGraph g, HyperVertex s, HyperVertex e) {
    	graph = g;
    	start = s.getName();
    	end = e.getName();
    }
    
    public HyperPathFind(HyperGraph g, myWord s, myWord e) {
    	graph = g;
    	start = s.toString();
    	end = e.toString();
    }
    
    public void Search(HyperGraph graph, LinkedList<HyperEdge> visitedEdges,
    		LinkedList<String> metNodes, LinkedList<String> toVisitNodes) {
    	System.out.format("start %s, end %s\n", start, end);
    	HyperEdge[] start_edges = allEdgesContain(start);
    	metNodes.add(start);
    	for (HyperEdge edge : start_edges) {
    		visitedEdges.add(edge);
    		// visit start_edges
    		for (HyperVertex v : edge.getVerices()){
    			metNodes.add(v.toMyWord().toString());
    			if (v.equals(end)) {
    				printPath(visitedEdges);
    			}
//    			System.out.println("route: " + edge.toMyTerm().toString());
			}
//    		System.out.println(edge.toMyTerm().toString());
    	}
    	// to visit edges
    	LinkedList<String> visited = metVertices(visitedEdges);
    	for (String node : metNodes) {
    		if (!visited.contains(node))
    			toVisitNodes.add(node);
    	}
    	depthFirst(graph, visitedEdges, metNodes, toVisitNodes);
    }
    
    private LinkedList<String> metVertices(LinkedList<HyperEdge> visitedEdges) {
    	LinkedList<String> visited = new LinkedList<String>();
    	for (HyperEdge edge : visitedEdges) {
    		for (HyperVertex node : edge.getVerices()) {
    			if (!visited.contains(node.toString()))
    				visited.add(node.toString());
    		}
    	}
    	return visited;
    }
    private HyperEdge[] allEdgesContain(String v) {
    	// TODO find all possible edges
    	List<HyperEdge> r = new ArrayList<HyperEdge>();
    	for (int i = 0; i < graph.edgeLen; i++) {
    		if (graph.getEdge(i).containsVertex(v))
    			r.add(graph.getEdge(i));
    	}
    	HyperEdge[] buff_edges = new HyperEdge[r.size()];
    	for (int i = 0; i < r.size(); i++) {
    		buff_edges[i] = r.get(i);
    	}
    	return buff_edges;
    }
    
    public void depthFirst(HyperGraph graph, LinkedList<HyperEdge> visitedEdges,
    		LinkedList<String> metNodes, LinkedList<String> toVisitNodes) {
    	LinkedList<HyperEdge> edges = graph.edgesContainsVertex(toVisitNodes.getLast());
        // examine adjacent nodes
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge)) {
                continue;
            }
            boolean reached_end = false;
            for (HyperVertex node : edge.getVerices()) {
            	if (node.equals(end)) {
            		visitedEdges.add(edge);
            		printPath(visitedEdges);
            		visitedEdges.removeLast();
            		reached_end = true;
            	}
            }
            if (reached_end)
            	break;
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            breadthFirst(graph, visited);
            visited.removeLast();
        }
    }

    private void printPath(LinkedList<HyperEdge> visited) {
        for (HyperEdge edge : visited) {
            System.out.print(edge.toString());
            System.out.print(" ");
        }
        System.out.println();
    }

}
