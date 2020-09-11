package uk.ac.babraham.DisorderedMethylation;

public class QuantifiedPosition implements Comparable<QuantifiedPosition> {

	private String chr;
	private int pos;
	
	private int meth_count = 0;
	private int unmeth_count = 0;
	private int concord_meth_count = 0;
	private int conconrd_unmeth_count = 0;
	private int mixed_count = 0;
	
	public QuantifiedPosition(String chr, int pos) {
		this.chr = chr;
		this.pos = pos;
	}
	
	public String chr () {
		return chr;
	}
	
	public int pos () {
		return pos;
	}
	
	public void add_observation(boolean methylated, boolean consistent) {
		if (methylated) {
			++meth_count;
			if (consistent) {
				++concord_meth_count;
			}
			else {
				++mixed_count;
			}
		}
		else {
			++unmeth_count;
			if (consistent) {
				++conconrd_unmeth_count;
			}
			else {
				++mixed_count;
			}
		}
	}
	
	public int meth_count () {
		return meth_count;
	}
	
	public int unmeth_count () {
		return unmeth_count;
	}
	
	public int concordant_count () {
		return conconrd_unmeth_count + concord_meth_count;
	}
	
	public int mixed_count () {
		return mixed_count;
	}
	
	public int total_count () {
		return meth_count+unmeth_count;
	}

	@Override
	public int compareTo(QuantifiedPosition qp) {
		if (this.chr.equals(qp.chr)) {
			return pos - qp.pos;
		}
		else {
			return (this.chr.compareTo(qp.chr));
		}
	}
	
	
	
}
