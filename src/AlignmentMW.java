import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AlignmentMW {

    private String s1;
    private String s2;
    private int scoreMatch;
    private int scoreMismatch;
    private int scoreIndel;
    private int[][] alignmentMatrix;

    /**
     * Cree une matrice d'alignement d'apres deux sequences et la remplit.
     *
     * @param s1 Premiere sequence pour la matrice
     * @param s2 Deuxieme sequence pour la matrice
     */
    public AlignmentMW(Sequence s1, Sequence s2) {
        this.scoreMatch = 5;
        this.scoreMismatch = -4;
        this.scoreIndel = -3;
        this.s1 = s1.seq;
        this.s2 = s2.seq;
        this.alignmentMatrix = new int[this.s1.length() + 1][this.s2.length() + 1];
        this.fillMatrix();
    }

    /**
     * Cree une matrice de substitution d'apres un fichier. Fonctionne avec les fichiers NUC.4.4 et BLOSUM65 fournis
     * dans le dossier /data.
     *
     * @param f Nom du fichier contenant la matrice
     * @throws FileNotFoundException Si le fichier n'a pas ete trouve.
     */
    public AlignmentMW(File f) throws FileNotFoundException {
        this.s2 = "";
        this.scoreMatch = 5;
        this.scoreMismatch = -4;
        this.scoreIndel = -3;

        Scanner scanner = new Scanner(f);
        String currLine;
        int i = 0;
        int j = 0;

        // On scanne une premiere fois le fichier afin de creer le tableau contenant la matrice.
        while (scanner.hasNextLine()) {
            currLine = scanner.nextLine();
            if (!currLine.startsWith("#")) {
                if (i == 0) {
                    i = currLine.replaceAll("\\s+", "").length();
                } else if (currLine.matches("^[A-Z| *].*")) {
                    j++;
                }
            }
        }

        scanner.close();
        scanner = new Scanner(f);
        this.alignmentMatrix = new int[i][j];
        i = 0;

        // On rescanne ensuite le fichier afin de remplir la matrice.
        while (scanner.hasNextLine()) {
            currLine = scanner.nextLine();
            if (!currLine.startsWith("#")) {
                if (currLine.startsWith(" ")) {
                    this.s1 = currLine.replaceAll("\\s", "");
                } else {
                    currLine = currLine.replaceAll("\\s+", ":");
                    String[] res = currLine.split(":");

                    this.s2 += res[0];
                    for (int k = 1; k < res.length; k++) {
                        if (res[k] != "") {
                            this.alignmentMatrix[i][k - 1] = Integer.parseInt(res[k]);
                        }
                    }
                    i++;
                }
            }
        }
        scanner.close();
    }

    /**
     * Fonction permettant de remplir la matrice d'alignement d'apres les regles suivantes :
     * https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm
     */
    private void fillMatrix() {
        this.alignmentMatrix[0][0] = 0;

        for (int i = 1; i < this.s1.length() + 1; i++) {
            this.alignmentMatrix[i][0] = this.alignmentMatrix[i - 1][0] + this.scoreIndel;
        }

        for (int i = 1; i < this.s2.length() + 1; i++) {
            this.alignmentMatrix[0][i] = this.alignmentMatrix[0][i - 1] + this.scoreIndel;
        }

        for (int i = 1; i <= this.s1.length(); i++) {
            for (int j = 1; j <= this.s2.length(); j++) {
                if (i == j) {
                    if (this.s1.charAt(i - 1) == this.s2.charAt(j - 1)) {
                        this.alignmentMatrix[i][j] = this.alignmentMatrix[i - 1][j - 1] + this.scoreMatch;
                    } else {
                        this.alignmentMatrix[i][j] = this.alignmentMatrix[i - 1][j - 1] + this.scoreMismatch;
                    }
                } else if (i < j && this.s1.charAt(i - 1) == this.s2.charAt(j - 1)) {
                    this.alignmentMatrix[i][j] = this.alignmentMatrix[i - 1][j] + this.scoreIndel;
                } else {
                    this.alignmentMatrix[i][j] = this.alignmentMatrix[i][j - 1] + this.scoreIndel;
                }
            }
        }
    }

    /**
     * Permet d'afficher la matrice de façon a peu pres jolie.
     */
    public void printMatrix() {
        int spaceSepX = 2;

        for (int i = 0; i < this.alignmentMatrix.length; i++) {
            System.out.println();

            for (int j = 0; j < this.alignmentMatrix[i].length; j++) {
                System.out.print("|");
                int len = Integer.toString(this.alignmentMatrix[i][j]).length();

                for (int k = 0; k < (3 + spaceSepX) - len; k++) {
                    System.out.print(" ");
                }

                System.out.print(alignmentMatrix[i][j]);
            }
            System.out.print("|");
        }
        System.out.println();
    }

    /**
     * Permet de calculer le score de l'alignement des deux sequences tel que fixe par le sujet.
     *
     * @return Le score entre les deux sequences
     */
    private int getScore() {
        int count = 0;
        int countMax = 0;

        for (int i = 1; i <= this.s1.length(); i++) {
            for (int j = 1; j <= this.s2.length(); j++) {
                if (i == j) {

                    if (this.alignmentMatrix[i][j] == this.alignmentMatrix[i - 1][j - 1] + this.scoreMatch) {
                        count += 1;
                        if (count > countMax)
                            countMax = count;
                    }
                    if (!(this.alignmentMatrix[i][j] == this.alignmentMatrix[i - 1][j - 1] + this.scoreMatch)) {
                        count = 0;
                    }
                }
            }
        }
        return countMax;
    }

    /**
     * Permet de calculer le score maximum de l'alignement tel que fixe par le sujet.
     *
     * @return Score maximal de l'alignement
     */
    private int getScoreMax() {
        return this.scoreMatch * Math.min(this.s1.length(), this.s2.length());
    }

    /**
     * Permet de calculer le score minimal de l'alignement tel que fixe par le sujet.
     *
     * @return Score minimal de l'alignement
     */
    private int getScoreMin() {
        return this.scoreIndel * (this.s1.length() + this.s2.length());
    }

    /**
     * Permet de calculer la distance entre les deux sequences de la matrice d'alignement.
     *
     * @return la distance entre les deux sequences
     */
    public double getDistance() {
        return (this.getScoreMax() - this.getScore()) / (double) (this.getScoreMax() - this.getScoreMin());
    }

    public static void main(String[] argc) {
        Sequence seq1 = new Sequence("ATTACG");
        Sequence seq2 = new Sequence("ATATCG");
        Sequence seq3 = new Sequence("ACCCCG");
        Sequence seq4 = new Sequence("GGGGAA");
        Sequence seq5 = new Sequence("TTTACG");

        AlignmentMW seq12 = new AlignmentMW(seq1, seq2);
        AlignmentMW seq13 = new AlignmentMW(seq1, seq3);
        AlignmentMW seq23 = new AlignmentMW(seq2, seq3);

        try {
            AlignmentMW nuc = new AlignmentMW(new File(System.getProperty("user.dir") + "/data/NUC.4.4"));
            AlignmentMW blossum = new AlignmentMW(new File(System.getProperty("user.dir") + "/data/BLOSUM65"));
            System.out.println("Importation des matrices de substitution termine.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Matrice d'alignement " + seq12.s1 + " et " + seq12.s2);
        seq12.printMatrix();
        System.out.println();

        System.out.println("Matrice d'alignement " + seq13.s1 + " et " + seq13.s2);
        seq13.printMatrix();
        System.out.println();

        System.out.println("Matrice d'alignement " + seq23.s1 + " et " + seq23.s2);
        seq23.printMatrix();
        System.out.println();

        System.out.println("l'alignement est :" + seq12.getDistance());
        System.out.println("l'alignement est :" + seq13.getDistance());
        System.out.println("l'alignement est :" + seq23.getDistance());

        System.out.println("GetScoreMin seq1 et 2 : " + seq12.getScoreMin());
        System.out.println("GetScoreMax seq1 et 2 : " + seq12.getScoreMax());
    }
}