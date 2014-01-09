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
	 * 是找出图上从起点到终点的所有路径吗
	 * 标记一个diff，每个节点只能被触发两次
	 */
    myWord start;
    myWord end;
    HyperGraph graph;
    ArrayList<LinkedList<myTerm>> path = new ArrayList<LinkedList <myTerm>>();
    
    int debug = 0;
    
    public HyperPathFind(HyperGraph g, String s, String e) {//应该算作new的过程
    	graph = g;
    	start = new myWord(s);//简单处理一下s new为start
    	end = new myWord(e);
    }
    
    public HyperPathFind(HyperGraph g, HyperVertex s, HyperVertex e) {//这输入是个点吗？貌似是  之前理解太差了
    	graph = g;
    	start = s.toMyWord();
    	end = e.toMyWord();
    }
    
    public HyperPathFind(HyperGraph g, myWord s, myWord e) {
    	graph = g;
    	start = s;
    	end = e;
    }
    
    public ArrayList<LinkedList<myTerm>> Search(LinkedList<HyperEdge> visitedEdges) {//输入是个out
    	
//    	System.out.format("start %s, end %s\n", start, end);
    	LinkedList<HyperEdge> start_edges = allEdgesContain(start);//包含起点的所有边
    	for (HyperEdge edge : start_edges) {
    		// visit start_edges
    		if (edge.containsVertex(end)) {//这个边有终点的话 输出
    			visitedEdges.add(edge);
//        		printPath(visitedEdges);
        		path.add(returnPath(visitedEdges));//returnPath返回是个LinkedList<term> add进term的
        		visitedEdges.removeLast();//删除末尾 也就是刚才加入的 为啥这样搞？
        		continue;
    		} else {
    			visitedEdges.add(edge);
    			depthFirst(visitedEdges);//没有终点的话 用这个
    			visitedEdges.removeLast();
    		}
    	}
    	return path;
    }
    /**
     * 返回所有graph上包含输入node的边
     * @param node
     * @return
     */
    @SuppressWarnings("unused")
	private LinkedList<HyperEdge> allEdgesContain(String node) {
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	for (HyperEdge edge : graph.getEdges()) {
    		if (edge.containsVertex(node))
    			re.add(edge);
    	}
		return re;
	}
    /**
     * 返回所有graph上包含输入node的边
     * @param node
     * @return
     */
    private LinkedList<HyperEdge> allEdgesContain(myWord node) {
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	for (HyperEdge edge : graph.getEdges()) {
    		if (edge.containsVertex(node))
    			re.add(edge);
    	}
		return re;
	}
    
    /**
     * Available edges in HyperPath: each node can only appears twice at most, or there will 
     * be redundant edges.
     */
	private LinkedList<HyperEdge> adjAvailEdges(HyperEdge e, LinkedList<HyperEdge> visited) {
    	LinkedList<HyperEdge> edges = graph.adjacentEdges(e);//所有包含 输入e 的边
    	LinkedList<HyperEdge> re = new LinkedList<HyperEdge>();
    	LinkedList<myWord> node_1 = new LinkedList<myWord>();
    	// very important!重要！
    	node_1.add(start);
    	LinkedList<myWord> node_2 = new LinkedList<myWord>();
    	for (HyperEdge edge : visited) {
    		for (HyperVertex node : edge.getVerices()) {//输入LinkedList<HyperEdge> visited中 每个边的每个节点
    			myWord tmp = node.toMyWord();
    			if (node_1.contains(tmp))
    				if (node_2.contains(tmp)) {//node1 2 都有这个节点就是依存树错误了 貌似
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
    				node_1.add(tmp);//缺1加1  缺2加2
    		}
    	}
    	
    	for (HyperEdge edge : edges) {//所有包含 输入e 的边  如果在 输入visited的节点中没有 并且  在输入visited的节点所在的边  node2中一个都没有
    		boolean add = true;
    		if (visited.contains(edge)) {
    			add = false;
    		}
    		for (HyperVertex node : edge.getVerices()) {
    			if (node_2.contains(node)) {
    				add = false;
    				break;
    			}
    		}
    		if (add)
    			re.add(edge);
    	}
    	return re;
    }
    /**
     * 深度优先递归找节点
     * @param visitedEdges
     */
    private void depthFirst(LinkedList<HyperEdge> visitedEdges) {
    	LinkedList<HyperEdge> edges = adjAvailEdges(visitedEdges.getLast(), visitedEdges);
        // examine adjacent nodes和上面的类似 找下一个节点是否有end
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge)) {
                continue;
            }
            if (edge.containsVertex(end)) {
            	visitedEdges.add(edge);
//        		printPath(visitedEdges);
        		path.add(returnPath(visitedEdges));
        		visitedEdges.removeLast();
        		break;
            }
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (HyperEdge edge : edges) {
            if (visitedEdges.contains(edge) || edge.containsVertex(end)) {//第一个条件是不是构成环了？
                continue;
            }
            visitedEdges.addLast(edge);
            depthFirst(visitedEdges);
            visitedEdges.removeLast();
        }
    }
/**
 * 打印路径
 * @param visitedEdges
 */
    @SuppressWarnings("unused")
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
    /**
     * 把输入的LinkedList<HyperEdge> visitedEdges加入一个新的MyTerm中返回这个MyTerm
     * @param visitedEdges
     * @return
     */
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
    
	public ArrayList<LinkedList<myTerm>> getPaths() {
		return path;
	}
}
