/**
 * 
 */
package ILP;

/**
 * @author Wang-Zhou
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Logic.*;
/**
 * HyperGraph for relation mining, a relation is treated as a hyper-edge,
 * a hyper-edge could connect many nodes, e.g., have_more_than_n_pubs(sam, john, 2)
 */
public class HyperGraph {
	// TODO modify Graph class into HyperGraph
	
    private Map<String, LinkedHashSet<String>> adjMap = new HashMap(); // map(nodeName, AdjacentSet<nodeName>)
    private Map<myTerm, HyperEdge> edgeMap = new HashMap();
    private Map<String, HyperVertex> vertexMap = new HashMap(); // map(nodeName, node)
    private List<HyperVertex> vertices = new ArrayList<HyperVertex>();
    private List<HyperEdge> edges = new ArrayList<HyperEdge>();
    
    public void addHyperVertex(HyperVertex v) {
    	vertices.add(v);
    	vertexMap.put(v.toString(), v);
    }
    
    public void addHypgerVertex(myWord wrd) {
    	HyperVertex v = new HyperVertex(wrd);
    	vertices.add(v);
    	vertexMap.put(wrd.toString(), v);
    	
    }
    
//    public void addHyperEdge(myTerm t, double w) {
//    	String s = "";
//    	for (int i = 0; i < nodes.length; i++) {
//    		s = s + nodes[i] + ',';
//    		LinkedHashSet<String> adjacent = adjMap.get(nodes[i]);
//            if(adjacent == null) {
//                adjacent = new LinkedHashSet();
//                adjMap.put(nodes[i], adjacent);
//            }
//            for (int j = 0; j < nodes.length; j ++) {
//            	if (i != j)
//            		adjacent.add(nodes[j]);	
//            }
//    	}
//    	edgeMap.put(t, new HyperEdge(name, nodes, w));
//    }

    public void addHyperEdge(String name, HyperVertex[] nodes, double w) {
    	String s = "";
    	// TODO when node not in vertices list
    	for (int i = 0; i < nodes.length; i++) {
    		if (!isIn(nodes[i])) {
    			// new vertex
    			
    		}
    		s = s + nodes[i] + ',';
    		LinkedHashSet<String> adjacent = adjMap.get(nodes[i].toString());
            if(adjacent == null) {
                adjacent = new LinkedHashSet<String>();
                adjMap.put(nodes[i].toString(), adjacent);
            }
            for (int j = 0; j < nodes.length; j ++) {
            	if (i != j)
            		adjacent.add(nodes[j].toString());	
            }
    	}
    	String t = String.format("%s(%s)", name, s.substring(0, s.length() - 2));
    	HyperEdge e = new HyperEdge(name, nodes, w);
    	edges.add(e);
    	edgeMap.put(new myTerm(t), e);
    }

    public void addTwoWayVertex(String node1, String node2) {
        addEdge(node1, node2);
        addEdge(node2, node1);
    }

    public boolean isConnected(String node1, String node2) {
        Set adjacent = map.get(node1);
        if(adjacent==null) {
            return false;
        }
        return adjacent.contains(node2);
    }

    public LinkedList<String> adjacentNodes(String last) {
        LinkedHashSet<String> adjacent = map.get(last);
        if(adjacent==null) {
            return new LinkedList();
        }
        return new LinkedList<String>(adjacent);
    }
    
    public boolean isIn(myWord node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name == node.toString()) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isIn(HyperVertex node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name == node.toString()) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isIn(String node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name == node) {
				found = true;
				break;
			}
		}
    	return found;
    }
}