/**
 * 
 */
package ILP;

import Logic.myTerm;
import Logic.myWord;

/**
 * @author Wang-Zhou
 *
 */
public class HyperEdge {

	/**
	 * HyperEdge that can connect multiple vertices.
	 */
	String name;
	HyperVertex[] vertices;
	double weight;
	int vertexLen;
	public HyperEdge(String rel, String[] nodes, double w) {
		name = rel;
		HyperVertex[] buff_vertices = new HyperVertex[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			buff_vertices[i].setName(nodes[i]);
		}
		vertices = buff_vertices;
		vertexLen = buff_vertices.length;
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
		vertexLen = buff_vertices.length;
		buff_vertices = null;
		weight = 0.0;
	}
	
	public HyperEdge(String rel, HyperVertex[] v, double w) {
		name = rel;
		vertices = v;
		vertexLen = v.length;
		weight = w;
	}
	
	public HyperEdge(String rel, HyperVertex[] v) {
		name = rel;
		vertices = v;
		vertexLen = v.length;
		weight = 0.0;
	}
	
	public HyperEdge(myTerm t, double w) {
		name = t.getPred().getName();
		HyperVertex[] v = new HyperVertex[t.getArgs().length];
		for (int i = 0; i < t.getArgs().length; i++) {
			v[i] = new HyperVertex(t.getArgs()[i]);
		}
		vertices = v;
		vertexLen = v.length;
		weight = w;
	}
	
	public HyperEdge(myTerm t) {
		name = t.getPred().getName();
		HyperVertex[] v = new HyperVertex[t.getArgs().length];
		for (int i = 0; i < t.getArgs().length; i++) {
			v[i] = new HyperVertex(t.getArgs()[i]);
		}
		vertices = v;
		vertexLen = v.length;
		weight = 0.0;
	}
	
	public void setWeight(double w) {
		weight = w;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public String getName() {
		return name;
	}
	
	public int vertexLen() {
		return vertexLen;
	}
	
	public HyperVertex getVertex(int i) {
		return vertices[i];
	}
	
	public HyperVertex[] getVerices() {
		return vertices;
	}
	
	public myTerm toMyTerm() {
		myWord[] words = new myWord[this.vertexLen];
		for (int i = 0; i < this.vertexLen; i++) {
			words[i] = new myWord(this.vertices[i].name);
		}
		return new myTerm(name, words);
	}
	
	public boolean equals(HyperEdge e) {
		if (this.name != e.getName())
			return false;
		else {
			for (int i = 0; i < vertexLen; i++) {
				if (!(this.getVertex(i).equals(e.getVertex(i))))
					return false;
			}
		}
		return true;
	}
	
	public boolean equals(myTerm t) {
		if (this.name != t.getPred().getName())
			return false;
		else {
			for (int i = 0; i < vertexLen; i++) {
				if (!(this.getVertex(i).equals(t.getArg(i))))
					return false;
			}
		}
		return true;
	}
	
	public boolean containsVertex(HyperVertex v) {
		for (int i = 0; i < vertexLen; i++) {
			if (this.getVertex(i).equals(v))
				return true;
		}
		return false;
	}
	
	public boolean containsVertex(String s) {
		for (int i = 0; i < vertexLen; i++) {
			if (this.getVertex(i).equals(s))
				return true;
		}
		return false;
	}
	
	public String toString() {
		return this.toMyTerm().toString();
	}
}
