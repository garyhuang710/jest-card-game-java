package jest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ensemble de cartes gagnees par un joueur.
 */
public class Jest implements Serializable {

    /** Cartes contenues dans le jest. */
    private final List<Carte> cartes = new ArrayList<>();

    /**
     * Construit un jest vide.
     */
    public Jest() {
    }

    /**
     * Ajoute une carte au jest.
     *
     * @param carte carte a ajouter
     */
    public void ajouter(Carte carte) {
        cartes.add(carte);
    }

    /**
     * Retourne une vue non modifiable des cartes du jest.
     *
     * @return cartes du jest
     */
    public List<Carte> getCartes() {
        return Collections.unmodifiableList(cartes);
    }

    /**
     * Retire et renvoie la premiere carte du jest si presente.
     *
     * @return premiere carte ou {@code null}
     */
    public Carte retirerPremiereCarte() {
        if (cartes.isEmpty()) {
            return null;
        }
        return cartes.remove(0);
    }

    /**
     * Retourne le nombre de cartes dans le jest.
     *
     * @return taille du jest
     */
    public int taille() {
        return cartes.size();
    }

    /**
     * Indique si le jest contient un joker.
     *
     * @return true si un joker est present
     */
    public boolean contientJoker() {
        for (Carte c : cartes) {
            if (c.estJoker()) return true;
        }
        return false;
    }

}
