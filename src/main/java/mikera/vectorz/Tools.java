package mikera.vectorz;

public final class Tools {
	public static void debugBreak(Object o) {
		o.toString();
	}
	
	/**
	 * Hashcode for an int, defined as the value of the int itself for consistency with java.lang.Integer
	 * 
	 * @param value
	 * @return
	 */
	public static int hashCode(int value) {
		return value;
	}
	
	
	/** 
	 * Hashcode for a double primitive
	 * 
	 * @param d
	 * @return
	 */
	public static int hashCode(double d) {
		return hashCode(Double.doubleToLongBits(d));
	}
	
	/**
	 * Hashcode for a long primitive
	 * @param l
	 * @return
	 */
	public static int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}

	public static double toDouble(Object object) {
		if (object instanceof Double) {
			return (Double)object;
		} else if (object instanceof Number) {
			return ((Number)object).doubleValue();
		} else {
			throw new IllegalArgumentException("Cannot convert to double: "+object.toString());
		}
	}

	/**
	 * Tests if two double values are approximately equal
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean epsilonEquals(double a, double b) {
		return epsilonEquals(a,b,Vectorz.TEST_EPSILON);
	}
	
	public static boolean epsilonEquals(double a, double b,double tolerance) {
		double diff=a-b;
		if ((diff>tolerance)||(diff<-tolerance)) return false;
		return true;
	}

	
}
