import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


/**
 * Set of utility functions for processing sequence data.
 * 
 * @author Olivier Dameron
 *
 */
public class Utils {
	
	
	/**
	 * Returns the first sequence from a fasta file.
	 * 
	 * Todo: check whether this sequence is coding(?)
	 * 
	 * @param f file containing sequence(s) in fasta format
	 * @return first sequence in the file
	 * @throws FileNotFoundException
	 * 
	 */
	public static String readFasta(File f) throws FileNotFoundException {
		  Scanner scanner = new Scanner(f);
		  String currentLine;
		  String currentSeq = "";
		  while (scanner.hasNextLine()) {
			  currentLine = scanner.nextLine();
			  if (currentLine.startsWith(">")) {
				  currentSeq = "";
			  }
			  else if (currentLine.equals("")) {
				  if (! currentSeq.equals("")) {
					  break;
				  }
			  }
			  else {
				  currentSeq += currentLine;
			  }
		  }
		  scanner.close();
		  return currentSeq;		
	}

	/**
	 * Function for extracting the taxon from an entry in the samples table.
	 * 
	 * @param sample (string composed of the name of the taxon (may contain spaces), a space, an open parenthesis, a gene name, a closed parenthesis.
	 * @return taxon
	 */
	public static String getTaxon(String sample) {
		int parenthesisIndex = sample.indexOf('(');
		return sample.substring(0, parenthesisIndex-1);
	}
	
	/**
	 * Function for extracting the gene name from an entry in the samples table.
	 * 
	 * @param sample (string composed of the name of the taxon (may contain spaces), a space, an open parenthesis, a gene name, a closed parenthesis.
	 * @return gene name
	 */
	public static String getGeneName(String sample) {
		int parenthesisIndex = sample.indexOf('(');
		return sample.substring(parenthesisIndex+1,sample.length()-1);
	}
	
	/**
	 * converts a DNA codon into an amino-acid.
	 * 
	 * from https://github.com/niemasd/Algorithm-Problem-Solutions/blob/master/ROSALIND%20Solutions/Bioinformatics%20Textbook%20Track/2A%20-%20Protein%20Translation%20Problem/ProteinTranslationProblem.java
	 * 
	 * @param codon
	 * @return single-letter string representing the amino-acid
	 */
	public static String codon2aa(String codon ) {
		switch(codon) {
		case "GCA": return "A";
		case "GCC": return "A";
		case "GCG": return "A";
		case "GCT": return "A";
		case "TGC": return "C";
		case "TGT": return "C";
		case "GAC": return "D";
		case "GAT": return "D";
		case "GAA": return "E";
		case "GAG": return "E";
		case "TTC": return "F";
		case "TTT": return "F";
		case "GGA": return "G";
		case "GGC": return "G";
		case "GGG": return "G";
		case "GGT": return "G";
		case "CAC": return "H";
		case "CAT": return "H";
		case "ATA": return "I";
		case "ATC": return "I";
		case "ATT": return "I";
		case "AAA": return "K";
		case "AAG": return "K";
		case "CTA": return "L";
		case "CTC": return "L";
		case "CTG": return "L";
		case "CTT": return "L";
		case "TTA": return "L";
		case "TTG": return "L";
		case "ATG": return "M";
		case "AAC": return "N";
		case "AAT": return "N";
		case "CCA": return "P";
		case "CCC": return "P";
		case "CCG": return "P";
		case "CCT": return "P";
		case "CAA": return "Q";
		case "CAG": return "Q";
		case "AGA": return "R";
		case "AGG": return "R";
		case "CGA": return "R";
		case "CGC": return "R";
		case "CGG": return "R";
		case "CGT": return "R";
		case "AGC": return "S";
		case "AGT": return "S";
		case "TCA": return "S";
		case "TCC": return "S";
		case "TCG": return "S";
		case "TCT": return "S";
		case "ACA": return "T";
		case "ACC": return "T";
		case "ACG": return "T";
		case "ACT": return "T";
		case "GTA": return "V";
		case "GTC": return "V";
		case "GTG": return "V";
		case "GTT": return "V";
		case "TGG": return "W";
		case "TAC": return "Y";
		case "TAT": return "Y";
		case "TGA": return "";
		case "TAA": return "";
		case "TAG": return "";
		default:    return "-1";
		}
	}
	
