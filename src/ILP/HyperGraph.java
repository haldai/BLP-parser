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
 * a hyper-edge could connect many nodes, e.g., have_more_than_n_pubs(sam, john, 2)这个解释对吗？
 */
public class HyperGraph {
	
    private Map<String, LinkedHashSet<String>> adjMap = new HashMap<String, LinkedHashSet<String>>(); // map(node, AdjacentSet<node>)
    private Map<String, HyperEdge> edgeMap = new HashMap<String, HyperEdge>(); //map(edgeName, edge)
    private Map<HyperEdge, LinkedHashSet<HyperEdge>> adjEdgeMap = new HashMap<HyperEdge, LinkedHashSet<HyperEdge>>();
    private Map<String, HyperVertex> vertexMap = new HashMap<String, HyperVertex>(); // map(nodeName, node)
    private Map<String, LinkedHashSet<HyperEdge>> nodeEdgeMap = new HashMap<String, LinkedHashSet<HyperEdge>>();
    private List<HyperVertex> vertices = new ArrayList<HyperVertex>();//到底是干屁的
    private List<HyperEdge> edges = new ArrayList<HyperEdge>();
    int edgeLen = 0;
    int vertexLen = 0;
    
    public int getEdgeLen() {
    	return edgeLen;
    }
    
    public int getVertexLen() {
    	return vertexLen;
    }
    
