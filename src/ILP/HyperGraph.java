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
import java.util.HashSet;
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
	
    private Map<String, LinkedHashSet<String>> adjMap = new HashMap<String, LinkedHashSet<String>>(); // map(node, AdjacentSet<node>)
    private Map<String, HyperEdge> edgeMap = new HashMap<String, HyperEdge>(); //map(edgeName, edge)
    private Map<HyperEdge, LinkedHashSet<HyperEdge>> adjEdgeMap = new HashMap<HyperEdge, LinkedHashSet<HyperEdge>>();
    private Map<String, HyperVertex> vertexMap = new HashMap<String, HyperVertex>(); // map(nodeName, node)
    private Map<String, LinkedHashSet<HyperEdge>> nodeEdgeMap = new HashMap<String, LinkedHashSet<HyperEdge>>();
    private List<HyperVertex> vertices = new ArrayList<HyperVertex>();
    private List<HyperEdge> edges = new ArrayList<HyperEdge>();
    int edgeLen = 0;
    int vertexLen = 0;
    
    public int getEdgeLen() {
    	return edgeLen;
    }
    
    public int getVertexLen() {
    	return vertexLen;
    }
    
    public void addHyperVertex(HyperVertex v) {
    	boolean contained = false;
    	for (HyperVertex vertex : vertices) {
    		if (vertex.equals(v))
    				contained = true;
    	}
    	if (!contained) {
    		vertices.add(v);	
    		vertexMap.put(v.toString(), v);
    		vertexLen = vertices.size();
    	}
    }
    
    public void addHyperVertex(myWord wrd) {
    	boolean contained = false;
    	for (HyperVertex vertex : vertices) {
    		if (vertex.equals(wrd))
    				contained = true;
    	}
    	if (!contained) {
    		HyperVertex v = new HyperVertex(wrd);
    		vertices.add(v);
    		vertexMap.put(wrd.toString(), v);
    		vertexLen = vertices.size();
    	}
    }
    
    public void addHyperVertex(String s) {
    	boolean contained = false;
    	for (HyperVertex vertex : vertices) {
    		if (vertex.equals(s))
    				contained = true;
    	}
    	if (!contained) {
    		HyperVertex v = new HyperVertex(s);
    		vertices.add(v);
    		vertexMap.put(s, v);
    		vertexLen = vertices.size();
    	}
    }
    
    private void updateAdjEdgeMap(HyperEdge e) {
    	LinkedHashSet<HyperEdge> new_adj = adjEdgeMap.get(e); // New a adjEdgeMap of e
    	if (new_adj == null) {
    		new_adj = new LinkedHashSet<HyperEdge>();
            adjEdgeMap.put(e, new_adj);
    	}
    	for (HyperEdge edge : this.edges) { // for all old edges
    		// if same, continue
    		if (edge.equals(e))
    			continue;
    		// if shareVertex(e, edge), update
    		LinkedHashSet<HyperEdge> adjacent = adjEdgeMap.get(edge);
			if (adjacent == null) {
				adjacent = new LinkedHashSet<HyperEdge>();
                adjEdgeMap.put(edge, adjacent);
			}
    		if (shareVertex(e, edge).size() > 0) {
    			// if shared, add a two-way "edge" of these two edges
//    			System.out.println("connected: " + e.toMyTerm().toString() + ", " + edge.toMyTerm().toString());
    			adjacent.add(e);
    			new_adj.add(edge);
    		}
    	}
    }
    
    private Set<HyperVertex> shareVertex(HyperEdge e_1, HyperEdge e_2) {
    	// return shared vertex between two hyper edges
    	Set<HyperVertex> shared = new HashSet<HyperVertex>();
    	for (HyperVertex v : e_1.getVerices()) {
    		if (e_2.containsVertex(v)) {
    			shared.add(v);
    		}
    	}
    	return shared;
    }
    
    public void addHyperEdge(myTerm t, double w) {
    	// add node
    	String[] nodes = new String[t.getArgs().length];
    	for (int i = 0; i < t.getArgs().length; i++) {
    		nodes[i] = vertexMap.get(t.getArg(i).toString()).toString();
    		if (!isVertex(t.getArg(i)))
    			addHyperVertex(t.getArg(i));
    	}
    	
    	// add edge
    	HyperEdge e = new HyperEdge(t,w);
    	edges.add(e);
    	edgeLen = edges.size();
    	edgeMap.put(t.toString(), e);
    	
    	// update node adjlist
    	for (int i = 0; i < nodes.length; i++) {
    		LinkedHashSet<String> adjacent = adjMap.get(nodes[i]);
    		LinkedHashSet<HyperEdge> belongs = nodeEdgeMap.get(nodes[i]);
            if(adjacent == null) {
            	// new an adjacent list term
                adjacent = new LinkedHashSet<String>();
                adjMap.put(nodes[i], adjacent);
            }
            if (belongs == null) {
            	belongs = new LinkedHashSet<HyperEdge>();
            	nodeEdgeMap.put(nodes[i], belongs);
            }
            for (int j = 0; j < nodes.length; j ++) {
            	if (i != j)
            		adjacent.add(nodes[j]);	
            }
            belongs.add(e);
            
    	}
    	
//    	HyperEdge e = new HyperEdge(t,w);
//    	edges.add(e);
//    	edgeLen = edges.size();
//    	edgeMap.put(t.toString(), e);
    	
    	// update map adjEdge
    	updateAdjEdgeMap(e);
    }