	/**
	 * converts a sequence of DNA nucleotides into the corresponding sequence of amino-acids
	 * 
	 * @param nuclSeq sequence of DNA nucleotides
	 * @return sequence of amino-acids
	 */
	public static String nucleotidesToAminoAcids(String nuclSeq) {
		String amino = "";
		for(int i = 0; i < nuclSeq.length(); i+=3)
		{
			amino += codon2aa(nuclSeq.substring(i, i+3));
		}
		return amino;
	}
	
	public static void main(String[] args) {
		System.out.println("Looking for files from: " + System.getProperty("user.dir"));
		try {
			/*
			 * DNA
			 */
			System.out.println();
			System.out.println("DNA sequences");
			LinkedList<String> samples = new LinkedList<String>();
			samples.add("Homo sapiens (HBA1)");
			samples.add("Pan paniscus (HBA1)");
			samples.add("Pan troglodytes (HBA1)");
			samples.add("Mus musculus (Hba_a1)");
			samples.add("Mus musculus (Hba_a2)");
			samples.add("Rattus norvegicus (Hba_a2-1)");
			samples.add("Rattus norvegicus (Hba_a2-2)");
			samples.add("Rattus norvegicus (Hba_a3)");
			samples.add("Felis catus (HBA1)");
			samples.add("Bos taurus (HBA)");
			samples.add("Danio rerio (hbaa1)");
			samples.add("Macaca mulatta (HBA2)");
			samples.add("Xenopus tropicalis (hba1)");
			int nbSamples = samples.size();
			
			ArrayList<Sequence> dataNucleotides = new ArrayList<Sequence>(nbSamples);
			String seqFilePath;
			for (String currentSample : samples) {
				seqFilePath = System.getProperty("user.dir") + "/data/" + Utils.getTaxon(currentSample).replace(' ', '_') + "_" + Utils.getGeneName(currentSample) + "_sequence.fa";
				dataNucleotides.add(new SequenceLabeled(new File(seqFilePath), Utils.getTaxon(currentSample) + " " + Utils.getGeneName(currentSample)));
			}
			ClusterOfSequences clusterHemoglobinNucleotides = new ClusterOfSequences(dataNucleotides);
			System.out.println("Hemo Newick : " + clusterHemoglobinNucleotides.getNewick());
			clusterHemoglobinNucleotides.clusterize();
			System.out.println("Hemo Newick clustered : " + clusterHemoglobinNucleotides.getNewick());
			
			
			/*
			 * PROTEINS
			 */
			System.out.println();
			System.out.println("PROTEIN sequences");
			ArrayList<Sequence> dataProteins = new ArrayList<Sequence>(nbSamples);
			for (Sequence dnaSeq: dataNucleotides) {
				System.out.println();
				System.out.println(((SequenceLabeled)dnaSeq).toString());
				System.out.println(((SequenceLabeled)dnaSeq).getSequence());
				System.out.println(Utils.nucleotidesToAminoAcids(((SequenceLabeled)dnaSeq).getSequence()));
				dataProteins.add(new SequenceLabeled(Utils.nucleotidesToAminoAcids(((SequenceLabeled)dnaSeq).getSequence()), ((SequenceLabeled)dnaSeq).toString()));
			}
			ClusterOfSequences clusterHemoglobinProteins = new ClusterOfSequences(dataProteins);
			System.out.println(clusterHemoglobinProteins.getNewick());
			clusterHemoglobinProteins.clusterize();
			System.out.println(clusterHemoglobinProteins.getNewick());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
