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
import java.util.Iterator;
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
    
    public LinkedList<HyperEdge> edgesContainsVertex(String node) {
    	// find all adjacent edges of a node
    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
    	for (int i = 0; i < edgeLen; i++) {
    		if (edges.get(i).containsVertex(node))
    			r.add(edges.get(i));
    	}
    	return r;
    }
    
    public LinkedList<HyperEdge> edgesContainsVertex(HyperVertex node) {
    	// find all adjacent edges of a node
    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
    	for (int i = 0; i < edgeLen; i++) {
    		if (edges.get(i).containsVertex(node))
    			r.add(edges.get(i));
    	}
    	return r;
    }

    public LinkedList<HyperEdge> edgesContainsVertex(myWord node) {
    	// find all adjacent edges of a node
    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
    	for (int i = 0; i < edgeLen; i++) {
    		if (edges.get(i).containsVertex(node.toString()))
    			r.add(edges.get(i));
    	}
    	return r;
    }
    
    public LinkedList<HyperEdge> adjacentEdges(HyperEdge e) {
    	// find all adjacent edges of an edge;
    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
    	for (Iterator<HyperEdge> it = edges.iterator(); it.hasNext();) {
    		HyperEdge tmp_e = (HyperEdge) it.next();
    		// for each contained vertex, find a list of its edges
    		for (int i = 0; i < tmp_e.vertexLen(); i++) {
    			HyperVertex tmp_v = tmp_e.getVertex(i);
    			LinkedList<HyperEdge> l = edgesContainsVertex(tmp_v);
    			for (Iterator<HyperEdge> it_2 = l.iterator(); it_2.hasNext();) {
    				HyperEdge tmp_e_2 = (HyperEdge) it_2.next();
    				if (!r.contains(tmp_e_2))
    					r.add(tmp_e_2);
    			}
    		}
    	}
    	return r;
    }
    
    public LinkedList<HyperEdge> adjacentEdges(myTerm t) {
    	for (Iterator<HyperEdge> it = edges.iterator(); it.hasNext(); ) {
    		HyperEdge e = it.next();
    		if (e.equals(t))
    			return adjacentEdges(e);
    	}
    	return null;
    }
    
    public LinkedList<String> adjacentNodes(String last) {
        LinkedHashSet<String> adjacent = adjMap.get(last);
        if(adjacent==null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(adjacent);
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
