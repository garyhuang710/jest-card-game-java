package jest.mvc;

import jest.engine.EtatPartie;
import jest.model.MainJoueur;

import java.util.List;

/**
 * Interface de vue pour l'affichage de la partie.
 */
public interface GameView {
    /**
     * Rafraichit l'affichage a partir de l'etat courant.
     *
     * @param etat etat courant
     * @param phase phase du jeu
     * @param joueurActif index du joueur actif
     * @param mainsCourantes mains distribuees
     */
    void rafraichir(EtatPartie etat, PhaseJeu phase, int joueurActif, List<MainJoueur> mainsCourantes);

    /**
     * Affiche un message de log.
     *
     * @param message message a afficher
     */
    void log(String message);
}
