package test;

import Logic.*;

public class tmptest {


	public static void main(String[] args) {
		Document doc = new Document("data/questions/questions.pred", 
				"data/questions/test/query_v2.dep.bak", false);
		
		for (int i = 0; i < doc.length(); i++) {
			System.out.println(doc.getSent(i).str());
		}
	}

}