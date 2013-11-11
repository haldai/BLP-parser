package test;

import jpl.*;
/*
 * 该测试有问题
 */
public class Test2
{ public static int fac(int n)
  { if (n == 1)
    { return 1;
    } else
    { 	Compound c = new Compound(
						"jpl_test_fac",
						new Term[] {
								new jpl.Integer(n - 1),
								new Variable("F")
						});
    	Query q = new Query(c);
    	q.oneSolution().get("F");
    	jpl.Integer ans = (jpl.Integer) q.oneSolution().get("F");
    	int re = ans.intValue();
    	return n * re;
//		return n * ((jpl.Integer)
//		new Query(new Compound("jpl_test_fac", new Term[]
//					 { new jpl.Integer(n - 1),
//					   new Variable("F")
//					 })).oneSolution().get("F")).intValue();
    }
  }

  public static void
  main( java.lang.String argv[] )
  { new Query("consult('test2.pl')").oneSolution();

    System.out.print( "calling Prolog to call Java to call Prolog...\n" );
    
    int x = fac(10);

    System.out.println( "factorial(10) = " + x);
  }
}
