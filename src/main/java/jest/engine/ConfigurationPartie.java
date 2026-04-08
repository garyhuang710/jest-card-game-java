package jest.engine;

import jest.rules.ReglesJeu;

import java.io.Serializable;
import java.util.Objects;

/**
 * Parametres de configuration d'une partie.
 */
public class ConfigurationPartie implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Nombre total de joueurs. */
    private final int nombreJoueurs;
    /** Nombre de joueurs controles par l'IA. */
    private final int nombreIA;
    /** Regles appliquees a la partie. */
    private final ReglesJeu regles;
    /** Indique si l'extension est activee. */
    private final boolean extensionActivee;

    /**
     * Construit une configuration de partie.
     *
     * @param nombreJoueurs nombre total de joueurs (3 ou 4)
     * @param nombreIA nombre de joueurs controles par l'IA
     * @param regles regles appliquees a la partie
     * @param extensionActivee activation de l'extension
     */
    public ConfigurationPartie(int nombreJoueurs, int nombreIA, ReglesJeu regles, boolean extensionActivee) {
        if (nombreJoueurs != 3 && nombreJoueurs != 4) {
            throw new IllegalArgumentException("Le jeu se joue à 3 ou 4 joueurs.");
        }
        if (nombreIA < 0 || nombreIA > nombreJoueurs) {
            throw new IllegalArgumentException("nombreIA doit être entre 0 et nombreJoueurs.");
        }
        this.nombreJoueurs = nombreJoueurs;
        this.nombreIA = nombreIA;
        this.regles = Objects.requireNonNull(regles, "regles");
        this.extensionActivee = extensionActivee;
    }

    /**
     * Retourne le nombre total de joueurs.
     *
     * @return nombre de joueurs
     */
    public int getNombreJoueurs() {
        return nombreJoueurs;
    }

    /**
     * Retourne le nombre de joueurs controles par l'IA.
     *
     * @return nombre d'IA
     */
    public int getNombreIA() {
        return nombreIA;
    }

    /**
     * Retourne les regles appliquees a la partie.
     *
     * @return regles de jeu
     */
    public ReglesJeu getRegles() {
        return regles;
    }

    /**
     * Indique si l'extension est activee.
     *
     * @return true si l'extension est activee
     */
    public boolean isExtensionActivee() {
        return extensionActivee;
    }
}
