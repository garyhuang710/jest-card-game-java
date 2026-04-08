package jest.engine;

import jest.model.Joueur;
import jest.model.Offre;
import jest.rules.ReglesJeu;

import java.util.ArrayList;
import java.util.List;

/**
 * Determine l'ordre de jeu pour la phase de prises.
 */
public class OrdreDeJeu {

    /**
     * Construit un utilitaire de calcul de l'ordre de jeu.
     */
    public OrdreDeJeu() {
    }

    /**
     * Determine le premier joueur a partir des cartes visibles.
     *
     * @param etat etat courant
     * @return index du joueur qui commence
     */
    public int determinerPremierJoueur(EtatPartie etat) {
        List<Joueur> joueurs = etat.getJoueurs();
        ReglesJeu regles = etat.getRegles();

        int meilleurIndex = -1;

        for (int i = 0; i < joueurs.size(); i++) {
            Offre offre = joueurs.get(i).getOffre();
            if (offre == null || !offre.estComplete()) {
                throw new IllegalStateException("Offre manquante ou incomplète pour " + joueurs.get(i).getNom());
            }

            if (meilleurIndex == -1) {
                meilleurIndex = i;
            } else {
                Offre meilleureOffre = joueurs.get(meilleurIndex).getOffre();
                int cmp = regles.comparerCartesVisibles(offre.getCarteVisible(), meilleureOffre.getCarteVisible());
                if (cmp > 0) {
                    meilleurIndex = i;
                }
            }
        }

        return meilleurIndex;
    }

    /**
     * Retourne la liste des index des joueurs ayant une offre complete.
     *
     * @param etat etat courant
     * @return index des offres completes
     */
    public List<Integer> indexOffresCompletes(EtatPartie etat) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o != null && o.estComplete()) {
                res.add(i);
            }
        }
        return res;
    }
}
