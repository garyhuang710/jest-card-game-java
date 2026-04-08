package jest.score;

import jest.model.Carte;
import jest.model.Jest;
import jest.rules.ReglesJeu;

/**
 * Calcule le score d'un jest selon les regles.
 */
public class CalculateurScore {

    /**
     * Construit un calculateur de score.
     */
    public CalculateurScore() {
    }

    /**
     * Calcule le score d'un jest selon les regles fournies.
     *
     * @param jest jest a evaluer
     * @param regles regles de scoring
     * @return score total
     */
    public int calculerScore(Jest jest, ReglesJeu regles) {
        VisiteurScore visiteur = creerVisiteur(jest, regles);
        for (Carte c : jest.getCartes()) {
            visiteur.visiter(c);
        }
        return visiteur.getScore();
    }

    /**
     * Calcule le score avec les regles de base (compatibilite).
     *
     * @param jest jest a evaluer
     * @return score total
     */
    public int calculerScore(Jest jest) {
        VisiteurScore visiteur = new VisiteurScoreDeBase(jest);
        for (Carte c : jest.getCartes()) {
            visiteur.visiter(c);
        }
        return visiteur.getScore();
    }

    /**
     * Cree le visiteur de score correspondant a la variante.
     *
     * @param jest jest a evaluer
     * @param regles regles de la partie
     * @return visiteur de score adapte
     */
    private VisiteurScore creerVisiteur(Jest jest, ReglesJeu regles) {
        String nom = regles.nomVariante();
        if (nom.equals("VARIANTE_CARREAUX_POSITIFS")) {
            return new VisiteurScoreCarreauxPositifs(jest);
        }
        if (nom.equals("VARIANTE_JOKER_FIXE")) {
            return new VisiteurScoreJokerFixe(jest);
        }
        return new VisiteurScoreDeBase(jest);
    }
}
