/**
 * 
 */
package Logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author daiwz
 *
 */
public class Document {

	/**
	 * Read file and comile it as a document.
	 */
	boolean tr = false; // true for trainning data, false for testing data. 
	             // training data has labels, while testing data does not.
	int length;
	ArrayList<Sentence> sentList = new ArrayList<Sentence>();
	ArrayList<ArrayList<myTerm>> labelList = new ArrayList<ArrayList<myTerm>>();
	ArrayList<Predicate> predList = new ArrayList<Predicate>();
	Map<String, String> word2codeMap = new HashMap<String,String>();
	Map<String, String> code2wordMap = new HashMap<String,String>();
	ArrayList<String> wordList = new ArrayList<String>();
	int dictLen = 0;
	
	// reading path of predicate file and sentence file.
	public Document(String path_pred, String path_sent, boolean train) {
		// read file		
		String[] fpred = readFileByLines(path_pred);
		System.out.println("Predicate number: " + fpred.length);
		String[] fsent = readFileByLines(path_sent);
		tr = train;
//		for (int i = 0; i < fsent.length; i++)
//			System.out.println(fsent[i]);
		System.out.println("Sentence number:" + fsent.length);
		length = fsent.length;
		// parse into sentences(train or test).
		// predicates
		for (int i = 0; i < fpred.length; i++) {
			String[] s = fpred[i].split("\\/");
			predList.add(new Predicate(s[0], Integer.parseInt(s[1])));
		}
		// sentences
		
		/*
		 * SUBSTITUTE TO DICT CODES
		 */
		for (int i = 0; i < fsent.length; i++) {
			fsent[i] = sub_dict(fsent[i]); 
		}
//		myTerm[][] myTerm[fsent.length][MAX_LABEL_LEN];
		ArrayList<ArrayList<myTerm>>buff_label_list = new ArrayList<ArrayList<myTerm>>();
		String line = null;
		String[] buff_line = new String[2];
		for (int i = 0; i < fsent.length; i++) {
			line = fsent[i];
			buff_line = line.split("\\:-");
//			System.out.println(buff_line[0]);
			ArrayList<myTerm> tmp_label_list = new ArrayList<myTerm>(); // current sentence
			if (tr) {
				// training data, split the labels
				String[] buff_label = buff_line[0].split("\\;");
				for (int j = 0; j < buff_label.length; j++) {
					String tmp_label = buff_label[j];
					boolean isPositive = true;
					if (tmp_label.startsWith("\\+(")) {
						tmp_label = tmp_label.substring(2);
						isPositive = false;
					} else if (tmp_label.startsWith("not(")) {
						tmp_label = tmp_label.substring(4, tmp_label.length() - 1);
						isPositive = false;
					}
					if (tmp_label.indexOf('(') == 0)
						tmp_label = "sem" + tmp_label;
					myTerm tmp_term = new myTerm(tmp_label);
					if (isPositive)
						tmp_term.setPositive();
					else
						tmp_term.setNegative();
					tmp_label_list.add(tmp_term);
				}
				buff_label_list.add(tmp_label_list);
				// parse sentences
			}
			sentList.add(new Sentence(buff_line[buff_line.length - 1]));
			// build feature
//			System.out.println(i);
		}
		labelList = buff_label_list;
		buff_label_list = null;
	}

	public Document() {}

	public static String[] readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		List<String> string_buffer = new ArrayList<String>();
		try {
			System.out.println("reading file:" + fileName);
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			String line = null;
			// line by line untill null
			while ((tempString = reader.readLine()) != null) {
				// print line number
				line = tempString.trim();			
				if (line.length() >= 0) {
					string_buffer.add(line);
//					System.out.println(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {;}
			}
		}
		String[] buff = new String[string_buffer.size()];
		for (int i = 0; i < string_buffer.size(); i++) {
			buff[i] = string_buffer.get(i);
		}
		return buff;
	}
	
	public int length() {
		return sentList.size();
	}
	
