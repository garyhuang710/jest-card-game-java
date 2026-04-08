package jest.score;

import jest.model.Carte;

/**
 * Interface de visiteur pour calculer un score.
 */
public interface VisiteurScore {
    /**
     * Visite une carte pour mettre a jour le score.
     *
     * @param carte carte a visiter
     */
    void visiter(Carte carte);

    /**
     * Retourne le score courant.
     *
     * @return score courant
     */
    int getScore();
}
