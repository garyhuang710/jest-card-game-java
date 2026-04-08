package jest.engine;

import jest.model.Carte;
import jest.model.ConditionTrophee;
import jest.model.Couleur;
import jest.model.Joueur;
import jest.model.Rang;
import jest.model.Trophee;
import jest.rules.ReglesJeu;
import jest.score.CalculateurScore;

import java.util.ArrayList;
import java.util.List;

/**
 * Attribue les trophees en fin de partie ou de manche.
 *
 * <p>Les trophees sont attribues sans modifier les jests pendant la selection
 * des gagnants, puis ajoutes une fois les decisions prises.</p>
 */
public class AttributionTrophees {

    private final CalculateurScore calculateurScore = new CalculateurScore();

    /**
     * Construit un gestionnaire d'attribution des trophees.
     */
    public AttributionTrophees() {
    }

    /**
     * Attribue l'ensemble des trophees disponibles aux joueurs.
     *
     * @param etat etat courant de la partie
     */
    public void attribuerTousLesTrophees(EtatPartie etat) {
        if (etat.getTrophees().isEmpty()) {
            return;
        }

        List<Trophee> trophees = etat.getTrophees();
        List<Integer> gagnants = new ArrayList<>();

        for (Trophee trophee : trophees) {
            gagnants.add(determinerGagnantPourUnTrophee(etat, trophee));
        }

        for (int i = 0; i < trophees.size(); i++) {
            int indexGagnant = gagnants.get(i);
            if (indexGagnant < 0) {
                continue;
            }
            Joueur gagnant = etat.getJoueurs().get(indexGagnant);
            gagnant.getJest().ajouter(trophees.get(i).getCarte());
        }

        etat.getTrophees().clear();
    }

    /**
     * Determine le gagnant pour un trophee donne.
     *
     * @param etat etat courant
     * @param trophee trophee a attribuer
     * @return index du joueur gagnant, ou -1 si aucun
     */
    private int determinerGagnantPourUnTrophee(EtatPartie etat, Trophee trophee) {
        ConditionTrophee condition = trophee.getCondition();

        switch (condition) {
            case JOKER:
                return gagnantJoker(etat);
            case PLUS_FORTE_CARTE_COULEUR:
                return gagnantCarteCouleur(etat, couleurCible(trophee), true);
            case PLUS_FAIBLE_CARTE_COULEUR:
                return gagnantCarteCouleur(etat, couleurCible(trophee), false);
            case MAJORITE_RANG:
                return gagnantMajoriteRang(etat, rangCible(trophee));
            case MEILLEUR_JEST:
                return gagnantMeilleurJest(etat, false);
            case MEILLEUR_JEST_SANS_JOKER:
                return gagnantMeilleurJest(etat, true);
            default:
                return -1;
        }
    }

    /**
     * Retourne l'index du joueur possedant le joker.
     *
     * @param etat etat courant
     * @return index du joueur ou -1 si absent
     */
    private int gagnantJoker(EtatPartie etat) {
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur joueur = etat.getJoueurs().get(i);
            if (joueur.getJest().contientJoker()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retourne le joueur avec la meilleure ou la plus faible carte d'une couleur.
     *
     * @param etat etat courant
     * @param couleur couleur cible
     * @param plusForte true pour la plus forte, false pour la plus faible
     * @return index du joueur ou -1 si aucun
     */
    private int gagnantCarteCouleur(EtatPartie etat, Couleur couleur, boolean plusForte) {
        if (couleur == null) {
            return -1;
        }
        int meilleur = -1;
        Carte meilleureCarte = null;
        ReglesJeu regles = etat.getRegles();

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur joueur = etat.getJoueurs().get(i);
            Carte candidate = meilleureCarteDeCouleur(joueur, couleur, regles, plusForte);
            if (candidate == null) {
                continue;
            }
            if (meilleureCarte == null) {
                meilleureCarte = candidate;
                meilleur = i;
                continue;
            }
            int cmp = regles.comparerCartesVisibles(candidate, meilleureCarte);
            if ((!plusForte && cmp < 0) || (plusForte && cmp > 0)) {
                meilleureCarte = candidate;
                meilleur = i;
            }
        }
        return meilleur;
    }

    /**
     * Retourne le joueur majoritaire sur un rang.
     *
     * @param etat etat courant
     * @param rang rang cible
     * @return index du joueur ou -1 si aucun
     */
    private int gagnantMajoriteRang(EtatPartie etat, Rang rang) {
        if (rang == null) {
            return -1;
        }
        int meilleur = -1;
        int meilleurCount = 0;
        ReglesJeu regles = etat.getRegles();

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur joueur = etat.getJoueurs().get(i);
            int count = compterRang(joueur, rang);
            if (count == 0) {
                continue;
            }
            if (count > meilleurCount) {
                meilleur = i;
                meilleurCount = count;
            } else if (count == meilleurCount) {
                Carte meilleureA = meilleureCarteDeRang(joueur, rang, regles);
                Carte meilleureB = meilleureCarteDeRang(etat.getJoueurs().get(meilleur), rang, regles);
                if (meilleureA != null && meilleureB != null
                        && regles.comparerCartesVisibles(meilleureA, meilleureB) > 0) {
                    meilleur = i;
                }
            }
        }
        return meilleur;
    }