	public boolean train() {
		return tr;
	}
	
	public Sentence getSent(int num) {
		return sentList.get(num);
	}
	
	public ArrayList<myTerm> getLabel(int num) {
		return labelList.get(num);
	}
	
	public ArrayList<ArrayList<myTerm>> getLabels() {
		return labelList;
	}
	
	public ArrayList<Predicate> getPredList() {
		return predList;
	}
	
	public ArrayList<Sentence> getSentences() {
		return sentList;
	}



	public void addSent(ArrayList<myTerm> label, Sentence sent) {
		
		this.labelList.add(label);
		this.sentList.add(sent);
	}
	
	public void setPredList(ArrayList<Predicate> p) {
		for (Predicate pp : p) {
			predList.add(pp.clone());
		}
	}
	
	/**
	 * build dictionary for chinese words
	 * @param org_words
	 * @return
	 */
	private void buildDict(ArrayList<myWord> org_words) {
		for (myWord w : org_words) {
			String word;
			if (w.getName().startsWith("d"))
				word = w.getName().substring(1);
			else
				word = w.getName();
			if (!wordList.contains(word)) {
				wordList.add(word);
				String code = String.format("w%d", dictLen);
				word2codeMap.put(word, code);
				code2wordMap.put(code, word);
				w.setName(code);
				dictLen++;
			}
		}
	}
	
	public void printDict() throws IOException {
		FileOutputStream fos = new FileOutputStream("out/words.dict");
		OutputStreamWriter osw=new OutputStreamWriter(fos);
		BufferedWriter fout=new BufferedWriter(osw);
		for (String word : wordList) {
			fout.write(String.format("%s\t%s\n", word2codeMap.get(word), word));
		}
		fout.close();
	}
	
	/**
	 * substitute all words in sentence into dict codes
	 * @param string
	 * @return
	 */
	private String sub_dict(String string) {
		// TODO get all words
		ArrayList<myTerm> labels = new ArrayList<myTerm>();
		ArrayList<myTerm> deps = new ArrayList<myTerm>();
		String label_str = "";
		String dep_str = "";
		if (tr) {
			label_str = string.split(":-")[0];
			dep_str = string.split(":-")[1];
			for (String s : label_str.split(";")) {
				labels.add(new myTerm(s));
			}
		} else {
			dep_str = string;
		}
		Sentence tmp_sent = new Sentence(dep_str);
		buildDict(new ArrayList<myWord>(Arrays.asList(tmp_sent.getWords())));
		for (myTerm t : tmp_sent.getTerms()) {
			for (myWord w : t.getArgs()) {
//				w.setName(word2codeMap.get(w.getName()));
				w.setName("u" + w.getName());
			}
		}
		
		if (tr) {
			for (myTerm t : labels) {
				for (myWord w : t.getArgs()) {
//					w.setName(word2codeMap.get(w.getName()));
					w.setName("u" + w.getName());
				}
			}
		}
		String re = "";
		for (myTerm t : labels) {
			re = re + t.toString() + ";";
		}
		if (!tr)
			re = ";";
		re = re.substring(0, re.length() - 1) + ":-" + tmp_sent.toTermString();
		return re;
	}

	public void addSent(String s) {
		s = sub_dict(s);
		String label = "";
		String sent = "";
		label = s.split(":-")[0];
		sent = s.split(":-")[1];
		this.sentList.add(new Sentence(sent));
	}
	
	public void setDocAs(Document doc) {
		this.predList = doc.getPredList();
		this.word2codeMap = doc.word2codeMap;
		this.code2wordMap = doc.code2wordMap;
		this.wordList = doc.wordList;
		this.dictLen = doc.dictLen;
	}
	
	public void setTrain(boolean t) {
		this.tr = t;
	}
	
