import java.util.ArrayList;

public class ClusterOfSequences {

    private ArrayList<ClusterOfSequences> subClusters;
    private ArrayList<Sequence> elements;

    /**
     * Construit un cluster a partir d'une sequence.
     *
     * @param element sequence
     */
    public ClusterOfSequences(Sequence element) {
        this.subClusters = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.elements.add(element);
    }

    /**
     * Construit un cluster a partir d'une liste de sequence.
     *
     * @param eltList liste de sequence
     */
    public ClusterOfSequences(ArrayList<Sequence> eltList) {
        this.subClusters = new ArrayList<>();
        this.elements = eltList;
    }

    /**
     * Construit un cluster a partir de deux autres clusters qui deviennent ses sous clusters.
     *
     * @param cluster1 Cluster a ajouter aux sous clusters
     * @param cluster2 Cluster a ajouter aux sous clusters
     */
    public ClusterOfSequences(ClusterOfSequences cluster1, ClusterOfSequences cluster2) {
        this.subClusters = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.subClusters.add(cluster1);
        this.subClusters.add(cluster2);
        this.elements.addAll(cluster1.elements);
        this.elements.addAll(cluster2.elements);
    }

    /**
     * Fonction recursive permettant la construction de la chaine au format Newick.
     *
     * @return String au format Newick.
     */
    private String getNewickIntermediate() {
        String newick = "";

        // S'il y a des sous clusters, on rappelle la fonction sur ces sous clusters tout en construisant la chaine.
        if (!this.subClusters.isEmpty()) {
            newick += "(";
            for (int i = 0; i < this.subClusters.size(); i++) {
                newick += this.subClusters.get(i).getNewickIntermediate();
                if (i == 0 && !this.subClusters.get(i).elements.isEmpty()) {
                    newick += ",";
                }
            }
            newick += ")";
        } else if (this.elements.size() == 1) { // On ajoute au newick la sequence.
            newick = this.elements.get(0).getSeq();
        } else { // Ce cas ci permet de gerer les clusters non clusterises et d'afficher les sequences presentes.
            newick += "(";
            for (int i = 0; i < this.elements.size(); i++) {
                newick += this.elements.get(i).getSeq();
                if (i < this.elements.size() - 1) {
                    newick += ",";
                }
            }
            newick += ")";
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
     * Permet de connaitre la distance entre deux clusters en calculant la distance entre chaque sequence
     *
     * @param aCluster Cluster avec lequel on calcule la distance
     * @return double correspondant a la distance
     */
    public double linkage(ClusterOfSequences aCluster) {
        double a = 0;
        int count = 0;

        // Pour chaque sequence, on calcule la distance avec chaque sequence de l'autre cluster.
        for (Sequence seq : this.elements) {
            for (Sequence subSeq : aCluster.elements) {
                a += seq.distance(subSeq);
                count++;
            }
        }

        // On retourne la moyenne des distances.
        return a / count;
    }

    /**
     * Permet de connaitre les deux clusters les plus proches dans une liste de clusters.
     *
     * @return Un nouveau cluster constitue des deux clusters les plus proches.
     */
    private ClusterOfSequences getClosest() {
        ArrayList<ClusterOfSequences> tmp = new ArrayList<>();
        double distMin = 1;

        for (ClusterOfSequences cl1 : this.subClusters) {
            for (ClusterOfSequences cl2 : this.subClusters) {
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
        return new ClusterOfSequences(tmp.get(0), tmp.get(1));
    }

    /**
     * Clusterize le cluster actuel de façon agglomerative.
     */
    public void clusterize() {
        // Pour chaque sequence on cree un cluster
        for (Sequence el : this.elements) {
            this.subClusters.add(new ClusterOfSequences(el));
        }
        /* On cree un nouveau cluster avec les deux clusters les plus proches tant qu'il reste plus de deux elements
           dans la liste de cluster. */
        while (this.subClusters.size() > 2) {
            this.subClusters.add(getClosest());
        }
    }

    /**
     * Fonction recursive permettant la construction de la chaine au format Newick de façon alignee et respectant
     * l'echelle. Prend en parametre la profondeur actuelle dans le cluster global, et la profondeur maximale.
     *
     * @param currDepth Profondeur actuelle dans le cluster de sequences
     * @param maxDepth  Profondeur maximale du cluster de sequences
     * @return String au format Newick
     */
    private String getNewickIntermediateAligned(int currDepth, int maxDepth) {
        String newick = "";
        // Meme fonctionnement que pour le getNewickIntermediate, on fait juste une boucle a divers endroit pour ajouter
        // des parentheses.

        if (!this.subClusters.isEmpty()) {
            newick += "(";
            if (this.elements.size() == 1) {
                for (int i = currDepth; i < maxDepth - 1; i++) {
                    newick += "(";
                }
                newick += this.elements.get(0).getSeq();
                for (int i = currDepth; i < maxDepth - 1; i++) {
                    newick += ")";
                }
                newick += ",";
            }
            for (int i = 0; i < this.subClusters.size(); i++) {
                newick += this.subClusters.get(i).getNewickIntermediateAligned(currDepth + 1, maxDepth);
                if (i < this.subClusters.size() - 1)
                    newick += ",";
            }
            newick += ")";
        } else {
            for (int i = currDepth; i < maxDepth; i++) {
                newick += "(";
            }
            for (int i = 0; i < this.elements.size(); i++) {
                newick += this.elements.get(i).getSeq();
                if (i < this.elements.size() - 1)
                    newick += ",";
            }
            for (int i = currDepth; i < maxDepth; i++) {
                newick += ")";
            }
        }
        return newick;
    }

    /**
     * Permet de connaitre la profondeur d'un cluster de sequences clusterise.
     *
     * @return Int correspondant a la profondeur d'un cluster de sequences
     */
    private int getDepth() {
        int maxDepth = 0;
        int tmp;

        if (!this.subClusters.isEmpty()) {
            for (ClusterOfSequences cl : this.subClusters) {
                tmp = cl.getDepth();
                if (tmp > maxDepth) {
                    maxDepth = tmp;
                }
            }
        }

        return maxDepth + 1;
    }

    /**
     * Renvoie le cluster sous forme de Newick avec toutes les feuilles alignees à droite. L'echelle est respectee.
     *
     * @return String au format Newick
     */
    public String getNewickAligned() {
        return this.getNewickIntermediateAligned(0, this.getDepth()) + ";";
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
     */
    public void clusterizeDivisive() {
        ArrayList<Sequence> tmp2;

        // S'il n'y a que deux elements, on cree deux sous clusters avec chacun une sequence
        if (this.elements.size() == 2) {
            for (Sequence el : this.elements) {
                this.subClusters.add(new ClusterOfSequences(el));
            }
        } else if (this.elements.size() > 2) {
            // S'il y a plus de deux elements on va chercher la liste d'elements la plus proche de la taille de notre
            // liste divisee par deux.
            ArrayList<Sequence> tmp = this.getClosestSeq(this.elements, this.elements.size() / 2);

            // S'il y a 5 elements ou plus et que le nombre est impair, on doit creer une autre liste avec les autres
            // elements de taille de la liste divisee par deux pour isoler l'element impair et le placer dans la liste
            // ou il sera le plus proche puis creer les subclusters.
            if (this.elements.size() >= 5 && this.elements.size() % 2 == 1) {
                ArrayList<Sequence> alone = new ArrayList<>(this.elements);
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
                // Sinon, on cree simplement la seconde liste d'apres la liste d'elements amputee de tmp.
                tmp2 = new ArrayList<>(this.elements);
                tmp2.removeAll(tmp);
            }
            // On cree les nouveaux clusters d'apres les listes obtenues
            this.subClusters.add(new ClusterOfSequences(tmp));
            this.subClusters.add(new ClusterOfSequences(tmp2));
            // On rappelle ensuite la methode sur les deux nouveaux sous clusters
            this.subClusters.get(0).clusterizeDivisive();
            this.subClusters.get(1).clusterizeDivisive();
        }
    }

    public static void main(String[] args) {
        Sequence seq1 = new Sequence("ATTACG");
        Sequence seq2 = new Sequence("ATATCG");
        Sequence seq3 = new Sequence("ACCCCG");
        Sequence seq4 = new Sequence("GCCGAG");
        Sequence seq5 = new Sequence("TCCCCG");

        ClusterOfSequences cl1 = new ClusterOfSequences(seq1);
        ClusterOfSequences cl2 = new ClusterOfSequences(seq2);
        ClusterOfSequences cl3 = new ClusterOfSequences(seq3);
        ClusterOfSequences cl4 = new ClusterOfSequences(seq4);
        ClusterOfSequences cl5 = new ClusterOfSequences(seq5);

        ArrayList<Sequence> listSeq = new ArrayList<>(5);
        listSeq.add(seq1);
        listSeq.add(seq2);
        listSeq.add(seq3);
        listSeq.add(seq4);
        listSeq.add(seq5);

        ClusterOfSequences bioCluster = new ClusterOfSequences(listSeq);
        ClusterOfSequences cl6 = new ClusterOfSequences(cl3, cl5);
        ClusterOfSequences bioCluster2 = new ClusterOfSequences(listSeq);

        System.out.println("Newick de biocluster non clusterized : " + bioCluster.getNewick());
        double t = System.nanoTime();
        bioCluster.clusterize();
        double t2 = System.nanoTime();
        System.out.println("Newick de biocluster clustierized de façon agglomerative : " + bioCluster.getNewick());
        System.out.println("Temps d'exécution : " + (t2 - t));

        System.out.println("Newick de biocluster non clusterized (aligne) : " + bioCluster2.getNewickAligned());
        t = System.nanoTime();
        bioCluster2.clusterizeDivisive();
        t2 = System.nanoTime();
        System.out.println("Newick de biocluster clustierized de façon divisive (aligne) : " + bioCluster2.getNewickAligned());
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