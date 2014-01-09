/**
 * 
 */
package Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.Math;

import jpl.Query;
import utils.*;
import ILP.*;
import Logic.*;

/**
 * @author Wang-Zhou
 *
 */
public class PathRuleTree {

	/**
	 * translate sentence and formula into decision tree and vice versa.
	 * 
	 * in logic domain, adding one term may not affect the classification
	 * distribution, so if any term cannot affect the distribution, just add
	 * the default term (e.g., contains certain variable or just in turn).
	 * 
	 * [optimization] if the variable was not introduced in before terms, its feature cannot
	 * be added
	 * 
	 * [optimization] must contain variable in head, so first add these two terms
	 */
	
	TreeNode root; // tree root - the first splitting node;第一个分类节点 也就是root 由于prolog限制 这个只能是正 所以不会分裂
	myTerm head; // logical term - head:-body., must have variable就是当前树的head 头
	LinkedList<Formula> rules = new LinkedList<Formula>();//规则集合？？在代码中看看
	Prolog prolog;//注意
	ArrayList<Predicate> pred_list = new ArrayList<Predicate>();//谓词的集合？
	
	
	private int maxHeight;
	/**
	 * 用输入初始化
	 * @param p
	 * @param preds
	 */
	public PathRuleTree(Prolog p, ArrayList<Predicate> preds) {
		root = null;
		head = null;
		prolog = p;
		pred_list = preds;
	}
		
	public myTerm getHead() {
		return head;
	}
	
