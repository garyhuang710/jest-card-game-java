package jest.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Représente une carte du jeu Jest.
 * <p>
 * Une carte est définie par :
 * <ul>
 *     <li>un {@link Rang}</li>
 *     <li>une {@link Couleur}</li>
 * </ul>
 * Elle fournit différentes méthodes utilitaires utilisées
 * lors des phases de jeu et du calcul du score.
 */
public class Carte implements Serializable {

    /** Couleur de la carte. */
    private final Couleur couleur;
    /** Rang de la carte. */
    private final Rang rang;

    /**
     * Crée une nouvelle carte.
     *
     * @param couleur couleur de la carte
     * @param rang rang de la carte
     */
    public Carte(Couleur couleur, Rang rang) {
        this.couleur = Objects.requireNonNull(couleur);
        this.rang = Objects.requireNonNull(rang);
    }

    /**
     * Retourne la couleur de la carte.
     *
     * @return couleur
     */
    public Couleur getCouleur() {
        return couleur;
    }

    /**
     * Retourne le rang de la carte.
     *
     * @return rang
     */
    public Rang getRang() {
        return rang;
    }

    /**
     * Valeur utilisée pour déterminer l'ordre de jeu
     * (comparaison des cartes visibles).
     *
     * @return valeur numérique du rang
     */
    public int valeurPourTour() {
        return rang.getValeur();
    }

    /**
     * Valeur de base utilisée pour le calcul du score.
     * <p>
     * Les ajustements liés aux variantes ou aux couleurs
     * sont appliqués ailleurs.
     *
     * @return valeur numérique du rang
     */
    public int valeurBasePourScore() {
        return rang.getValeur();
    }

    /**
     * Indique si la carte est noire (PIQUES ou TREFLES).
     *
     * @return true si la carte est noire
     */
    public boolean estNoire() {
        return couleur == Couleur.PIQUES || couleur == Couleur.TREFLES;
    }

    /**
     * Indique si la carte est rouge (CARREAUX ou COEURS).
     *
     * @return true si la carte est rouge
     */
    public boolean estRouge() {
        return couleur == Couleur.CARREAUX || couleur == Couleur.COEURS;
    }

    /**
     * Indique si la carte est un Joker.
     *
     * @return true si le rang est {@link Rang#JOKER}
     */
    public boolean estJoker() {
        return rang == Rang.JOKER;
    }

    /**
     * Retourne une representation textuelle de la carte.
     *
     * @return chaine descriptive
     */
    @Override
    public String toString() {
        return rang + " de " + couleur;
    }
}
