package utils;

public class Tuple<X, Y> {
	public final X x; 
	public final Y y;
	
	public Tuple(X x, Y y) { 
		this.x = x; 
		this.y = y; 
	} 
	
	public boolean equals(Object o) {
		if (!(o instanceof Tuple<?, ?>))
			return false;
		else {
			@SuppressWarnings("unchecked")
			Tuple<X, Y> tup = (Tuple<X, Y>) o;
			return ((tup.x == x) && (tup.y == y));
		}
	}
	
	public String toString() {
		return String.format("<%s, %s>", x.toString(), y.toString());
	}
} 
