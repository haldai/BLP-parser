/**
 * 
 */
package Boosting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import utils.Tuple;
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
				data = weightedRandSample(labels, sentences, label_weights, 5);
//				for (int i = 0; i < labels.size(); i++) {
//					for (int j = 0; j < labels.get(i).size(); j++) {
//						double rand = Math.random()/5;
//						if (label_weights.get(i).get(j) > rand)
//							data.addData(labels.get(i).get(j), sentences.get(i));
//					}
//				}
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
			
			// use the rule to evaluate the whole document and reset the weight
			SentSat cur_tree_sent_sat = rule.evaluateThis(new Data(doc)); // current tree sentence satisfy samples
			
			double err = 1 - cur_tree_sent_sat.getAccuracy(); // error of current tree, can be set weight
			double cov = cur_tree_sent_sat.getCov();
			
			// TODO set new (negative) labels and reweight
			for (int k = 0; k < cur_tree_sent_sat.getAllSats().size(); k++) {
				Sentence tmp_sent = cur_tree_sent_sat.getAllSent(k);
				SatisfySamples tmp_sat = cur_tree_sent_sat.getAllSats(k);
				
				int sent_idx = k;
				
				// TODO deal with uncovered samples
				for (int ii = 0; ii < labels.get(sent_idx).size(); ii++) {
					if (!tmp_sat.getNegative().contains(labels.get(sent_idx).get(ii)) 
							&& !tmp_sat.getPositive().contains(labels.get(sent_idx).get(ii))) {
						// TODO assign weight
						double new_weight = 0.0;
						new_weight = label_weights.get(sent_idx).get(ii) + 1 - cov;
						label_weights.get(sent_idx).set(ii, new_weight);
					}
				}
				
				// TODO deal with negative samples
				for (myTerm tmp_term : tmp_sat.getNegative()) {
					ArrayList<myTerm> tmp_labels = labels.get(sent_idx);
					int label_idx = 0;
					if (tmp_labels.contains(tmp_term)) {
						label_idx = tmp_labels.indexOf(tmp_term);
					} else {
						// add new negative sample
						myTerm new_label = tmp_term.clone();
						if (!tmp_term.isPositive())
							new_label.setNegative();
						else
							new_label.setPositive();
						tmp_labels.add(tmp_term.clone());
						label_idx = tmp_labels.size() - 1;
						label_weights.get(sent_idx).add(0.0);
					}
					
					// TODO assign weight
					double new_weight = 0.0;
					new_weight = label_weights.get(sent_idx).get(label_idx) + 1-tmp_term.getWeight();
					label_weights.get(sent_idx).set(label_idx, new_weight);
					
				}
				
				// TODO deal with positive samples
				for (myTerm tmp_term : tmp_sat.getPositive()) {
					double new_weight = 0.0;
					if (!tmp_term.isPositive())
						continue;
					else {
						ArrayList<myTerm> tmp_labels = labels.get(sent_idx);
						int label_idx = 0;
						if (tmp_labels.contains(tmp_term)) {
							label_idx = tmp_labels.indexOf(tmp_term);
						}
						// TODO set weight
//						new_weight = tmp_labels.get(label_idx).getWeight();
						
//						label_weights.get(sent_idx).set(label_idx, new_weight);
					}
				}
			}
			
			
			
			// TODO normalize weights
			double sum = 0;
			for (ArrayList<Double> d_list : label_weights) {
				for (Double d : d_list) {
					sum = sum + d;
				}
			}
			
			for (int ii = 0; ii < label_weights.size(); ii++) {
				for (int jj = 0; jj < label_weights.get(ii).size(); jj++) {
					label_weights.get(ii).set(jj, label_weights.get(ii).get(jj)/sum);
				}
			}
			
		}
		return re;
	}
	/**
	 * A-ES sampling
	 * @param labels
	 * @param sentences
	 * @param weight
	 * @return
	 */
	private Data weightedRandSample(ArrayList<ArrayList<myTerm>> labels, ArrayList<Sentence> sentences,
			ArrayList<ArrayList<Double>> weight, int num) {
		Data re = new Data();
		double[] max_n = new double[num] ;
		Map<Double, Tuple> map = new HashMap<Double, Tuple>(); 
		for (int i = 0; i < num; i++) {
			max_n[i] = 0.0;
		}
		ArrayList<myTerm> re_label = new ArrayList<myTerm>();
		ArrayList<Sentence> re_sent = new ArrayList<Sentence>();
		ArrayList<Double> k = new ArrayList<Double>();
		
		for (int i = 0; i < sentences.size(); i++) {
			for (int j = 0; j < labels.get(i).size(); j++) {
				double u = Math.random();
				double kk = Math.pow(u, (double) 1/weight.get(i).get(j));
				k.add(kk);
				map.put(kk, new Tuple<Integer, Integer>(i,j));
			}
		}
		Double[] k_array = k.toArray(new Double[k.size()]);
		Arrays.sort(k_array, Collections.reverseOrder());
		for (int i = 0; i < num; i++) {
			int x = (Integer) map.get(k_array[i]).x;
			int y = (Integer) map.get(k_array[i]).y;
			if (re.getSents().contains(sentences.get(x))) {
				int idx_sent = re.getSents().indexOf(sentences.get(x));
				re.getLabel(idx_sent).add(labels.get(x).get(y));
			} else
				re.addData(labels.get(x).get(y), sentences.get(x));
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