//    public void addHyperEdge(String name, HyperVertex[] nodes, double w) {
//    	
//    	String s = "";
//    	for (int i = 0; i < nodes.length; i++) {
//    		if (!isVertex(nodes[i])) {
//    			// new vertex
//    			addHyperVertex(nodes[i]);
//    		}
//    		s = s + nodes[i].toString() + ',';
//    		LinkedHashSet<String> adjacent = adjMap.get(nodes[i].getName());
//            if(adjacent == null) {
//                adjacent = new LinkedHashSet<String>();
//                adjMap.put(nodes[i].getName(), adjacent);
//            }
//            for (int j = 0; j < nodes.length; j ++) {
//            	if (i != j)
//            		adjacent.add(nodes[j].getName());	
//            }
//    	}
//    	String t = String.format("%s(%s)", name, s.substring(0, s.length() - 2));
//    	HyperEdge e = new HyperEdge(name, nodes, w);
//    	edges.add(e);
//    	edgeLen = edges.size();
//    	edgeMap.put(t, e);
//    	// add map adjEdge
//    	updateAdjEdgeMap(e);
//    }
    
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

//    public void addHyperEdge(String name, HyperVertex[] nodes) {
//    	addHyperEdge(name, nodes, 0.0);
//    	edgeLen = edges.size();
//    }
    
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
    
//    public LinkedList<HyperEdge> edgesContainsVertex(String node) {
//    	// find all adjacent edges of a node
//    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
//    	for (int i = 0; i < edgeLen; i++) {
//    		if (edges.get(i).containsVertex(node))
//    			r.add(edges.get(i));
//    	}
//    	return r;
//    }
//    
//    public LinkedList<HyperEdge> edgesContainsVertex(HyperVertex node) {
//    	// find all adjacent edges of a node
//    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
//    	for (int i = 0; i < edgeLen; i++) {
//    		if (edges.get(i).containsVertex(node))
//    			r.add(edges.get(i));
//    	}
//    	return r;
//    }
//
//    public LinkedList<HyperEdge> edgesContainsVertex(myWord node) {
//    	// find all adjacent edges of a node
//    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
//    	for (int i = 0; i < edgeLen; i++) {
//    		if (edges.get(i).containsVertex(node.toString()))
//    			r.add(edges.get(i));
//    	}
//    	return r;
//    }
    
//    public LinkedList<HyperEdge> adjacentEdges(HyperEdge e) {
//    	// find all adjacent edges of an edge;
//    	LinkedList<HyperEdge> r = new LinkedList<HyperEdge>();
//    	for (Iterator<HyperEdge> it = edges.iterator(); it.hasNext();) {
//    		HyperEdge tmp_e = (HyperEdge) it.next();
//    		// for each contained vertex, find a list of its edges
//    		for (int i = 0; i < tmp_e.vertexLen(); i++) {
//    			HyperVertex tmp_v = tmp_e.getVertex(i);
//    			LinkedList<HyperEdge> l = edgesContainsVertex(tmp_v);
//    			for (Iterator<HyperEdge> it_2 = l.iterator(); it_2.hasNext();) {
//    				HyperEdge tmp_e_2 = (HyperEdge) it_2.next();
//    				if (!r.contains(tmp_e_2) &&  (!tmp_e_2.equals(e)))
//    					r.add(tmp_e_2);
//    			}
//    		}
//    	}
//    	return r;
//    }
    
   
    public LinkedList<HyperEdge> adjacentEdges(HyperEdge last) {
        LinkedHashSet<HyperEdge> adjacent = adjEdgeMap.get(last);
        if(adjacent == null) {
            return new LinkedList<HyperEdge>();
        }
        return new LinkedList<HyperEdge>(adjacent);
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
        LinkedHashSet<String> adjacent = adjMap.get(vertexMap.get(last));
        if(adjacent == null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(adjacent);
    }
    
    
    public LinkedList<HyperVertex> adjacentNodes(myWord w) {
    	return adjacentNodes(vertexMap.get(w.toString()));
    }
    
    public LinkedList<HyperVertex> adjacentNodes(HyperVertex v) {
    	return adjacentNodes(v);
    }
    
    public boolean isVertex(myWord node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name.equals(node.toString())) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isVertex(HyperVertex node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name.equals(node.toString())) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isVertex(String node) {
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name.equals(node)) {
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
    
    public HyperVertex getVertex(int i) {
    	return vertices.get(i);
    }
    
    public LinkedList<HyperEdge> edgesContainsVertex(String last) {
    	LinkedHashSet<HyperEdge> belongs = nodeEdgeMap.get(last);
        if(belongs == null) {
            return new LinkedList<HyperEdge>();
        }
        return new LinkedList<HyperEdge>(belongs);
    }
}
