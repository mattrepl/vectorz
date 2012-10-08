package mikera.vectorz;

import static org.junit.Assert.*;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.IndexedArrayVector;
import mikera.vectorz.impl.IndexedSubVector;

import org.junit.Test;


public class TestVectors {
	@Test public void testSubVectors() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) data[i]=i;
		
		ArraySubVector v=new ArraySubVector(data);
		assertEquals(10,v.get(10),0.0);
		assertTrue(v.isReference());
		
		ArraySubVector v2=v.subVector(5, 90);
		assertEquals(90,v2.length());
		assertEquals(15,v2.get(10),0.0);
		assertTrue(v2.isReference());
		
		ArraySubVector v3=v2.subVector(5,80);
		assertEquals(20,v3.get(10),0.0);
		assertTrue(v3.isReference());
		
		v3.set(10, -99);
		assertEquals(-99,v.get(20),0.0);
	}
	
	@Test public void testWrap() {
		double[] data=new double[]{1,2,3};
		
		Vector v=Vector.wrap(data);
		data[0]=13;
		assertEquals(13,v.get(0),0.0);
	}
	
	@Test public void testSubVectorCopy() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) data[i]=i;
		ArraySubVector v=new ArraySubVector(data);	
		
		assertEquals(Arrays.hashCode(data),v.hashCode());
		
		ArraySubVector v2=new ArraySubVector(v);
		assertEquals(v,v2);
		assertTrue(v2.isReference());
		
		v.set(10,Math.PI);
		assertTrue(!v.equals(v2));
	}
	
	@Test public void testString() {
		assertEquals("[0.0,0.0]",new Vector2().toString());
		assertEquals("[1.0,2.0]",Vectorz.create(1,2).toString());
	}
	
	private void testParse(AVector v) {
		assertEquals(v,Vectorz.parse(v.toString()));
	}

	
	private void testClone(AVector v) {
		AVector cv=v.clone();
		int len=cv.length();
		assertEquals(v.length(), len);
		assertFalse(cv.isReference());
		assertTrue((cv.length()==0)||cv.isMutable());		
		assertEquals(v,cv);
		
		for (int i=0; i<len; i++) {
			double x=cv.get(i);
			// clone should have equal values
			assertEquals(v.get(i),x,0.0000001);
			cv.set(i, x+1);
			
			// changing clone should not change original
			assertNotSame(v.get(i),cv.get(i));
		}
	}
	
	public void testOutOfBounds(AVector v) {
		try {
			v.set(-1, 0.0);
			fail("Should be out of bounds!");
		} catch (IndexOutOfBoundsException x) {
			// OK!
		}
		
		try {
			v.set(v.length(), 0.0);
			fail("Should be out of bounds!");
		} catch (IndexOutOfBoundsException x) {
			// OK!
		}
	}
	
	public void testSubVectorMutability(AVector v) {
		// defensive copy
		v=v.clone();
		assertTrue(!v.isReference());
		
		int vlen=v.length();
		
		int start=Math.min(vlen/2,vlen-1);
		
		AVector s1 = v.subVector(start, vlen-start);
		AVector s2 = v.subVector(start, vlen-start);
		
		assertTrue(s1.isReference());
		assertNotSame(s1,s2);
		
		int len=s1.length();
		for (int i=0; i<len; i++) {
			double x=s2.get(i);
			// clone should have equal values
			assertEquals(s1.get(i),x,0.0000001);
			s1.set(i, x+1);
			
			// change should be reflected in both subvectors
			assertEquals(s1.get(i),s2.get(i),0.0000001);
		}
	}
	
	
	private void testZero(AVector v) {
		v=v.clone();
		v.multiply(0.0);
		assertTrue(v.isZeroVector());
	}
	

	private void testNormalise(AVector v) {
		v=v.clone();
		v.set(0,v.get(0)+Math.random());
		v.normalise();
		assertTrue(v.isUnitLengthVector());
	}
	
	private void doNonDegenerateTests(AVector v) {
		if (v.length()==0) return;
		testSubVectorMutability(v);
		testNormalise(v);
	}
	
	private void doGenericTests(AVector v) {
		doNonDegenerateTests(v);
		testClone(v);
		testParse(v);
		testZero(v);
		testOutOfBounds(v);
	}

	@Test public void genericTests() {
		doGenericTests(new Vector3(1.0,2.0,3.0));
		doGenericTests(new Vector2(1.0,2.0));
		
		// zero-length Vectors
		doGenericTests(Vector.of());
		doGenericTests(Vector.wrap(new double[0]));
		doGenericTests(new Vector3(1.0,2.0,3.0).subVector(2, 0));
		
		for (int j=0; j<10; j++) {
			double[] data=new double[j];
			for (int i=0; i<j; i++) data[i]=i;
			doGenericTests(Vectorz.create(data));
			doGenericTests(new Vector(data));
		}
		
		double[] data=new double[100];
		int[] indexes=new int[100];
		for (int i=0; i<100; i++) {
			data[i]=i;
			indexes[i]=i;
		}

		doGenericTests(new ArraySubVector(data));
		doGenericTests(IndexedArrayVector.wrap(data,indexes));
		doGenericTests(IndexedSubVector.wrap(Vector.of(data),indexes));
		
		doGenericTests(new Vector(data).subVector(25, 50));
		doGenericTests(new ArraySubVector(data).subVector(25, 50));
		
		AVector v3 = new Vector3(1.0,2.0,3.0);
		doGenericTests(v3.subVector(1, 2));

		AVector joined = Vectorz.join(v3, Vectorz.create(data));
		doGenericTests(joined);
		
		AVector v4 = Vectorz.create(1.0,2.0,3.0,4.0);
		doGenericTests(v4);
		
		AMatrix m1=Matrixx.createRandomSquareMatrix(5);
		doGenericTests(m1.getRow(1));
		doGenericTests(m1.getColumn(1));
		
		AMatrix m2=Matrixx.createRandomSquareMatrix(3);
		doGenericTests(m2.getRow(1));
		doGenericTests(m2.getColumn(1));

		
	}
	
	@Test public void testDistances() {
		Vector3 v=Vector3.of(1,2,3);
		Vector3 v2=Vector3.of(2,4,6);
		assertEquals(v.magnitude(),v.distance(v2),0.000001);
		assertEquals(6,v.distanceL1(v2),0.000001);
		assertEquals(3,v.distanceLinf(v2),0.000001);
	}
	
	@Test public void testClamping() {
		Vector3 v=Vector3.of(1,2,3);
		v.clamp(1.5, 2.5);
		assertEquals(Vector3.of(1.5,2,2.5),v);
	}
	
	@Test public void testClampMin() {
		Vector3 v=Vector3.of(1,2,3);
		v.clampMin(1.5);
		assertEquals(Vector3.of(1.5,2,3),v);
	}
	
	@Test public void testClampMax() {
		Vector3 v=Vector3.of(1,2,3);
		v.clampMax(2.5);
		assertEquals(Vector3.of(1,2,2.5),v);
	}
}
