package jest.ia;

import jest.engine.EtatPartie;
import jest.model.Carte;
import jest.model.Couleur;
import jest.model.MainJoueur;
import jest.model.Offre;

/**
 * Strategie IA basee sur des heuristiques simples.
 */
public class StrategieCoherente implements StrategieDeJeu {

    /**
     * Construit une strategie IA coherente.
     */
    public StrategieCoherente() {
    }

    /**
     * Choisit la carte visible a partir d'une heuristique.
     *
     * @param main main du joueur
     * @param etat etat courant
     * @param indexJoueur index du joueur
     * @return index de la carte rendue visible
     */
    @Override
    public int choisirIndexVisible(MainJoueur main, EtatPartie etat, int indexJoueur) {
        // Idée : rendre visible la meilleure carte "pour commencer" MAIS éviter d’exposer un gros carreau si possible.
        Carte c0 = main.get(0);
        Carte c1 = main.get(1);

        int score0 = desirabiliteVisible(c0, etat, indexJoueur);
        int score1 = desirabiliteVisible(c1, etat, indexJoueur);

        return (score1 > score0) ? 1 : 0;
    }

    /**
     * Choisit une action de prise (cible et visibilite).
     *
     * @param etat etat courant
     * @param indexActif index du joueur actif
     * @return action de prise
     */
    @Override
    public ActionPrise choisirActionPrise(EtatPartie etat, int indexActif) {
        // Cas spécial : si la seule offre complète est la sienne -> il doit prendre chez lui
        int nbCompletes = 0;
        int seulComplet = -1;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o != null && o.estComplete()) {
                nbCompletes++;
                seulComplet = i;
            }
        }

        if (nbCompletes == 1 && seulComplet == indexActif) {
            // prend visible chez soi (peu importe, l’offre est complète)
            return new ActionPrise(indexActif, true);
        }

        // Choisir la meilleure cible (offre complète) selon une heuristique
        int meilleureCible = -1;
        int meilleureValeur = Integer.MIN_VALUE;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (i == indexActif) continue;

            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o == null || !o.estComplete()) continue;

            Carte visible = o.getCarteVisible();
            int valeur = desirabilitePrendre(visible, etat, indexActif);

            if (meilleureCible == -1 || valeur > meilleureValeur) {
                meilleureCible = i;
                meilleureValeur = valeur;
            }
        }

        if (meilleureCible == -1) {
            // devrait pas arriver, mais sécurité
            return new ActionPrise(indexActif, true);
        }

        // Visible ou cachée ?
        // Si la visible est vraiment mauvaise (carreau), tenter la cachée.
        Carte visible = etat.getJoueurs().get(meilleureCible).getOffre().getCarteVisible();
        boolean prendreVisible = desirabilitePrendre(visible, etat, indexActif) >= 0;

        return new ActionPrise(meilleureCible, prendreVisible);
    }

    // ------------------ Heuristiques simples et cohérentes ------------------

    /**
     * Calcule la desirabilite d'une carte rendue visible.
     *
     * @param carte carte a evaluer
     * @param etat etat courant
     * @param indexJoueur index du joueur
     * @return score de desirabilite
     */
    private int desirabiliteVisible(Carte carte, EtatPartie etat, int indexJoueur) {
        // Plus c’est fort selon les règles de comparaison, plus ça aide à commencer.
        // Mais si c’est un gros carreau, on évite de le montrer (malus).
        int base = carte.valeurPourTour(); // AS=1, JOKER=0 etc.
        if (!carte.estJoker() && carte.getCouleur() == Couleur.CARREAUX) {
            return base - 10; // gros malus: éviter d’exposer carreaux
        }
        return base;
    }

    /**
     * Calcule la desirabilite de prendre une carte.
     *
     * @param carte carte a evaluer
     * @param etat etat courant
     * @param indexActif index du joueur actif
     * @return score de desirabilite
     */
    private int desirabilitePrendre(Carte carte, EtatPartie etat, int indexActif) {
        if (carte == null) return Integer.MIN_VALUE;
        if (carte.estJoker()) return 0; // Joker peut aider/coûter, on reste neutre

        int v = carte.valeurBasePourScore(); // AS=1 ici, ajusté par règle "As seul" dans score

        Couleur c = carte.getCouleur();

        // trèfles/piques : souvent bons
        if (c == Couleur.PIQUES || c == Couleur.TREFLES) return v;

        // carreaux : généralement mauvais (score négatif)
        if (c == Couleur.CARREAUX) return -2 * v;

        // coeurs : 0 sans joker, mais peut devenir négatif si on a joker + 1..3 coeurs
        if (c == Couleur.COEURS) {
            boolean aJoker = etat.getJoueurs().get(indexActif).getJest().contientJoker();
            if (aJoker) return -v; // prudence
            return 0;
        }

        return 0;
    }
}
