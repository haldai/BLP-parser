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
	
    private Map<String, LinkedHashSet<String>> adjMap = new HashMap<String, LinkedHashSet<String>>(); // map(nodeName, AdjacentSet<nodeName>)
    private Map<String, HyperEdge> edgeMap = new HashMap<String, HyperEdge>(); //map(edgeName, edge)
    private Map<String, HyperVertex> vertexMap = new HashMap<String, HyperVertex>(); // map(nodeName, node)
    private List<HyperVertex> vertices = new ArrayList<HyperVertex>();
    private List<HyperEdge> edges = new ArrayList<HyperEdge>();
    int edgeLen = 0;
    int vertexLen = 0;
    
    public void addHyperVertex(HyperVertex v) {
    	vertices.add(v);
    	vertexMap.put(v.toString(), v);
    	vertexLen = vertices.size();
    }
    
    public void addHyperVertex(myWord wrd) {
    	HyperVertex v = new HyperVertex(wrd);
    	vertices.add(v);
    	vertexMap.put(wrd.toString(), v);
    	vertexLen = vertices.size();
    }
    
    public void addHyperVertex(String s) {
    	HyperVertex v = new HyperVertex(s);
    	vertices.add(v);
    	vertexMap.put(s, v);
    	vertexLen = vertices.size();
    }
    
    public void addHyperEdge(myTerm t, double w) {
    	String[] nodes = new String[t.getArgs().length];
    	for (int i = 0; i < t.getArgs().length; i++) {
    		nodes[i] = t.getArg(i).toString();
    		if (!isVertex(t.getArg(i)))
    			addHyperVertex(t.getArg(i));
    	}
    	for (int i = 0; i < nodes.length; i++) {
    		LinkedHashSet<String> adjacent = adjMap.get(nodes[i]);
            if(adjacent == null) {
                adjacent = new LinkedHashSet<String>();
                adjMap.put(nodes[i], adjacent);
            }
            for (int j = 0; j < nodes.length; j ++) {
            	if (i != j)
            		adjacent.add(nodes[j]);	
            }
    	}
    	HyperEdge e = new HyperEdge(t,w);
    	edges.add(e);
    	edgeLen = edges.size();
    	edgeMap.put(t.toString(), e);
    }

    public void addHyperEdge(String name, HyperVertex[] nodes, double w) {
    	String s = "";
    	for (int i = 0; i < nodes.length; i++) {
    		if (!isVertex(nodes[i])) {
    			// new vertex
    			addHyperVertex(nodes[i]);
    		}
    		s = s + nodes[i].toString() + ',';
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
    	edgeLen = edges.size();
    	edgeMap.put(t, e);
    }
    
    public void addHyperEdge(String s) {
    	myTerm t = new myTerm(s);
    	addHyperEdge(t, 0.0);
    	edgeLen = edges.size();
    }
    
    public void addHyperEdge(String s, double w) {
    	myTerm t = new myTerm(s);
    	addHyperEdge(t, w);
    	edgeLen = edges.size();
    }
    
    public void addHyperEdge(myTerm t) {
    	addHyperEdge(t, 0.0);
    	edgeLen = edges.size();
    }

    public void addHyperEdge(String name, HyperVertex[] nodes) {
    	addHyperEdge(name, nodes, 0.0);
    	edgeLen = edges.size();
    }
    
    public boolean isConnected(String node1, String node2) {
        Set adjacent = adjMap.get(node1);
        if(adjacent == null) {
            return false;
        }
        return adjacent.contains(node2);
    }
    
    public boolean isConnected(myWord w1, myWord w2) {
        return isConnected(w1.toString(), w2.toString());
    }
    
    public boolean isConnected(HyperVertex v1, HyperVertex v2) {
        return isConnected(v1.name, v2.name);
    }

    public LinkedList<String> adjacentNodes(String last) {
        LinkedHashSet<String> adjacent = adjMap.get(last);
        if(adjacent==null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(adjacent);
    }
    
    public LinkedList<HyperEdge> adjacentEdges(String node) {
    	// TODO find all adjacent edges of a node
    	return null;
    }
    
    public LinkedList<String> adjacentNodes(myWord w) {
    	return adjacentNodes(w.toString());
    }
    
    public LinkedList<String> adjacentNodes(HyperVertex v) {
    	return adjacentNodes(v.name);
    }
    
    public boolean isVertex(myWord node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name == node.toString()) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isVertex(HyperVertex node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name == node.toString()) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isVertex(String node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name == node) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public List<HyperEdge> getEdges(){
    	return edges;
    }
    
    public HyperEdge getEdge(int i) {
    	return edges.get(i);
    }
    
    public List<HyperVertex> getVertices() {
    	return vertices;
    }
    
    public HyperVertex getVertice(int i) {
    	return vertices.get(i);
    }
    
}
