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
	Sentence[] sent_list;
	myTerm[] label_list;
	Predicate[] pred_list;
	// reading path of predicate file and sentence file.
	public Document(String path_pred, String path_sent, boolean train) {
		// read file		
		String[] fpred = readFileByLines(path_pred);
		System.out.println(fpred.length);
		String[] fsent = readFileByLines(path_sent);
		System.out.println(fsent.length);
		// TODO parse into sentences(train or test).
		// TODO predicates
		Predicate[] buff_pred_list = new Predicate[fpred.length];
		for (int i = 0; i < fpred.length; i++) {
			String[] s = fpred[i].split("\\/");
			buff_pred_list[i] = new Predicate(s[0], Integer.parseInt(s[1]));
		}
		pred_list = buff_pred_list;
		buff_pred_list = null;
		// TODO sentences
		Sentence[] buff_sent_list = new Sentence[fsent.length];
		myTerm[][] buff_label_list = new myTerm[fsent.length][MAX_LABEL_LEN];
		String line = null;
		String[] buff_line = new String[2];
		for (int i = 0; i < fsent.length; i++) {
			line = fsent[i];
			buff_line = line.split("\\:-");
		}
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
}
