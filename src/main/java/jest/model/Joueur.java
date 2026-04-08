package jest.model;

import jest.ia.StrategieDeJeu;

import java.io.Serializable;

/**
 * Representer un joueur humain ou IA.
 */
public class Joueur implements Serializable {

    /** Nom du joueur. */
    private final String nom;
    /** Indique si le joueur est une IA. */
    private final boolean estIA;

    /** Jest associe au joueur. */
    private final Jest jest = new Jest();
    /** Offre courante du joueur. */
    private Offre offre;

    /** Strategie utilisee par l'IA (non serialisee). */
    private transient StrategieDeJeu strategie;


    /**
     * Construit un joueur.
     *
     * @param nom nom du joueur
     * @param estIA true si le joueur est controle par l'IA
     */
    public Joueur(String nom, boolean estIA) {
        this.nom = nom;
        this.estIA = estIA;
    }

    /**
     * Retourne le nom du joueur.
     *
     * @return nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Indique si le joueur est une IA.
     *
     * @return true si IA
     */
    public boolean estIA() {
        return estIA;
    }

    /**
     * Retourne le jest du joueur.
     *
     * @return jest
     */
    public Jest getJest() {
        return jest;
    }

    /**
     * Retourne l'offre actuelle du joueur.
     *
     * @return offre ou {@code null}
     */
    public Offre getOffre() {
        return offre;
    }

    /**
     * Definit l'offre actuelle du joueur.
     *
     * @param offre offre a definir
     */
    public void definirOffre(Offre offre) {
        this.offre = offre;
    }

    /**
     * Retourne la strategie de l'IA.
     *
     * @return strategie ou {@code null}
     */
    public StrategieDeJeu getStrategie() {
        return strategie;
    }

    /**
     * Definit la strategie de l'IA.
     *
     * @param strategie strategie a definir
     */
    public void setStrategie(StrategieDeJeu strategie) {
        this.strategie = strategie;
    }

    /**
     * Retourne une representation textuelle du joueur.
     *
     * @return chaine descriptive
     */
    @Override
    public String toString() {
        return nom + (estIA ? " [IA]" : "");
    }
}
