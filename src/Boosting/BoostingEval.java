/**
 * 
 */
package Boosting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import ILP.*;
import Logic.*;


/**
 * @author daiwz
 *
 */
public class BoostingEval {

	/**
	 * 
	 */
	Prolog prolog;
	Eval eval;
	Predicate[] pred_list;
	
	public BoostingEval(Prolog p) {
		prolog = p;
		eval = new Eval(prolog);
	}

	public void addPredicates(Predicate[] p) {
		pred_list = p;
		eval = new Eval(prolog, pred_list);
	}

	
	public void evalAndPrint(ArrayList<ArrayList<Formula>> rules, ArrayList<Double> weights, Sentence sent) {
		
		ArrayList<myTerm> out = evalSent(rules, weights, sent);
		for (myTerm t : out) {
			if ((t.getWeight() > 0 ) && (t.isPositive()))
				System.out.print(t.toString() + ", ");
		}
		System.out.println();
	}
	
	public void evalAndPrintAll(ArrayList<ArrayList<Formula>> rules, ArrayList<Double> weights, ArrayList<Sentence> sents) {
		for (Sentence sent : sents) {
			ArrayList<myTerm> out = evalSent(rules, weights, sent);
			for (myTerm t : out) {
				if ((t.getWeight() > 0 ) && (t.isPositive()))
					System.out.print(t.toString() + ", ");
			}
			System.out.println();
		}
	}
	
	public void evalAndPrintAll(ArrayList<ArrayList<Formula>> rules, ArrayList<Double> weights, Sentence[] sents) {
		for (Sentence sent : sents) {
			ArrayList<myTerm> out = evalSent(rules, weights, sent);
			for (myTerm t : out) {
				if ((t.getWeight() > 0 ) && (t.isPositive()))
					System.out.print(t.toString() + ", ");
			}
			System.out.println();
		}
	}
	
	public ArrayList<myTerm> evalSent(ArrayList<ArrayList<Formula>> rules, ArrayList<Double> weights, Sentence sent) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<ArrayList<myTerm>> tmp_results = new ArrayList<ArrayList<myTerm>>();
		for (int i = 0; i < rules.size(); i++) {
			LogicProgram lp = new LogicProgram(rules.get(i));
			eval.setRules(lp);
			ArrayList<LinkedList<myTerm>> result_one_by_one = eval.evalOneByOneSent(lp, sent);
			ArrayList<myTerm> merged_result = mergeProbResultsSent(result_one_by_one);
			tmp_results.add(merged_result);
		}
		
		re = mergeProbRuleResults(tmp_results, weights);
		return re;
	}
	
	private ArrayList<myTerm> mergeProbRuleResults(ArrayList<ArrayList<myTerm>> results, ArrayList<Double> weights) {
		int rule_num = results.size();
		
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<Integer> re_cnt = new ArrayList<Integer>();
		
		for (int i = 0; i < rule_num; i++) {
			Collection<myTerm> tmp_result_list = results.get(i);
			double wt = weights.get(i);
			for (myTerm t : tmp_result_list) {
				if (re.contains(t)) {
					int idx = re.indexOf(t);
					myTerm the_t = re.get(idx); // the term already in ans_list
					if (t.isPositive() == the_t.isPositive())
						the_t.setWeight(the_t.getWeight() + t.getWeight()*wt);
					else {
						the_t.setWeight(the_t.getWeight() + (-t.getWeight())*wt);
					}
					re_cnt.set(idx, re_cnt.get(idx) + 1); // record the counts
				} else {
					myTerm new_t = t.clone();
					new_t.setWeight(t.getWeight()*wt);
					re.add(new_t);
					re_cnt.add(1);
				}
			}
		}
		return re;
	}
	
	private ArrayList<myTerm> mergeProbResultsSent(ArrayList<LinkedList<myTerm>> results) {
		int rule_num = results.size();
		
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<Integer> re_cnt = new ArrayList<Integer>();
		
		for (int i = 0; i < rule_num; i++) {
			Collection<myTerm> tmp_result_list = results.get(i);
			for (myTerm t : tmp_result_list) {
				if (re.contains(t)) {
					int idx = re.indexOf(t);
					myTerm the_t = re.get(idx); // the term already in ans_list
					if (t.isPositive() == the_t.isPositive())
						the_t.setWeight(the_t.getWeight() + t.getWeight());
					else {
						the_t.setWeight(the_t.getWeight() + (-t.getWeight()));
					}
					re_cnt.set(idx, re_cnt.get(idx) + 1); // record the counts
					} else {
						re.add(t);
						re_cnt.add(1);
					}
				}
			}
			
		for (int k = 0; k < re.size(); k++) {
			re.get(k).setWeight((double) re.get(k).getWeight()/re_cnt.get(k));
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
							the_t.setWeight(the_t.getWeight() + (1 - t.getWeight()));
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
	
}
