package jest.ia;

/**
 * Action de prise choisie par une strategie d'IA.
 */
public class ActionPrise {
    private final int indexCible;
    private final boolean prendreVisible;

    /**
     * Construit une action de prise.
     *
     * @param indexCible index du joueur cible
     * @param prendreVisible true pour prendre la carte visible
     */
    public ActionPrise(int indexCible, boolean prendreVisible) {
        this.indexCible = indexCible;
        this.prendreVisible = prendreVisible;
    }

    /**
     * Retourne l'index du joueur cible.
     *
     * @return index de la cible
     */
    public int getIndexCible() {
        return indexCible;
    }

    /**
     * Indique si la carte visible doit etre prise.
     *
     * @return true si la carte visible est prise
     */
    public boolean isPrendreVisible() {
        return prendreVisible;
    }
}