    /**
     * Retourne le joueur avec le meilleur jest, avec option sans joker.
     *
     * @param etat etat courant
     * @param sansJoker true pour exclure les jests contenant un joker
     * @return index du joueur ou -1 si aucun
     */
    private int gagnantMeilleurJest(EtatPartie etat, boolean sansJoker) {
        int meilleur = -1;
        int meilleurScore = Integer.MIN_VALUE;
        ReglesJeu regles = etat.getRegles();

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur joueur = etat.getJoueurs().get(i);
            if (sansJoker && joueur.getJest().contientJoker()) {
                continue;
            }
            int score = calculateurScore.calculerScore(joueur.getJest(), regles);
            if (meilleur == -1 || score > meilleurScore) {
                meilleur = i;
                meilleurScore = score;
            } else if (score == meilleurScore) {
                if (compareMeilleureCarteDuJest(etat, i, meilleur) > 0) {
                    meilleur = i;
                }
            }
        }
        return meilleur;
    }

    /**
     * Compte les cartes d'un rang dans le jest d'un joueur.
     *
     * @param joueur joueur a evaluer
     * @param rang rang cible
     * @return nombre de cartes de ce rang
     */
    private int compterRang(Joueur joueur, Rang rang) {
        int c = 0;
        for (Carte carte : joueur.getJest().getCartes()) {
            if (carte.estJoker()) {
                continue;
            }
            if (carte.getRang() == rang) {
                c++;
            }
        }
        return c;
    }

    /**
     * Retourne la meilleure carte d'une couleur pour un joueur.
     *
     * @param joueur joueur a evaluer
     * @param couleur couleur cible
     * @param regles regles de jeu
     * @param plusForte true pour la plus forte, false pour la plus faible
     * @return meilleure carte ou {@code null}
     */
    private Carte meilleureCarteDeCouleur(Joueur joueur, Couleur couleur, ReglesJeu regles, boolean plusForte) {
        Carte meilleure = null;
        for (Carte c : joueur.getJest().getCartes()) {
            if (c.estJoker()) {
                continue;
            }
            if (c.getCouleur() != couleur) {
                continue;
            }
            if (meilleure == null) {
                meilleure = c;
                continue;
            }
            int cmp = regles.comparerCartesVisibles(c, meilleure);
            if ((!plusForte && cmp < 0) || (plusForte && cmp > 0)) {
                meilleure = c;
            }
        }
        return meilleure;
    }

    /**
     * Retourne la meilleure carte d'un rang (plus forte couleur) pour un joueur.
     *
     * @param joueur joueur a evaluer
     * @param rang rang cible
     * @param regles regles de jeu
     * @return meilleure carte ou {@code null}
     */
    private Carte meilleureCarteDeRang(Joueur joueur, Rang rang, ReglesJeu regles) {
        Carte meilleure = null;
        for (Carte c : joueur.getJest().getCartes()) {
            if (c.estJoker()) {
                continue;
            }
            if (c.getRang() != rang) {
                continue;
            }
            if (meilleure == null) {
                meilleure = c;
                continue;
            }
            if (regles.comparerCartesVisibles(c, meilleure) > 0) {
                meilleure = c;
            }
        }
        return meilleure;
    }

    /**
     * Compare la meilleure carte du jest pour deux joueurs.
     *
     * @param etat etat courant
     * @param a index du premier joueur
     * @param b index du second joueur
     * @return resultat de comparaison (positif si a gagne)
     */
    private int compareMeilleureCarteDuJest(EtatPartie etat, int a, int b) {
        Carte meilleureA = meilleureCarteDuJest(etat, a);
        Carte meilleureB = meilleureCarteDuJest(etat, b);

        if (meilleureA == null && meilleureB == null) {
            return 0;
        }
        if (meilleureA != null && meilleureB == null) {
            return 1;
        }
        if (meilleureA == null) {
            return -1;
        }

        return etat.getRegles().comparerCartesVisibles(meilleureA, meilleureB);
    }

    /**
     * Retourne la meilleure carte d'un jest pour un tie-break.
     *
     * @param etat etat courant
     * @param index index du joueur
     * @return meilleure carte ou {@code null}
     */
    private Carte meilleureCarteDuJest(EtatPartie etat, int index) {
        Carte meilleure = null;
        for (Carte c : etat.getJoueurs().get(index).getJest().getCartes()) {
            if (meilleure == null) {
                meilleure = c;
                continue;
            }
            if (etat.getRegles().comparerCartesVisibles(c, meilleure) > 0) {
                meilleure = c;
            }
        }
        return meilleure;
    }

    /**
     * Retourne la couleur cible du trophee.
     *
     * @param trophee trophee a evaluer
     * @return couleur cible ou {@code null}
     */
    private Couleur couleurCible(Trophee trophee) {
        if (trophee.getCouleurCondition() != null) {
            return trophee.getCouleurCondition();
        }
        if (trophee.getCarte().estJoker()) {
            return null;
        }
        return trophee.getCarte().getCouleur();
    }

    /**
     * Retourne le rang cible du trophee.
     *
     * @param trophee trophee a evaluer
     * @return rang cible ou {@code null}
     */
    private Rang rangCible(Trophee trophee) {
        if (trophee.getRangCondition() != null) {
            return trophee.getRangCondition();
        }
        if (trophee.getCarte().estJoker()) {
            return null;
        }
        return trophee.getCarte().getRang();
    }
}
