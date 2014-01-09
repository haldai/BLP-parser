/**
 * 
 */
package ILP;

import java.util.ArrayList;

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
	HyperVertex[] vertices;//是点的list吗
	double weight;
	int vertexLen;
	public HyperEdge(String rel, String[] nodes, double w) {
		name = rel;
		HyperVertex[] buff_vertices = new HyperVertex[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			buff_vertices[i].setName(nodes[i]);//输入node的名字 下面存到了public的 vertices中
		}
		vertices = buff_vertices;
		vertexLen = buff_vertices.length;
		buff_vertices = null;
		weight = w;//输入来的 直接存为public的变量 有啥用  干啥用的？
	}
	
	public HyperEdge(String rel, String[] nodes) {//和上很相似
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
	
	public HyperEdge(String rel, HyperVertex[] v, double w) {//直接输入HyperVertex数组、name、weight等  把数组拷贝一下 
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
	
	public HyperEdge(myTerm t, double w) {//谓词的名字作为名字？ body里面所有节点都作为节点 先不做啥处理
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
	
	public myTerm toMyTerm() {//貌似是逆向过程 返回的pred的name和每个的name 
		ArrayList<myWord> words = new ArrayList<myWord>(this.vertexLen);
		for (int i = 0; i < this.vertexLen; i++) {
			words.add(new myWord(this.vertices[i].name));
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
	
	public boolean containsVertex(HyperVertex v) {//this是否是输入的子集
		for (int i = 0; i < vertexLen; i++) {
			if (this.getVertex(i).equals(v))
				return true;
		}
		return false;
	}
	
	public boolean containsVertex(myWord v) {
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
