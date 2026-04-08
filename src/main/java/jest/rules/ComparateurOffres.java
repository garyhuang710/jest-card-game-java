package jest.rules;

import jest.model.Offre;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Permet de trier les offres selon la carte visible, en utilisant les règles.
 */
public class ComparateurOffres implements Comparator<Offre>, Serializable {

    private static final long serialVersionUID = 1L;

    /** Regles utilisees pour comparer les offres. */
    private final ReglesJeu regles;

    /**
     * Construit un comparateur base sur des regles de jeu.
     *
     * @param regles regles de comparaison
     */
    public ComparateurOffres(ReglesJeu regles) {
        this.regles = regles;
    }

    /**
     * Compare deux offres selon leur carte visible.
     *
     * @param o1 premiere offre
     * @param o2 seconde offre
     * @return resultat de comparaison
     */
    @Override
    public int compare(Offre o1, Offre o2) {
        // Comparator : ordre croissant.
        // On veut souvent "la meilleure en premier" => on inversera au besoin via reversed().
        return regles.comparerCartesVisibles(o1.getCarteVisible(), o2.getCarteVisible());
    }
}
