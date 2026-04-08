package jest.ui.gui;

import jest.engine.ConfigurationPartie;
import jest.engine.EtatPartie;
import jest.model.Joueur;
import jest.model.MainJoueur;
import jest.mvc.GameView;
import jest.mvc.JeuController;
import jest.mvc.PhaseJeu;
import jest.rules.ReglesDeBase;
import jest.rules.ReglesJeu;
import jest.rules.ReglesVarianteCarreauxPositifs;
import jest.rules.ReglesVarianteJokerFixe;
import jest.score.CalculateurScore;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vue graphique Swing.
 */
public class VueSwing extends JFrame implements GameView {

    /** Controleur de jeu. */
    private final JeuController controller;
    /** Zone de texte d'affichage. */
    private final JTextArea displayArea = new JTextArea(10, 80);
    /** Etiquette affichant la taille de la pioche. */
    private final JLabel piocheLabel = new JLabel("Pioche : --");
    /** Etiquette affichant le gagnant. */
    private final JLabel winnerLabel = new JLabel("Gagnant : --");
    /** Etiquette centrale du gagnant. */
    private final JLabel winnerCenterLabel = new JLabel("");
    /** Etiquette affichant le tour. */
    private final JLabel tourLabel = new JLabel("Tour : --");
    /** Panneau des offres. */
    private final JPanel offresPanel = new JPanel();
    /** Panneau des boutons. */
    private final JPanel boutonPanel = new JPanel();
    /** Calculateur de score. */
    private final CalculateurScore calculateurScore = new CalculateurScore();
    
    /** Etat courant affiche. */
    private EtatPartie etatCourant;
    /** Phase courante. */
    private PhaseJeu phaseCourante;
    /** Index du joueur actif courant. */
    private int joueurActifCourant = -1;
    /** Mains distribuees pour la phase de choix. */
    private List<MainJoueur> mainsCourantes = new ArrayList<>();

    /**
     * Construit la vue graphique.
     *
     * @param controller controleur de jeu
     */
    public VueSwing(JeuController controller) {
        super("Jest - GUI");
        this.controller = Objects.requireNonNull(controller);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Panneau d'informations en haut
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(1200, 150));
        piocheLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        piocheLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        winnerLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        winnerLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        tourLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        tourLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(piocheLabel, BorderLayout.WEST);
        headerPanel.add(tourLabel, BorderLayout.CENTER);
        headerPanel.add(winnerLabel, BorderLayout.EAST);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Panneau des offres au centre avec bannière de gagnant
        offresPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        offresPanel.setBackground(new Color(220, 240, 220));
        JScrollPane offreScroll = new JScrollPane(offresPanel);
        offreScroll.setBorder(BorderFactory.createTitledBorder("Offres des joueurs"));

        winnerCenterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerCenterLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        winnerCenterLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel winnerPanel = new JPanel(new BorderLayout());
        winnerPanel.add(winnerCenterLabel, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(winnerPanel, BorderLayout.NORTH);
        centerWrapper.add(offreScroll, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Panneau des boutons en bas
        boutonPanel.setLayout(new FlowLayout());
        add(boutonPanel, BorderLayout.SOUTH);

        afficherMenuPrincipal();
    }

    /**
     * Ouvre la fenetre Swing.
     */
    public void ouvrir() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    /**
     * Rafraichit l'etat affiche.
     *
     * @param etat etat courant
     * @param phase phase courante
     * @param joueurActif index du joueur actif
     * @param mainsCourantes mains distribuees
     */
    @Override
    public void rafraichir(EtatPartie etat, PhaseJeu phase, int joueurActif, List<MainJoueur> mainsCourantes) {
        SwingUtilities.invokeLater(() -> {
            this.etatCourant = etat;
            this.phaseCourante = phase;
            this.joueurActifCourant = joueurActif;
            this.mainsCourantes = new ArrayList<>(mainsCourantes);
            afficherEtat();
        });
    }

    /**
     * Affiche un message de log dans la zone de texte.
     *
     * @param message message a afficher
     */
    @Override
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            displayArea.append("[LOG] " + message + "\n");
            displayArea.setCaretPosition(displayArea.getDocument().getLength());
        });
    }

    /**
     * Affiche le menu principal dans le panneau de boutons.
     */
    private void afficherMenuPrincipal() {
        boutonPanel.removeAll();
        
        JButton btnNew = new JButton("Nouvelle partie");
        btnNew.addActionListener(e -> creerNouvellePartieGUI());
        boutonPanel.add(btnNew);
        
        JButton btnLoad = new JButton("Charger partie");
        btnLoad.addActionListener(e -> charger());
        boutonPanel.add(btnLoad);
        
        JButton btnQuit = new JButton("Quitter");
        btnQuit.addActionListener(e -> System.exit(0));
        boutonPanel.add(btnQuit);
        
        boutonPanel.revalidate();
        boutonPanel.repaint();
    }

    /**
     * Met a jour l'affichage principal selon l'etat courant.
     */
    private void afficherEtat() {
        if (etatCourant == null) {
            displayArea.setText("Aucune partie en cours.\n\nUtilise les boutons ci-dessous pour commencer.");
            piocheLabel.setText("Pioche : --");
            winnerLabel.setText("Gagnant : --");
            winnerCenterLabel.setText("");
            tourLabel.setText("Tour : --");
            offresPanel.removeAll();
            offresPanel.revalidate();
            offresPanel.repaint();
            afficherMenuPrincipal();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== JEST ===  ");
        sb.append("ROUND ").append(etatCourant.getNumeroRound());
        sb.append(" / ").append(etatCourant.getRegles().nomVariante());
        sb.append(" / extension=").append(etatCourant.isExtensionActivee() ? "OUI" : "NON").append("\n");
        sb.append("Phase : ").append(phaseCourante).append("  |  ");
        sb.append("Pioche : ").append(etatCourant.getPioche().size()).append(" cartes  |  ");
        sb.append("Trophées : ").append(etatCourant.getTrophees());
        sb.append("\n");
        
        if (joueurActifCourant >= 0 && joueurActifCourant < etatCourant.getJoueurs().size()) {
            sb.append("\n>>> Joueur actif : ").append(etatCourant.getJoueurs().get(joueurActifCourant).getNom()).append(" <<<\n");
        }

        // Ajouter les informations de phase ici plutôt que dans afficherBoutons
        if (phaseCourante == PhaseJeu.CHOIX_VISIBLES) {
            construireTexteChoixVisibles(sb);
        } else if (phaseCourante == PhaseJeu.PRISES) {
            construireTextePrises(sb);
        } else if (phaseCourante == PhaseJeu.TERMINEE) {
            sb.append("\n=== PARTIE TERMINEE ===\n");
            afficherScoresFinaux(sb);
        }

        displayArea.setText(sb.toString());
        piocheLabel.setText("Pioche : " + etatCourant.getPioche().size() + (etatCourant.getPioche().size() > 1 ? " cartes" : " carte"));
        if (joueurActifCourant >= 0 && joueurActifCourant < etatCourant.getJoueurs().size()) {
            tourLabel.setText("Tour : " + etatCourant.getJoueurs().get(joueurActifCourant).getNom());
        } else {
            tourLabel.setText("Tour : --");
        }

        if (phaseCourante == PhaseJeu.TERMINEE) {
            Joueur gagnant = calculerGagnant();
            String texteGagnant = (gagnant != null ? ("Gagnant : " + gagnant.getNom()) : "Gagnant : --");
            winnerLabel.setText(texteGagnant);
            winnerCenterLabel.setText(texteGagnant.toUpperCase());
        } else {
            winnerLabel.setText("Gagnant : --");
            winnerCenterLabel.setText("");
        }
        
        // Afficher les offres graphiquement
        afficherOffresGraphiques();
        
        afficherBoutons();
    }

    /**
     * Affiche les offres sous forme graphique.
     */
    private void afficherOffresGraphiques() {
        offresPanel.removeAll();
        
        if (etatCourant != null) {
            for (Joueur joueur : etatCourant.getJoueurs()) {
                OffrePanel offrePanel = new OffrePanel(joueur);
                offresPanel.add(offrePanel);
            }
        }
        
        offresPanel.revalidate();
        offresPanel.repaint();
    }

    /**
     * Ajoute les scores finaux dans le texte d'affichage.
     *
     * @param sb buffer a completer
     */
    private void afficherScoresFinaux(StringBuilder sb) {
        if (etatCourant == null) return;
        
        sb.append("\nScores finaux :\n");
        Joueur gagnant = calculerGagnant();
        int scoreMax = Integer.MIN_VALUE;
        for (Joueur j : etatCourant.getJoueurs()) {
            int score = calculateurScore.calculerScore(j.getJest(), etatCourant.getRegles());
            sb.append("  ").append(j.getNom()).append(" : ").append(score).append(" points\n");
            if (score > scoreMax) {
                scoreMax = score;
            }
        }

        if (gagnant != null) {
            sb.append("\n");
            sb.append("====================================\n");
            sb.append("  ").append(gagnant.getNom().toUpperCase()).append(" GAGNE !\n");
            sb.append("  Score : ").append(scoreMax).append(" points\n");
            sb.append("====================================\n");
        }
    }

    /**
     * Calcule le joueur gagnant selon le score.
     *
     * @return joueur gagnant ou {@code null}
     */
    private Joueur calculerGagnant() {
        Joueur gagnant = null;
        int scoreMax = Integer.MIN_VALUE;
        for (Joueur j : etatCourant.getJoueurs()) {
            int score = calculateurScore.calculerScore(j.getJest(), etatCourant.getRegles());
            if (score > scoreMax) {
                scoreMax = score;
                gagnant = j;
            }
        }
        return gagnant;
    }

    /**
     * Met a jour les boutons selon la phase.
     */
    private void afficherBoutons() {
        boutonPanel.removeAll();

        if (phaseCourante == PhaseJeu.ATTENTE_INITIALISATION || etatCourant == null) {
            afficherMenuPrincipal();
            return;
        }

        if (phaseCourante == PhaseJeu.EN_ATTENTE_ROUND) {
            JButton btnStart = new JButton("Démarrer le round");
            btnStart.addActionListener(e -> controller.demarrerRound());
            boutonPanel.add(btnStart);

            JButton btnSave = new JButton("Sauvegarder");
            btnSave.addActionListener(e -> sauvegarder());
            boutonPanel.add(btnSave);

            JButton btnQuit = new JButton("Quitter");
            btnQuit.addActionListener(e -> System.exit(0));
            boutonPanel.add(btnQuit);
        }

        if (phaseCourante == PhaseJeu.CHOIX_VISIBLES) {
            afficherBoutonsChoixVisible();
        }

        if (phaseCourante == PhaseJeu.PRISES) {
            afficherBoutonsPrise();
        }

        if (phaseCourante == PhaseJeu.TERMINEE) {
            JButton btnNew = new JButton("Nouvelle partie");
            btnNew.addActionListener(e -> creerNouvellePartieGUI());
            boutonPanel.add(btnNew);

            JButton btnSave = new JButton("Sauvegarder");
            btnSave.addActionListener(e -> sauvegarder());
            boutonPanel.add(btnSave);

            JButton btnQuit = new JButton("Quitter");
            btnQuit.addActionListener(e -> System.exit(0));
            boutonPanel.add(btnQuit);
        }

        boutonPanel.revalidate();
        boutonPanel.repaint();
    }

    /**
     * Construit le texte d'aide pour le choix des visibles.
     *
     * @param sb buffer a completer
     */
    private void construireTexteChoixVisibles(StringBuilder sb) {
        for (int i = 0; i < etatCourant.getJoueurs().size(); i++) {
            Joueur j = etatCourant.getJoueurs().get(i);
            if (j.estIA() || (j.getOffre() != null && j.getOffre().estComplete())) {
                continue;
            }
            
            if (i >= mainsCourantes.size() || mainsCourantes.get(i) == null) {
                continue;
            }

            MainJoueur main = mainsCourantes.get(i);
            sb.append("\n").append(j.getNom()).append(" : tes cartes sont :\n");
            sb.append("  1) ").append(main.get(0)).append("\n");
            sb.append("  2) ").append(main.get(1)).append("\n");
        }
    }

    /**
     * Construit le texte d'aide pour la phase de prises.
     *
     * @param sb buffer a completer
     */
    private void construireTextePrises(StringBuilder sb) {
        if (joueurActifCourant < 0 || joueurActifCourant >= etatCourant.getJoueurs().size()) {
            return;
        }

        Joueur actif = etatCourant.getJoueurs().get(joueurActifCourant);
        if (actif.estIA()) {
            return;
        }

        sb.append("\n\nTOUR DE ").append(actif.getNom()).append("\n");
        sb.append("Choisis une cible :\n");

        for (int i = 0; i < etatCourant.getJoueurs().size(); i++) {
            Joueur j = etatCourant.getJoueurs().get(i);
            if (j.getOffre() == null || !j.getOffre().estComplete()) {
                continue;
            }

            sb.append("  ").append(i).append(" - ").append(j.getNom()).append(" : ").append(j.getOffre()).append("\n");
        }
    }

    /**
     * Affiche les boutons pour choisir une carte visible.
     */
    private void afficherBoutonsChoixVisible() {
        for (int i = 0; i < etatCourant.getJoueurs().size(); i++) {
            Joueur j = etatCourant.getJoueurs().get(i);
            if (j.estIA() || (j.getOffre() != null && j.getOffre().estComplete())) {
                continue;
            }
            
            if (i >= mainsCourantes.size() || mainsCourantes.get(i) == null) {
                continue;
            }

            MainJoueur main = mainsCourantes.get(i);
            final int joueurIdx = i;
            
            // Créer un panneau pour chaque carte avec bouton
            for (int cardIdx = 0; cardIdx < 2; cardIdx++) {
                JPanel carteAvecBouton = new JPanel();
                carteAvecBouton.setLayout(new BoxLayout(carteAvecBouton, BoxLayout.Y_AXIS));
                carteAvecBouton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                // Afficher la carte graphiquement
                CartePanel cartePanel = new CartePanel(main.get(cardIdx), false);
                cartePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                carteAvecBouton.add(cartePanel);
                
                carteAvecBouton.add(Box.createRigidArea(new Dimension(0, 5)));
                
                // Bouton sous la carte
                final int finalCardIdx = cardIdx;
                JButton btn = new JButton(j.getNom() + " : Rendre visible");
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.addActionListener(e -> controller.choisirCarteVisible(joueurIdx, finalCardIdx));
                carteAvecBouton.add(btn);
                
                boutonPanel.add(carteAvecBouton);
            }
        }
    }

    /**
     * Affiche les boutons de prise pour le joueur humain actif.
     */
    private void afficherBoutonsPrise() {
        if (joueurActifCourant < 0 || joueurActifCourant >= etatCourant.getJoueurs().size()) {
            return;
        }

        Joueur actif = etatCourant.getJoueurs().get(joueurActifCourant);
        if (actif.estIA()) {
            return;
        }

        // Compter les autres offres complètes
        int offresCompletesAutres = 0;
        for (int i = 0; i < etatCourant.getJoueurs().size(); i++) {
            if (i == joueurActifCourant) continue;
            Joueur j = etatCourant.getJoueurs().get(i);
            if (j.getOffre() != null && j.getOffre().estComplete()) {
                offresCompletesAutres++;
            }
        }

        for (int i = 0; i < etatCourant.getJoueurs().size(); i++) {
            Joueur j = etatCourant.getJoueurs().get(i);
            if (j.getOffre() == null || !j.getOffre().estComplete()) {
                continue;
            }

            boolean peutPrendreSoi = (i == joueurActifCourant && offresCompletesAutres == 0);
            if (i == joueurActifCourant && !peutPrendreSoi) {
                continue;
            }

            final int cibleIdx = i;
            String nomCible = (i == joueurActifCourant) ? "toi-même" : j.getNom();
            
            JButton btnVisible = new JButton("Prendre chez " + nomCible + " (VISIBLE)");
            btnVisible.addActionListener(e -> controller.jouerPrise(cibleIdx, true));
            boutonPanel.add(btnVisible);

            JButton btnCachee = new JButton("Prendre chez " + nomCible + " (CACHEE)");
            btnCachee.addActionListener(e -> controller.jouerPrise(cibleIdx, false));
            boutonPanel.add(btnCachee);
        }
    }

    /**
     * Lance la creation d'une nouvelle partie via des boites de dialogue.
     */
    private void creerNouvellePartieGUI() {
        try {
            int nbJ = lireEntier("Nombre total de joueurs (3-4)", 3, 4);
            int nbIA = lireEntier("Nombre de joueurs IA (0-" + (nbJ - 1) + ")", 0, nbJ - 1);
            ReglesJeu regles = lireRegles();
            boolean extension = lireOuiNon("Activer l'extension (CINQ) ?");
            List<String> noms = lireNoms(nbJ);

            ConfigurationPartie config = new ConfigurationPartie(nbJ, nbIA, regles, extension);
            controller.nouvellePartie(config, noms);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Configuration annulée ou invalide : " + ex.getMessage());
        }
    }

    /**
     * Lit un entier via une boite de dialogue.
     *
     * @param message message affiche
     * @param min borne minimale
     * @param max borne maximale
     * @return valeur saisie
     */
    private int lireEntier(String message, int min, int max) {
        while (true) {
            String s = JOptionPane.showInputDialog(this, message, min);
            if (s == null) {
                throw new IllegalStateException("Saisie annulée");
            }
            try {
                int v = Integer.parseInt(s.trim());
                if (v < min || v > max) {
                    JOptionPane.showMessageDialog(this, "Valeur hors limites: " + min + "-" + max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Entrez un entier valide.");
            }
        }
    }

    /**
     * Lit une confirmation oui/non.
     *
     * @param message message affiche
     * @return true si oui
     */
    private boolean lireOuiNon(String message) {
        int res = JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    /**
     * Lit la variante de regles choisie.
     *
     * @return regles correspondantes
     */
    private ReglesJeu lireRegles() {
        String[] options = {"BASE", "CARREAUX_POSITIFS", "JOKER_FIXE"};
        int idx = JOptionPane.showOptionDialog(this, "Choisis la variante", "Variante",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (idx == 1) return new ReglesVarianteCarreauxPositifs();
        if (idx == 2) return new ReglesVarianteJokerFixe();
        return new ReglesDeBase();
    }

    /**
     * Lit les noms des joueurs.
     *
     * @param nbJ nombre de joueurs
     * @return liste des noms
     */
    private List<String> lireNoms(int nbJ) {
        List<String> noms = new ArrayList<>();
        for (int i = 0; i < nbJ; i++) {
            String nom = JOptionPane.showInputDialog(this, "Nom du joueur " + (i + 1), "J" + (i + 1));
            if (nom == null || nom.isBlank()) {
                throw new IllegalStateException("Nom manquant pour le joueur " + (i + 1));
            }
            noms.add(nom.trim());
        }
        return noms;
    }

    /**
     * Ouvre une boite de dialogue pour sauvegarder la partie.
     */
    private void sauvegarder() {
        String path = JOptionPane.showInputDialog(this, "Fichier de sauvegarde", "sauvegarde_jest.dat");
        if (path == null || path.isBlank()) {
            return;
        }
        try {
            controller.sauvegarderPartie(path.trim());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de sauvegarde : " + ex.getMessage());
        }
    }

    /**
     * Ouvre une boite de dialogue pour charger une partie.
     */
    private void charger() {
        String path = JOptionPane.showInputDialog(this, "Charger depuis", "sauvegarde_jest.dat");
        if (path == null || path.isBlank()) {
            return;
        }
        try {
            controller.chargerPartie(path.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement : " + ex.getMessage());
        }
    }
}
