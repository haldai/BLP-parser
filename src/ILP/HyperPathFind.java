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
    		LinkedList<String> visitedNodes) {
    	// "visited edges"
        LinkedList<String> nodes = graph.adjacentNodes(visitedNodes.getLast());
        LinkedList<HyperEdge> edges = graph.adjacentEdges(visitedEdges.getLast());
        // examine adjacent nodes
        
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge)) {
                continue;
            }
            boolean reached_end = false;
            // store visited edges
            for (int i = 0; i < edge.vertexLen(); i++) {
            	// TODO modify the node into edges
            	String v = edge.getVertex(i).getName();
            	if (v == end) {
                    visitedNodes.add(v);
                    visitedEdges.add(edge);
                    printPath(visitedEdges);
                    visitedNodes.removeLast();
                    visitedEdges.removeLast();
                    reached_end = true;
                }
            }
            if (reached_end)
            	break;
        }
        // in depth-first, recursion needs to come after visiting adjacent nodes
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge) || edge.containsVertex(end)) {
                continue;
            }
//            visited.addLast(node);
            visitedEdges.addLast(edge);
            depthFirst(graph, visitedEdges, visitedNodes);
            visitedNodes.removeLast();
        }
    }

    public void printPath(LinkedList<HyperEdge> visited) {
    	
        for (HyperEdge edge : visited) {
            System.out.print(edge.toString());
            System.out.print(" ");
        }
        System.out.println();
    }
}
