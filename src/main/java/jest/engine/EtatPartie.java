package jest.engine;

import jest.model.Carte;
import jest.model.Joueur;
import jest.model.Trophee;
import jest.rules.ReglesJeu;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Etat mutable d'une partie en cours.
 *
 * <p>Contient la pioche, les joueurs, les trophees et les parametres
 * necessaires pour piloter un round.</p>
 */
public class EtatPartie implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Liste des joueurs de la partie. */
    private final List<Joueur> joueurs;
    /** Pioche courante. */
    private final Deque<Carte> pioche;
    /** Trophees a attribuer. */
    private final List<Trophee> trophees;

    /** Regles de jeu appliquees. */
    private final ReglesJeu regles;
    /** Indique si l'extension est activee. */
    private final boolean extensionActivee;

    /** Nombre de joueurs controles par l'IA. */
    private final int nombreIA;

    /** Numero du round courant. */
    private int numeroRound;
    /** Tas report utilise entre deux rounds. */
    private final List<Carte> tasReport = new ArrayList<>();

    // Observateurs pour notifier les vues (pattern Observer simple).
    private transient List<Consumer<EtatPartie>> observateurs = new CopyOnWriteArrayList<>();

    /**
     * Construit un etat de partie.
     *
     * @param joueurs joueurs participants
     * @param pioche pioche initiale
     * @param trophees trophees disponibles
     * @param regles regles de jeu
     * @param extensionActivee activation de l'extension
     * @param nombreIA nombre de joueurs controles par l'IA
     */
    public EtatPartie(List<Joueur> joueurs,
                      Deque<Carte> pioche,
                      List<Trophee> trophees,
                      ReglesJeu regles,
                      boolean extensionActivee,
                      int nombreIA) {
        this.joueurs = new ArrayList<>(Objects.requireNonNull(joueurs));
        this.pioche = new ArrayDeque<>(Objects.requireNonNull(pioche));
        this.trophees = new ArrayList<>(Objects.requireNonNull(trophees));
        this.regles = Objects.requireNonNull(regles);
        this.extensionActivee = extensionActivee;
        this.nombreIA = nombreIA;
        this.numeroRound = 1;
    }

    // Compatibilité : anciens appels => 0 IA
    /**
     * Construit un etat de partie sans IA (compatibilite).
     *
     * @param joueurs joueurs participants
     * @param pioche pioche initiale
     * @param trophees trophees disponibles
     * @param regles regles de jeu
     * @param extensionActivee activation de l'extension
     */
    public EtatPartie(List<Joueur> joueurs,
                      Deque<Carte> pioche,
                      List<Trophee> trophees,
                      ReglesJeu regles,
                      boolean extensionActivee) {
        this(joueurs, pioche, trophees, regles, extensionActivee, 0);
    }

    /**
     * Retourne la liste des joueurs.
     *
     * @return joueurs
     */
    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    /**
     * Retourne la pioche courante.
     *
     * @return pioche
     */
    public Deque<Carte> getPioche() {
        return pioche;
    }

    /**
     * Retourne les trophees a attribuer.
     *
     * @return liste de trophees
     */
    public List<Trophee> getTrophees() {
        return trophees;
    }

    /**
     * Retourne les regles appliquees.
     *
     * @return regles
     */
    public ReglesJeu getRegles() {
        return regles;
    }

    /**
     * Indique si l'extension est activee.
     *
     * @return true si l'extension est activee
     */
    public boolean isExtensionActivee() {
        return extensionActivee;
    }

    /**
     * Retourne le nombre d'IA dans la partie.
     *
     * @return nombre d'IA
     */
    public int getNombreIA() {
        return nombreIA;
    }

    /**
     * Retourne le numero du round courant.
     *
     * @return numero de round
     */
    public int getNumeroRound() {
        return numeroRound;
    }

    /**
     * Incremente le numero du round courant.
     */
    public void incrementerRound() {
        numeroRound++;
    }

    /**
     * Indique si la pioche est vide.
     *
     * @return true si la pioche est vide
     */
    public boolean piocheVide() {
        return pioche.isEmpty();
    }

    /**
     * Retourne le tas report utilise entre deux rounds.
     *
     * @return tas report
     */
    public List<Carte> getTasReport() {
        return tasReport;
    }

    /**
     * Ajoute un observateur notifie lors des changements d'etat.
     *
     * @param observateur observateur a ajouter
     */
    public void ajouterObservateur(Consumer<EtatPartie> observateur) {
        observateurs.add(Objects.requireNonNull(observateur));
    }

    /**
     * Retire un observateur.
     *
     * @param observateur observateur a retirer
     */
    public void retirerObservateur(Consumer<EtatPartie> observateur) {
        observateurs.remove(observateur);
    }

    /**
     * Notifie les observateurs d'un changement d'etat.
     */
    public void notifierChangement() {
        if (observateurs == null) {
            return;
        }
        for (var obs : observateurs) {
            obs.accept(this);
        }
    }

    /**
     * Reinitialise la liste d'observateurs lors de la deserialisation.
     *
     * @param ois flux d'entree
     * @throws java.io.IOException si une erreur IO survient
     * @throws ClassNotFoundException si une classe est introuvable
     */
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        observateurs = new CopyOnWriteArrayList<>();
    }
}
