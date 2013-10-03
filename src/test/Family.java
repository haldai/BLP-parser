package test;

import java.util.Hashtable;
import jpl.*;
import jpl.Query;
/*
 * 不要读取pl数据库，除非文件头已经定义dynamic. 在jpl.JPL中声明新的predicate自动被判定为dynamic，
 * 除非被compile_predicate/1修改。
 */
public class Family
{
	public static void
	main( String argv[] )
	{
		
		jpl.JPL.init();
//		String t1 = "consult('family.pl')";
//		Query q1 = new Query(t1);
//
//		System.out.println( t1 + " " + (q1.hasSolution() ? "succeeded" : "failed") );

		//--------------------------------------------------
		
		String child = "child_of";
		Compound c = new Compound(
						child,
						new Term[] {
								new Atom("tom"),
								new Atom("steve")
						});
		String assertz = "assertz";
		new Query("assertz(child_of(joe, ralf)).").hasSolution();
		new Query("assertz(child_of(mary, joe)).").hasSolution();
		new Query("assertz(child_of(steve, joe)).").hasSolution();
		new Query("assertz(descendent_of(X, Y):-child_of(X, Y)).").hasSolution();
		new Query("assert((descendent_of(X, Y):-child_of(Z, Y),descendent_of(X, Z))).").hasSolution();
		new Query("retract((descendent_of(X, Y):-child_of(Z, Y),descendent_of(X, Z))).").hasSolution();
		Query q0 = new Query(
				assertz,
				new Term[] { c });
		if (q0.hasSolution()) {
			System.out.println("assertion of 'assertz(child_of(tom, steve)).' complete");
		}
		
		String t2 = "child_of(joe, ralf)";
		Query q2 = new Query(t2);

		System.out.println( t2 + " is " + (q2.hasSolution() ? "provable" : "not provable") );

		//--------------------------------------------------

		String t3 = "descendent_of(steve, ralf)";
		Query q3 = new Query(t3);

		System.out.println( t3 + " is " +(q3.hasSolution() ? "provable" : "not provable") );

		//--------------------------------------------------

		String t4 = "descendent_of(X, ralf)";
		Query q4 = new Query(t4);

		System.out.println( "first solution of " + t4 + ": X = " + q4.oneSolution().get("X"));

		//--------------------------------------------------

		java.util.Hashtable[] ss4 = q4.allSolutions();

		System.out.println( "all solutions of " + t4);
		for ( int i=0 ; i<ss4.length ; i++ ) {
			System.out.println( "X = " + ss4[i].get("X"));
		}

		//--------------------------------------------------

		System.out.println( "each solution of " + t4);
		while ( q4.hasMoreSolutions() ){
			java.util.Hashtable s4 = q4.nextSolution();
			System.out.println( "X = " + s4.get("X"));
		}

		//--------------------------------------------------

		String t5 = "descendent_of(X,Y)";
		Query q5 = new Query(t5);

		System.out.println( "each solution of " + t5 );
		while ( q5.hasMoreSolutions() ){
			java.util.Hashtable s5 = q5.nextSolution();
			System.out.println( "X = " + s5.get("X") + ", Y = " + s5.get("Y"));
		}


	}

}


