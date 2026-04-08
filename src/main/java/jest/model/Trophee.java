package jest.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Trophee associe a une carte et a une condition d'attribution.
 */
public class Trophee implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Carte du trophee. */
    private final Carte carte;
    /** Condition d'attribution. */
    private final ConditionTrophee condition;
    /** Couleur cible pour l'attribution, si applicable. */
    private final Couleur couleurCondition;
    /** Rang cible pour l'attribution, si applicable. */
    private final Rang rangCondition;

    /**
     * Construit un trophee.
     *
     * @param carte carte du trophee
     * @param condition condition d'attribution
     */
    public Trophee(Carte carte, ConditionTrophee condition) {
        this(carte, condition, null, null);
    }

    /**
     * Construit un trophee avec une couleur ou un rang cible.
     *
     * @param carte carte du trophee
     * @param condition condition d'attribution
     * @param couleurCondition couleur cible (nullable)
     * @param rangCondition rang cible (nullable)
     */
    public Trophee(Carte carte,
                   ConditionTrophee condition,
                   Couleur couleurCondition,
                   Rang rangCondition) {
        this.carte = Objects.requireNonNull(carte, "carte");
        this.condition = Objects.requireNonNull(condition, "condition");
        this.couleurCondition = couleurCondition;
        this.rangCondition = rangCondition;
    }

    /**
     * Retourne la carte du trophee.
     *
     * @return carte du trophee
     */
    public Carte getCarte() {
        return carte;
    }

    /**
     * Retourne la condition d'attribution.
     *
     * @return condition d'attribution
     */
    public ConditionTrophee getCondition() {
        return condition;
    }

    /**
     * Retourne la couleur cible pour l'attribution, si definie.
     *
     * @return couleur cible ou {@code null}
     */
    public Couleur getCouleurCondition() {
        return couleurCondition;
    }

    /**
     * Retourne le rang cible pour l'attribution, si defini.
     *
     * @return rang cible ou {@code null}
     */
    public Rang getRangCondition() {
        return rangCondition;
    }

    /**
     * Retourne une representation textuelle du trophee.
     *
     * @return chaine descriptive
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(condition.toString());
        if (couleurCondition != null) {
            sb.append("(").append(couleurCondition).append(")");
        }
        if (rangCondition != null) {
            sb.append("(").append(rangCondition).append(")");
        }
        sb.append(" -> ").append(carte);
        return sb.toString();
    }
}
