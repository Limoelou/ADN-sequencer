public class Sequence {

	protected String seq;

	/**
	 * Constructeur de base.
	 */
	public Sequence() {
	}

	/**
	 * Constructeur d'apres une sequence sous forme de string.
	 * @param s sequence
	 */
	public Sequence(String s) {
		this.seq = s;
	}

	/**
	 * Constructeur d'apres une autre instance de Sequence.
	 * @param s Sequence
	 */
	public Sequence(Sequence s) {
		this.seq = s.seq;
	}

	/**
	 * Get la sequence sous forme de string.
	 * @return sequence
	 */
	public String getSeq() {
		return this.seq;
	}

	/**
	 * Retourne la sequence sous forme de string.
	 * @return seq
	 */
	public String toString() {
		return this.seq;
	}

	/**
	 * Permet de calculer la distance entre deux sequences sous forme de double.
	 * @param otherSeq Sequence avec laquelle on calcule la distance.
	 * @return double correspondant a la distance entre les sequences.
	 */
	public double distance(Sequence otherSeq) {
		String s = otherSeq.getSeq();
		String seq = this.getSeq();

		int length = Math.min(s.length(), seq.length());
		int sum = 0;

		for (int i = 0; i < length; i++) {
			if (seq.charAt(i) != s.charAt(i)) {
				sum ++;
			}
		}

		sum += Math.abs(seq.length()-s.length());

		double a = (double)sum/(double)length;
		return a;
	}

	public static void main(String[] args) {
		Sequence seq1 = new Sequence("ATTACG");
		Sequence seq2 = new Sequence("ATATCG");
		Sequence seq3 = new Sequence("ACCCCG");
		Sequence seq4 = new Sequence("GGGGAA");
		Sequence seq5 = new Sequence("TTTACG");

		System.out.println("dist(seq1,seq1): " + seq1.distance(seq1));
		System.out.println("dist(seq1,seq2): " + seq1.distance(seq2));
	    System.out.println("dist(seq2,seq1): " + seq2.distance(seq1));
	    System.out.println("dist(seq1,seq3): " + seq1.distance(seq3));
	    System.out.println("dist(seq2,seq3): " + seq2.distance(seq3));
	    System.out.println("dist(seq1,seq4): " + seq1.distance(seq4));
	}
}


