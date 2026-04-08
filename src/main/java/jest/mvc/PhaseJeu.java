package jest.mvc;

/**
 * Phases de l'execution d'une partie.
 */
public enum PhaseJeu {
    /** Phase d'initialisation avant le premier round. */
    ATTENTE_INITIALISATION,
    /** Phase d'attente entre deux rounds. */
    EN_ATTENTE_ROUND,
    /** Phase de choix des cartes visibles. */
    CHOIX_VISIBLES,
    /** Phase de prises. */
    PRISES,
    /** Phase de fin de partie. */
    TERMINEE
}
