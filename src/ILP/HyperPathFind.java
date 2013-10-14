/**
 * 
 */
package ILP;

import Logic.*;
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
    
    public void Search(LinkedList<HyperEdge> visitedEdges) {
    	System.out.format("start %s, end %s\n", start, end);
    	LinkedList<HyperEdge> start_edges = allEdgesContain(start);
    	for (HyperEdge edge : start_edges) {
    		// visit start_edges
    		if (edge.containsVertex(end)) {
    			visitedEdges.add(edge);
        		printPath(visitedEdges);
        		visitedEdges.removeLast();
        		continue;
    		} else {
    			visitedEdges.add(edge);
    			depthFirst(visitedEdges);
    			visitedEdges.removeLast();
    		}
    	}
    }
    
    private LinkedList<HyperEdge> allEdgesContain(String node) {
		// TODO Auto-generated method stub
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	for (HyperEdge edge : graph.getEdges()) {
    		if (edge.containsVertex(start))
    			re.add(edge);
    	}
		return re;
	}

	private LinkedList<HyperEdge> adjAvailEdges(HyperEdge e, LinkedList<HyperEdge> visited) {
    	LinkedList<HyperEdge> edges = graph.adjacentEdges(e);
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	LinkedList<String> node_1 = new LinkedList<String>();
    	LinkedList<String> node_2 = new LinkedList<String>();
    	for (HyperEdge edge : visited) {
    		for (HyperVertex node : edge.getVerices()) {
    			String tmp = node.getName();
    			if (node_1.contains(tmp))
    				if (node_2.contains(tmp)) {
    					System.out.println("error");
    					System.out.println(e.toString());
    					System.out.println(node_1.toString());
    					System.out.println(node_2.toString());
    					System.out.println(visited.toString());
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
        for (HyperEdge edge : visitedEdges) {
            System.out.print(edge.toString());
            System.out.print(" ");
        }
        System.out.println();
    }
}
