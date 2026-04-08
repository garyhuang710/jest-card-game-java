package jest.rules;

import jest.model.Carte;
import jest.model.Couleur;
import jest.model.Joker;

import java.io.Serializable;

/**
 * Contrat des regles utilise par le moteur.
 */
public interface ReglesJeu extends Serializable {
    /**
     * Retourne le nom de la variante.
     *
     * @return nom de la variante
     */
    String nomVariante();

    /**
     * Retourne le nombre de trophees a attribuer.
     *
     * @param nombreJoueurs nombre de joueurs
     * @return nombre de trophees
     */
    int nombreTrophees(int nombreJoueurs);

    /**
     * Compare deux cartes visibles pour determiner l'ordre.
     *
     * @param a premiere carte
     * @param b seconde carte
     * @return resultat de comparaison
     */
    int comparerCartesVisibles(Carte a, Carte b);

    /**
     * Retourne la force d'une couleur.
     *
     * @param couleur couleur a evaluer
     * @return force de la couleur
     */
    ForceCouleur forceDe(Couleur couleur);

    /**
     * Retourne la valeur d'une carte pour l'ordre du tour.
     *
     * @param carte carte a evaluer
     * @return valeur pour l'ordre du tour
     */
    int valeurPourTour(Carte carte);
}
