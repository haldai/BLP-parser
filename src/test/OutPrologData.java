package test;

import java.io.IOException;

import Logic.Document;

public class OutPrologData {

	static public void main(String[] args) throws IOException {
		Document doc = new Document();
		doc.readConll("data/data_revised.pred", 
				"../data/case/segmentQueryTest1", true);
		doc.printDocPl("../data/case/segmentQueryTest1.pl");
		doc.printLabelPl("../data/case/segmentQueryTest1.label");
		doc.printSent("../data/case/segmentQueryTest1.sent");
		System.out.println("finished");
	}
}
