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
import java.util.concurrent.CountDownLatch;

import utils.Tuple;
import ILP.*;
import Logic.*;
import Tree.*;

/**
 * @author Wang-Zhou
 * @modifiyer 田植良
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
	
	public AdaBoostOutput train(Document doc) throws Exception {

		AdaBoostOutput re = new AdaBoostOutput();
		pred_list = doc.getPredList();
		
		ArrayList<ArrayList<myTerm>> labels = doc.getLabels();//标签 每个句子 每个标签
		ArrayList<Sentence> sentences = doc.getSentences();
		
		// assign each label term an weight计算全部的个数 准备求初始权重
		int total_label = 0;
		for (ArrayList<myTerm> t_list : labels)
			for (myTerm t : t_list) {
				total_label++;
			}
		System.out.println(total_label);
		
		ArrayList<ArrayList<Double>> label_weights = new ArrayList<ArrayList<Double>>(labels.size());
		//权重 一直在用的 外层是标签 内层是每个标签的Term  为啥是Term？？？ 因为Term个数代表样本个数 按照样本个数分配初始权重
		for (int i = 0; i < labels.size(); i++) {
			label_weights.add(new ArrayList<Double>(labels.get(i).size()));
			for (int j = 0; j < labels.get(i).size(); j++) {
				label_weights.get(i).add((double) 1/total_label);
			}
		}
		
		int turn = -1;
		double err = 1;//好像没啥用
		// repeat training until meets the turn limit
		while (turn < T) {
			turn++;
			prolog = null;//清空一下
			prolog = new Prolog();
			
			ArrayList<Formula> rules = new ArrayList<Formula>();//Formula就是预测出的表达式 解码的时候用的
			
			
			// sample the labels to produce a path
			Data data = new Data();
			if (turn == 0) {//if else没啥用 直接调函数吧 这个函数是根据权重随机抽样的 抽出来的本轮要训的数据返回到data中
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
			
			// paths from current data 貌似是从data中找出的所有标签下的所有路径
			ArrayList<LinkedList<myTerm>> all_paths = new ArrayList<LinkedList<myTerm>>();//外层是所有标签 内层是标签对于的path
			ArrayList<Integer> all_paths_sent = new ArrayList<Integer>(); // sentence num of path？？？？
			ArrayList<myTerm> all_heads = new ArrayList<myTerm>();//标签    给本轮用
			for (int i = 0; i < data.getSents().size(); i++)
				for (int j = 0; j < data.getLabel(i).size(); j++) {//对每一句话中 每一个标签进行遍历 加入标签 加入path 再加入序号（相当于数组下标）
					all_heads.add(data.getLabel(i).get(j));
					all_paths.addAll(findPath(data.getLabel(i).get(j), data.getSent(i)));
					all_paths_sent.add(i);
				}

//			for (int i = 0; i < all_paths.size(); i++) {
//				System.out.println(all_heads.get(i) + ":-" + all_paths.get(i));
//			}
//			System.out.println();
			// substitute all the paths and store the pattern with its frequency
			//把标签放到path的开头 这样就形成了个类似  标签-path的pair
			ArrayList<ArrayList<myTerm>> all_sub_paths = new ArrayList<ArrayList<myTerm>>();
			ArrayList<Integer> all_sub_paths_count = new ArrayList<Integer>();
			Map<ArrayList<myTerm>, ArrayList<Integer>> pat_path_map = new HashMap<ArrayList<myTerm>, ArrayList<Integer>>();
			for (int i = 0; i < all_paths.size(); i++) {
				LinkedList<myTerm> path = all_paths.get(i);
				myTerm head = all_heads.get(i);
				ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
				all_terms.add(head);
				all_terms.addAll(path);
				Substitute subs = new Substitute(all_terms);//做Substitute操作？？？
				ArrayList<myTerm> all_sub_terms = subs.getSubTerms();
				
				
//				for (int k = 0; k < all_sub_terms.size(); k++) {
//					System.out.print(all_sub_terms.get(k).toPrologString() + ", ");
//				}
//				System.out.println();
				
			//统计每个标签每个路径的频次	substitute all the paths and store the pattern with its frequency
				if (all_sub_paths.contains(all_sub_terms)){//总路径中有all_sub_terms吗 有的话就用他的位置path_idx做  没有的话就是else 就插入
					int path_idx = all_sub_paths.indexOf(all_sub_terms);//这个到底是啥？？？
					int path_idx_last = all_sub_paths.lastIndexOf(all_sub_terms);
					if (all_sub_paths.get(path_idx).get(0).isPositive() == all_sub_terms.get(0).isPositive()){
						//总路径中的这个路径的正负性是否和这个路径相同   是的话count++ 存入map
						all_sub_paths_count.set(path_idx, all_sub_paths_count.get(path_idx) + 1);
						pat_path_map.get(all_sub_paths.get(path_idx)).add(i);
					}
					if (path_idx != path_idx_last 
							&& (all_sub_paths.get(path_idx_last).get(0).isPositive() == all_sub_terms.get(0).isPositive())) {
						//不是最后一个节点 但和最后一个节点正负性相同 最后一个节点做是的话count++ 存入map   
						all_sub_paths_count.set(path_idx_last, all_sub_paths_count.get(path_idx_last) + 1);
						pat_path_map.get(all_sub_paths.get(path_idx_last)).add(i);
					}
					if (path_idx == path_idx_last 
							&& (all_sub_paths.get(path_idx_last).get(0).isPositive() != all_sub_terms.get(0).isPositive())) {
						//是最后节点但正负性不同
						if (pat_path_map.get(all_sub_terms) == null)
							pat_path_map.put(all_sub_terms, new ArrayList<Integer>());
						pat_path_map.get(all_sub_terms).add(i);
						all_sub_paths.add(all_sub_terms);//插入
						all_sub_paths_count.add(1);
					}
				} else {//count++ 存入map 并插入
					if (pat_path_map.get(all_sub_terms) == null)
						pat_path_map.put(all_sub_terms, new ArrayList<Integer>());
					pat_path_map.get(all_sub_terms).add(i);
					all_sub_paths.add(all_sub_terms);
					all_sub_paths_count.add(1);
				}
			}
			
			//貌似打印all_sub_paths的PrologString形式  频次小于100的排除 为啥频次是size？？
			System.out.println("=============patterns================");
			for (int pt = 0; pt < all_sub_paths.size(); pt++) {
				if (pat_path_map.get(all_sub_paths.get(pt)).size() < 10)
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
			for (int pt = 0; pt < all_sub_paths.size(); pt++) {//遍历所有路径
				if (pat_path_map.get(all_sub_paths.get(pt)).size() < 10)//个数小于100就扔了
					continue;
				cnt++;
				LinkedList<myTerm> path = new LinkedList<myTerm>();
				Sentence path_sent = new Sentence(0,0);
				myTerm head = new myTerm();
				try {
					path = all_paths.get(pat_path_map.get(all_sub_paths.get(pt)).get(0));
					//当前遍历到的路径的 int list的第0个  位置的all_paths 这是个路径
					int aa = all_paths_sent.get(pat_path_map.get(all_sub_paths.get(pt)).get(0));//句子的标号？？？
					path_sent = data.getSent(aa);//得到对应的句子
					head = all_heads.get(pat_path_map.get(all_sub_paths.get(pt)).get(0));//当前遍历到的路径的 int list的第0个  位置的all_heads
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
				PathRuleTree rule = new PathRuleTree(prolog, doc.getPredList());// doc.getPredList是输入 prolog是处女
				System.out.println("tree :" + cnt);
				rule.buildTree(data, head, path, path_sent);//直接调用buildTree 重要的部分 咔咔一顿干
				for (Formula f : rule.getPrologRules())
					if (Math.abs(f.getWeight() - 0.5) > 0.05)
						//和0.5差的小了 认为分的不开 就不加入结果规则库了 是个意思吗 那这也是重要策略！！！？？
						rules.add(f);
			}
			
			System.out.println("=============Rules================");
			for (int i = 0; i < rules.size(); i++) {
				System.out.println(rules.get(i).toString());
			}
			System.out.println(String.format("*****TURN %d*****\n", turn));
			// 评价并重新赋权重use the rule to evaluate the whole document and reset the weight
			SentSat cur_tree_sent_sat = new SentSat();
			try {
//				Data eval_data = RandSample(doc, 2000);
				long begintime = System.currentTimeMillis();
				cur_tree_sent_sat = yapThreadsEvaluateRules(new Data(doc), rules, 10);//用这个预测，让我得到权重
				long endtime=System.currentTimeMillis();
				System.out.println("thread:" + (endtime - begintime));
				
//				begintime = System.currentTimeMillis();
//				cur_tree_sent_sat = yapEvaluateRules(new Data(doc), rules);
//				endtime=System.currentTimeMillis();
//				System.out.println("no thread:" + (endtime - begintime));

			} catch (IOException e) {
				e.printStackTrace();
			} // current tree sentence satisfy samples
			
			err = 1 - cur_tree_sent_sat.getAccuracy(); // 错误率 没啥error of current tree
			if (err > 0.5) {
				double cov = cur_tree_sent_sat.getCov();//打印 没啥
				System.out.println(err + "/" + cov);
				turn--;//减少轮次？？？为啥>0.5就--
				continue;
			}
			double cov = cur_tree_sent_sat.getCov();//打印 没啥
			System.out.println(err + "/" + cov);
			
			// TODO set new (negative) labels and reweight
			for (int k = 0; k < cur_tree_sent_sat.getAllSats().size(); k++) {//遍历？？？？
				//Sentence tmp_sent = cur_tree_sent_sat.getAllSent(k);
				SatisfySamples tmp_sat = cur_tree_sent_sat.getAllSats(k);
				
				int sent_idx = k;
				
				// 未覆盖到集合里的样本 deal with uncovered samples
				for (int ii = 0; ii < labels.get(sent_idx).size(); ii++) {
					if (!tmp_sat.getNegative().contains(labels.get(sent_idx).get(ii))//不在 Negative也不在Positive 就认为是未覆盖到
							&& !tmp_sat.getPositive().contains(labels.get(sent_idx).get(ii))) {
						// assign weight
						double new_weight = 0.0;
						new_weight = label_weights.get(sent_idx).get(ii) * Math.exp(1*2) * 2; // cost sensitive
						label_weights.get(sent_idx).set(ii, new_weight);
					}
				}
				
				// deal with negative samples
				for (myTerm tmp_term : tmp_sat.getNegative()) {//遍历Negative中的
					ArrayList<myTerm> tmp_labels = labels.get(sent_idx);
					int label_idx = 0;
					if (tmp_labels.contains(tmp_term)) {//相当于废屁
						label_idx = tmp_labels.indexOf(tmp_term);
						// assign weight
						double new_weight = 0.0;
						if (tmp_labels.get(label_idx).isPositive()) {//负例中对应的label是正的还是负的 也就是说是否一致
							new_weight = label_weights.get(sent_idx).get(label_idx) 
									* Math.exp(-(tmp_term.getWeight()*1*2)) * 2; //好好研究下这种cost函数 cost sensitive
						} else {
							new_weight = label_weights.get(sent_idx).get(label_idx)
									* Math.exp(-(tmp_term.getWeight()*(-1*2)) * 2);
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
				
				// deal with positive samples
				for (myTerm tmp_term : tmp_sat.getPositive()) {
					double new_weight = 0.0;
					if (!tmp_term.isPositive() || (tmp_term.getWeight() < 0))
						continue;
					else {
						ArrayList<myTerm> tmp_labels = labels.get(sent_idx);
						int label_idx = 0;
						if (tmp_labels.contains(tmp_term)) {
							label_idx = tmp_labels.indexOf(tmp_term);
							// set weight
							if (tmp_labels.get(label_idx).isPositive()) {//正负性一致
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
			
			
			
			// normalize weights对权重归一化
			double sum = 0;//计算权重总和（简单加和）
			for (ArrayList<Double> d_list : label_weights) {
				for (Double d : d_list) {
					sum = sum + d;
				}
			}
			
			for (int ii = 0; ii < label_weights.size(); ii++) {//归一化方式：除以总和 
				for (int jj = 0; jj < label_weights.get(ii).size(); jj++) {
					label_weights.get(ii).set(jj, label_weights.get(ii).get(jj)/sum);
				}
			}
			double wweight = 0.5*Math.log((1-err)/(err));
			re.addWeakRules(rules, wweight);//加入adaboostoutput的序列 这个是将要完事了？
		}
		return re;
	}
	
	/**
	 * randomly sample some labeled data from database
	 * @param doc: document for evaluation
	 * @param num: number of sampling data
	 * @return: sampled data
	 */
	private Data RandSample(Document doc, int num) {
		Data re = new Data();
		int total = doc.getSentences().size();
		
		ArrayList<Double> weight= new ArrayList<Double>(total);
		for (int i = 0; i < total; i++) {
			weight.add(1/((double) total));
		}
		
		Map<Double, ArrayList<Integer>> map = new HashMap<Double, ArrayList<Integer>>();
		ArrayList<myTerm> re_label = new ArrayList<myTerm>();
		ArrayList<Sentence> re_sent = new ArrayList<Sentence>();
		ArrayList<Double> k = new ArrayList<Double>();
		
		for (int i = 0; i < total; i++) {
			double u = Math.random();
			double kk = Math.pow(u, (double) 1/weight.get(i));
			k.add(kk);
			if (map.get(kk) == null) {
				map.put(kk, new ArrayList<Integer>());
				map.get(kk).add(i);
			}
			else {
				map.get(kk).add(i);
			}
		}
		
		Double[] k_array = k.toArray(new Double[k.size()]);
		Arrays.sort(k_array, Collections.reverseOrder());
		
		int i = 0, t = 0;
		OK:
		while(i < k_array.length) {
			ArrayList<Integer> idxs = map.get(k_array[i]);
			i++;
			for (int idx : idxs) {
				if (t > num)
					break OK;
				if (re.getSents().contains(doc.getSent(idx))) {
					continue;
				} else {
					re.addData(doc.getLabel(idx), doc.getSent(idx));
					t++;
				}
			}
		}
		
		return re;
	}

	/**
	 * A-ES sampling算法的方式 根据权重对样本随机抽样  输入num应该是设定取前多少个
	 * @param labels
	 * @param sentences
	 * @param weight
	 * @return
	 */
	private Data weightedRandSample(ArrayList<ArrayList<myTerm>> labels, ArrayList<Sentence> sentences,
			ArrayList<ArrayList<Double>> weight, int num) {
		Data re = new Data();
		
		Map<Double, ArrayList<Tuple<Integer, Integer>>> map = new HashMap<Double, ArrayList<Tuple<Integer, Integer>>>();//建立map结构方便排序
		
		ArrayList<Double> k = new ArrayList<Double>();
		
		for (int i = 0; i < sentences.size(); i++) {
			for (int j = 0; j < labels.get(i).size(); j++) {
				double u = Math.random();
				double kk = Math.pow(u, (double) 1/weight.get(i).get(j));//就这行 有用
				k.add(kk);
				if (map.get(kk) == null) {
					map.put(kk, new ArrayList<Tuple<Integer, Integer>>());
					map.get(kk).add(new Tuple<Integer, Integer>(i,j));
				} else {
					map.get(kk).add(new Tuple<Integer, Integer>(i,j));
				}
			}
		}
		Double[] k_array = k.toArray(new Double[k.size()]);
		Arrays.sort(k_array, Collections.reverseOrder());
		int i = 0, t = 0;
		OK:
		while(i < k_array.length) {
			ArrayList<Tuple<Integer,Integer>> tuples= map.get(k_array[i]);
			i++;

			for (Tuple <Integer, Integer> tup : tuples){
				t++;
				if (t > num)//大于次数返回了
					break OK;
				int x = (Integer) tup.x;
				if (re.getSents().contains(sentences.get(x))) {//已经有了就不加了
//					int idx_sent = re.getSents().indexOf(sentences.get(x));
//					re.getLabel(idx_sent).add(labels.get(x).get(y));
					continue;
				} else {
					re.addData(labels.get(x), sentences.get(x));//还没有  加进去
				}
			}
		}
		
		return re;
	}
	
	/**
	 * 在图中找路径，别的地方实现过的
	 * @param label
	 * @param sent
	 * @return
	 */
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
	/**
	 * 在重新赋权值过程中，需要用到的预测，在这里用yap做了  这个要用新的替换掉  和望洲data里给我的python脚本做的事情一样
	 * @param data
	 * @param rules
	 * @param thread_num
	 * @return
	 * @throws Exception
	 */
	public SentSat yapThreadsEvaluateRules(Data data, ArrayList<Formula> rules, int thread_num) throws Exception {
		SentSat re = new SentSat();
		YapEvalThread yap_threads = new YapEvalThread(data, rules, thread_num);
		int sent_num = data.getSents().size();
		int cur_num = 0;
		while(cur_num < sent_num) {
			for (int i = 0; i < thread_num; i++) {
				cur_num = cur_num + 1;
				if (cur_num > sent_num - 1)
					break;
				String num_str = String.valueOf(i);
				new Thread(yap_threads, num_str).start();
			}
			
			yap_threads.setNumber(yap_threads.getNumber() + thread_num);
		}
		
		ArrayList<ArrayList<myTerm>> merged_result = yap_threads.getMergedResult();
		for (int k = 0; k < data.getSents().size(); k++) {
			SatisfySamples tmp_sat = new SatisfySamples();
			tmp_sat.setSatisifySamplesProb(data.getLabel(k), new LinkedList<myTerm>(merged_result.get(k)));
			re.addSentSat(data.getLabel(k), data.getSent(k), tmp_sat);
		}
		re.setTotal();
		
		return re;
	}
	/**
	 * 貌似不用看
	 * @param data
	 * @param rules
	 * @return
	 * @throws Exception
	 */
	public SentSat yapEvaluateRules(Data data, ArrayList<Formula> rules) throws Exception {
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
				ps.waitFor();
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
	
	/**
	 * 貌似不用看
	 * @param data
	 * @param rules
	 * @return
	 */
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
	
	/**
	 * 貌似不用看
	 * @param results
	 * @return
	 */
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
