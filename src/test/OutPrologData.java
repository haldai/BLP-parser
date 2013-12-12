package test;

import java.io.IOException;

import Logic.Document;

public class OutPrologData {

	static public void main(String[] args) throws Exception {
		Document doc = new Document();
		doc.readConll("data/data_revised.pred", 
				"../data/zhiliang/train1_1w.train", true);
		doc.printDocPl("../data/zhiliang/train1_1w.train.pl");
		doc.printLabelPl("../data/zhiliang/train1_1w.train.label");
		doc.printSent("../data/zhiliang/train1_1w.train.sent");
		System.out.println("finished");
	}
}
