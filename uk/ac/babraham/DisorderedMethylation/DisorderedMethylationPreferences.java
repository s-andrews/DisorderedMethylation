package uk.ac.babraham.DisorderedMethylation;

public class DisorderedMethylationPreferences {

	private int minObservationsPerCpG = 5;
	private int minCpGsPerRead = 4;

	private static DisorderedMethylationPreferences prefs = new DisorderedMethylationPreferences();
	
	private DisorderedMethylationPreferences() {}
	
	public static DisorderedMethylationPreferences getInstance() {
		return prefs;
	}
	
	public int minObservationsPerCpG () {
		return minObservationsPerCpG;
	}
	
	public int minCpGsPerRead () {
		return minCpGsPerRead;
	}
	
}
