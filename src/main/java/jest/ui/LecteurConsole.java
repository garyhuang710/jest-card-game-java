package jest.ui;

import java.util.Scanner;

/**
 * Utilitaire de lecture d'entrees console.
 */
public class LecteurConsole {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Construit un lecteur console.
     */
    public LecteurConsole() {
    }

    /**
     * Lit un entier dans un intervalle.
     *
     * @param message message d'invite
     * @param min borne minimale
     * @param max borne maximale
     * @return entier valide
     */
    public int lireEntier(String message, int min, int max) {
        while (true) {
            System.out.print(message + " (" + min + "-" + max + ") : ");
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Flux d'entrée fermé");
            }
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Valeur invalide.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrez un nombre.");
            }
        }
    }

    /**
     * Lit un choix parmi une liste de valeurs.
     *
     * @param message message d'invite
     * @param choix choix possibles
     * @return choix valide en majuscules
     */
    public String lireChoix(String message, String... choix) {
        while (true) {
            System.out.print(message + " ");
            for (String c : choix) System.out.print("[" + c + "] ");
            System.out.print(": ");
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Flux d'entrée fermé");
            }
            String s = scanner.nextLine().trim().toUpperCase();
            for (String c : choix) {
                if (c.equalsIgnoreCase(s)) return c.toUpperCase();
            }
            System.out.println("Choix invalide.");
        }
    }
}
