package jest.ui.gui;

import jest.model.Carte;
import jest.model.Couleur;
import jest.model.Joker;
import jest.model.Rang;

import javax.swing.*;
import java.awt.*;

/**
 * Composant Swing affichant une carte.
 */
public class CartePanel extends JPanel {
    /** Carte affichee. */
    private Carte carte;
    /** Indique si la carte est cachee. */
    private boolean cachee;
    private static final int LARGEUR = 60;
    private static final int HAUTEUR = 90;

    /**
     * Construit un panneau de carte.
     *
     * @param carte carte a afficher
     * @param cachee true si la carte est cachee
     */
    public CartePanel(Carte carte, boolean cachee) {
        this.carte = carte;
        this.cachee = cachee;
        setPreferredSize(new Dimension(LARGEUR, HAUTEUR));
        setMinimumSize(new Dimension(LARGEUR, HAUTEUR));
        setMaximumSize(new Dimension(LARGEUR, HAUTEUR));
    }

    /**
     * Dessine le composant de carte.
     *
     * @param g contexte graphique
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cachee) {
            dessinerDos(g2d);
        } else if (carte != null) {
            dessinerCarte(g2d);
        }
    }

    /**
     * Dessine le dos de la carte.
     *
     * @param g2d contexte graphique
     */
    private void dessinerDos(Graphics2D g2d) {
        // Fond bleu foncé
        g2d.setColor(new Color(30, 60, 120));
        g2d.fillRoundRect(0, 0, LARGEUR, HAUTEUR, 10, 10);
        
        // Bordure
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, LARGEUR, HAUTEUR, 10, 10);
        
        // Motif de dos
        g2d.setColor(new Color(50, 80, 140));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                g2d.fillOval(10 + i * 15, 10 + j * 20, 8, 8);
            }
        }
    }

    /**
     * Dessine la face de la carte.
     *
     * @param g2d contexte graphique
     */
    private void dessinerCarte(Graphics2D g2d) {
        // Fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, LARGEUR, HAUTEUR, 10, 10);
        
        // Bordure
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, LARGEUR, HAUTEUR, 10, 10);

        if (carte instanceof Joker) {
            dessinerJoker(g2d);
        } else {
            dessinerCarteNormale(g2d);
        }
    }

    /**
     * Dessine une carte standard (non joker).
     *
     * @param g2d contexte graphique
     */
    private void dessinerCarteNormale(Graphics2D g2d) {
        Couleur couleur = carte.getCouleur();
        Rang rang = carte.getRang();
        
        // Couleur de la carte
        Color couleurCarte = getCouleurAWT(couleur);
        
        // Symbole de couleur en haut
        g2d.setColor(couleurCarte);
        String symbole = getSymboleCouleur(couleur);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int xSymbole = (LARGEUR - fm.stringWidth(symbole)) / 2;
        g2d.drawString(symbole, xSymbole, 25);
        
        // Valeur au centre
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String valeur = String.valueOf(rang.getValeur());
        fm = g2d.getFontMetrics();
        int xValeur = (LARGEUR - fm.stringWidth(valeur)) / 2;
        g2d.drawString(valeur, xValeur, HAUTEUR / 2 + 7);
        
        // Rang en bas (pour les rangs spéciaux)
        if (rang == Rang.DEUX || rang == Rang.TROIS || rang == Rang.QUATRE || rang == Rang.AS) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String nomRang = rang.toString();
            fm = g2d.getFontMetrics();
            int xRang = (LARGEUR - fm.stringWidth(nomRang)) / 2;
            g2d.drawString(nomRang, xRang, HAUTEUR - 8);
        }
    }

    /**
     * Dessine un joker.
     *
     * @param g2d contexte graphique
     */
    private void dessinerJoker(Graphics2D g2d) {
        // Dégradé arc-en-ciel
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 0, 0),
            LARGEUR, HAUTEUR, new Color(138, 43, 226)
        );
        g2d.setPaint(gradient);
        g2d.fillOval(10, 20, LARGEUR - 20, HAUTEUR - 40);
        
        // Texte JOKER
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String texte = "JOKER";
        int x = (LARGEUR - fm.stringWidth(texte)) / 2;
        g2d.drawString(texte, x, HAUTEUR / 2 + 5);
        
        // Étoiles
        g2d.setColor(Color.YELLOW);
        int[] xPoints = {LARGEUR / 2, LARGEUR / 2 + 3, LARGEUR / 2 + 6, LARGEUR / 2 + 2, LARGEUR / 2 + 3, LARGEUR / 2, LARGEUR / 2 - 3, LARGEUR / 2 - 2, LARGEUR / 2 - 6, LARGEUR / 2 - 3};
        int[] yPoints = {15, 18, 23, 20, 27, 22, 27, 20, 23, 18};
        g2d.fillPolygon(xPoints, yPoints, 10);
    }

    /**
     * Convertit une couleur de carte en couleur AWT.
     *
     * @param couleur couleur du modele
     * @return couleur AWT
     */
    private Color getCouleurAWT(Couleur couleur) {
        switch (couleur) {
            case COEURS: return new Color(220, 20, 60);
            case CARREAUX: return new Color(255, 140, 0);
            case TREFLES: return new Color(0, 128, 0);
            case PIQUES: return Color.BLACK;
            default: return Color.GRAY;
        }
    }

    /**
     * Retourne le symbole associe a une couleur.
     *
     * @param couleur couleur du modele
     * @return symbole a afficher
     */
    private String getSymboleCouleur(Couleur couleur) {
        switch (couleur) {
            case COEURS: return "♥";
            case CARREAUX: return "♦";
            case TREFLES: return "♣";
            case PIQUES: return "♠";
            default: return "?";
        }
    }
}
