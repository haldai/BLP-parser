/**
 * 
 */
package Logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static utils.utils.MAX_LABEL_LEN;;
/**
 * @author daiwz
 *
 */
public class Document {

	/**
	 * Read file and comile it as a document.
	 */
	boolean tr; // true for trainning data, false for testing data. 
	             // training data has labels, while testing data does not.
	int length;
	Sentence[] sentList;
	myTerm[][] labelList;
	Predicate[] predList;
	// reading path of predicate file and sentence file.
	public Document(String path_pred, String path_sent, boolean train) {
		// read file		
		String[] fpred = readFileByLines(path_pred);
		System.out.println(fpred.length);
		String[] fsent = readFileByLines(path_sent);
//		for (int i = 0; i < fsent.length; i++)
//			System.out.println(fsent[i]);
		System.out.println(fsent.length);
		length = fsent.length;
		// parse into sentences(train or test).
		// predicates
		Predicate[] buff_pred_list = new Predicate[fpred.length];
		for (int i = 0; i < fpred.length; i++) {
			String[] s = fpred[i].split("\\/");
			buff_pred_list[i] = new Predicate(s[0], Integer.parseInt(s[1]));
		}
		predList = buff_pred_list;
		buff_pred_list = null;
		// sentences
		Sentence[] buff_sentList = new Sentence[fsent.length];
		myTerm[][] buff_label_list = new myTerm[fsent.length][MAX_LABEL_LEN];
		String line = null;
		String[] buff_line = new String[2];
		for (int i = 0; i < fsent.length; i++) {
			line = fsent[i];
			buff_line = line.split("\\:-");
//			System.out.println(buff_line[0]);
			if (train) {
				// training data, split the labels
				String[] buff_label = buff_line[0].split("\\,");
				for (int j = 0; j < buff_label.length; j++) {
					buff_label_list[i][j] = new myTerm(buff_label[j]);
				}
				// parse sentences
			}
			buff_sentList[i] = new Sentence(buff_line[buff_line.length - 1]);
//			System.out.println(i);
		}
		sentList = buff_sentList;
		labelList = buff_label_list;
		buff_sentList = null;
		buff_label_list = null;
		// TODO test parse results
	}
	
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
				if (line.length() > 0) {
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
		return length;
	}
	
	public boolean train() {
		return tr;
	}
	
	public Sentence getSent(int num) {
		return sentList[num];
	}
	
	public myTerm[] getLabel(int num) {
		return labelList[num];
	}
	
	public Predicate[] getPredList() {
		return predList;
	}
	
	public Sentence[] getSentences() {
		return sentList;
	}
}
