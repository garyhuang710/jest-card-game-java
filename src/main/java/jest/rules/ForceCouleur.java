package jest.rules;

import java.io.Serializable;

/**
 * Plus le rang est grand, plus la couleur est forte.
 * PIQUES > TREFLES > CARREAUX > COEURS
 */
public enum ForceCouleur implements Serializable {
    /** Force minimale. */
    COEURS(1),
    /** Deuxieme force. */
    CARREAUX(2),
    /** Troisieme force. */
    TREFLES(3),
    /** Force maximale. */
    PIQUES(4);

    private final int force;

    ForceCouleur(int force) {
        this.force = force;
    }

    /**
     * Retourne la force numerique de la couleur.
     *
     * @return force de la couleur
     */
    public int getForce() {
        return force;
    }
}
