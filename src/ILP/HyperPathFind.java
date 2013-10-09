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
    
    private HyperEdge[] allContainedEdges(String v) {
    	// TODO find all possible  edges
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
    
    public void depthFirst(HyperGraph graph, LinkedList<String> visitedEdges, LinkedList<String> visited) {
    	// "visited edges"
        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        // examine adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(end)) {
                visited.add(node);
                printPath(visited);
                visited.removeLast();
                break;
            }
        }
        // in depth-first, recursion needs to come after visiting adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(end)) {
                continue;
            }
            visited.addLast(node);
            depthFirst(graph, visited);
            visited.removeLast();
        }
    }

    public void printPath(LinkedList<String> visited) {
        for (String node : visited) {
            System.out.print(node);
            System.out.print(" ");
        }
        System.out.println();
    }
}
