import java.util.ArrayList;

public class ClusterOfSequencesBis {

    private ArrayList<ClusterOfSequencesBis> subClusters;
    private Sequence element;

    /**
     * Constructeur par defaut.
     */
    public ClusterOfSequencesBis() {
        this.subClusters = new ArrayList<>();
    }

    /**
     * Construit un ClusterOfSequencesBis d'apres la sequence en parametre
     *
     * @param element Sequence
     */
    public ClusterOfSequencesBis(Sequence element) {
        this.subClusters = new ArrayList<>();
        this.element = element;
    }

    /**
     * Construit un ClusterOfSequenceBis d'apres les deux clusters en parametre qui deviennent ses sous clusters.
     *
     * @param cluster1 Cluster
     * @param cluster2 Cluster
     */
    public ClusterOfSequencesBis(ClusterOfSequencesBis cluster1, ClusterOfSequencesBis cluster2) {
        this.subClusters = new ArrayList<>();
        this.subClusters.add(cluster1);
        this.subClusters.add(cluster2);
    }

    /**
     * Fonction recursive permettant la construction de la chaine au format Newick.
     *
     * @return String au format Newick.
     */
    private String getNewickIntermediate() {
        String newick = "";

        if (!this.subClusters.isEmpty()) {
            newick += "(";
            for (int i = 0; i < this.subClusters.size(); i++) {
                newick += this.subClusters.get(i).getNewickIntermediate();
                if (i == 0) {
                    newick += ",";
                }
            }
            newick += ")";
        } else if (this.element != null) {
            newick = this.element.getSeq();
        }

        return newick;
    }

    /**
     * Renvoie le cluster sous format Newick.
     *
     * @return String au format Newick.
     */
    public String getNewick() {
        return this.getNewickIntermediate() + ";";
    }

    /**
     * Fonction recursive permettant de collecter toutes les sequences d'un cluster
     *
     * @return Une liste contenant toutes les sequences d'un cluster
     */
    private ArrayList<Sequence> getAllSeq() {
        ArrayList<Sequence> list = new ArrayList<>();

        if (this.element == null) {
            list.addAll(this.subClusters.get(0).getAllSeq());
            list.addAll(this.subClusters.get(1).getAllSeq());
        } else {
            list.add(this.element);
        }

        return list;
    }

    /**
     * Permet de connaitre la distance entre deux clusters en calculant la distance entre chaque sequence
     *
     * @param aCluster Cluster avec lequel on calcule la distance
     * @return double correspondant a la distance
     */
    public double linkage(ClusterOfSequencesBis aCluster) {
        double a = 0;
        int count = 0;

        for (Sequence seq : this.getAllSeq()) {
            for (Sequence subSeq : aCluster.getAllSeq()) {
                a += seq.distance(subSeq);
                count++;
            }
        }

        return a / count;
    }

    /**
     * Permet de connaitre les deux clusters les plus proches dans une liste de clusters.
     *
     * @return Un nouveau cluster constitue des deux clusters les plus proches.
     */
    private ClusterOfSequencesBis getClosest() {
        ArrayList<ClusterOfSequencesBis> tmp = new ArrayList<>();
        double distMin = 1;

        for (ClusterOfSequencesBis cl1 : this.subClusters) {
            for (ClusterOfSequencesBis cl2 : this.subClusters) {
                if (cl1 != cl2) {
                    double dist = cl1.linkage(cl2);

                    if (dist <= distMin) {
                        distMin = dist;
                        tmp.clear();
                        tmp.add(cl1);
                        tmp.add(cl2);
                    }
                }
            }
        }
        this.subClusters.removeAll(tmp);
        return new ClusterOfSequencesBis(tmp.get(0), tmp.get(1));
    }

    /**
     * Clusterize le cluster actuel de façon agglomerative.
     * @param elements la liste de sequence a utiliser pour clusteriser.
     */
    public void clusterize(ArrayList<Sequence> elements) {
        for (Sequence el : elements) {
            this.subClusters.add(new ClusterOfSequencesBis(el));
        }
        while (this.subClusters.size() > 2) {
            this.subClusters.add(getClosest());
        }
    }

    /**
     * Permet de construire une liste de sequence de taille nb consituee des sequences les plus proches de la liste
     * passee en parametre
     *
     * @param list Liste de sequence sur laquelle se baser
     * @param nb   Nombre de sequence dans la liste a renvoyer
     * @return La liste de sequence de taille nb la plus proche d'apres la liste passee en parametre
     */
    private ArrayList<Sequence> getClosestSeq(ArrayList<Sequence> list, int nb) {

        ArrayList<Sequence> tmp = new ArrayList<>(list);
        ArrayList<Sequence> l1 = new ArrayList<>();
        ArrayList<Sequence> l2 = new ArrayList<>();
        double distMin = 1;

        // On cherche les deux sequences de la liste les plus proches puis on les retire de la liste
        for (Sequence el1 : tmp) {
            for (Sequence el2 : tmp) {
                if (el1 != el2) {
                    double dist = el1.distance(el2);

                    if (dist <= distMin) {
                        distMin = dist;
                        l1.clear();
                        l1.add(el1);
                        l1.add(el2);
                    }
                }
            }
        }
        tmp.removeAll(l1);

        /*
         *  Tant que la liste a renvoyer n'est pas a la taille definie, on continue a la remplir avec l'element le plus
         *  proche de la liste tampon
         */
        while (l1.size() < nb) {
            distMin = 1;
            for (Sequence el : tmp) {
                double dist = (new ClusterOfSequences(l1)).linkage(new ClusterOfSequences(el));

                if (dist <= distMin) {
                    distMin = dist;
                    l2.clear();
                    l2.add(el);
                }
            }
            l1.addAll(l2);
            tmp.removeAll(l2);
            l2.clear();
        }

        return l1;
    }

