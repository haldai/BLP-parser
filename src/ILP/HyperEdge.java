/**
 * 
 */
package ILP;

import Logic.myTerm;
import Logic.Predicate;

/**
 * @author Wang-Zhou
 *
 */
public class HyperEdge {

	/**
	 * HyperEdge that can connect multiple vertices.
	 */
	String name = null;
	HyperVertex[] vertices = null;
	double weight = 0.0;
	public HyperEdge(String rel, String[] nodes, double w) {
		name = rel;
		HyperVertex[] buff_vertices = new HyperVertex[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			buff_vertices[i].setName(nodes[i]);
		}
		vertices = buff_vertices;
		buff_vertices = null;
		weight = w;
	}
	
	public HyperEdge(String rel, String[] nodes) {
		name = rel;
		HyperVertex[] buff_vertices = new HyperVertex[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			buff_vertices[i].setName(nodes[i]);
		}
		vertices = buff_vertices;
		buff_vertices = null;
		weight = 0.0;
	}
	
	public HyperEdge(String rel, HyperVertex[] v, double w) {
		name = rel;
		vertices = v;
		weight = w;
	}
	
	public HyperEdge(String rel, HyperVertex[] v) {
		name = rel;
		vertices = v;
		weight = 0.0;
	}
	
	public HyperEdge(myTerm t, double w) {
		name = t.getPred().getName();
		HyperVertex[] v = new HyperVertex[t.getArgs().length];
		for (int i = 0; i < t.getArgs().length; i++) {
			v[i] = new HyperVertex(t.getArgs()[i]);
		}
		vertices = v;
		weight = w;
	}
	
	public HyperEdge(myTerm t) {
		name = t.getPred().getName();
		HyperVertex[] v = new HyperVertex[t.getArgs().length];
		for (int i = 0; i < t.getArgs().length; i++) {
			v[i] = new HyperVertex(t.getArgs()[i]);
		}
		vertices = v;
		weight = 0.0;
	}
	
	public void setWeight(double w) {
		weight = w;
	}
	
	public double getWeight() {
		return weight;
	}
}