    public void addHyperVertex(HyperVertex v) {//如果有输入的边的话 做标记 没有的话加入
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
    
    public void addHyperVertex(myWord wrd) {//同上
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
    
    public void addHyperVertex(String s) {//同上
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
    
    private void updateAdjEdgeMap(HyperEdge e) {//名称“更改邻接边的map” 注意这个结构是很大的  要弄清HyperEdge这个数据结构
    	LinkedHashSet<HyperEdge> new_adj = adjEdgeMap.get(e); // New a adjEdgeMap of e为啥是新建的？？
    	if (new_adj == null) {//没有的话 新建LinkedHashSet 并放入
    		new_adj = new LinkedHashSet<HyperEdge>();
            adjEdgeMap.put(e, new_adj);
    	}
    	for (HyperEdge edge : this.edges) { // for all old edges每个已有的边
    		// if same, continue
    		if (edge.equals(e))
    			continue;
    		// if shareVertex(e, edge), update
    		LinkedHashSet<HyperEdge> adjacent = adjEdgeMap.get(edge);//得到adjEdgeMap这个map的value 是一个很大结构的HyperEdge
			if (adjacent == null) {//为空则新建
				adjacent = new LinkedHashSet<HyperEdge>();
                adjEdgeMap.put(edge, adjacent);
			}
    		if (shareVertex(e, edge).size() > 0) {//对于已有的边（old edges）和输入的边 是否有边Vertex
    			// if shared, add a two-way "edge" of these two edges
//    			System.out.println("connected: " + e.toMyTerm().toString() + ", " + edge.toMyTerm().toString());
    			adjacent.add(e);//存新边的有临街的边
    			new_adj.add(edge);//存老边的邻接边
    		}
    	}
    }
    
    private Set<HyperVertex> shareVertex(HyperEdge e_1, HyperEdge e_2) {
    	// return shared vertex between two hyper edges
    	Set<HyperVertex> shared = new HashSet<HyperVertex>();
    	for (HyperVertex v : e_1.getVerices()) {//得到他的vertices数组 v数数组中每个元素
    		if (e_2.containsVertex(v)) {//e2中是否包含当前节点（在e1中有的节点）
    			shared.add(v);//加入共有节点
    		}
    	}
    	return shared;
    }
    
    public void addHyperEdge(myTerm t, double w) {//加入边 输入是一个term和权重
    	// add node
    	String[] nodes = new String[t.getArgs().length];//node相当于当前term的word的数组
    	for (int i = 0; i < t.getArgs().length; i++) {
    		nodes[i] = vertexMap.get(t.getArg(i).toString()).toString();//输入myTerm t中的word的string形式存入nodes数组
    		if (!isVertex(t.getArg(i)))//输入myTerm t中的word 是否在当前vertices中  没有则加入vertices  vertices是所有边的集合
    			addHyperVertex(t.getArg(i));
    	}
    	
    	// add edge
    	HyperEdge e = new HyperEdge(t,w);//新建个e t貌似是单独的一个边vertices 放在了vertices数组中
    	edges.add(e);
    	edgeLen = edges.size();
    	edgeMap.put(t.toString(), e);//存map 例假
    	
    	// update node adjlist 
    	for (int i = 0; i < nodes.length; i++) {//完全没懂  再看！！！！
    		LinkedHashSet<String> adjacent = adjMap.get(nodes[i]);//当前word在adjMap里查
    		LinkedHashSet<HyperEdge> belongs = nodeEdgeMap.get(nodes[i]);//当前word在nodeEdgeMap里查
            if(adjacent == null) {
            	// new an adjacent list term
                adjacent = new LinkedHashSet<String>();
                adjMap.put(nodes[i], adjacent);//再加入回去 为啥啊？？？？
            }
            if (belongs == null) {
            	belongs = new LinkedHashSet<HyperEdge>();
            	nodeEdgeMap.put(nodes[i], belongs);
            }
            for (int j = 0; j < nodes.length; j ++) {
            	if (i != j)
            		adjacent.add(nodes[j]);	//不等于当前节点（按标号来）的word （node[j]）放入LinkedHashSet<String> adjacent
            }
            belongs.add(e);//在后面才把HyperEdge e加入LinkedHashSet<HyperEdge> belongs 
            
    	}
    	
//    	HyperEdge e = new HyperEdge(t,w);
//    	edges.add(e);
//    	edgeLen = edges.size();
//    	edgeMap.put(t.toString(), e);
    	
    	// update map adjEdge
    	updateAdjEdgeMap(e);//更新一轮map
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
    
    public boolean isConnected(String node1, String node2) {//输入A（node1）作为索引在adjMap中的值包含 输入B（node2）
        @SuppressWarnings("rawtypes")
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
    
   
    public LinkedList<HyperEdge> adjacentEdges(HyperEdge last) {//返回输入last所在的LinkedHashSet<HyperEdge> 通过adjEdgeMap
        LinkedHashSet<HyperEdge> adjacent = adjEdgeMap.get(last);
        if(adjacent == null) {
            return new LinkedList<HyperEdge>();
        }
        return new LinkedList<HyperEdge>(adjacent);
    }
    
    public LinkedList<HyperEdge> adjacentEdges(myTerm t) {//当前edges中的HyperEdge 如果和输入myTerm t相等 返回这个边的LinkedHashSet<HyperEdge>
    	for (Iterator<HyperEdge> it = edges.iterator(); it.hasNext(); ) {
    		HyperEdge e = it.next();
    		if (e.equals(t))
    			return adjacentEdges(e);
    	}
    	return null;
    }
    
    public LinkedList<String> adjacentNodes(String last) {//返回输入last所在的LinkedHashSet<HyperEdge> 通过adjMap
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
    
    public boolean isVertex(myWord node) {//当前vertices中是否有和输入相同的节点
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name.equals(node.toString())) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isVertex(HyperVertex node) {//同上
    	boolean found = false;
    	for (int k = 0; k < vertices.size(); k++) {
			if (vertices.get(k).name.equals(node.toString())) {
				found = true;
				break;
			}
		}
    	return found;
    }
    
    public boolean isVertex(String node) {//同上
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
    
    public LinkedList<HyperEdge> edgesContainsVertex(String last) {//返回输入last所在的LinkedHashSet<HyperEdge> 通过nodeEdgeMap
    	LinkedHashSet<HyperEdge> belongs = nodeEdgeMap.get(last);
        if(belongs == null) {
            return new LinkedList<HyperEdge>();
        }
        return new LinkedList<HyperEdge>(belongs);
    }
}
