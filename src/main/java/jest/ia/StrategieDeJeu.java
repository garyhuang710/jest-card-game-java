package jest.ia;

import jest.engine.EtatPartie;
import jest.model.MainJoueur;

/**
 * Strategie de jeu pour les joueurs IA.
 */
public interface StrategieDeJeu {

    /**
     * Retourne l'index de la carte rendue visible.
     *
     * @param main main du joueur
     * @param etat etat courant
     * @param indexJoueur index du joueur
     * @return index de la carte visible (0 ou 1)
     */
    int choisirIndexVisible(MainJoueur main, EtatPartie etat, int indexJoueur);

    /**
     * Choisit la cible et la visibilite pour l'action de prise.
     *
     * @param etat etat courant
     * @param indexActif index du joueur actif
     * @return action de prise
     */
    ActionPrise choisirActionPrise(EtatPartie etat, int indexActif);
}
