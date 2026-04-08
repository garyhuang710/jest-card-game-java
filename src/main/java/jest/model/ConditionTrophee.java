package jest.model;

import java.io.Serializable;

/**
 * Conditions possibles pour attribuer un trophee.
 */
public enum ConditionTrophee implements Serializable {
    /** Plus forte carte dans la couleur du trophee. */
    PLUS_FORTE_CARTE_COULEUR,
    /** Plus faible carte dans la couleur du trophee. */
    PLUS_FAIBLE_CARTE_COULEUR,
    /** Majorite de cartes du rang du trophee. */
    MAJORITE_RANG,
    /** Joueur qui possede le joker. */
    JOKER,
    /** Joueur ayant le meilleur jest. */
    MEILLEUR_JEST,
    /** Joueur ayant le meilleur jest sans joker. */
    MEILLEUR_JEST_SANS_JOKER;
}
