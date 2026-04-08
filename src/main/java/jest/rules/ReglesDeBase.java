package jest.rules;

import jest.model.Carte;
import jest.model.Couleur;
import jest.model.Joker;

/**
 * Regles de base du jeu.
 */
public class ReglesDeBase implements ReglesJeu {

    /**
     * Construit les regles de base.
     */
    public ReglesDeBase() {
    }

    /**
     * Retourne le nombre de trophees selon le nombre de joueurs.
     *
     * @param nombreJoueurs nombre de joueurs
     * @return nombre de trophees
     */
    @Override
    public int nombreTrophees(int nombreJoueurs) {
        if (nombreJoueurs == 3) return 2;
        if (nombreJoueurs == 4) return 1;
        throw new IllegalArgumentException("Le jeu se joue à 3 ou 4 joueurs. Reçu: " + nombreJoueurs);
    }

    /**
     * Compare deux cartes visibles en utilisant valeur puis force de couleur.
     *
     * @param a premiere carte
     * @param b seconde carte
     * @return resultat de comparaison
     */
    @Override
    public int comparerCartesVisibles(Carte a, Carte b) {
        int va = valeurPourTour(a);
        int vb = valeurPourTour(b);

        if (va != vb) {
            return Integer.compare(va, vb); // >0 si a est plus forte
        }

        // égalité de valeur : départage par force de couleur
        Couleur ca = couleurPourDepartage(a);
        Couleur cb = couleurPourDepartage(b);

        int fa = forceDe(ca).getForce();
        int fb = forceDe(cb).getForce();

        return Integer.compare(fa, fb);
    }

    /**
     * Retourne la force associee a une couleur.
     *
     * @param couleur couleur a evaluer
     * @return force de la couleur
     */
    @Override
    public ForceCouleur forceDe(Couleur couleur) {
        switch (couleur) {
            case PIQUES:
                return ForceCouleur.PIQUES;
            case TREFLES:
                return ForceCouleur.TREFLES;
            case CARREAUX:
                return ForceCouleur.CARREAUX;
            case COEURS:
                return ForceCouleur.COEURS;
            default:
                throw new IllegalArgumentException("Couleur inconnue: " + couleur);
        }
    }

    /**
     * Retourne la valeur d'une carte pour l'ordre du tour.
     *
     * @param carte carte a evaluer
     * @return valeur pour le tour
     */
    @Override
    public int valeurPourTour(Carte carte) {
        if (carte instanceof Joker || carte.estJoker()) {
            return 0;
        }
        // As vaut 1, déjà le cas dans Rang
        return carte.valeurPourTour();
    }

    /**
     * Retourne la couleur utilisee pour departager les egalites.
     *
     * @param carte carte a evaluer
     * @return couleur de departage
     */
    private Couleur couleurPourDepartage(Carte carte) {
        // Le Joker a une valeur 0 : si jamais on doit départager 2 jokers (peu probable),
        // on lui donne la plus faible force (COEURS) pour rester déterministe.
        if (carte instanceof Joker || carte.estJoker()) {
            return Couleur.COEURS;
        }
        return carte.getCouleur();
    }

    /**
     * Retourne le nom de la variante.
     *
     * @return nom de la variante
     */
    @Override
    public String nomVariante() {
        return "BASE";
    }

}
