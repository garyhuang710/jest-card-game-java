package jest.mvc;

import jest.engine.AttributionTrophees;
import jest.engine.ConfigurationPartie;
import jest.engine.EtatPartie;
import jest.engine.FabriquePartie;
import jest.engine.GestionRound;
import jest.engine.GestionSauvegarde;
import jest.engine.OrdreDeJeu;
import jest.engine.Prise;
import jest.ia.ActionPrise;
import jest.ia.StrategieCoherente;
import jest.model.Joueur;
import jest.model.MainJoueur;
import jest.model.Offre;
import jest.rules.ReglesJeu;
import jest.score.CalculateurScore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controleur principal qui coordonne l'etat de jeu et les vues.
 */
public class JeuController {

    private final GestionRound gestionRound = new GestionRound();
    private final Prise prise = new Prise();
    private final OrdreDeJeu ordre = new OrdreDeJeu();
    private final GestionSauvegarde sauvegarde = new GestionSauvegarde();
    private final AttributionTrophees attributionTrophees = new AttributionTrophees();

    private final List<GameView> vues = new CopyOnWriteArrayList<>();

    private EtatPartie etat;
    private PhaseJeu phase = PhaseJeu.ATTENTE_INITIALISATION;
    private List<MainJoueur> mainsCourantes = new ArrayList<>();
    private Set<Integer> prisesEffectuees = new HashSet<>();
    private int joueurActif = -1;

    /**
     * Construit un controleur de jeu.
     */
    public JeuController() {
    }

    /**
     * Enregistre une vue et la rafraichit si une partie est deja chargee.
     *
     * @param vue vue a enregistrer
     */
    public synchronized void enregistrerVue(GameView vue) {
        vues.add(Objects.requireNonNull(vue));
        if (etat != null) {
            vue.rafraichir(etat, phase, joueurActif, new ArrayList<>(mainsCourantes));
        }
    }

    /**
     * Retourne la phase courante.
     *
     * @return phase courante
     */
    public synchronized PhaseJeu getPhase() {
        return phase;
    }

    /**
     * Retourne l'index du joueur actif.
     *
     * @return index du joueur actif
     */
    public synchronized int getJoueurActif() {
        return joueurActif;
    }

    /**
     * Retourne une copie des mains courantes.
     *
     * @return liste des mains courantes
     */
    public synchronized List<MainJoueur> getMainsCourantes() {
        return new ArrayList<>(mainsCourantes);
    }

