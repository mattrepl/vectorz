package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Base class for strided vectors backed with a double array and fixed offset / stride
 * @author Mike
 *
 */
public abstract class BaseStridedVector extends AStridedVector {
	private static final long serialVersionUID = 7038506080494281379L;

	protected BaseStridedVector(int length, double[] data, int offset, int stride) {
		super(length, data);
		this.stride=stride;
		this.offset=offset;
		if ((offset<0)) throw new IndexOutOfBoundsException();
		if (length>0) {
			// check if last element is in the array
			int lastOffset=(offset+(length-1)*stride);
			if ((lastOffset>=data.length)||(lastOffset<0)) throw new IndexOutOfBoundsException("StridedVector ends outside array");
		}
	}

	protected final int stride;
	protected final int offset;

	@Override
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int getStride() {
		return stride;
	}

	@Override
	protected final int index(int i) {
		return offset+i*stride;
	}

	@Override
	public final double get(int i) {
		checkIndex(i);
		return data[offset+i*stride];
	}
	
	@Override
	public final double unsafeGet(int i) {
		return data[offset+i*stride];
	}
	
	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[offset+i*stride]=value;
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[offset+i*stride]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[offset+i*stride]+=value;
	}
	
	@Override
	public double dotProduct(double[] ds, int off) {
		return DoubleArrays.dotProduct(ds, off, data, offset, stride, length);
	}
	
	@Override
	public double dotProduct(double[] ds, int doffset, int dstride) {
		return DoubleArrays.dotProduct(data, offset, stride, ds, doffset, dstride, length);
	}
	
	@Override
	public void set(AVector v) {
		int length=checkSameLength(v);
		v.copyTo(0, data, offset, length, stride);
	}
	
	@Override
	public double dotProduct(AVector v) {
		checkLength(v.length());
		return v.dotProduct(getArray(), getArrayOffset(), getStride());
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]=data[offset+(i*stride)];
		}
	}
	
	@Override
	public void validate() {
		if (length>0) {
			if ((offset<0)||(offset>=data.length)) throw new VectorzException("offset out of bounds: "+offset);
			int lastIndex=offset+(stride*(length-1));
			if ((lastIndex<0)||(lastIndex>=data.length)) throw new VectorzException("lastIndex out of bounds: "+lastIndex);
		}
		
		super.validate();
	}
}