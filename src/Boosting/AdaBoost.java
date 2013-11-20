/**
 * 
 */
package Boosting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
		
		int turn = -1;
		// repeat training until meets the turn limit
		while (turn <= T) {
			turn++;
			
			RuleTree rule = new RuleTree(prolog, doc.getPredList());
			
			// sample the labels to produce a path
			Data data = new Data();
			if (turn == 0) {
				data = new Data(doc);
			} else {
				for (int i = 0; i < labels.size(); i++) {
					for (int j = 0; j < labels.get(i).size(); j++) {
						double rand = Math.random()/5;
						if (label_weights.get(i).get(j) > rand)
							data.addData(labels.get(i).get(j), sentences.get(i));
					}
				}
			}
			
			// paths from current data
			ArrayList<LinkedList<myTerm>> all_paths = new ArrayList<LinkedList<myTerm>>();
			ArrayList<myTerm> all_heads = new ArrayList<myTerm>();
			for (int i = 0; i < data.getSents().size(); i++)
				for (int j = 0; j < data.getLabel(i).size(); j++) {
					all_heads.add(data.getLabel(i).get(j));
					all_paths.addAll(findPath(data.getLabel(i).get(j), data.getSent(i)));
				}

			for (int i = 0; i < all_paths.size(); i++) {
				System.out.println(all_heads.get(i) + ":-" + all_paths.get(i));
			}
			System.out.println();
			// substitute all the paths and store the pattern with its frequency
			ArrayList<ArrayList<myTerm>> all_sub_paths = new ArrayList<ArrayList<myTerm>>();
			ArrayList<Integer> all_sub_paths_count = new ArrayList<Integer>();
			Map<ArrayList<myTerm>, ArrayList<Integer>> pat_path_map = new HashMap<ArrayList<myTerm>, ArrayList<Integer>>();
			for (int i = 0; i < all_paths.size(); i++) {
				LinkedList<myTerm> path = all_paths.get(i);
				myTerm head = all_heads.get(i);
				ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
				all_terms.add(head);
				all_terms.addAll(path);
				Substitute subs = new Substitute(all_terms);
				ArrayList<myTerm> all_sub_terms = subs.getSubTerms();
				
				
				for (int k = 0; k < all_sub_terms.size(); k++) {
					System.out.print(all_sub_terms.get(k).toPrologString() + ", ");
				}
				System.out.println();
				
				if (all_sub_paths.contains(all_sub_terms)) {
					int path_idx = all_sub_paths.indexOf(all_sub_terms);
					all_sub_paths_count.set(path_idx, all_sub_paths_count.get(path_idx) + 1);
					pat_path_map.get(all_sub_paths.get(path_idx)).add(i);
				} else {
					if (pat_path_map.get(all_sub_terms) == null)
						pat_path_map.put(all_sub_terms, new ArrayList<Integer>());
					pat_path_map.get(all_sub_terms).add(i);
					all_sub_paths.add(all_sub_terms);
					all_sub_paths_count.add(1);
				}
			}
			
			System.out.println("=============patterns================");
			for (int i = 0; i < all_sub_paths.size(); i++) {
				for (int j = 0; j < all_sub_paths.get(i).size(); j++) {
					System.out.print(all_sub_paths.get(i).get(j).toPrologString() + ", ");
				}
				System.out.println();
			}
			
			// find the most frequent pattern
			int max_freq = -100, max_freq_idx = 0;			
			for (int i = 0; i < all_sub_paths.size(); i++)
				if (all_sub_paths_count.get(i) > max_freq) {
					max_freq = all_sub_paths_count.get(i);
					max_freq_idx = i;
				}
			
			// use the head and path that represents the most frequent pattern to build tree
			System.out.println(pat_path_map.get(all_sub_paths.get(max_freq_idx)));
			myTerm head = all_heads.get(pat_path_map.get(all_sub_paths.get(max_freq_idx)).get(0));
			LinkedList<myTerm> path = all_paths.get(pat_path_map.get(all_sub_paths.get(max_freq_idx)).get(0));
			rule.buildTree(data, head, path);
			
			// TODO use the rule to evaluate the whole document and reset the weight
			rule.evaluateThis(new Data(doc));
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
