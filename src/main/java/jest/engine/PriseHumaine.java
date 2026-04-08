package jest.engine;

import jest.ui.LecteurConsole;

/**
 * Gere la prise d'un joueur humain via la console.
 */
public class PriseHumaine {

    /**
     * Construit un gestionnaire de prise humaine.
     */
    public PriseHumaine() {
    }

    /**
     * Joue un tour de prise pour un joueur humain.
     *
     * @param etat etat courant
     * @param indexActif index du joueur actif
     * @param console lecteur console
     */
    public void jouerTourHumain(EtatPartie etat, int indexActif, LecteurConsole console) {
        var actif = etat.getJoueurs().get(indexActif);

        int cible = console.lireEntier("Choisis l'index de la cible", 0, etat.getJoueurs().size() - 1);

        String vc = console.lireChoix("Prendre VISIBLE ou CACHEE ?", "V", "C");
        boolean prendreVisible = vc.equals("V");

        new Prise().prendreCarte(etat, indexActif, cible, prendreVisible);
    }
}
