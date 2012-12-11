package mikera.indexz.impl;


public class SequentialIndex extends ComputedIndex {
	private static final long serialVersionUID = 8586796655048075367L;

	private final int start;
	
	public SequentialIndex(int start, int length) {
		super(length);
		this.start=start;
	}
	
	@Override
	public int get(int i) {
		assert((i>=0)&&(i<length));
		return start+i;
	}
}
