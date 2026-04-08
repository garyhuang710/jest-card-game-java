package jest.ui.gui;

import jest.mvc.JeuController;

/**
 * Point d'entree GUI (Swing uniquement).
 */
public class MainJeuGUI {
    /**
     * Constructeur prive pour classe utilitaire.
     */
    private MainJeuGUI() {
    }

    /**
     * Lance l'interface graphique.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        JeuController controller = new JeuController();
        VueSwing vueSwing = new VueSwing(controller);
        controller.enregistrerVue(vueSwing);
        vueSwing.ouvrir();
    }
}