	public void printDocPl() throws IOException {
		FileOutputStream fos = new FileOutputStream("out/doc.pl");
		OutputStreamWriter osw=new OutputStreamWriter(fos);
		BufferedWriter fout=new BufferedWriter(osw);
		for (int i = 0; i < this.sentList.size(); i++) {
			fout.write(String.format("%% sentence %d\n", i));
			for (myTerm t : this.sentList.get(i).getTerms())
				fout.write(String.format("%s.\n", t.toString()));
			for (myTerm f : this.sentList.get(i).getFeatures())
				fout.write(String.format("%s.\n", f.toString()));
			fout.write("\n");
		}
		fout.close();
	}
	
	public void readConll(String path_pred, String path_sent, boolean train) {
		// read file		
		String[] fpred = readFileByLines(path_pred);
		System.out.println("Predicate number: " + fpred.length);
		String[] fsent = readFileByLines(path_sent);
		tr = train;
		
		// predicates
		for (int i = 0; i < fpred.length; i++) {
			String[] s = fpred[i].split("\\/");
			predList.add(new Predicate(s[0], Integer.parseInt(s[1])));
		}
		
		// sentences
		ArrayList<myWord> tmp_wordList = new ArrayList<myWord>();
		ArrayList<myTerm> tmp_termList = new ArrayList<myTerm>();
		ArrayList<myTerm> tmp_featList = new ArrayList<myTerm>();
		ArrayList<myTerm> tmp_labelList = new ArrayList<myTerm>();
		for (int i = 0; i < fsent.length; i++) {
			String line = fsent[i];
			if (line.length() == 0) {
				// end of a sentence
				Sentence tmp_sent = new Sentence(tmp_termList.size(), tmp_wordList.size());
				tmp_sent.termList = tmp_termList.toArray(new myTerm[tmp_termList.size()]);
				tmp_sent.featList = tmp_featList;
				tmp_sent.wordList = tmp_wordList.toArray(new myWord[tmp_wordList.size()]);
				
				this.sentList.add(tmp_sent);
				if (tr)
					this.labelList.add(tmp_labelList);
				
				tmp_wordList = new ArrayList<myWord>();
				tmp_termList = new ArrayList<myTerm>();
				tmp_featList = new ArrayList<myTerm>();
				tmp_labelList = new ArrayList<myTerm>();
			} else {
				// read words and features
				String[] args = line.split("\t");
				// add word
				myWord cur_word = new myWord(String.format("%s_%s_%s", "u"+args[1], args[0], args[3]));
				tmp_wordList.add(cur_word);
				
				// add feature
				tmp_featList.add(new myTerm(String.format("postag(%s,%s_POS)", cur_word.toString(), args[3])));
				tmp_featList.add(new myTerm(String.format("class(%s,%s_CLS)", cur_word.toString(), "c"+args[2])));
				tmp_featList.add(new myTerm(String.format("spoc(%s,%s_SPOC)", cur_word.toString(), "s"+args[8])));
				
				// add term
				String pred_name = args[7].toLowerCase();
				if (!pred_name.equals("hed")) {
					int term_father = Integer.parseInt(args[6]);
					String[] args_f = fsent[i + term_father - Integer.parseInt(args[0])].split("\t");
					String father_str = String.format("%s_%s_%s", "u"+args_f[1], args_f[0], args_f[3]);
					myTerm tmp_term = new myTerm(String.format("%s(%s,%s)", pred_name, father_str, cur_word.toString()));
					tmp_termList.add(tmp_term);
				}
				
				// add label
				if (tr) {
					String label_name = args[10].toLowerCase();
					if (!label_name.equals("null") && (!label_name.equals("hed"))) {
						int label_father = Integer.parseInt(args[9]);
						String[] args_l_f = fsent[i + label_father - Integer.parseInt(args[0])].split("\t");
						String l_son_str = String.format("%s_%s_%s", "u"+args_l_f[1], args_l_f[0], args_l_f[3]);
						myTerm tmp_label = new myTerm(String.format("%s(%s,%s)", label_name, cur_word.toString(), l_son_str));
						tmp_labelList.add(tmp_label);
					}
				}
			}
		}
		this.length = this.sentList.size();
	}
}
