package jest.model;

import java.io.Serializable;

/**
 * Représente le rang d'une carte dans le jeu Jest.
 * <p>
 * Les rangs définissent la valeur numérique utilisée :
 * <ul>
 *     <li>pour déterminer le premier joueur (comparaison des cartes visibles)</li>
 *     <li>pour le calcul des scores</li>
 * </ul>
 * <p>
 * L'extension du jeu ajoute le rang {@link #CINQ}.
 */
public enum Rang implements Serializable {

    /** As : valeur 1 */
    AS(1),

    /** Deux : valeur 2 */
    DEUX(2),

    /** Trois : valeur 3 */
    TROIS(3),

    /** Quatre : valeur 4 */
    QUATRE(4),

    /**
     * Cinq : valeur 5.
     * <p>
     * Rang ajouté par l'extension du jeu.
     */
    CINQ(5),

    /**
     * Joker : valeur spéciale.
     * <p>
     * Sa valeur numérique est 0 car son effet est traité
     * spécifiquement par les règles de score.
     */
    JOKER(0);

    /** Valeur numérique du rang */
    private final int valeur;

    Rang(int valeur) {
        this.valeur = valeur;
    }

    /**
     * Retourne la valeur numérique associée à ce rang.
     *
     * @return valeur du rang (AS=1, ..., CINQ=5, JOKER=0)
     */
    public int getValeur() {
        return valeur;
    }
}
