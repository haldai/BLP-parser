/**
 * 
 */
package Logic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import ILP.SatisfySamples;
import Tree.Data;
import Tree.SentSat;

/**
 * @author daiwz
 *
 */
public class YapEvalThread implements Runnable{

	/**
	 * 
	 */
	String tmp_dir = System.getProperty("user.dir") + "/tmp/";
//	String tmp_input = tmp_dir + "tmp_input.pl";
	String tmp_rule = tmp_dir + "tmp_rules.pl";
	String tmp_sent = tmp_dir + "tmp_sent_xx.pl";
	String tmp_query = tmp_dir + "tmp_query.pl";
	String tmp_ground_prog = tmp_dir + "tmp_ground_prog_xx.pl";
	String tmp_evidence = tmp_dir + "tmp_evidence_xx.pl";
	String tmp_ground_query = tmp_dir + "tmp_ground_query_xx.pl";
	
	ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	ArrayList<ArrayList<myTerm>> merged_result = new ArrayList<ArrayList<myTerm>>();
	Data data = new Data();
	
	private CountDownLatch countDownLatch; // countdown latch for waiting all thread finish
	
	int number = 0;
	int currentThreads = 0;
	int totalThreads = 0;

	public YapEvalThread(Data d, ArrayList<Formula> rules, int total_threads) throws Exception {
		
		data = d;
		merged_result = new ArrayList<ArrayList<myTerm>>(d.getSents().size());
		totalThreads = total_threads;
		
		for (int i = 0; i < d.getSents().size(); i++) {
			merged_result.add(new ArrayList<myTerm>());
		}
		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tmp_rule));
		BufferedWriter frule = new BufferedWriter(osw);
		for (Formula f : rules) {
			frule.write(f.toString() + '\n');
		}
		
		frule.close();
		
	}
	
	public void setCountDownLatch(CountDownLatch cdl) {
		this.countDownLatch = cdl;
	}
	
	public void removeCoundDownLatch() {
		this.countDownLatch = null;
	}
	
	@Override
	public void run() {
		
		int num = Integer.parseInt(Thread.currentThread().getName());
		int sent_num = number + num;
		
		if (sent_num < data.getSents().size()) {
			
			String num_str = String.valueOf(num);
			
			String cur_sent = tmp_sent.replace("xx", num_str);
			String cur_ground_query = tmp_ground_query.replace("xx", num_str);
			String cur_ground_prog = tmp_ground_prog.replace("xx", num_str);
			String cur_evidence = tmp_evidence.replace("xx", num_str);
	
			String yap_cmd = String.format("yap -L prolog/ground_hack.pl -- %s %s %s %s %s %s",
					tmp_rule, tmp_query, cur_sent, cur_ground_prog, cur_evidence, cur_ground_query);
			
			Sentence sent = data.getSent(sent_num);
			
			OutputStreamWriter osw;
			try {
				// print current sentence
				osw = new OutputStreamWriter(new FileOutputStream(cur_sent));
				BufferedWriter fsent = new BufferedWriter(osw);
				for (myTerm t : sent.getTerms())
					fsent.write(t.toString() + ".\n");
				for (myTerm t : sent.getFeatures())
					fsent.write(t.toString() + ".\n");
				fsent.close();
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			try {
				Process ps = Runtime.getRuntime().exec(yap_cmd);
				ps.waitFor();
				System.out.print(loadStream(ps.getInputStream()));
				System.err.print(loadStream(ps.getErrorStream()));
				ps.destroy();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// read tmp_ground_prog
			File file = new File(cur_ground_prog);
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(file));
			
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
				countDownLatch.countDown();
				sync_merge(sent_num, sent_result); // synchronize merging procedure
				
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sync_merge(int sent_num, ArrayList<myTerm> sent_result) {
		synchronized(this) {
			merged_result.set(sent_num, sent_result);
		}
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
	
	public ArrayList<ArrayList<myTerm>> getMergedResult() {
		return merged_result;
	}
	
	public void setNumber(int n) {
		number = n;
	}
	
	public int getNumber() {
		return number;
	}
	
	public synchronized void waitForAll() {
        currentThreads++;
        if(currentThreads < totalThreads) {
            try {
                wait();
            } catch (Exception e) {}
        }
        else {
            currentThreads = 0;
            notifyAll();
        }
    }

}