	public void setHead(myTerm h) {
		head = h;
	}
	
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode r) {
		root = r;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public void setMaxHeight(int h) {
		maxHeight = h;
	}
	/**
	 * 返回当前的rules
	 * @return
	 */
	public LinkedList<Formula> getPrologRules() {
		return rules;
	}
	/**
	 * build a tree from one path 为啥说from one path？？ 感觉就是做了个Substitute操作和 正负性的赋值
	 * @param doc: training instances in document
	 * @param path: path of available terms for split
	 */
	public void buildTree(Data data, myTerm head, LinkedList<myTerm> path, Sentence sent) {
		ArrayList<myTerm> cand = new ArrayList<myTerm>(); // candidate terms
		// substitution and get more features (temporarily only use words themselves)
		ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
		all_terms.add(head);
		all_terms.addAll(path);//声明并把head path加入
		Substitute subs = new Substitute(all_terms);//以前就不明白 艹   下面貌似都是对于这个做
		ArrayList<myTerm> all_sub_terms = subs.getSubTerms();//这是Substitute处理后的Term list
		ArrayList<myWord> word_list = subs.getWordList();
		ArrayList<myWord> var_list = subs.getVarList();
		// set head term 拷贝 并赋正负 并删之前all_sub_terms存的head
		this.setHead(all_sub_terms.get(0).clone());
		if (head.isPositive())
			this.head.setPositive();
		else
			this.head.setNegative();
		// debug
		all_sub_terms.remove(0);
		ArrayList<myTerm> subed_path = new ArrayList<myTerm>();
		// get subed path
		for (myTerm t : all_sub_terms) {//subed_path就相当于输入的path 用Substitute变换之后得到的
			subed_path.add(t.clone());
		}
		// add path as candidate terms, then build more feature as candidate terms
		ArrayList<myTerm> feature = buildFeature(var_list, word_list, sent.getFeatures());//提取特征
		root = createRoot(data, subed_path, feature);
	}

	
	private TreeNode createRoot(Data data, ArrayList<myTerm> subed_path,
			ArrayList<myTerm> feature) {
		
		TreeNode node = new TreeNode();
		
		// use current subed path as head
		node.addTermNodes(subed_path);
		
		// build path_data
		Prolog path_prolog = new Prolog();
		
		PathSat path_sat = new PathSat();//注意！！
		
		for (int i = 0; i < data.getSents().size(); i++) {
			Sentence sent = data.getSent(i);
			if (sent.getTerms().length < 1)
				continue;
			ArrayList<PathData> path_datas = computePaths(sent, node.getTermNodes(), this.getHead(), path_prolog);
			//一个句子转为路径集合 可能是所有的决策路径 是所有经过node（根节点的路径吗）
			//node.getTermNodes()中getTermNodes() 是对于Treenode中的termNodes中的节点   isPositiveBranch为负的话 反转正负 为正不用 存下来并返回Term的list
			//node.getTermNodes()到底是啥 不懂啊！！！！？？？
			for (PathData pd : path_datas) {//遍历path   
				pd.setSentNum(i);//path中句子的编号
				if (data.getLabel(i).contains(pd.getLabel())) {//貌似是当前path的预测值?标签 是否是句子的标签
					pd.setClass(true);
				}
				else {
					pd.setClass(false);
				}
				path_sat.addPath(pd);//最终存放path的结构   ：挑出了每个特征对应的路径
			}
		}

		// add negative sample paths into candidate这就是望洲说的 负样本也要加进来   注意 （但是为何要加那一段还要好好理解）
		feature = addTermFromNegSamps(path_sat.getNegative(), feature);
		/*
		 *  FINISHING ROOT NODE BUILDING
		 */
		node.setHierarchy(1); // root is first layer所谓的根节点 其实是第一层
		node.setBranchPositive();//根节点的标签为正
		ArrayList<myTerm> used = new ArrayList<myTerm>();
		// remove all used candidate terms把用过的负样本存到used 没用过的存在原来的feature中
		ArrayList<myTerm> tt = new ArrayList<myTerm>();
		for (myTerm tmp_term : feature) {
			if (node.getTermNodes().contains(tmp_term)) {
				used.add(tmp_term.clone());
			} else {
				tt.add(tmp_term.clone());
			}
		}
		
		feature = tt;
		// create childrens
		// FIRST TERM IN PROLOG RULE MUST BE TRUE!!!

		node.setTrueChild(create(path_sat, feature, used, node, true));//存在TreeNode的trueChild中  create过程在下面
		node.setFalseChild(null);//存在TreeNode的falseChild中 为何没有create操作 
		return node;
	}

	private ArrayList<PathData> computePaths(Sentence sent,
			ArrayList<myTerm> subed_path, myTerm head, Prolog path_prolog) {
		// TODO Auto-generated method stub
		ArrayList<PathData> re = new ArrayList<PathData>();
		for (myTerm t : sent.getTerms()) {//句子中的所有Term
			path_prolog.assertz(t.toString());//认为这个term是对的？
		}
		String query_str = "";
		ArrayList<myWord> vars = new ArrayList<myWord>();
		for (myTerm t : subed_path) {
			query_str = query_str + t.toPrologString() + ",";//对于当前的Term 生成prolog语言的形式 add到输出序列
			for (myWord w : t.getArgs()) {//每个Term中的每个word 都存入vars 注意是uniq的存
				if (!vars.contains(w))
					vars.add(w);
			}
		}
		query_str = query_str.substring(0, query_str.length() - 1) + ".";
		Query q = new Query(query_str);
		while (q.hasMoreSolutions()) {
			@SuppressWarnings("rawtypes")
			java.util.Hashtable ans = q.nextSolution();
			ArrayList<myWord> ans_words = new ArrayList<myWord>(vars.size());
			for (myWord v : vars) {//把包含var中在该方法中有出现的prolog语句放入ans_words
				ans_words.add(new myWord(ans.get(v.toPrologString()).toString()));
			}
			re.add(new PathData(vars, ans_words, sent, this.head));//对应的变量add 返回时候返回这个 重要！
		}
		// retract all terms prolog的一个语句 消除刚才声明的变量
		for (myTerm t : sent.getTerms()) {
			path_prolog.retract(t.toString());
		}
		return re;
	}

	/**
	 * main procedure for creating a tree node建节点（非root节点）的主要过程
	 * PathSat father_sat可能是当前所有的path的数据，
	 * @data is the data for training a tree
	 * @candidateTerms are the candidate terms for a tree 候选term
	 */
	
	
	
	public TreeNode create(PathSat father_sat, ArrayList<myTerm> features, ArrayList<myTerm> usedTerms, TreeNode father, boolean branch) {
		/*
		 * START 
		 * 
		 */
		TreeNode node = new TreeNode();
		node.setFather(father);//根据输入设father
		
		if (father != null) {
			node.setHierarchy(father.getHierarchy() + 1); // set hierarchy of 1
			father.setIsLeaf(false);
		}
		
		if (branch)//根据输入设branch
			node.setBranchPositive();
		else
			node.setBranchNegative();

		/*
		 * NOT ROOT NODE根节点不在这里面做了
		 */
		// if father has enough layer or accuracy, return
		if ((node.getHierarchy() > utils.MAX_HIERARCHY_NUM)) {//到最大层数了
			node.setIsLeaf(true);
			Formula form = toFormula(father, branch);//形成公式
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
			form.setWeight(father_sat.getAccuracy());	
			rules.add(form);//把公式加入rules
//				System.out.println(data.size() + "/" + form.toString());
			return node;
		} else if(father_sat.getAccuracy() >= utils.MAX_ACC_CRI) {//准确率>=1才行(见参数设置)
			// father's accuracy is enough for a positivesample
			node.setIsLeaf(true);//是也在
			Formula form = toFormula(father, branch);//形成公式
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
			form.setWeight(father_sat.getAccuracy());
			rules.add(form);
//				System.out.println(data.size() + "/"  + form.toString());
			return node;
		} else if(father_sat.getAccuracy() <= utils.MAX_INACC_CRI) {//准确率<=0 也返回 不干了！
			// father's accuracy is enough for a negative sample
			node.setIsLeaf(true);
			Formula form = toFormula(father, branch);
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
			form.setWeight(father_sat.getAccuracy());
			rules.add(form);
//				System.out.println(data.size() + "/" + form.toString());
			return node;
		} else if (features.isEmpty()) {//没特征了
			// no candidates
			node.setIsLeaf(true);
			Formula form = toFormula(father, branch);
//				if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//					myTerm h = form.getHead().get(0).clone();
//					h.setPositive();
//					form.getHead().clear();
//					form.pushHead(h);
//					form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//				} else
			form.setWeight(father_sat.getAccuracy());
			rules.add(form);
//				System.out.println(data.size() + "/" + form.toString());
			return node;
		} else	{//从当前father看
			// else split current node
			double maxGain = -100.0; //一个极小值而已 设啥都行 维护着该层的熵差最大值 covered positive & covered negative
			myTerm max_gain_term = new myTerm();
			ArrayList<myTerm> max_form_body = new ArrayList<myTerm>();
			ArrayList<myTerm> no_improve_terms = new ArrayList<myTerm>();
			PathSat maxCovSat = new PathSat();
			PathSat maxUncovSat = new PathSat();
			

			
			Formula cur_form = toFormula(father, branch);//获取公式 现在这个公式 注意！！
			ArrayList<myTerm> appeared = cur_form.getBody();
			
			features = removeDup(features, appeared);//删没用特征
			for (myTerm f : features) {
				PathSat covSat = new PathSat();
				PathSat uncovSat = new PathSat();
				
				//当前feature放入公式body的开头 feature for each available feature
				cur_form.pushBody(f);
				
				myWord var = f.getArg(0); //f的第0个word 这个word到底是啥 貌似就是Term中的第一个词 类似刘德华 variable involved
				
				for (PathData tmp_pd : father_sat.getAll()) {//遍历所有的路径path
					int idx = tmp_pd.getVarList().indexOf(var);//对于这个路径找到var的位置
					boolean covered = false;
					for (myTerm t : tmp_pd.getWordFeature(idx)){//找到该位置的Feature
						myTerm feat = t.clone(); // feature involved
						feat.setArg(0, var); //应该是在feature中用变量var替换掉word  substitute variable to word for comparing
						if (feat.equals(f)) {//相当于对所有路径中，当前循环到的feature等于 路径中（变量替换掉word）之后的那个term 就视为覆盖到了 好好想想！
							// covered
							covered = true;//标记为覆盖到
							break;
						}
					}
					if (covered) {
						// add to covered set
						covSat.addPath(tmp_pd);
					}else {
						// add to uncovered set
						uncovSat.addPath(tmp_pd);
					}
				}
				// compute foilgain分别计算covered  uncovered中的熵差
				double cov_foilgain = foilGain(covSat, father_sat);
				double uncov_foilgain = foilGain(uncovSat, father_sat);
				double total_gain = Math.abs(cov_foilgain - uncov_foilgain);
				
				//维护着最大的熵差
				if (maxGain <= total_gain){
					maxGain = total_gain;
					maxCovSat = covSat;
					maxUncovSat = uncovSat;
					max_gain_term = f.clone();
					max_gain_term.setPositive();//这是嘎哈？？？
					max_form_body = new ArrayList<myTerm>();
					for (myTerm tmp_term : cur_form.getBody()){
						max_form_body.add(tmp_term.clone());
					}
				}
				if (total_gain <= 0.0)//熵反而增加了 说明加入这个分裂 是错的 pop   加入”没有提升term“中 还要细想想
					no_improve_terms.add(f);
				cur_form.popBody();
			}
			if (maxGain <= 0.0) {//0 了结束
				node.setIsLeaf(true);
				Formula form = toFormula(father, branch);
//					if ((form.getHead().size() == 1) && (!form.getHead().get(0).isPositive())) {
//						myTerm h = form.getHead().get(0).clone();
//						h.setPositive();
//						form.getHead().clear();
//						form.pushHead(h);
//						form.setWeight(1 - father.getSentSat(branch).getAccuracy());
//					} else
				form.setWeight(father_sat.getAccuracy());
				rules.add(form);
//					System.out.println(data.size() + "/"  + form.toString());
				return node;
			}
//				System.out.println(maxGain + ": " + max_gain_term.toPrologString());
			/*
			 * 貌似是以上判断出是否要加 现在开始加了 加的步骤如下
			 * 看看！ADD NEW TERM AS NODE
			 * 1. create node;
			 * 2. remove candidate node;
			 * 3. create node's children.
			 */
			
			node.addTermNodes(max_gain_term);//加入分裂节点序列termNodes
//			// add unequality constraints
//			ArrayList<myTerm> uneq = buildUnequalFeature(getAllVars(max_form_body));
//			for (myTerm t : uneq) {
//				if (!max_form_body.contains(t))
//						node.addTermNodes(t);
//			}
			
			// add negative candidates
//				candidateTerms.removeAll(no_improve_terms);
			for (myTerm tmp_term : node.getTermNodes()) {//把用过节点的Term加入usedTerms 关键看看怎么用usedTerms！！！
				usedTerms.add(tmp_term.clone());
			}
			
			ArrayList<myTerm> cov_Features = new ArrayList<myTerm>();//cover Features
			for (myTerm tmp_term : features) {//当前在用的特征 features是输入之后删除没用的特征之后的  把这些放入已覆盖的特征 重点关注输入和递归函数中的输入
				cov_Features.add(tmp_term.clone());
			}
			ArrayList<myTerm> uncov_Features = new ArrayList<myTerm>();//同理
			for (myTerm tmp_term : features) {
				uncov_Features.add(tmp_term.clone());
			}
			cov_Features = addTermFromNegSamps(maxCovSat.getNegative(), cov_Features);
			uncov_Features = addTermFromNegSamps(maxUncovSat.getNegative(), uncov_Features);
			
			// remove added nodes and useless nodes用过的usedTerms 和没用的no_improve_terms都删除
			ArrayList<myTerm> tt = new ArrayList<myTerm>();
			for (myTerm tmp_term : cov_Features) {
				if (!(usedTerms.contains(tmp_term)) && !(no_improve_terms.contains(tmp_term)))
					tt.add(tmp_term.clone());
			}
			cov_Features = tt;
			tt = null;
			
			tt = new ArrayList<myTerm>();
			for (myTerm tmp_term : uncov_Features) {//对于cov_Features和uncov_Features一样？？想想
				if (!(usedTerms.contains(tmp_term)) && !(no_improve_terms.contains(tmp_term)))
					tt.add(tmp_term.clone());
			}
			uncov_Features = tt;
			tt = null;
			
			// create children递归的形式吗
			node.setFalseChild(create(maxUncovSat, uncov_Features, usedTerms, node, false));//
			node.setTrueChild(create(maxCovSat, cov_Features, usedTerms, node, true));
			return node;
		}
	}
	/**
	 * 可能是删除和app中重复的？
	 * @param cands
	 * @param app
	 * @return
	 */
	private ArrayList<myTerm> removeDup(ArrayList<myTerm> cands,//望洲说这是删掉没用的啥东西的啊？从所有候选中删 用过的？
			ArrayList<myTerm> app) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<Predicate> app_P = new ArrayList<Predicate>();
		ArrayList<myWord> app_V = new ArrayList<myWord>();
		
		ArrayList<Predicate> dup_feat = new ArrayList<Predicate>();
		dup_feat.add(new Predicate("postag/2"));
		dup_feat.add(new Predicate("class/2"));
		dup_feat.add(new Predicate("wordfeat/2"));//只加这三个谓词
		for (myTerm a : app) {//遍历输入app，有为正的而且谓词是以上三个之一 加入谓词的第i=1个getArg（word）
			if (a.isPositive() && dup_feat.contains(a.getPred())){
				app_P.add(a.getPred());
				app_V.add(a.getArg(1));
			}
		}
		for (myTerm t : cands) {//候选义项中包含这三个谓词的进行遍历  不包含的直接add到要返回的数据结构
			if (dup_feat.contains(t.getPred()) && (app_P.size() > 0)) {
				for (int i = 0; i < app_P.size(); i++) {//谓词 如果有又不在app_V中又不在app_P中的t  要add进来
					if (!(t.getPred().equals(app_P.get(i)) && t.getArg(0).equals(app_V.get(i))))
						re.add(t.clone());
				}
			} else {
				re.add(t.clone());
			}
		}
		return re;
	}

	
	/**
	 * 从负例中提特征？？ 不理解实现
	 * add features extracted from negative samples
	 * @param sat: satisfaction info of node, contains negative samples
	 * @param cand: candidate terms
	 */
	private ArrayList<myTerm> addTermFromNegSamps(ArrayList<PathData> path_data, ArrayList<myTerm> cand) {
		for (int i = 0; i < path_data.size(); i ++) {//遍历输入的每个PathData
			PathData pd = path_data.get(i);
			ArrayList<myTerm> neo_cand = new ArrayList<myTerm>();
			for (int m = 0; m < pd.getWordList().size(); m++) {//每个PathData中每个词（还是每个变量var？）
				myWord var = pd.getVar(m);
				for (int n = 0; n < pd.getWordFeature(m).size(); n++) {//每个特征
					myTerm tmp_term = pd.getWordFeature(m).get(n).clone();//有该特征的Term？？？？特征为啥是Term类型 
					tmp_term.setArg(0, var);//第0个特征用pd.getVar(m)替换掉？？？？是取负例的某种策略吗
					neo_cand.add(tmp_term);
				}
			}
			for (myTerm tt : neo_cand) {//uniq的加入
				if (!cand.contains(tt))
					cand.add(tt);
			}
		}
		return cand;
	}
	/**
	 * given a formula and a set of instances with label, compute the accuracy
	 * @param f: formula
	 * @param label: list of labels
	 * @param data: list of instances
	 * @return: satisfy samples (including negative samples)
	 */
	private SentSat getSatSamps(Formula f, ArrayList<ArrayList<myTerm>> label, ArrayList<Sentence> data) {
		Eval eval = new Eval(prolog, pred_list);
		eval.setRule(f);
		SentSat sat = eval.evalAllSat(label, data);
		// IMPORTANT! must retract all rules
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sat;
	}
	/**
	 * given a formula and a doc, compute the accuracy
	 * @param f: formula
	 * @param doc: document
	 * @return: satisfy samples (including negative samples)
	 */
	private SentSat getDocSatSamps(Formula f, Document doc) {
		Eval eval = new Eval(prolog, pred_list);
		eval.setRule(f);
		SentSat sat = eval.evalDocSat(doc);
		// IMPORTANT! must retract all rules
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sat;
	}
	/**
	 * 计算准确率given satisfy samples then compute accuracy
	 * @return: accuracy
	 */
	private double computeAccuracy(SentSat sat) {
		int p = sat.getAllPosNum();
		int n = sat.getAllNegNum();
		double t = (double) p + n;
		if (t == 0)
			t = 0.000000000000001;
		return (double) p/t;
	}

	/**
	 * compute available terms
	 * @node the node to build
	 * @candidateTerms a candidate set for choosing
	 */
	private ArrayList<myTerm> getAvailTerms(TreeNode node, ArrayList<myTerm> candidateTerms) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		ArrayList<String> appVars = new ArrayList<String>();
		ArrayList<myTerm> historyTerms = node.getAncestorNodes();
		for (myTerm t : historyTerms) {
			for (myWord w : t.getArgs()) {
				if ((w.isVar()) && !appVars.contains(w.toPrologString())) {
					appVars.add(w.toPrologString());					
				}
			}
		}
		for (myTerm t : candidateTerms) {
			boolean banned = false;
			boolean found = false;
			if (!appVars.isEmpty()) {
				for (myWord w : t.getArgs()) {
					if (w.isVar()) {
						for (String s : appVars) {
							if (s.equals(w.toPrologString())) {
								found = true;
								break;
							}
						}
					}
					if (!found)
						banned = true;
				}
			}
			if (!banned)
				re.add(t);
		}
		return re;
	}
	/**
	 * return a formula from a node to its root应该是构建公式 但是没太清楚实现 输入好像是node节点
	 * @param node
	 * @return
	 */
	private Formula toFormula(TreeNode node, boolean son_branch) {
		Formula re = new Formula();
		re.pushHead(head);
		// deep clone
		LinkedList<myTerm> fa = new LinkedList<myTerm>();
		for (myTerm t : node.toTerms()) {//输入node节点所有term 不是的话son_branch 反转正负性  都add进来
			myTerm n_t = t.clone();
			if (!son_branch)//不是的话 反转正负性
				n_t.flip();
			fa.add(n_t);
		}
		re.pushBodyToFirst(fa);//fa放到body的头
		while (node.getFather() != null){//逐步向上找父亲  也是：把每个term add进来
			TreeNode tmp_father_node = node.getFather();
			fa = new LinkedList<myTerm>();
			for (myTerm tmp_t : tmp_father_node.toTerms()) {
				fa.add(tmp_t.clone());
			}
			if (!node.isPositiveBranch()) {//？？？嘎哈的 为负的话每个fa都反转
				for (myTerm t : fa)
					t.flip();
			}
			re.pushBodyToFirst(fa);//每个都放在开头
			node = tmp_father_node;
		}
		return re;
	}
	
	/**
	 * find all variables in a list of terms
	 * @param terms
	 * @return
	 */
	private ArrayList<myWord> getAllVars(ArrayList<myTerm> terms) {
		ArrayList<myWord> re = new ArrayList<myWord>();
		for (myTerm t : terms) {
			for (myWord arg : t.getArgs()) {
				if (!re.contains(arg) && arg.isVar())
					re.add(arg);
			}
		}
		return re;
	}
	/**
	 * add unequal constraints
	 * @param vars
	 * @return
	 */
	private ArrayList<myTerm> buildUnequalFeature(ArrayList<myWord> vars) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		for (int i = 0; i < vars.size(); i++)
			for (int j = i + 1; j < vars.size(); j++) {
				if (!vars.get(i).equals(vars.get(j))) {
					myTerm tmp_term = new CommonPredicates().prologEqual(vars.get(i), vars.get(j));
					tmp_term.setNegative();
					re.add(tmp_term);
				}
			}
		return re;
	}
	/**
	 * Add feature of each word in path
	 * @param words: words in path
	 * @param vars: variables that represent words
	 * @param feat_list: feature list of plenty 
	 * @return: a list of terms as feature
	 */
	private ArrayList<myTerm> buildFeature(ArrayList<myWord> vars, ArrayList<myWord> words, ArrayList<myTerm> feat_list) {
		ArrayList<myTerm> re = new ArrayList<myTerm>();
		if (words.size() == vars.size()) {
			myTerm tmp_term = new myTerm();
			for (int i = 0; i < words.size(); i++) {
				for (myTerm feat_term : feat_list) {//遍历每个word的每个feature
					if (feat_term.getArg(0).equals(words.get(i))) {//feature的第0个==当前word
						tmp_term = feat_term.clone();
						tmp_term.setArg(0, vars.get(i));
						if (!re.contains(tmp_term))//uniq
							re.add(tmp_term);
					}
				}
//				tmp_term = new CommonPredicates().prologEqual(vars.get(i), words.get(i).getZeroConst());
//				myTerm tmp_neg_term = new CommonPredicates().prologEqual(words.get(i), vars.get(i));
//				tmp_neg_term.setNegative();
				
//				tmp_term = new CommonPredicates().posTag(vars.get(i), words.get(i).toPostagWord());
//				re.add(tmp_neg_term);
//				if (!re.contains(tmp_term))
//					re.add(tmp_term);
			}
		} else {
			System.out.println("Number of words and number of variables does not meet");
		}
		return re;
	}
	
	private ArrayList<myTerm> candFromSamps(myTerm negSamp, Sentence sent) {
		HyperGraph graph = new HyperGraph();
 	   	myTerm[] terms = sent.getTerms();
 	   	myWord[] words = sent.getWords();
 	   	for (myWord word : words) {
 	   		graph.addHyperVertex(word);
 	   	}
 	   	
 	   	for (myTerm term : terms) {
 	   		graph.addHyperEdge(term);
 	   	}
 	   	HyperVertex start = new HyperVertex(negSamp.getArg(0));
 	   	HyperVertex end = new HyperVertex(negSamp.getArg(1));
 	   	HyperPathFind pf = new HyperPathFind(graph, start, end);
 	   	LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
 	   	pf.Search(visitedEdges);
 	 	// place substitution
 	   	ArrayList<myTerm> cand = new ArrayList<myTerm>(); // candidate terms
		// substitution and get more features (temporarily only use words themselves)
 	   	for (LinkedList<myTerm> path : pf.getPaths()) {
 	   		ArrayList<myTerm> all_terms = new ArrayList<myTerm>(path.size() + 1);
 	   		all_terms.add(negSamp);
 	   		all_terms.addAll(path);
 	   		Substitute subs = new Substitute(all_terms);
 	   		ArrayList<myTerm> all_sub_terms = subs.getSubTerms();
 	   		ArrayList<myWord> word_list = subs.getWordList();
 	   		ArrayList<myWord> var_list = subs.getVarList();
 	   		ArrayList<myTerm> feature = buildFeature(var_list, word_list, sent.getFeatures());
 			// set head term
// 			this.setHead(all_sub_terms.get(0));
 			all_sub_terms.remove(0);
 			// add path as candidate terms, then build more feature as candidate terms
 			cand.addAll(feature);
 	   	}
//		for (myTerm t : cand) {
//			System.out.println(t.toPrologString());
//		}
 	   	return cand;
	}
	/**
	 * 计算两堆儿的熵的差值
	 * Gain(R0, R1) := t * ( log2(p1/(p1+n1)) - log2(p0/(p0+n0)) ).加这一层节点之后的熵 - 之前的熵 
	 * R0 denotes a rule before adding a new literal.
	 * R1 is an extesion of R0.
	 * p0 denotes the number of positive tupels, covered by R0,
	 * p1 the number of positive tupels, covered by R1.
	 * n0 and n1 are the number of negative tupels, covered by the according rule.
	 * t is the number of positive tupels, covered by R0 as well as by R1.
	 * @param r1
	 * @param r0
	 * @return
	 */
	private double foilGain(PathSat ps1, PathSat ps0) {

		int t = ps1.getPositive().size();
		//为正的数量 为正/为负数量的差 能算出来紊乱程度 就是熵（这个不是准确率吧，应该是coverd，类似覆盖率吧？？？）
		//要仔细想想是为啥
		double acc1 = ps1.getAccuracy();
		if (acc1 == 0.0)
			acc1 = 0.000000000000001;
		double acc0 = ps0.getAccuracy();
		if (acc0 == 0.0)
			acc0 = 0.000000000000001;
		double re = (double) t*(Math.log(acc1) - Math.log(acc0));
		return re;
		
	}
	
	private double splitInfo(SentSat sat) {
		double p = sat.getCov();
		if (p == 0)
				p = 0.000000000000001;
		return (double) -(p*Math.log(p) + (1-p)*Math.log(1-p));
	}
	
	/**
	 * 基本也是没人用 只有没用的RuleTree中调用 暂时不看 明天问问望洲？？？
	 * @param data
	 * @return
	 */
	public SentSat evaluateThis(Data data) {
		// evaluate given data by current rules
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
		
		// merge all the results by averaging the probability
		ArrayList<ArrayList<myTerm>> merged_result = mergeProbResults(result_one_by_one);

		// Calculate accuracy from merged_result
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
	 * 和yapEvaluateThread相似，这个是内部实现的yapEvaluateThread 可以在建树过程中实现 不细看了
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public SentSat yapEvaluateThis(Data data) throws IOException {
		// evaluate given data by current rules
		SentSat re = new SentSat();
		String tmp_dir = System.getProperty("user.dir") + "/tmp/";
		String tmp_rule = tmp_dir + "tmp_rules.pl";
		String tmp_sent = tmp_dir + "tmp_sent.pl";
		String tmp_query = tmp_dir + "tmp/tmp_query.pl";
		String tmp_ground_prog = tmp_dir + "tmp_ground_prog.pl";
		String tmp_evidence = tmp_dir + "tmp_evidence.pl";
		String tmp_ground_query = tmp_dir + "tmp_ground_query.pl";
		
		String yap_cmd = String.format("yap -L /home/daiwz/Projects/problog2/assist/ground_hack.pl -- %s %s %s %s %s %s",
				tmp_rule, tmp_query, tmp_sent, tmp_ground_prog, tmp_evidence, tmp_ground_query);
		
		// write problog file
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tmp_rule));
		BufferedWriter frule = new BufferedWriter(osw);
		for (Formula f : this.rules) {
			frule.write(f.toPrologString() + '\n');
		}
		frule.close();
		
		// for each sentence, call yap to compute
		for (Sentence sent : data.getSents()) {
			osw = new OutputStreamWriter(new FileOutputStream(tmp_sent));
			BufferedWriter fsent = new BufferedWriter(osw);
			for (myTerm t : sent.getTerms())
				fsent.write(t.toString() + ".\n");
			for (myTerm t : sent.getFeatures())
				fsent.write(t.toString() + ".\n");
			fsent.close();
			String[] cmds = { "/bin/sh", "-c", new String(yap_cmd.getBytes(), "utf-8") };
			try {
				Process ps = Runtime.getRuntime().exec(cmds);
				System.out.print(loadStream(ps.getInputStream()));
				System.err.print(loadStream(ps.getErrorStream()));
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
				
		// evaluate each rule in current tree one by one
//		ArrayList<SentSat> all_sat = eval.evalOneByOneSat(lp, data.getLabels(), data.getSents());
		
		// get the list of answer for each sentence by each rule
//		ArrayList<ArrayList<LinkedList<myTerm>>> result_one_by_one = eval.evalOneByOne(lp, data.getSents());
		
		// merge all the results by averaging the probability
//		ArrayList<ArrayList<myTerm>> merged_result = mergeProbResults(result_one_by_one);

		// Calculate accuracy from merged_result
//		for (int k = 0; k < merged_result.size(); k++) {
//			SatisfySamples tmp_sat = new SatisfySamples();
//			tmp_sat.setSatisifySamplesProb(data.getLabel(k), new LinkedList<myTerm>(merged_result.get(k)));
//			re.addSentSat(data.getLabel(k), data.getSent(k), tmp_sat);
//		}
//		re.setTotal();
		
		return re;
	}
	/**
	 * 应该是adaboosting中调用的关键，计算每轮、下一轮如何分配时候用Merge results of different probabilistic rules
	 * @param results
	 * @return: merged results, with probability
	 */
	private ArrayList<ArrayList<myTerm>> mergeProbResults(
			ArrayList<ArrayList<LinkedList<myTerm>>> results) {
		int rule_num = results.size();
		int sent_num = results.get(0).size();
		
		ArrayList<ArrayList<myTerm>> re = new ArrayList<ArrayList<myTerm>>(sent_num);
		
		for (int j = 0; j < sent_num; j++) {
			ArrayList<myTerm> tmp_ans_list = new ArrayList<myTerm>();
			ArrayList<Integer> tmp_ans_list_cnt = new ArrayList<Integer>();
			
			for (int i = 0; i < rule_num; i++) {//遍历每个句子中的每条rule
				LinkedList<myTerm> tmp_result_list = results.get(i).get(j);//先i后j这个好好想想？？
				for (myTerm t : tmp_result_list) {
					if (tmp_ans_list.contains(t)) {//tmp_ans_list是否包含结果中这个term 不包含则加入
						int idx = tmp_ans_list.indexOf(t);
						
						myTerm the_t = tmp_ans_list.get(idx); //tmp_ans_list已有的 相应位置的Term the_t the term already in ans_list
						
						if (t.isPositive() == the_t.isPositive())//已有的和新来的根据正负性，加减权重
							the_t.setWeight((double) (the_t.getWeight() + t.getWeight()));
						else {
							the_t.setWeight((double) (the_t.getWeight() + (1 - t.getWeight())));
						}
						
						tmp_ans_list_cnt.set(idx, tmp_ans_list_cnt.get(idx) + 1);
						//相当于对于对应位置的count++ record the counts 是记录所有该位置的term个数的
					} else {//第一次 破处
						tmp_ans_list.add(t);
						tmp_ans_list_cnt.add(1);
					}
				}
			}
			
			for (int k = 0; k < tmp_ans_list.size(); k++) {
				tmp_ans_list.get(k).setWeight((double) tmp_ans_list.get(k).getWeight()/tmp_ans_list_cnt.get(k));
			}//计算加权平均？？ 恩 应该是
			
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
