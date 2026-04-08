package jest.engine;

import jest.ia.ActionPrise;
import jest.model.MainJoueur;
import jest.model.Offre;
import jest.ui.LecteurConsole;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolution d'un round avec interaction console.
 */
public class ResolutionRoundCLI {

    /**
     * Construit un gestionnaire de round CLI.
     */
    public ResolutionRoundCLI() {
    }

    /**
     * Joue un round complet (creation des offres puis prises).
     *
     * @param etat etat courant
     * @param mains mains distribuees
     * @param console lecteur console
     */
    public void jouerUnRound(EtatPartie etat, List<MainJoueur> mains, LecteurConsole console) {
        GestionRound gestion = new GestionRound();

        // ===== Création des offres =====
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            var joueur = etat.getJoueurs().get(i);

            if (!joueur.estIA()) {
                // HUMAIN
                gestion.creerOffreHumaine(etat, i, mains.get(i), console);
            } else {
                // IA
                var strategie = joueur.getStrategie();
                if (strategie == null) {
                    throw new IllegalStateException(
                            "Le joueur IA " + joueur.getNom() + " n'a pas de stratégie."
                    );
                }

                int indexVisible = strategie.choisirIndexVisible(mains.get(i), etat, i);
                int indexCachee = (indexVisible == 0) ? 1 : 0;

                var carteVisible = mains.get(i).get(indexVisible);
                var carteCachee = mains.get(i).get(indexCachee);

                joueur.definirOffre(new Offre(carteCachee, carteVisible));
            }
        }

        // ===== Phase des prises =====
        int actif = new OrdreDeJeu().determinerPremierJoueur(etat);

        Set<Integer> aDejaJoue = new HashSet<>();
        Prise prise = new Prise();

        while (aDejaJoue.size() < etat.getJoueurs().size()) {
            aDejaJoue.add(actif);

            int cible;
            boolean prendreVisible;

            var joueurActif = etat.getJoueurs().get(actif);

            if (!joueurActif.estIA()) {
                // HUMAIN
                cible = choisirCibleHumaine(etat, actif, console);
                String vc = console.lireChoix("Prendre Visible ou Cachee ?", "V", "C");
                prendreVisible = vc.equals("V");
            } else {
                // IA
                var strat = joueurActif.getStrategie();
                ActionPrise action = strat.choisirActionPrise(etat, actif);
                cible = action.getIndexCible();
                prendreVisible = action.isPrendreVisible();

            }

            var carte = prise.prendreCarte(etat, actif, cible, prendreVisible);

            if (aDejaJoue.size() == etat.getJoueurs().size()) {
                break;
            }

            if (cible != actif && !aDejaJoue.contains(cible)) {
                actif = cible;
            } else {
                actif = meilleurJoueurRestantAvecOffreComplete(etat, aDejaJoue);
            }
        }
    }

    // ============================================================

    /**
     * Demande a un joueur humain de choisir une cible valide.
     *
     * @param etat etat courant
     * @param actif index du joueur actif
     * @param console lecteur console
     * @return index de la cible
     */
    private int choisirCibleHumaine(EtatPartie etat, int actif, LecteurConsole console) {
        int nbCompletes = 0;
        int seulComplet = -1;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            var o = etat.getJoueurs().get(i).getOffre();
            if (o != null && o.estComplete()) {
                nbCompletes++;
                seulComplet = i;
            }
        }

        if (nbCompletes == 1 && seulComplet == actif) {
            return actif;
        }

        while (true) {
            int cible = console.lireEntier("Choisis la cible", 0, etat.getJoueurs().size() - 1);

            if (cible == actif) {
                continue;
            }

            var offre = etat.getJoueurs().get(cible).getOffre();
            if (offre == null || !offre.estComplete()) {
                continue;
            }

            return cible;
        }
    }

    /**
     * Selectionne le meilleur joueur restant avec une offre complete.
     *
     * @param etat etat courant
     * @param aDejaJoue index des joueurs deja passes
     * @return index du meilleur joueur restant
     */
    private int meilleurJoueurRestantAvecOffreComplete(EtatPartie etat, Set<Integer> aDejaJoue) {
        int meilleur = -1;

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (aDejaJoue.contains(i)) continue;

            Offre o = etat.getJoueurs().get(i).getOffre();
            if (o == null || !o.estComplete()) continue;

            if (meilleur == -1) {
                meilleur = i;
            } else {
                int cmp = etat.getRegles().comparerCartesVisibles(
                        o.getCarteVisible(),
                        etat.getJoueurs().get(meilleur).getOffre().getCarteVisible()
                );
                if (cmp > 0) meilleur = i;
            }
        }

        if (meilleur == -1) {
            throw new IllegalStateException("Aucun joueur restant avec une offre complète.");
        }
        return meilleur;
    }
}
