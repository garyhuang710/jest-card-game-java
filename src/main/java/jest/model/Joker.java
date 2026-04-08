package jest.model;

/**
 * Carte Joker avec un comportement specifique.
 */
public class Joker extends Carte {

    /**
     * Construit un joker avec des valeurs fictives.
     */
    public Joker() {
        super(Couleur.PIQUES, Rang.AS); // valeurs fictives
    }

    /**
     * Retourne une valeur de tour neutre pour le joker.
     *
     * @return 0
     */
    @Override
    public int valeurPourTour() {
        return 0;
    }

    /**
     * Retourne une valeur de base neutre pour le score.
     *
     * @return 0
     */
    @Override
    public int valeurBasePourScore() {
        return 0;
    }

    /**
     * Indique que le joker n'est ni noir ni rouge.
     *
     * @return false
     */
    @Override
    public boolean estNoire() {
        return false;
    }

    /**
     * Indique que le joker n'est ni noir ni rouge.
     *
     * @return false
     */
    @Override
    public boolean estRouge() {
        return false;
    }

    /**
     * Indique que cette carte est un joker.
     *
     * @return true
     */
    @Override
    public boolean estJoker() {
        return true;
    }

    /**
     * Le joker n'a pas de couleur.
     *
     * @return jamais
     * @throws UnsupportedOperationException toujours levee
     */
    @Override
    public Couleur getCouleur() {
        throw new UnsupportedOperationException("Le Joker n'a pas de couleur");
    }

    /**
     * Le joker n'a pas de rang.
     *
     * @return jamais
     * @throws UnsupportedOperationException toujours levee
     */
    @Override
    public Rang getRang() {
        throw new UnsupportedOperationException("Le Joker n'a pas de rang");
    }

    /**
     * Retourne une representation textuelle du joker.
     *
     * @return chaine "JOKER"
     */
    @Override
    public String toString() {
        return "JOKER";
    }
}
