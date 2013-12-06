/**
 * 
 */
package Boosting;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
	private static final double P = utils.utils.BOOSTING_SAMPLING_POR;
	private Prolog prolog; // prolog engine
	ArrayList<Predicate> pred_list = null;
	
	public AdaBoost(Prolog p) {
		prolog = p;
	}
	
	public AdaBoostOutput train(Document doc) {

		AdaBoostOutput re = new AdaBoostOutput();
		pred_list = doc.getPredList();
		
		ArrayList<ArrayList<myTerm>> labels = doc.getLabels();
		ArrayList<Sentence> sentences = doc.getSentences();
		
		// assign each label term an weight
		int total_label = 0;
		for (ArrayList<myTerm> t_list : labels)
			for (myTerm t : t_list) {
				total_label++;
			}
		System.out.println(total_label);
		
		ArrayList<ArrayList<Double>> label_weights = new ArrayList<ArrayList<Double>>(labels.size());
		for (int i = 0; i < labels.size(); i++) {
			label_weights.add(new ArrayList<Double>(labels.get(i).size()));
			for (int j = 0; j < labels.get(i).size(); j++) {
				label_weights.get(i).add((double) 1/total_label);
			}
		}
		
		int turn = -1;
		double err = 1;
		// repeat training until meets the turn limit
		while (turn < T) {
			turn++;
			prolog = null;
			prolog = new Prolog();
			
			ArrayList<Formula> rules = new ArrayList<Formula>();
			
			
			// sample the labels to produce a path
			Data data = new Data();
			if (turn == 0) {
				data = weightedRandSample(labels, sentences, label_weights, (int) (P*total_label));
			} else {
				data = weightedRandSample(labels, sentences, label_weights, (int) (P*total_label));
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

//			for (int i = 0; i < all_paths.size(); i++) {
//				System.out.println(all_heads.get(i) + ":-" + all_paths.get(i));
//			}
//			System.out.println();
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
				
				
//				for (int k = 0; k < all_sub_terms.size(); k++) {
//					System.out.print(all_sub_terms.get(k).toPrologString() + ", ");
//				}
//				System.out.println();
				
				if (all_sub_paths.contains(all_sub_terms)) {
					int path_idx = all_sub_paths.indexOf(all_sub_terms);
					int path_idx_last = all_sub_paths.lastIndexOf(all_sub_terms);
					if (all_sub_paths.get(path_idx).get(0).isPositive() == all_sub_terms.get(0).isPositive()) {
						all_sub_paths_count.set(path_idx, all_sub_paths_count.get(path_idx) + 1);
						pat_path_map.get(all_sub_paths.get(path_idx)).add(i);
					}
					if (path_idx != path_idx_last 
							&& (all_sub_paths.get(path_idx_last).get(0).isPositive() == all_sub_terms.get(0).isPositive())) {
						all_sub_paths_count.set(path_idx_last, all_sub_paths_count.get(path_idx_last) + 1);
						pat_path_map.get(all_sub_paths.get(path_idx_last)).add(i);
					}
					if (path_idx == path_idx_last 
							&& (all_sub_paths.get(path_idx_last).get(0).isPositive() != all_sub_terms.get(0).isPositive())) {
						if (pat_path_map.get(all_sub_terms) == null)
							pat_path_map.put(all_sub_terms, new ArrayList<Integer>());
						pat_path_map.get(all_sub_terms).add(i);
						all_sub_paths.add(all_sub_terms);
						all_sub_paths_count.add(1);
					}
				} else {
					if (pat_path_map.get(all_sub_terms) == null)
						pat_path_map.put(all_sub_terms, new ArrayList<Integer>());
					pat_path_map.get(all_sub_terms).add(i);
					all_sub_paths.add(all_sub_terms);
					all_sub_paths_count.add(1);
				}
			}
			
			System.out.println("=============patterns================");
			for (int pt = 0; pt < all_sub_paths.size(); pt++) {
				if (pat_path_map.get(all_sub_paths.get(pt)).size() < 5)
					continue;
				for (int j = 0; j < all_sub_paths.get(pt).size(); j++) {
					System.out.print(all_sub_paths.get(pt).get(j).toPrologString() + ", ");
				}
				System.out.println();
			}
			System.out.println(String.format("*****TURN %d*****\n", turn));
			
			// find the most frequent pattern
//			int max_freq = -100, max_freq_idx = 0;			
//			for (int i = 0; i < all_sub_paths.size(); i++)
//				if (all_sub_paths_count.get(i) > max_freq) {
//					max_freq = all_sub_paths_count.get(i);
//					max_freq_idx = i;
//				}
			
			// use the head and path that represents the most frequent pattern to build tree
//			max_freq = ((int) Math.random() * 10) % all_sub_paths.size(); // randomly choose one
//			System.out.println(pat_path_map.get(all_sub_paths.get(max_freq_idx)));
//			myTerm head = all_heads.get(pat_path_map.get(all_sub_paths.get(max_freq_idx)).get(0));
//			LinkedList<myTerm> path = all_paths.get(pat_path_map.get(all_sub_paths.get(max_freq_idx)).get(0));
			// learn all patterns then add to rule list
			int cnt = 0;
			for (int pt = 0; pt < all_sub_paths.size(); pt++) {
				if (pat_path_map.get(all_sub_paths.get(pt)).size() < 5)
					continue;
				cnt++;
				LinkedList<myTerm> path = new LinkedList<myTerm>();
				Sentence path_sent = new Sentence(0,0);
				myTerm head = new myTerm();
				try {
					path = all_paths.get(pat_path_map.get(all_sub_paths.get(pt)).get(0));
					path_sent = data.getSent(pat_path_map.get(all_sub_paths.get(pt)).get(0));
					head = all_heads.get(pat_path_map.get(all_sub_paths.get(pt)).get(0));
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
				RuleTree rule = new RuleTree(prolog, doc.getPredList());
				System.out.println("tree :" + cnt);
				rule.buildTree(data, head, path, path_sent);
				for (Formula f : rule.getPrologRules())
					if (Math.abs(f.getWeight() - 0.5) > 0.05)
						rules.add(f);
			}
			
			System.out.println("=============Rules================");
			for (int i = 0; i < rules.size(); i++) {
				System.out.println(rules.get(i).toString());
			}
			System.out.println(String.format("*****TURN %d*****\n", turn));
			// use the rule to evaluate the whole document and reset the weight
			SentSat cur_tree_sent_sat = new SentSat();
			try {
				cur_tree_sent_sat = yapEvaluateRules(new Data(doc), rules);
			} catch (IOException e) {
				e.printStackTrace();
			} // current tree sentence satisfy samples
			
			err = 1 - cur_tree_sent_sat.getAccuracy(); // error of current tree, can be set weight
			if (err > 0.5) {
				double cov = cur_tree_sent_sat.getCov();
				System.out.println(err + "/" + cov);
				turn--;
				continue;
			}
			double cov = cur_tree_sent_sat.getCov();
			System.out.println(err + "/" + cov);
			
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
						new_weight = label_weights.get(sent_idx).get(ii) * Math.exp(1) * 2; // cost sensitive
						label_weights.get(sent_idx).set(ii, new_weight);
					}
				}
				
				// TODO deal with negative samples
				for (myTerm tmp_term : tmp_sat.getNegative()) {
					ArrayList<myTerm> tmp_labels = labels.get(sent_idx);
					int label_idx = 0;
					if (tmp_labels.contains(tmp_term)) {
						label_idx = tmp_labels.indexOf(tmp_term);
						// TODO assign weight
						double new_weight = 0.0;
						if (tmp_labels.get(label_idx).isPositive()) {
							new_weight = label_weights.get(sent_idx).get(label_idx) 
									* Math.exp(-(tmp_term.getWeight()*1)) * 2; // cost sensitive
						} else {
							new_weight = label_weights.get(sent_idx).get(label_idx) 
									* Math.exp(-(tmp_term.getWeight()*(-1)));
						}
//						new_weight = label_weights.get(sent_idx).get(label_idx) 
//								* Math.exp(-(tmp_term.getWeight() - 0.5)*2);
						label_weights.get(sent_idx).set(label_idx, new_weight);
					} else {
//						// add new negative sample
//						myTerm new_label = tmp_term.clone();
//						if (tmp_term.isPositive())
//							new_label.setNegative();
//						else
//							new_label.setPositive();
//						tmp_labels.add(new_label.clone());
//						label_idx = tmp_labels.size() - 1;
//						label_weights.get(sent_idx).add(0.0);
					}
					
					
					
				}
				
				// TODO deal with positive samples
				for (myTerm tmp_term : tmp_sat.getPositive()) {
					double new_weight = 0.0;
					if (!tmp_term.isPositive() || (tmp_term.getWeight() < 0))
						continue;
					else {
						ArrayList<myTerm> tmp_labels = labels.get(sent_idx);
						int label_idx = 0;
						if (tmp_labels.contains(tmp_term)) {
							label_idx = tmp_labels.indexOf(tmp_term);
							// TODO set weight
							if (tmp_labels.get(label_idx).isPositive()) {
								new_weight = label_weights.get(sent_idx).get(label_idx) 
										* Math.exp(-(tmp_term.getWeight()*1));
							} else {
								new_weight = label_weights.get(sent_idx).get(label_idx) 
										* Math.exp(-(tmp_term.getWeight()*(-1)));
							}
							label_weights.get(sent_idx).set(label_idx, new_weight);
						}
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
			double wweight = 0.5*Math.log((1-err)/(err));
			re.addWeakRules(rules, wweight);
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
		Map<Double, Tuple<Integer, Integer>> map = new HashMap<Double, Tuple<Integer, Integer>>(); 
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
//				continue;
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
	
	public SentSat yapEvaluateRules(Data data, ArrayList<Formula> rules) throws IOException {
		// evaluate given data by current rules
		SentSat re = new SentSat();
		String tmp_dir = System.getProperty("user.dir") + "/tmp/";
//		String tmp_input = tmp_dir + "tmp_input.pl";
		String tmp_rule = tmp_dir + "tmp_rules.pl";
		String tmp_sent = tmp_dir + "tmp_sent.pl";
		String tmp_query = tmp_dir + "tmp_query.pl";
		String tmp_ground_prog = tmp_dir + "tmp_ground_prog.pl";
		String tmp_evidence = tmp_dir + "tmp_evidence.pl";
		String tmp_ground_query = tmp_dir + "tmp_ground_query.pl";
		
		String yap_cmd = String.format("yap -L prolog/ground_hack.pl -- %s %s %s %s %s %s",
				tmp_rule, tmp_query, tmp_sent, tmp_ground_prog, tmp_evidence, tmp_ground_query);
		
//		String problog_cmd = "./home/daiwz/Projects/problog2/src/problog.py " + tmp_input;
		
		// write problog file
//		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tmp_input));
//		BufferedWriter finput = new BufferedWriter(osw);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tmp_rule));
		BufferedWriter frule = new BufferedWriter(osw);
		for (Formula f : rules) {
			frule.write(f.toString() + '\n');
		}
		frule.close();
				
		// for each sentence, call yap to compute
		ArrayList<ArrayList<myTerm>> merged_result = new ArrayList<ArrayList<myTerm>>();
		for (Sentence sent : data.getSents()) {
			osw = new OutputStreamWriter(new FileOutputStream(tmp_sent));
			BufferedWriter fsent = new BufferedWriter(osw);
			for (myTerm t : sent.getTerms())
				fsent.write(t.toString() + ".\n");
			for (myTerm t : sent.getFeatures())
				fsent.write(t.toString() + ".\n");
			fsent.close();
						
			try {
				Process ps = Runtime.getRuntime().exec(yap_cmd);
				System.out.print(loadStream(ps.getInputStream()));
				System.err.print(loadStream(ps.getErrorStream()));
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
			// read tmp_ground_prog
			File file = new File(tmp_ground_prog);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tempString, line;
			ArrayList<myTerm> sent_result = new ArrayList<myTerm>();
			while ((tempString = reader.readLine()) != null) {
				// print line number
				line = tempString.trim();			
				if (line.length() >= 0) {
					String[] weighted_term = line.split("::");
					myTerm term = new myTerm(weighted_term[1].replaceAll("\'", ""));
					term.setWeight((Double.parseDouble(weighted_term[0]) - 0.5) * 2);
					sent_result.add(term);
				}
			}
			
			merged_result.add(sent_result);
			reader.close();
		}
				
		// evaluate each rule in current tree one by one
//		ArrayList<SentSat> all_sat = eval.evalOneByOneSat(lp, data.getLabels(), data.getSents());
		
		// get the list of answer for each sentence by each rule
//		ArrayList<ArrayList<LinkedList<myTerm>>> result_one_by_one = eval.evalOneByOne(lp, data.getSents());
		
		// merge all the results by averaging the probability
//		ArrayList<ArrayList<myTerm>> merged_result = mergeProbResults(result_one_by_one);

		// Calculate accuracy from merged_result
		for (int k = 0; k < merged_result.size(); k++) {
			SatisfySamples tmp_sat = new SatisfySamples();
			tmp_sat.setSatisifySamplesProb(data.getLabel(k), new LinkedList<myTerm>(merged_result.get(k)));
			re.addSentSat(data.getLabel(k), data.getSent(k), tmp_sat);
		}
		re.setTotal();
		
		return re;
	}	
	
	public SentSat evaluateRules(Data data, ArrayList<Formula> rules) {
		// TODO evaluate given data by current rules
		SentSat re = new SentSat();
		LogicProgram lp = new LogicProgram();
		lp.addRules(rules);
		// predicates
		
		Eval eval = new Eval(prolog, pred_list);
		eval.setRules(lp);
		
		// evaluate each rule in current tree one by one
//		ArrayList<SentSat> all_sat = eval.evalOneByOneSat(lp, data.getLabels(), data.getSents());
		
		// get the list of answer for each sentence by each rule
		ArrayList<ArrayList<LinkedList<myTerm>>> result_one_by_one = eval.evalOneByOne(lp, data.getSents());
		
		// TODO merge all the results by averaging the probability
		ArrayList<ArrayList<myTerm>> merged_result = mergeProbResults(result_one_by_one);

		// TODO Calculate accuracy from merged_result
		for (int k = 0; k < merged_result.size(); k++) {
			SatisfySamples tmp_sat = new SatisfySamples();
			tmp_sat.setSatisifySamplesProb(data.getLabel(k), new LinkedList<myTerm>(merged_result.get(k)));
			re.addSentSat(data.getLabel(k), data.getSent(k), tmp_sat);
		}
		re.setTotal();
		
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return re;
	}
	
	private ArrayList<ArrayList<myTerm>> mergeProbResults(
			ArrayList<ArrayList<LinkedList<myTerm>>> results) {
		int rule_num = results.size();
		int sent_num = results.get(0).size();
		
		ArrayList<ArrayList<myTerm>> re = new ArrayList<ArrayList<myTerm>>(sent_num);
		
		for (int j = 0; j < sent_num; j++) {
			ArrayList<myTerm> tmp_ans_list = new ArrayList<myTerm>();
			ArrayList<Integer> tmp_ans_list_cnt = new ArrayList<Integer>();
			
			for (int i = 0; i < rule_num; i++) {
				LinkedList<myTerm> tmp_result_list = results.get(i).get(j);
				for (myTerm t : tmp_result_list) {
					if (tmp_ans_list.contains(t)) {
						int idx = tmp_ans_list.indexOf(t);
						
						myTerm the_t = tmp_ans_list.get(idx); // the term already in ans_list
						
						if (t.isPositive() == the_t.isPositive())
							the_t.setWeight(the_t.getWeight() + t.getWeight());
						else {
							the_t.setWeight(the_t.getWeight() + (-t.getWeight()));
						}
						
						tmp_ans_list_cnt.set(idx, tmp_ans_list_cnt.get(idx) + 1); // record the counts
					} else {
						tmp_ans_list.add(t);
						tmp_ans_list_cnt.add(1);
					}
				}
			}
			
			for (int k = 0; k < tmp_ans_list.size(); k++) {
				tmp_ans_list.get(k).setWeight((double) tmp_ans_list.get(k).getWeight()/tmp_ans_list_cnt.get(k));
			}
			
			re.add(tmp_ans_list);
		}
		return re;
	}
	
	static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		in = new BufferedInputStream(in);
		StringBuffer buffer = new StringBuffer();
		while( (ptr = in.read()) != -1 ) {
			buffer.append((char)ptr);
		}
		return buffer.toString();
	}
}
