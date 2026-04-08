package jest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Main temporaire d'un joueur (deux cartes).
 */
public class MainJoueur implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Cartes de la main (toujours deux). */
    private final List<Carte> cartes = new ArrayList<>(2);

    /**
     * Construit une main de deux cartes.
     *
     * @param premiere premiere carte
     * @param deuxieme deuxieme carte
     */
    public MainJoueur(Carte premiere, Carte deuxieme) {
        cartes.add(Objects.requireNonNull(premiere, "premiere"));
        cartes.add(Objects.requireNonNull(deuxieme, "deuxieme"));
    }

    /**
     * Retourne une vue non modifiable des cartes de la main.
     *
     * @return cartes de la main
     */
    public List<Carte> getCartes() {
        return Collections.unmodifiableList(cartes);
    }

    /**
     * Retourne la carte a l'index specifie.
     *
     * @param index index de la carte
     * @return carte a l'index
     */
    public Carte get(int index) {
        return cartes.get(index);
    }
}
