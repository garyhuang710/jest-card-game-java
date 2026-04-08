package jest.ui;

import jest.engine.EtatPartie;
import jest.engine.GestionRound;
import jest.model.Joueur;
import jest.model.MainJoueur;
import jest.mvc.GameView;
import jest.mvc.JeuController;
import jest.mvc.PhaseJeu;
import jest.score.CalculateurScore;

import java.util.List;
import java.util.Objects;

/**
 * Vue en ligne de commande.
 */
public class VueCLI implements GameView {

    private final JeuController controller;
    private final LecteurConsole console;
    private PhaseJeu dernierePhase = null;
    private int dernierActif = -2;

    /**
     * Construit une vue CLI.
     *
     * @param controller controleur de jeu
     * @param console lecteur console
     */
    public VueCLI(JeuController controller, LecteurConsole console) {
        this.controller = Objects.requireNonNull(controller);
        this.console = Objects.requireNonNull(console);
    }

    /**
     * Rafraichit l'affichage et declenche les interactions utilisateurs si besoin.
     *
     * @param etat etat courant
     * @param phase phase courante
     * @param joueurActif index du joueur actif
     * @param mainsCourantes mains distribuees
     */
    @Override
    public void rafraichir(EtatPartie etat, PhaseJeu phase, int joueurActif, List<MainJoueur> mainsCourantes) {
        if (etat == null) {
            return;
        }

        afficherEtat(etat, phase, joueurActif);

        if (phase == PhaseJeu.CHOIX_VISIBLES && phase != dernierePhase) {
            demanderCartesVisibles(etat, mainsCourantes);
        }

        if (phase == PhaseJeu.PRISES) {
            if (joueurActif != -1 && joueurActif != dernierActif) {
                demanderPrise(etat, joueurActif);
            }
        }

        dernierePhase = phase;
        dernierActif = joueurActif;
    }

    /**
     * Affiche un message de log.
     *
     * @param message message a afficher
     */
    @Override
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }

    /**
     * Affiche l'etat de la partie.
     *
     * @param etat etat courant
     * @param phase phase courante
     * @param joueurActif index du joueur actif
     */
    private void afficherEtat(EtatPartie etat, PhaseJeu phase, int joueurActif) {
        System.out.println();
        System.out.println("=== ROUND " + etat.getNumeroRound() + " / " + etat.getRegles().nomVariante()
                + " / extension=" + (etat.isExtensionActivee() ? "OUI" : "NON") + " ===");
        System.out.println("Phase : " + phase);
        if (joueurActif >= 0 && joueurActif < etat.getJoueurs().size()) {
            System.out.println("Joueur actif : " + etat.getJoueurs().get(joueurActif).getNom());
        }
        System.out.println("Pioche : " + etat.getPioche().size() + " cartes");
        System.out.println("Trophées : " + etat.getTrophees());
        System.out.println("Offres :");
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur j = etat.getJoueurs().get(i);
            System.out.println("  " + i + " - " + j.getNom() + " -> " + j.getOffre());
        }
        System.out.println("Jests (taille) :");
        for (Joueur j : etat.getJoueurs()) {
            System.out.println("  " + j.getNom() + " : " + j.getJest().getCartes());
        }
        if (phase == PhaseJeu.TERMINEE) {
            afficherScoresFinaux(etat);
        }
    }

    /**
     * Demande aux joueurs humains de choisir leur carte visible.
     *
     * @param etat etat courant
     * @param mainsCourantes mains distribuees
     */
    private void demanderCartesVisibles(EtatPartie etat, List<MainJoueur> mainsCourantes) {
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            var joueur = etat.getJoueurs().get(i);
            if (joueur.estIA()) {
                continue;
            }
            if (joueur.getOffre() != null && joueur.getOffre().estComplete()) {
                continue;
            }
            MainJoueur main = mainsCourantes.get(i);
            if (main == null) {
                continue;
            }
            System.out.println("\n" + joueur.getNom() + " : tes cartes sont :");
            System.out.println("1) " + main.get(0));
            System.out.println("2) " + main.get(1));
            try {
                int choixVisible = console.lireEntier("Quelle carte rendre visible ?", 1, 2);
                controller.choisirCarteVisible(i, choixVisible - 1);
            } catch (Exception e) {
                System.out.println("[CLI] Entrée console indisponible, action ignorée.");
                return;
            }
        }
    }

    /**
     * Demande a un joueur humain de choisir une prise.
     *
     * @param etat etat courant
     * @param joueurActif index du joueur actif
     */
    private void demanderPrise(EtatPartie etat, int joueurActif) {
        var joueur = etat.getJoueurs().get(joueurActif);
        if (joueur.estIA()) {
            return;
        }
        System.out.println("\nTOUR DE " + joueur.getNom());
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            var offre = etat.getJoueurs().get(i).getOffre();
            System.out.println("  " + i + " - " + etat.getJoueurs().get(i).getNom() + " : " + offre);
        }
        try {
            int cible = console.lireEntier("Choisis la cible", 0, etat.getJoueurs().size() - 1);
            String vc = console.lireChoix("Prendre Visible ou Cachee ?", "V", "C");
            boolean prendreVisible = vc.equals("V");
            controller.jouerPrise(cible, prendreVisible);
        } catch (Exception e) {
            System.out.println("[CLI] Entrée console indisponible, action ignorée.");
        }
    }

    /**
     * Affiche les scores finaux.
     *
     * @param etat etat courant
     */
    private void afficherScoresFinaux(EtatPartie etat) {
        CalculateurScore calc = new CalculateurScore();
        System.out.println("\n=== SCORES FINAUX ===");
        for (var joueur : etat.getJoueurs()) {
            int s = calc.calculerScore(joueur.getJest(), etat.getRegles());
            System.out.println(joueur.getNom() + " : " + s);
        }
    }
}
