package jest.engine;

import jest.model.Offre;

/**
 * Strategie IA simple basee sur la meilleure carte visible.
 */
public class StrategieSimpleIA {

    /**
     * Construit une strategie IA simple.
     */
    public StrategieSimpleIA() {
    }

    /**
     * Choisit une cible pour l'IA (meilleure offre complete).
     *
     * @param etat etat courant
     * @param indexActif index du joueur actif
     * @return index de la cible
     */
    public int choisirCible(EtatPartie etat, int indexActif) {
        int meilleur = -1;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (i == indexActif) continue;
            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o == null || !o.estComplete()) continue;

            if (meilleur == -1) {
                meilleur = i;
            } else {
                int cmp = etat.getRegles().comparerCartesVisibles(
                        etat.getJoueurs().get(i).getOffre().getCarteVisible(),
                        etat.getJoueurs().get(meilleur).getOffre().getCarteVisible()
                );
                if (cmp > 0) meilleur = i;
            }
        }

        // Règle spéciale : si la seule offre complète est la sienne, elle doit prendre chez elle.
        if (meilleur == -1) return indexActif;

        return meilleur;
    }

    /**
     * Indique que l'IA choisit toujours la carte visible.
     *
     * @return true si la carte visible doit etre prise
     */
    public boolean prendreVisible() {
        return true;
    }
}