    /**
     * Clusterize le cluster actuel de façon divisive.
     * @param elements la liste de sequence a utiliser pour clusteriser.
     */
    public void clusterizeDivisive(ArrayList<Sequence> elements) {
        ArrayList<Sequence> tmp2;

        if (elements.size() == 2) { // S'il y a deux elements, on cree deux sous clusters d'apres les sequences.
            for (Sequence el : elements) {
                this.subClusters.add(new ClusterOfSequencesBis(el));
            }
        } else if (elements.size() == 1) { // S'il n'y a qu'un seul element, on l'assigne au cluster courant.
            this.element = elements.get(0);
        } else if (elements.size() > 2) {
            ArrayList<Sequence> tmp = this.getClosestSeq(elements, elements.size() / 2);

            if (elements.size() >= 5 && elements.size() % 2 == 1) {
                // S'il y a 5 elements ou plus et que le nombre est impair, on doit creer une autre liste avec les
                // autres elements de taille de la liste divisee par deux pour isoler l'element impair et le placer dans
                // la liste ou il sera le plus proche puis creer les subclusters.
                ArrayList<Sequence> alone = new ArrayList<>(elements);
                alone.removeAll(tmp);
                tmp2 = this.getClosestSeq(alone, alone.size() - 1);
                alone.removeAll(tmp2);

                if ((new ClusterOfSequences(tmp).linkage(new ClusterOfSequences(alone))) <
                        (new ClusterOfSequences(tmp2).linkage(new ClusterOfSequences(alone)))) {
                    tmp.addAll(alone);
                } else {
                    tmp2.addAll(alone);
                }
            } else {
                tmp2 = elements;
                tmp2.removeAll(tmp);
            }
            this.subClusters.add(new ClusterOfSequencesBis());
            this.subClusters.add(new ClusterOfSequencesBis());
            this.subClusters.get(0).clusterizeDivisive(tmp);
            this.subClusters.get(1).clusterizeDivisive(tmp2);
        }
    }

    public static void main(String[] args) {
        Sequence seq1 = new Sequence("ATTACG");
        Sequence seq2 = new Sequence("ATATCG");
        Sequence seq3 = new Sequence("ACCCCG");
        Sequence seq4 = new Sequence("GCCGAG");
        Sequence seq5 = new Sequence("TCCCCG");

        ClusterOfSequencesBis cl1 = new ClusterOfSequencesBis(seq1);
        ClusterOfSequencesBis cl2 = new ClusterOfSequencesBis(seq2);
        ClusterOfSequencesBis cl3 = new ClusterOfSequencesBis(seq3);
        ClusterOfSequencesBis cl4 = new ClusterOfSequencesBis(seq4);
        ClusterOfSequencesBis cl5 = new ClusterOfSequencesBis(seq5);

        ArrayList<Sequence> listSeq = new ArrayList<>(5);
        listSeq.add(seq1);
        listSeq.add(seq2);
        listSeq.add(seq3);
        listSeq.add(seq4);
        listSeq.add(seq5);

        ClusterOfSequencesBis bioCluster = new ClusterOfSequencesBis();
        ClusterOfSequencesBis cl6 = new ClusterOfSequencesBis(cl3, cl5);
        ClusterOfSequencesBis bioCluster2 = new ClusterOfSequencesBis();

        double t = System.nanoTime();
        bioCluster.clusterize(listSeq);
        double t2 = System.nanoTime();
        System.out.println("Newick de biocluster clustierized de façon agglomerative : " + bioCluster.getNewick());
        System.out.println("Temps d'exécution : " + (t2 - t));

        t = System.nanoTime();
        bioCluster2.clusterizeDivisive(listSeq);
        t2 = System.nanoTime();
        System.out.println("Newick de biocluster clustierized de façon divisive : " + bioCluster2.getNewick());
        System.out.println("Temps d'exécution : " + (t2 - t));

        System.out.println("Distance between cl1 and cl2 : " + cl1.linkage(cl2));
        System.out.println("Distance between cl1 and cl3 : " + cl1.linkage(cl3));
        System.out.println("Distance between cl2 and cl1 : " + cl2.linkage(cl1));
        System.out.println("Distance between cl2 and cl3 : " + cl2.linkage(cl3));
        System.out.println("Distance between cl3 and cl1 : " + cl3.linkage(cl1));
        System.out.println("Distance between cl3 and cl2 : " + cl3.linkage(cl2));
        System.out.println("Distance between cl6 and cl4 : " + cl6.linkage(cl4));
    }
}