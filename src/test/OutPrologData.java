package test;

import java.io.IOException;

import Logic.Document;

public class OutPrologData {

	static public void main() throws IOException {
		Document doc = new Document();
		doc.readConll("data/data_revised.pred", 
				"data/segmentQueryTrain_lemma1", true);
		doc.printDocPl();
	}

}
