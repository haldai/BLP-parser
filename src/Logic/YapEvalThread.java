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
	String tmp_dir = System.getProperty("user.dir") + "/tmp/";//pl文件
//	String tmp_input = tmp_dir + "tmp_input.pl";
	String tmp_rule = tmp_dir + "tmp_rules.pl";
	String tmp_sent = tmp_dir + "tmp_sent_xx.pl";
	String tmp_query = tmp_dir + "tmp_query.pl";
	String tmp_ground_prog = tmp_dir + "tmp_ground_prog_xx.pl";
	String tmp_evidence = tmp_dir + "tmp_evidence_xx.pl";
	String tmp_ground_query = tmp_dir + "tmp_ground_query_xx.pl";
	
	ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	ArrayList<ArrayList<myTerm>> merged_result = new ArrayList<ArrayList<myTerm>>();//merged_result注意
	Data data = new Data();//全部？
	
	int number = 0;
	int currentThreads = 0;
	int totalThreads = 0;

	public YapEvalThread(Data d, ArrayList<Formula> rules, int total_threads) throws Exception {
		
		data = d;
		merged_result = new ArrayList<ArrayList<myTerm>>(d.getSents().size());
		totalThreads = total_threads;
		
		for (int i = 0; i < d.getSents().size(); i++) {//每个句子加入merged_result
			merged_result.add(new ArrayList<myTerm>());
		}
		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tmp_rule));
		BufferedWriter frule = new BufferedWriter(osw);
		for (Formula f : rules) {//打出来每个rules
			frule.write(f.toString() + '\n');
		}
		
		frule.close();
		
	}

	@Override
	public void run() {
		
		int num = Integer.parseInt(Thread.currentThread().getName());
		int sent_num = number + num;
		
		if (sent_num < data.getSents().size()) {
			
			String num_str = String.valueOf(num);
			
			String cur_sent = tmp_sent.replace("xx", num_str);//用num的str形式替换掉XX 不错的想法
			String cur_ground_query = tmp_ground_query.replace("xx", num_str);
			String cur_ground_prog = tmp_ground_prog.replace("xx", num_str);
			String cur_evidence = tmp_evidence.replace("xx", num_str);
	
			String yap_cmd = String.format("yap -L prolog/ground_hack.pl -- %s %s %s %s %s %s",
					tmp_rule, tmp_query, cur_sent, cur_ground_prog, cur_evidence, cur_ground_query);//存下 可能准备打印这个 是一条命令吗
			
			Sentence sent = data.getSent(sent_num);
			
			OutputStreamWriter osw;
			try {
				// print current sentence打印当前的句子？貌似只有Term啊
				osw = new OutputStreamWriter(new FileOutputStream(cur_sent));
				BufferedWriter fsent = new BufferedWriter(osw);
				for (myTerm t : sent.getTerms())
					fsent.write(t.toString() + ".\n");
				for (myTerm t : sent.getFeatures())//为啥总有特征这东西
					fsent.write(t.toString() + ".\n");
				fsent.close();
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			try {
				Process ps = Runtime.getRuntime().exec(yap_cmd);//应该是区分线程的执行yap_cmd这命令
				ps.waitFor();
				System.out.print(loadStream(ps.getInputStream()));//应该是打印ps相关的东西
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
				while ((tempString = reader.readLine()) != null) {//读一行file
					// print line number
					line = tempString.trim();
					if (line.length() >= 0) {
						String[] weighted_term = line.split("::");//默认有权重？
						myTerm term = new myTerm(weighted_term[1].replaceAll("\'", ""));//替换掉' 并存在term中 下两步用term
						term.setWeight((Double.parseDouble(weighted_term[0]) - 0.5) * 2);//是用-0.5*2的算法处理了权重吗
						sent_result.add(term);//sent_result 下面用
					}
				}
				
				merged_result.set(sent_num, sent_result);//替换掉merged_result中对应位置的term 根据sent_num 
				//sent_num是根据一个考验设定的数和另一个线程给的数定的
				
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		waitForAll();
		
	}

	
	static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		in = new BufferedInputStream(in);//读到buffer然后返回string 这个string是线程安全的
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
            notifyAll();//唤醒所有
        }
    }

}
