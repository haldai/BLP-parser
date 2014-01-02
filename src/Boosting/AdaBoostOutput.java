/**
 * 
 */
package Boosting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import Logic.*;
import Tree.*;
/**
 * @author daiwz
 *
 */
public class AdaBoostOutput {

	/**
	 * 
	 */
	ArrayList<ArrayList<Formula>> weakRules = new ArrayList<ArrayList<Formula>>();
	ArrayList<Double> ruleWeights = new ArrayList<Double>();
	
	public AdaBoostOutput() {}
	public ArrayList<ArrayList<Formula>> getWeakRules() {
		return weakRules;
	}
	
	public void addWeakRules(ArrayList<Formula> t, double w) {
		weakRules.add(t);
		ruleWeights.add(w);
	}
	
	public ArrayList<Double> getWeights() {
		return ruleWeights;
	}
	
	public void setWeakRules(ArrayList<ArrayList<Formula>> rules) {
		this.weakRules = rules;
	}
	
	public void setWeights(ArrayList<Double> weights) {
		this.ruleWeights = weights;
	}
	
	public void writeToFile(String out_path) throws IOException {
		for (int i = 0; i < weakRules.size(); i++) {
			String s = out_path + "/" + "rules" + i + ".pl";
			FileOutputStream fos = new FileOutputStream(s);
			OutputStreamWriter osw=new OutputStreamWriter(fos);
			BufferedWriter fout=new BufferedWriter(osw);
			fout.write(String.format("%%weight=%10.10f\n", ruleWeights.get(i)));
			for (int j = 0; j < weakRules.get(i).size(); j++)
				fout.write(String.format("%s\n", weakRules.get(i).get(j).toString()));
			fout.close();
		}
	}
	
	public void loadFromFiles(String dir_path) {
		File file = new File(dir_path); 
		File[] file_array = file.listFiles();
		for (int i = 0; i < file_array.length; i++) {
			if (file_array[i].isFile()) {

				double tmp_weight = 0.0;
				ArrayList<Formula> tmp_forms = new ArrayList<Formula>();
				
				if (file_array[i].getName().endsWith(".pl")) {
					// Read file
					String fileName = file_array[i].getAbsolutePath();
					String[] string_buffer = Document.readFileByLines(fileName);
					for (String line : string_buffer) {
						if (line.startsWith("%%weight"))
							tmp_weight = Double.valueOf(line.split("=")[1]);
						else {
							tmp_forms.add(new Formula(line));
						}
					}
				}
				this.addWeakRules(tmp_forms, tmp_weight);
				
			}
		}
	}
}
