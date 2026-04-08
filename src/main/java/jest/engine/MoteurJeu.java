package jest.engine;

import jest.model.Carte;
import jest.model.Joueur;
import jest.model.MainJoueur;

import java.util.List;

/**
 * Execute une partie complete en mode automatique.
 */
public class MoteurJeu {

    private final GestionRound gestionRound = new GestionRound();
    private final ResolutionRound resolutionRound = new ResolutionRound();

    /**
     * Construit un moteur de jeu automatique.
     */
    public MoteurJeu() {
    }

    /**
     * Lance une partie automatique jusqu'a la fin de la pioche.
     *
     * @param etat etat initial de la partie
     */
    public void jouerPartieAutomatique(EtatPartie etat) {
        // ROUND 1 : distribution depuis pioche
        List<MainJoueur> mains = gestionRound.distribuerDeuxCartesParJoueur(etat);
        gestionRound.creerOffresAutomatiques(etat, mains);

        while (true) {
            // phase prises (1 carte gagnée par joueur)
            resolutionRound.jouerPhasePrisesAutomatique(etat);

            // Si la pioche ne peut plus fournir une carte par joueur, la partie se termine : chacun prend la carte restante de son offre
            if (etat.getPioche().size() < etat.getJoueurs().size()) {
                prendreDerniereCarteDeChaqueOffre(etat);
                break;
            }

            // Préparer round suivant (règle officielle)
            gestionRound.recupererCartesRestantesDansTasReport(etat);
            gestionRound.ajouterCartesPiocheEtReferenceAuTasReport(etat);
            mains = gestionRound.melangerEtRedistribuerDepuisTasReport(etat);
            gestionRound.creerOffresAutomatiques(etat, mains);

            etat.incrementerRound();
        }
    }

    /**
     * Attribue la derniere carte de chaque offre a son proprietaire.
     *
     * @param etat etat courant
     */
    private void prendreDerniereCarteDeChaqueOffre(EtatPartie etat) {
        for (Joueur joueur : etat.getJoueurs()) {
            Carte restante = joueur.getOffre().carteRestante();
            if (restante == null) {
                throw new IllegalStateException("Pas de carte restante pour " + joueur.getNom());
            }
            joueur.getJest().ajouter(restante);
            // on vide l'offre pour éviter confusion
            joueur.definirOffre(null);
        }
    }
}
