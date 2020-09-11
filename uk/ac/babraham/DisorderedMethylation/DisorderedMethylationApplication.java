package uk.ac.babraham.DisorderedMethylation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public class DisorderedMethylationApplication {

	private HashMap<String, QuantifiedPosition> cpgs = new HashMap<String, QuantifiedPosition>();
	
	public DisorderedMethylationApplication (File infile, File outfile) {
		
		parseBAM(infile);
		printResults(outfile);
		
	}
	
	private void printResults(File outfile) {
		try {
			PrintWriter pr = new PrintWriter(outfile);
			
			String [] headers = new String [] {"chr","pos","meth_count","unmeth_count","concordant","mixed"};
			pr.println(String.join("\t", headers));
			
			
			QuantifiedPosition [] all_positions = cpgs.values().toArray(new QuantifiedPosition[0]);
			
			Arrays.sort(all_positions);
			
			for (QuantifiedPosition qp : all_positions) {
				
				// Check it's well enough observed to be kept
				if (qp.total_count() < DisorderedMethylationPreferences.getInstance().minObservationsPerCpG()) continue;
				
				// It's good enough so we can print the details of this.
				StringBuffer sb = new StringBuffer();
				sb.append(qp.chr());
				sb.append("\t");
				sb.append(qp.pos());
				sb.append("\t");
				sb.append(qp.meth_count());
				sb.append("\t");
				sb.append(qp.unmeth_count());
				sb.append("\t");
				sb.append(qp.concordant_count());
				sb.append("\t");
				sb.append(qp.mixed_count());

				pr.println(sb.toString());

			}
			
			pr.close();
			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	private void parseBAM(File bamFile) {
		SamReader sam = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(bamFile);
		SAMRecordIterator sam_it = sam.iterator();
		
		int count = 0;
		
		while (sam_it.hasNext()) {
		
			++count;
			if (count % 1000000 == 0) {
				System.err.println("Read "+count+" alignments");
			}
			
			SAMRecord r = sam_it.next();
						
			String methString = r.getAttribute("XM").toString();

			int meth_count = 0;
			int unmeth_count = 0;

			HashMap<Integer, Boolean> cpg_positions = new HashMap<Integer,Boolean>();
						
			String chr = r.getReferenceName();
			
			// We use bare chromosome names
			if (chr.startsWith("chr")) {
				chr = chr.substring(3);
			}
				
			// Find the base where the SNP overlaps
			for (AlignmentBlock b : r.getAlignmentBlocks()) {
				
				for (int blockPos = 0; blockPos < b.getLength(); blockPos++) {
					// See if this is a CpG
					int readIndex = (b.getReadStart()-1)+blockPos;
					
//					System.out.println("Read start="+b.getReadStart()+" offset="+blockPos+" stringLen="+methString.length());
					// TODO: Do we need to do something different if the mapping is reversed?
					int genomicPos = b.getReferenceStart()+blockPos;
					if (methString.charAt(readIndex) == 'Z' ) {
						cpg_positions.put(genomicPos, true);
						++meth_count;
					}
					else if (methString.charAt(readIndex) == 'z' ) {
						cpg_positions.put(genomicPos, false);
						++unmeth_count;
					}
				}				
			}
			
			// See if we have enough data to include this read
			if (meth_count + unmeth_count < DisorderedMethylationPreferences.getInstance().minCpGsPerRead()) continue;
			
			// Now we can work out which category this read is in and increment
			// the appropriate counts for the CpGs.
			
			boolean consistent = false;
			
			if (meth_count == 0 || unmeth_count == 0) {
				consistent = true;
			}
			
			Iterator<Integer> it = cpg_positions.keySet().iterator();
			
			while (it.hasNext()) {
				int pos = it.next();
				String posString = chr+":"+pos;
				
				if (!cpgs.containsKey(posString)) {
					cpgs.put(posString,new QuantifiedPosition(chr,pos));
				}
				
				// Now we increment the value
				cpgs.get(posString).add_observation(cpg_positions.get(pos), consistent);
			}	
		}
	}
	
	
	public static void main(String[] args) {
		File infile = new File(args[0]);
		File outfile = new File(args[1]);

		new DisorderedMethylationApplication(infile, outfile);
	}

}
