/**
 * 
 */
package Boosting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import ILP.*;
import Logic.*;
import Tree.*;

/**
 * @author Wang-Zhou
 *
 */
public class AdaBoost {

	/**
	 * Adaboost main class
	 */
	private static final int T = utils.utils.BOOSTING_TURNS;
	private Prolog prolog; // prolog engine
	
	public AdaBoost(Prolog p) {
		prolog = p;
	}
	
	public AdaBoostOutput train(Document doc) {

		AdaBoostOutput re = new AdaBoostOutput();
		
		ArrayList<ArrayList<myTerm>> labels = doc.getLabels();
		ArrayList<Sentence> sentences = new ArrayList<Sentence>(Arrays.asList(doc.getSentences()));
		
		// assign each label term an weight
		int total_label = 0;
		for (ArrayList<myTerm> t_list : labels)
			for (myTerm t : t_list) {
				total_label++;
			}
		ArrayList<ArrayList<Double>> label_weights = new ArrayList<ArrayList<Double>>(labels.size());
		for (int i = 0; i < labels.size(); i++) {
			label_weights.add(new ArrayList<Double>(labels.get(i).size()));
			for (int j = 0; j < labels.get(i).size(); j++) {
				label_weights.get(i).add((double) 1/total_label);
			}
		}
		
		int turn = 0;
		// repeat training until meets the turn limit
		while (turn <= T) {
			
			RuleTree rule = new RuleTree(prolog);
			
			// sample the labels to produce a path
			Data data = new Data();
			if (turn == 0) {
				data = new Data(doc);
			} else {
				for (int i = 0; i < labels.size(); i++) {
					for (int j = 0; j < labels.get(i).size(); j++) {
						double rand = Math.random();
						if (label_weights.get(i).get(j) > rand)
							data.addData(labels.get(i).get(j), sentences.get(i));
					}
				}
			}
			
			// paths from current data
			ArrayList<LinkedList<myTerm>> all_paths = new ArrayList<LinkedList<myTerm>>();
			ArrayList<myTerm> all_heads = new ArrayList<myTerm>();
			for (int i = 0; i < sentences.size(); i++)
				for (int j = 0; j < labels.get(i).size(); j++) {
					all_heads.add(labels.get(i).get(j));
					all_paths.addAll(findPath(labels.get(i).get(j), sentences.get(i)));
				}
			
			// substitute all the paths and store the pattern with its frequency
			ArrayList<ArrayList<myTerm>> all_sub_paths = new ArrayList<ArrayList<myTerm>>();
			ArrayList<Integer> all_sub_paths_count = new ArrayList<Integer>();
			for (int i = 0; i < all_paths.size(); i++) {
				LinkedList<myTerm> path = all_paths.get(i);
				myTerm head = all_heads.get(i);
				ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
				all_terms.add(head);
				all_terms.addAll(path);
				Substitute subs = new Substitute(all_terms);
				ArrayList<myTerm> all_sub_terms = subs.getSubTerms();
				if (all_sub_paths.contains(all_sub_terms)) {
					int path_idx = all_sub_paths.indexOf(all_sub_terms);
					all_sub_paths_count.set(path_idx, all_sub_paths_count.get(path_idx) + 1);
				} else {
					all_sub_paths.add(all_sub_terms);
					all_sub_paths_count.add(0);
				}
			}
			
			// find the most frequent pattern
			int max_freq = -100, max_freq_idx = 0;			
			for (int i = 0; i < all_sub_paths.size(); i++)
				if (all_sub_paths_count.get(i) > max_freq) {
					max_freq = all_sub_paths_count.get(i);
					max_freq_idx = i;
				}
			
			// TODO use the head and path that represents the most frequent pattern to build tree
			myTerm head = null;
			LinkedList<myTerm> path = null;
			rule.buildTree(data, head, path);
		}
		return re;
	}
	
	private static ArrayList<LinkedList<myTerm>> findPath(myTerm label, Sentence sent) {
		HyperGraph graph = new HyperGraph();
		myTerm[] terms = sent.getTerms();
		myWord[] words = sent.getWords();
		for (myWord word : words) {
			graph.addHyperVertex(word);
		}
		
		for (myTerm term : terms) {
			graph.addHyperEdge(term);
		}
		
		HyperVertex start = new HyperVertex(label.getArg(0));
		HyperVertex end = new HyperVertex(label.getArg(1));
		HyperPathFind pf = new HyperPathFind(graph, start, end);
		LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
		pf.Search(visitedEdges);
		
		return pf.getPaths();
	}
}
