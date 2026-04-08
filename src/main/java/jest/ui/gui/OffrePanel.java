package jest.ui.gui;

import jest.model.Joueur;
import jest.model.Offre;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau Swing affichant l'offre d'un joueur.
 */
public class OffrePanel extends JPanel {
    
    /**
     * Construit un panneau d'offre pour un joueur.
     *
     * @param joueur joueur dont l'offre est affichee
     */
    public OffrePanel(Joueur joueur) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setBackground(new Color(240, 240, 240));
        
        // Nom du joueur
        JLabel nomLabel = new JLabel(joueur.getNom() + (joueur.estIA() ? " (IA)" : ""));
        nomLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(nomLabel, BorderLayout.NORTH);
        
        // Cartes
        JPanel cartesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        cartesPanel.setOpaque(false);
        
        Offre offre = joueur.getOffre();
        if (offre != null) {
            if (offre.getCarteVisible() != null) {
                cartesPanel.add(new CartePanel(offre.getCarteVisible(), false));
            } else {
                cartesPanel.add(creerPlaceholder("Visible ?"));
            }

            if (offre.getCarteCachee() != null) {
                cartesPanel.add(new CartePanel(offre.getCarteCachee(), true));
            } else {
                cartesPanel.add(creerPlaceholder("Cachee ?"));
            }
        } else {
            JLabel enAttente = new JLabel("En attente...");
            enAttente.setForeground(Color.GRAY);
            cartesPanel.add(enAttente);
        }
        
        add(cartesPanel, BorderLayout.CENTER);
        
        // Jest (nombre de cartes)
        JLabel jestLabel = new JLabel("Jest: " + joueur.getJest().getCartes().size() + " cartes");
        jestLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        jestLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(jestLabel, BorderLayout.SOUTH);
    }

    /**
     * Cree un composant placeholder pour une carte absente.
     *
     * @param texte texte a afficher
     * @return composant placeholder
     */
    private JComponent creerPlaceholder(String texte) {
        JLabel label = new JLabel(texte, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(60, 90));
        label.setMinimumSize(new Dimension(60, 90));
        label.setMaximumSize(new Dimension(60, 90));
        label.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        label.setForeground(Color.GRAY);
        return label;
    }
}
