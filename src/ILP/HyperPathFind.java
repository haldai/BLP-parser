/**
 * 
 */
package ILP;

import Logic.*;

import java.util.ArrayList;
import java.util.LinkedList;

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
    String start;
    String end;
    HyperGraph graph;
    ArrayList<LinkedList<myTerm>> path = new ArrayList<LinkedList <myTerm>>();
    
    int debug = 0;
    
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
    
    public ArrayList<LinkedList<myTerm>> Search(LinkedList<HyperEdge> visitedEdges) {
    	
    	System.out.format("start %s, end %s\n", start, end);
    	LinkedList<HyperEdge> start_edges = allEdgesContain(start);
    	for (HyperEdge edge : start_edges) {
    		// visit start_edges
    		if (edge.containsVertex(end)) {
    			visitedEdges.add(edge);
        		printPath(visitedEdges);
        		path.add(returnPath(visitedEdges));
        		visitedEdges.removeLast();
        		continue;
    		} else {
    			visitedEdges.add(edge);
    			depthFirst(visitedEdges);
    			visitedEdges.removeLast();
    		}
    	}
    	return path;
    }
    
    private LinkedList<HyperEdge> allEdgesContain(String node) {
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	for (HyperEdge edge : graph.getEdges()) {
    		if (edge.containsVertex(start))
    			re.add(edge);
    	}
		return re;
	}
    /**
     * Available edges in HyperPath: each node can only appears twice at most, or there will 
     * be redundant edges.
     */
	private LinkedList<HyperEdge> adjAvailEdges(HyperEdge e, LinkedList<HyperEdge> visited) {
    	LinkedList<HyperEdge> edges = graph.adjacentEdges(e);
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	LinkedList<String> node_1 = new LinkedList<String>();
    	// very important!
    	node_1.add(start);
    	LinkedList<String> node_2 = new LinkedList<String>();
    	for (HyperEdge edge : visited) {
    		for (HyperVertex node : edge.getVerices()) {
    			String tmp = node.getName();
    			if (node_1.contains(tmp))
    				if (node_2.contains(tmp)) {
    					System.out.println("Redundancy Error !!");
//    					System.out.println(e.toString());
//    					System.out.println(node_1.toString());
//    					System.out.println(node_2.toString());
//    					System.out.println(visited.toString());
    					System.exit(0);
    				}
    				else
    					node_2.add(tmp);
    			else
    				node_1.add(tmp);
    		}
    	}
    	
    	for (HyperEdge edge : edges) {
    		boolean add = true;
    		if (visited.contains(edge)) {
    			add = false;
    		}
    		for (HyperVertex node : edge.getVerices()) {
    			if (node_2.contains(node.getName())) {
    				add = false;
    				break;
    			}
    		}
    		if (add)
    			re.add(edge);
    	}
    	return re;
    }
    
    private void depthFirst(LinkedList<HyperEdge> visitedEdges) {
    	LinkedList<HyperEdge> edges = adjAvailEdges(visitedEdges.getLast(), visitedEdges);
        // examine adjacent nodes
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge)) {
                continue;
            }
            if (edge.containsVertex(end)) {
            	visitedEdges.add(edge);
        		printPath(visitedEdges);
        		path.add(returnPath(visitedEdges));
        		visitedEdges.removeLast();
        		break;
            }
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge) || edge.containsVertex(end)) {
                continue;
            }
            visitedEdges.addLast(edge);
            depthFirst(visitedEdges);
            visitedEdges.removeLast();
        }
    }

    private void printPath(LinkedList<HyperEdge> visitedEdges) {
    	debug++;
        for (HyperEdge edge : visitedEdges) {
            System.out.print(edge.toString());
            System.out.print(" ");
        }
        System.out.println(debug);
        if (debug >= 2) {
        	System.out.println("Multi Paths in Tree!!!");
        	System.exit(0);
        }
    }
    
    private LinkedList<myTerm> returnPath(LinkedList<HyperEdge> visitedEdges) {
    	LinkedList<myTerm> re = new LinkedList<myTerm>();
        for (HyperEdge edge : visitedEdges) {
        	re.add(edge.toMyTerm());
        }
        if (re.size() > 0)
        	return re;
        else
        	return null;
    }
}
