package jest.engine;

import jest.model.Carte;
import jest.model.Joueur;
import jest.model.Offre;

/**
 * Execute une prise d'une carte dans une offre cible.
 */
public class Prise {

    /**
     * Construit un gestionnaire de prise.
     */
    public Prise() {
    }

    /**
     * Effectue une prise de carte sur une offre cible.
     *
     * @param etat etat courant
     * @param indexActif index du joueur actif
     * @param indexCible index du joueur cible
     * @param prendreVisible true pour prendre la carte visible, sinon la cachee
     * @return carte prise
     */
    public Carte prendreCarte(EtatPartie etat, int indexActif, int indexCible, boolean prendreVisible) {
        if (indexActif < 0 || indexActif >= etat.getJoueurs().size()) {
            throw new IllegalArgumentException("indexActif invalide");
        }
        if (indexCible < 0 || indexCible >= etat.getJoueurs().size()) {
            throw new IllegalArgumentException("indexCible invalide");
        }

        Joueur actif = etat.getJoueurs().get(indexActif);
        Joueur cible = etat.getJoueurs().get(indexCible);

        Offre offreCible = cible.getOffre();
        if (offreCible == null || !offreCible.estComplete()) {
            throw new IllegalStateException("Impossible de prendre: l'offre de " + cible.getNom() + " n'est pas complète");
        }

        Carte prise = prendreVisible ? offreCible.prendreVisible() : offreCible.prendreCachee();
        actif.getJest().ajouter(prise);
        return prise;
    }
}
