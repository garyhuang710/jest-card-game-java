package jest.engine;

import jest.model.Carte;
import jest.model.Joueur;
import jest.model.MainJoueur;
import jest.model.Offre;
import jest.ui.LecteurConsole;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gere la preparation et la distribution d'un round.
 */
public class GestionRound {
    private final Random alea = new Random();

    /**
     * Construit un gestionnaire de round.
     */
    public GestionRound() {
    }

    /**
     * Distribue deux cartes par joueur depuis la pioche.
     *
     * @param etat etat courant
     * @return liste des mains distribuees
     */
    public List<MainJoueur> distribuerDeuxCartesParJoueur(EtatPartie etat) {
        List<MainJoueur> mains = new ArrayList<>();
        for (Joueur joueur : etat.getJoueurs()) {
            if (etat.getPioche().size() < 2) {
                throw new IllegalStateException("Pas assez de cartes pour distribuer 2 cartes à " + joueur.getNom());
            }
            Carte c1 = etat.getPioche().pop();
            Carte c2 = etat.getPioche().pop();
            mains.add(new MainJoueur(c1, c2));
        }
        return mains;
    }

    /**
     * Cree les offres automatiquement a partir des mains distribuees.
     *
     * <p>Par defaut, la carte d'index 0 est cachee et celle d'index 1 est visible.</p>
     *
     * @param etat etat courant
     * @param mains mains distribuees
     */
    public void creerOffresAutomatiques(EtatPartie etat, List<MainJoueur> mains) {
        if (mains.size() != etat.getJoueurs().size()) {
            throw new IllegalArgumentException("Le nombre de mains ne correspond pas au nombre de joueurs.");
        }

        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur joueur = etat.getJoueurs().get(i);
            MainJoueur main = mains.get(i);

            Carte carteCachee = main.get(0);
            Carte carteVisible = main.get(1);

            joueur.definirOffre(new Offre(carteCachee, carteVisible));
        }
    }

    /**
     * Recupere la carte restante de chaque offre et la place dans le tas report.
     *
     * @param etat etat courant
     */
    public void recupererCartesRestantesDansTasReport(EtatPartie etat) {
        etat.getTasReport().clear();

        for (var joueur : etat.getJoueurs()) {
            var offre = joueur.getOffre();
            if (offre == null) {
                throw new IllegalStateException("Offre manquante pour " + joueur.getNom());
            }
            // Il doit rester exactement 1 carte à ce moment-là
            var restante = offre.carteRestante();
            if (restante == null) {
                throw new IllegalStateException("Aucune carte restante dans l'offre de " + joueur.getNom());
            }
            etat.getTasReport().add(restante);
        }
    }

    /**
     * Ajoute une carte de la pioche par joueur dans le tas report.
     *
     * @param etat etat courant
     */
    public void ajouterCartesPiocheEtReferenceAuTasReport(EtatPartie etat) {
        int n = etat.getJoueurs().size();

        // Ajouter n cartes de la pioche (une par joueur)
        for (int i = 0; i < n; i++) {
            if (etat.getPioche().isEmpty()) {
                throw new IllegalStateException("Pioche vide : impossible d'ajouter les cartes pour le round suivant.");
            }
            etat.getTasReport().add(etat.getPioche().pop());
        }
    }

    /**
     * Melange le tas report et redistribue deux cartes par joueur.
     *
     * @param etat etat courant
     * @return nouvelles mains distribuees
     */
    public List<MainJoueur> melangerEtRedistribuerDepuisTasReport(EtatPartie etat) {
        Collections.shuffle(etat.getTasReport(), alea);

        int n = etat.getJoueurs().size();
        if (etat.getTasReport().size() < 2 * n) {
            throw new IllegalStateException("TasReport insuffisant pour redistribuer 2 cartes par joueur.");
        }

        List<MainJoueur> mains = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < n; i++) {
            var c1 = etat.getTasReport().get(index++);
            var c2 = etat.getTasReport().get(index++);
            mains.add(new MainJoueur(c1, c2));
        }

        // après distribution, on vide le tasReport
        etat.getTasReport().clear();
        return mains;
    }

    /**
     * Demande a un joueur humain quelle carte rendre visible et cree son offre.
     *
     * @param etat etat courant
     * @param indexJoueur index du joueur
     * @param main main du joueur
     * @param console lecteur console pour poser la question
     */
    public void creerOffreHumaine(EtatPartie etat, int indexJoueur, MainJoueur main, LecteurConsole console) {
        var joueur = etat.getJoueurs().get(indexJoueur);

        int choixVisible = console.lireEntier("Quelle carte veux-tu rendre VISIBLE ?", 1, 2);
        int choixCachee = (choixVisible == 1) ? 2 : 1;

        var carteVisible = main.get(choixVisible - 1);
        var carteCachee = main.get(choixCachee - 1);

        joueur.definirOffre(new Offre(carteCachee, carteVisible));
    }

    /**
     * Cree automatiquement l'offre d'un joueur a partir de sa main.
     *
     * @param etat etat courant
     * @param indexJoueur index du joueur
     * @param main main du joueur
     */
    public void creerOffreAutomatiquePourJoueur(EtatPartie etat, int indexJoueur, MainJoueur main) {
        var joueur = etat.getJoueurs().get(indexJoueur);
        var carteCachee = main.get(0);
        var carteVisible = main.get(1);
        joueur.definirOffre(new Offre(carteCachee, carteVisible));
    }
}
