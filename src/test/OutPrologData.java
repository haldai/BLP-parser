package test;

import java.io.IOException;

import Logic.Document;

public class OutPrologData {

	static public void main(String[] args) throws Exception {
		Document doc = new Document();
		doc.readConll("data/data_revised.pred", 
				"../data/traindata1_num1_1p20.train", true);
		doc.printDocPl("../data/traindata1_num1_1p20.train.spoc.pl");
		doc.printLabelPl("../data/traindata1_num1_1p20.train.label");
		doc.printSent("../data/traindata1_num1_1p20.train.sent");
		System.out.println("finished");
	}
}