    /**
     * Demarre une nouvelle partie a partir d'une configuration.
     *
     * @param config configuration de la partie
     * @param noms noms des joueurs
     */
    public synchronized void nouvellePartie(ConfigurationPartie config, List<String> noms) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(noms);
        FabriquePartie fabrique = new FabriquePartie();
        etat = fabrique.creerNouvellePartie(config, noms);
        reattacherObservateurs();
        attribuerStrategieAuxIA();
        phase = PhaseJeu.EN_ATTENTE_ROUND;
        joueurActif = -1;
        mainsCourantes = new ArrayList<>();
        notifierChangement();
        log("Nouvelle partie créée (" + noms.size() + " joueurs).");
    }

    /**
     * Charge une partie depuis un fichier.
     *
     * @param fichier chemin du fichier
     * @throws IOException si la lecture echoue
     * @throws ClassNotFoundException si une classe serialisee manque
     */
    public synchronized void chargerPartie(String fichier) throws IOException, ClassNotFoundException {
        etat = sauvegarde.charger(fichier);
        reattacherObservateurs();
        attribuerStrategieAuxIA();
        phase = PhaseJeu.EN_ATTENTE_ROUND;
        joueurActif = -1;
        mainsCourantes = new ArrayList<>();
        notifierChangement();
        log("Partie chargée depuis " + fichier);
    }

    /**
     * Sauvegarde la partie en cours.
     *
     * @param fichier chemin du fichier de sauvegarde
     * @throws IOException si l'ecriture echoue
     */
    public synchronized void sauvegarderPartie(String fichier) throws IOException {
        if (etat == null) {
            throw new IllegalStateException("Aucune partie en cours à sauvegarder.");
        }
        sauvegarde.sauvegarder(etat, fichier);
        log("Sauvegarde effectuée dans " + fichier);
    }

    /**
     * Demarre un nouveau round si l'etat le permet.
     */
    public synchronized void demarrerRound() {
        if (etat == null) {
            log("Aucune partie en cours : crée ou charge une partie avant de démarrer.");
            return;
        }
        if (phase != PhaseJeu.EN_ATTENTE_ROUND) {
            log("Round déjà démarré ou partie terminée. Phase actuelle: " + phase);
            return;
        }

        try {
            mainsCourantes = gestionRound.distribuerDeuxCartesParJoueur(etat);
            prisesEffectuees = new HashSet<>();
            joueurActif = -1;

            appliquerOffresIA();

            boolean offresCompletes = toutesOffresCompletes();
            phase = offresCompletes ? PhaseJeu.PRISES : PhaseJeu.CHOIX_VISIBLES;

            if (phase == PhaseJeu.PRISES) {
                initialiserPrises();
            }

            notifierChangement();
            log("Round " + etat.getNumeroRound() + " démarré.");
        } catch (Exception e) {
            log("Erreur lors du démarrage du round : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retourne la main d'un joueur si disponible.
     *
     * @param indexJoueur index du joueur
     * @return main ou {@code null}
     */
    public synchronized MainJoueur getMainPour(int indexJoueur) {
        if (indexJoueur < 0 || indexJoueur >= mainsCourantes.size()) {
            return null;
        }
        return mainsCourantes.get(indexJoueur);
    }

    /**
     * Enregistre le choix de la carte visible pour un joueur humain.
     *
     * @param indexJoueur index du joueur
     * @param indexVisible index de la carte visible
     */
    public synchronized void choisirCarteVisible(int indexJoueur, int indexVisible) {
        if (phase != PhaseJeu.CHOIX_VISIBLES) {
            throw new IllegalStateException("Pas en phase de choix des cartes visibles.");
        }
        var main = mainsCourantes.get(indexJoueur);
        if (main == null) {
            throw new IllegalStateException("Aucune main disponible pour ce joueur.");
        }
        if (indexVisible < 0 || indexVisible >= main.getCartes().size()) {
            throw new IllegalArgumentException("Index de carte visible invalide.");
        }
        int indexCachee = (indexVisible == 0) ? 1 : 0;
        var carteVisible = main.get(indexVisible);
        var carteCachee = main.get(indexCachee);
        etat.getJoueurs().get(indexJoueur).definirOffre(new Offre(carteCachee, carteVisible));
        mainsCourantes.set(indexJoueur, null);
        log(etat.getJoueurs().get(indexJoueur).getNom() + " rend " + carteVisible + " visible.");

        if (toutesOffresCompletes()) {
            phase = PhaseJeu.PRISES;
            initialiserPrises();
        }

        notifierChangement();
    }

    /**
     * Applique une prise pour le joueur actif.
     *
     * @param cible index du joueur cible
     * @param prendreVisible true pour prendre la carte visible
     */
    public synchronized void jouerPrise(int cible, boolean prendreVisible) {
        if (phase != PhaseJeu.PRISES) {
            throw new IllegalStateException("Pas en phase de prises.");
        }
        if (joueurActif < 0) {
            throw new IllegalStateException("Aucun joueur actif.");
        }
        if (cible == joueurActif && autreOffreCompleteDisponible(joueurActif)) {
            throw new IllegalArgumentException("On ne peut pas prendre sur sa propre offre tant qu'une autre offre complète existe.");
        }
        appliquerPrise(new ActionPrise(cible, prendreVisible));
    }

    /**
     * Retourne la liste des cibles valides pour le joueur actif.
     *
     * @return index des cibles possibles
     */
    public synchronized List<Integer> ciblesPossibles() {
        List<Integer> cibles = new ArrayList<>();
        if (etat == null) {
            return cibles;
        }
        boolean autresCompletes = false;
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            var offre = etat.getJoueurs().get(i).getOffre();
            if (offre != null && offre.estComplete()) {
                cibles.add(i);
                if (joueurActif >= 0 && i != joueurActif) {
                    autresCompletes = true;
                }
            }
        }
        if (joueurActif >= 0 && autresCompletes) {
            cibles.removeIf(idx -> idx == joueurActif);
        }
        return cibles;
    }

    /**
     * Indique si une autre offre complete est disponible pour un joueur.
     *
     * @param actif index du joueur actif
     * @return true si une autre offre complete existe
     */
    private boolean autreOffreCompleteDisponible(int actif) {
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (i == actif) continue;
            var offre = etat.getJoueurs().get(i).getOffre();
            if (offre != null && offre.estComplete()) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // Internes

    /**
     * Reattache les observateurs apres chargement d'une partie.
     */
    private void reattacherObservateurs() {
        if (etat == null) return;
        etat.ajouterObservateur(e -> informerVues());
    }

    /**
     * Notifie les observateurs de l'etat.
     */
    private void notifierChangement() {
        if (etat != null) {
            etat.notifierChangement();
        }
    }

    /**
     * Rafraichit toutes les vues avec un snapshot des mains.
     */
    private void informerVues() {
        List<MainJoueur> snapshot = new ArrayList<>(mainsCourantes);
        for (var vue : vues) {
            vue.rafraichir(etat, phase, joueurActif, snapshot);
        }
    }

    /**
     * Envoie un message de log a toutes les vues.
     *
     * @param message message a diffuser
     */
    private void log(String message) {
        for (var vue : vues) {
            vue.log(message);
        }
    }

    /**
     * Verifie si toutes les offres sont completes.
     *
     * @return true si toutes les offres sont completes
     */
    private boolean toutesOffresCompletes() {
        for (var joueur : etat.getJoueurs()) {
            var offre = joueur.getOffre();
            if (offre == null || !offre.estComplete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Applique les choix de cartes visibles pour les IA.
     */
    private void appliquerOffresIA() {
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            Joueur joueur = etat.getJoueurs().get(i);
            if (!joueur.estIA()) {
                continue;
            }
            var strategie = joueur.getStrategie();
            if (strategie == null) {
                throw new IllegalStateException("IA sans stratégie pour " + joueur.getNom());
            }
            var main = mainsCourantes.get(i);
            int indexVisible = strategie.choisirIndexVisible(main, etat, i);
            int indexCachee = (indexVisible == 0) ? 1 : 0;
            joueur.definirOffre(new Offre(main.get(indexCachee), main.get(indexVisible)));
            mainsCourantes.set(i, null);
            log("IA " + joueur.getNom() + " rend visible " + joueur.getOffre().getCarteVisible());
        }
    }

    /**
     * Initialise la phase de prises et declenche la boucle IA si necessaire.
     */
    private void initialiserPrises() {
        prisesEffectuees.clear();
        joueurActif = ordre.determinerPremierJoueur(etat);
        log("Premier joueur pour les prises : " + etat.getJoueurs().get(joueurActif).getNom());
        avancerIABoucle();
    }

    /**
     * Execute les prises automatiques tant que le joueur actif est une IA.
     */
    private void avancerIABoucle() {
        while (phase == PhaseJeu.PRISES && joueurActif >= 0 && etat.getJoueurs().get(joueurActif).estIA()) {
            var strat = etat.getJoueurs().get(joueurActif).getStrategie();
            if (strat == null) {
                throw new IllegalStateException("IA sans stratégie lors des prises.");
            }
            ActionPrise action = strat.choisirActionPrise(etat, joueurActif);
            appliquerPrise(action);
        }
    }

    /**
     * Applique une action de prise et met a jour l'etat.
     *
     * @param action action de prise
     */
    private void appliquerPrise(ActionPrise action) {
        int actif = joueurActif;
        var carte = prise.prendreCarte(etat, actif, action.getIndexCible(), action.isPrendreVisible());
        log(etat.getJoueurs().get(actif).getNom() + " prend " + carte + " chez " + etat.getJoueurs().get(action.getIndexCible()).getNom());
        prisesEffectuees.add(actif);

        // Si aucune offre complète ne reste, on clôt immédiatement le round
        if (!existeOffreComplete()) {
            cloturerRound();
            notifierChangement();
            return;
        }

        if (prisesEffectuees.size() == etat.getJoueurs().size()) {
            cloturerRound();
            notifierChangement();
            return;
        }

        if (action.getIndexCible() != actif && !prisesEffectuees.contains(action.getIndexCible())) {
            joueurActif = action.getIndexCible();
        } else {
            joueurActif = meilleurJoueurRestantAvecOffreComplete();
        }

        notifierChangement();
        avancerIABoucle();
    }

    /**
     * Indique si au moins une offre complete existe.
     *
     * @return true si une offre complete existe
     */
    private boolean existeOffreComplete() {
        for (var joueur : etat.getJoueurs()) {
            var offre = joueur.getOffre();
            if (offre != null && offre.estComplete()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne le meilleur joueur restant avec une offre complete.
     *
     * @return index du meilleur joueur restant
     */
    private int meilleurJoueurRestantAvecOffreComplete() {
        int meilleur = -1;
        ReglesJeu regles = etat.getRegles();
        for (int i = 0; i < etat.getJoueurs().size(); i++) {
            if (prisesEffectuees.contains(i)) {
                continue;
            }
            var offre = etat.getJoueurs().get(i).getOffre();
            if (offre == null || !offre.estComplete()) {
                continue;
            }
            if (meilleur == -1) {
                meilleur = i;
            } else {
                int cmp = regles.comparerCartesVisibles(offre.getCarteVisible(), etat.getJoueurs().get(meilleur).getOffre().getCarteVisible());
                if (cmp > 0) {
                    meilleur = i;
                }
            }
        }
        if (meilleur == -1) {
            throw new IllegalStateException("Aucun joueur restant avec offre complète.");
        }
        return meilleur;
    }

    /**
     * Cloture le round et prepare le suivant ou termine la partie.
     */
    private void cloturerRound() {
        // Si la pioche n'a plus assez de cartes pour préparer un nouveau round, on termine la partie
        if (etat.getPioche().size() < etat.getJoueurs().size()) {
            prendreDernieresCartesEtTerminer();
            return;
        }

        gestionRound.recupererCartesRestantesDansTasReport(etat);
        gestionRound.ajouterCartesPiocheEtReferenceAuTasReport(etat);
        mainsCourantes = gestionRound.melangerEtRedistribuerDepuisTasReport(etat);
        nettoyerOffres();
        etat.incrementerRound();
        phase = PhaseJeu.CHOIX_VISIBLES;
        joueurActif = -1; // pas de joueur actif pendant le choix des visibles
        appliquerOffresIA();
        if (toutesOffresCompletes()) {
            phase = PhaseJeu.PRISES;
            initialiserPrises();
        }
    }

    /**
     * Distribue les dernieres cartes et termine la partie.
     */
    private void prendreDernieresCartesEtTerminer() {
        for (var joueur : etat.getJoueurs()) {
            var restante = joueur.getOffre().carteRestante();
            joueur.getJest().ajouter(restante);
            joueur.definirOffre(null);
        }
        attributionTrophees.attribuerTousLesTrophees(etat);
        phase = PhaseJeu.TERMINEE;
        mainsCourantes = new ArrayList<>();
        joueurActif = -1;
        log("Fin de partie : pioche vide.");
        afficherScoresFinaux();
    }

    /**
     * Nettoie les offres pour un nouveau round.
     */
    private void nettoyerOffres() {
        for (var joueur : etat.getJoueurs()) {
            joueur.definirOffre(null);
        }
    }

    /**
     * Assigne une strategie par defaut aux joueurs IA.
     */
    private void attribuerStrategieAuxIA() {
        if (etat == null) return;
        int n = etat.getJoueurs().size();
        int nbIA = etat.getNombreIA();
        for (int i = n - nbIA; i < n; i++) {
            if (i >= 0) {
                etat.getJoueurs().get(i).setStrategie(new StrategieCoherente());
            }
        }
    }

    /**
     * Calcule et affiche les scores finaux.
     */
    private void afficherScoresFinaux() {
        CalculateurScore calc = new CalculateurScore();
        Joueur gagnant = null;
        int scoreMax = Integer.MIN_VALUE;
        
        for (var joueur : etat.getJoueurs()) {
            int score = calc.calculerScore(joueur.getJest(), etat.getRegles());
            log("Score final " + joueur.getNom() + " = " + score);
            if (score > scoreMax) {
                scoreMax = score;
                gagnant = joueur;
            }
        }
        
        if (gagnant != null) {
            log("");
            log("====================================");
            log("  " + gagnant.getNom().toUpperCase() + " GAGNE AVEC " + scoreMax + " POINTS !");
            log("====================================");
        }
    }
}
