package jest.score;

import jest.model.Carte;
import jest.model.Couleur;
import jest.model.Jest;
import jest.model.Rang;

import java.util.HashMap;
import java.util.Map;

/**
 * Visiteur de score pour la variante carreaux positifs.
 */
public class VisiteurScoreCarreauxPositifs implements VisiteurScore {

    private int score = 0;

    private int nbCoeurs = 0;
    private boolean aJoker = false;

    private final Map<Couleur, Integer> compteurCouleur = new HashMap<>();
    private final Map<Integer, Boolean> aPiqueValeur = new HashMap<>();
    private final Map<Integer, Boolean> aTrefleValeur = new HashMap<>();

    private final Jest jest;

    /**
     * Construit un visiteur pour un jest donne.
     *
     * @param jest jest a evaluer
     */
    public VisiteurScoreCarreauxPositifs(Jest jest) {
        this.jest = jest;
        for (Couleur c : Couleur.values()) {
            compteurCouleur.put(c, 0);
        }
    }

    /**
     * Visite une carte et met a jour le score.
     *
     * @param carte carte a visiter
     */
    @Override
    public void visiter(Carte carte) {
        if (carte.estJoker()) {
            aJoker = true;
            return;
        }

        Couleur couleur = carte.getCouleur();
        int valeur = carte.valeurBasePourScore(); // AS=1

        compteurCouleur.put(couleur, compteurCouleur.get(couleur) + 1);

        if (couleur == Couleur.COEURS) {
            nbCoeurs++;
            return;
        }

        // VARIANTE : carreaux positifs
        if (couleur == Couleur.CARREAUX) {
            score += valeur;
            return;
        }

        // PIQUES / TREFLES
        score += valeur;

        // paire noire +2
        if (couleur == Couleur.PIQUES) {
            aPiqueValeur.put(valeur, true);
            if (aTrefleValeur.getOrDefault(valeur, false)) score += 2;
        } else if (couleur == Couleur.TREFLES) {
            aTrefleValeur.put(valeur, true);
            if (aPiqueValeur.getOrDefault(valeur, false)) score += 2;
        }
    }

    /**
     * Calcule le score final en appliquant les regles conditionnelles.
     *
     * @return score final
     */
    @Override
    public int getScore() {
        appliquerRegleAsSeul();
        appliquerRegleJokerCoeurs(); // identique à base
        return score;
    }

    /**
     * Applique la regle de l'as seul dans sa couleur.
     */
    private void appliquerRegleAsSeul() {
        for (Carte c : jest.getCartes()) {
            if (c.estJoker()) continue;
            if (c.getRang() == Rang.AS) {
                Couleur couleur = c.getCouleur();
                int nb = compteurCouleur.getOrDefault(couleur, 0);
                if (nb == 1) score += 4;
            }
        }
    }

    /**
     * Applique la regle speciale du joker avec les coeurs.
     */
    private void appliquerRegleJokerCoeurs() {
        if (!aJoker) return;

        if (nbCoeurs == 0) {
            score += 4;
            return;
        }

        if (nbCoeurs >= 1 && nbCoeurs <= 3) {
            for (Carte c : jest.getCartes()) {
                if (!c.estJoker() && c.getCouleur() == Couleur.COEURS) {
                    score -= c.valeurBasePourScore();
                }
            }
            return;
        }

        if (nbCoeurs == 4) {
            for (Carte c : jest.getCartes()) {
                if (!c.estJoker() && c.getCouleur() == Couleur.COEURS) {
                    score += c.valeurBasePourScore();
                }
            }
        }
    }
}
