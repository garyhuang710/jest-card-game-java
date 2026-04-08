package jest.engine;

import jest.model.Offre;

import java.util.HashSet;
import java.util.Set;

/**
 * Gere la phase de prises en mode automatique.
 */
public class ResolutionRound {

    /**
     * Construit un resolveur de round automatique.
     */
    public ResolutionRound() {
    }

    /**
     * Joue la phase de prises pour tous les joueurs en mode automatique.
     *
     * @param etat etat courant
     */
    public void jouerPhasePrisesAutomatique(EtatPartie etat) {
        int n = etat.getJoueurs().size();
        Set<Integer> aDejaJoue = new HashSet<>();

        OrdreDeJeu ordre = new OrdreDeJeu();
        Prise prise = new Prise();

        int actif = ordre.determinerPremierJoueur(etat);

        while (aDejaJoue.size() < n) {
            aDejaJoue.add(actif);

            int cible = choisirCibleAutomatique(etat, actif);
            prise.prendreCarte(etat, actif, cible, true);

            if (aDejaJoue.size() == n) {
                break;
            }

            if (cible != actif && !aDejaJoue.contains(cible)) {
                actif = cible;
            } else {
                actif = meilleurJoueurRestantAvecOffreComplete(etat, aDejaJoue);
            }
        }
    }

    /**
     * Choisit la cible automatique pour un joueur actif.
     *
     * @param etat etat courant
     * @param actif index du joueur actif
     * @return index de la cible
     */
    private int choisirCibleAutomatique(EtatPartie etat, int actif) {
        int nbCompletes = 0;
        int seulIndexComplet = -1;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o != null && o.estComplete()) {
                nbCompletes++;
                seulIndexComplet = i;
            }
        }

        if (nbCompletes == 0) {
            throw new IllegalStateException("Aucune offre complète disponible (incohérent).");
        }

        if (nbCompletes == 1 && seulIndexComplet == actif) {
            return actif;
        }

        int meilleur = -1;
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (i == actif) {
                continue;
            }

            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o == null || !o.estComplete()) {
                continue;
            }

            if (meilleur == -1) {
                meilleur = i;
            } else {
                int cmp = etat.getRegles().comparerCartesVisibles(
                        etat.getJoueurs().get(i).getOffre().getCarteVisible(),
                        etat.getJoueurs().get(meilleur).getOffre().getCarteVisible()
                );
                if (cmp > 0) {
                    meilleur = i;
                }
            }
        }

        if (meilleur == -1) {
            throw new IllegalStateException(
                    "Aucune offre complète chez un autre joueur, alors que nbCompletes != 1 actif."
            );
        }

        return meilleur;
    }

    /**
     * Selectionne le meilleur joueur restant avec une offre complete.
     *
     * @param etat etat courant
     * @param aDejaJoue joueurs ayant deja joue
     * @return index du meilleur joueur restant
     */
    private int meilleurJoueurRestantAvecOffreComplete(EtatPartie etat, Set<Integer> aDejaJoue) {
        int meilleur = -1;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (aDejaJoue.contains(i)) {
                continue;
            }

            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o == null || !o.estComplete()) {
                continue;
            }

            if (meilleur == -1) {
                meilleur = i;
            } else {
                int cmp = etat.getRegles().comparerCartesVisibles(
                        etat.getJoueurs().get(i).getOffre().getCarteVisible(),
                        etat.getJoueurs().get(meilleur).getOffre().getCarteVisible()
                );
                if (cmp > 0) {
                    meilleur = i;
                }
            }
        }

        if (meilleur == -1) {
            throw new IllegalStateException("Aucun joueur restant avec une offre complète.");
        }

        return meilleur;
    }
}
