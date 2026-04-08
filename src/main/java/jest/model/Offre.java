package jest.model;

import java.io.Serializable;

/**
 * Offre composee d'une carte visible et d'une carte cachee.
 */
public class Offre implements Serializable {

    /** Carte cachee de l'offre. */
    private Carte carteCachee;
    /** Carte visible de l'offre. */
    private Carte carteVisible;

    /**
     * Construit une offre.
     *
     * @param carteCachee carte cachee
     * @param carteVisible carte visible
     */
    public Offre(Carte carteCachee, Carte carteVisible) {
        this.carteCachee = carteCachee;
        this.carteVisible = carteVisible;
    }

    /**
     * Retourne la carte cachee.
     *
     * @return carte cachee
     */
    public Carte getCarteCachee() {
        return carteCachee;
    }

    /**
     * Retourne la carte visible.
     *
     * @return carte visible
     */
    public Carte getCarteVisible() {
        return carteVisible;
    }

    /**
     * Indique si l'offre contient deux cartes.
     *
     * @return true si l'offre est complete
     */
    public boolean estComplete() {
        return carteCachee != null && carteVisible != null;
    }

    /**
     * Prend la carte visible et la retire de l'offre.
     *
     * @return carte visible
     */
    public Carte prendreVisible() {
        Carte c = carteVisible;
        carteVisible = null;
        return c;
    }

    /**
     * Prend la carte cachee et la retire de l'offre.
     *
     * @return carte cachee
     */
    public Carte prendreCachee() {
        Carte c = carteCachee;
        carteCachee = null;
        return c;
    }

    /**
     * Retourne la carte restante (visible ou cachee).
     *
     * @return carte restante ou {@code null}
     */
    public Carte carteRestante() {
        if (carteVisible != null) return carteVisible;
        if (carteCachee != null) return carteCachee;
        return null;
    }

    /**
     * Retourne une representation textuelle de l'offre.
     *
     * @return chaine descriptive
     */
    @Override
    public String toString() {
        String visible = (carteVisible == null) ? "<VIDE>" : carteVisible.toString();
        String cachee = (carteCachee == null) ? "<VIDE>" : "CARTE_CACHEE";
        return "Offre{visible=" + visible + ", cachee=" + cachee + "}";
    }
}
