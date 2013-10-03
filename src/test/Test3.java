package test;

import jpl.*;

public class Test3 {
	public static void main(String[] args) {
		if (!Query.hasSolution("consult('test3.pl').")) {
			System.out.println("consult error");
		} else {
			test3a("ria");
		}
	}
	static void test3a(String sent) {
		jpl.Variable X = new jpl.Variable("X");
		jpl.Query q3 = new jpl.Query("parent", new jpl.Term[] {X, new jpl.Atom(sent)});
		System.out.println("Parent of " + sent + " is " + q3.oneSolution().get("X"));
	}
}
