package jest.ui;

import jest.engine.ConfigurationPartie;
import jest.mvc.JeuController;
import jest.mvc.PhaseJeu;
import jest.rules.ReglesDeBase;
import jest.rules.ReglesJeu;
import jest.rules.ReglesVarianteCarreauxPositifs;
import jest.rules.ReglesVarianteJokerFixe;
import jest.ui.gui.VueSwing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrée CLI + GUI du jeu Jest.
 */
public class MainJeuCLI {

    private static final String FICHIER_SAUVEGARDE = "sauvegarde_jest.dat";

    /**
     * Constructeur prive pour classe utilitaire.
     */
    private MainJeuCLI() {
    }

    /**
     * Point d'entree principal.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        LecteurConsole console = new LecteurConsole();
        Scanner scanner = new Scanner(System.in);

        JeuController controller = new JeuController();
        VueCLI vueCLI = new VueCLI(controller, console);
        controller.enregistrerVue(vueCLI);

        boolean lancerGui = console.lireChoix("Lancer aussi la GUI ?", "O", "N").equals("O");
        VueSwing vueSwing = null;
        if (lancerGui) {
            vueSwing = new VueSwing(controller);
            controller.enregistrerVue(vueSwing);
            vueSwing.ouvrir();
        }

        // Boucle CLI dans un thread dédié pour ne pas bloquer l'EDT Swing
        Thread cliThread = new Thread(() -> boucleCli(controller, console, scanner), "cli-loop");
        cliThread.setDaemon(false);
        cliThread.start();
    }

    /**
     * Boucle principale de la CLI.
     *
     * @param controller controleur de jeu
     * @param console lecteur console
     * @param scanner scanner pour les noms
     */
    private static void boucleCli(JeuController controller, LecteurConsole console, Scanner scanner) {
        // Boucle principale : proposer nouvelle partie / chargement / quitter, puis enchaîner jusqu'à fin de partie.
        while (true) {
            boolean partieCreee = false;

            while (!partieCreee) {
                System.out.println("\n=== JEST (CLI+GUI) ===");
                System.out.println("1 - Nouvelle partie");
                System.out.println("2 - Charger partie");
                System.out.println("3 - Quitter");

                int choix = console.lireEntier("Choix", 1, 3);

                if (choix == 3) return;

                if (choix == 2) {
                    try {
                        controller.chargerPartie(FICHIER_SAUVEGARDE);
                        partieCreee = true;
                    } catch (Exception e) {
                        System.out.println("Impossible de charger : " + e.getMessage());
                    }
                } else {
                    var config = creerConfiguration(console);
                    var noms = saisirNoms(scanner, config.getNombreJoueurs());
                    controller.nouvellePartie(config, noms);
                    partieCreee = true;
                }
            }

            // Confirmer avant de démarrer le round 1
            System.out.println("\nPrêt à démarrer la partie ?");
            System.out.println("1 - Démarrer");
            System.out.println("2 - Quitter");
            int start = console.lireEntier("Choix", 1, 2);
            if (start == 2) return;

            controller.demarrerRound();

            while (controller.getPhase() != PhaseJeu.TERMINEE) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Partie terminée : proposer une nouvelle partie.
            System.out.println("\nPartie terminée. Relancer une partie ?");
            System.out.println("1 - Oui");
            System.out.println("2 - Non (quitter)");
            int rejouer = console.lireEntier("Choix", 1, 2);
            if (rejouer == 2) {
                return;
            }
        }
    }

    /**
     * Construit la configuration a partir des choix utilisateur.
     *
     * @param console lecteur console
     * @return configuration de partie
     */
    private static ConfigurationPartie creerConfiguration(LecteurConsole console) {
        int nombreJoueurs = console.lireEntier("Nombre total de joueurs", 3, 4);
        int nombreIA = console.lireEntier("Nombre de joueurs IA", 0, nombreJoueurs - 1);
        ReglesJeu regles = choisirRegles(console);
        boolean extension = console.lireChoix("Activer l'extension (CINQ) ?", "O", "N").equals("O");

        return new ConfigurationPartie(nombreJoueurs, nombreIA, regles, extension);
    }

    /**
     * Saisit les noms des joueurs.
     *
     * @param scanner scanner d'entree
     * @param nombreJoueurs nombre de joueurs
     * @return liste des noms
     */
    private static List<String> saisirNoms(Scanner scanner, int nombreJoueurs) {
        List<String> noms = new ArrayList<>();
        for (int i = 0; i < nombreJoueurs; i++) {
            System.out.print("Nom du joueur " + (i + 1) + " : ");
            noms.add(scanner.nextLine().trim());
        }
        return noms;
    }

    /**
     * Demande a l'utilisateur la variante de regles.
     *
     * @param console lecteur console
     * @return regles choisies
     */
    private static ReglesJeu choisirRegles(LecteurConsole console) {
        System.out.println("\nChoisis la variante :");
        System.out.println("1 - BASE");
        System.out.println("2 - CARREAUX_POSITIFS");
        System.out.println("3 - JOKER_FIXE");

        int choix = console.lireEntier("Variante", 1, 3);

        if (choix == 2) return new ReglesVarianteCarreauxPositifs();
        if (choix == 3) return new ReglesVarianteJokerFixe();
        return new ReglesDeBase();
    }
}
