package jest.engine;

import jest.model.Carte;
import jest.model.ConditionTrophee;
import jest.model.Couleur;
import jest.model.Joker;
import jest.model.Joueur;
import jest.model.Rang;
import jest.model.Trophee;
import jest.rules.ReglesJeu;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Fabrique (builder) responsable de l'initialisation d'une nouvelle partie :
 * <ul>
 *     <li>création des joueurs (humains et IA)</li>
 *     <li>construction du deck (base + extension optionnelle)</li>
 *     <li>mélange du deck</li>
 *     <li>tirage des trophées</li>
 *     <li>création de la pioche</li>
 * </ul>
 */
public class FabriquePartie {

    private final Random alea = new Random();

    /**
     * Construit une fabrique de partie.
     */
    public FabriquePartie() {
    }

    /**
     * Crée un nouvel état de partie initialisé et prêt à jouer.
     *
     * @param config configuration de la partie (nombre joueurs, IA, règles, extension)
     * @param nomsJoueurs noms des joueurs, dans l'ordre (taille = nombre de joueurs)
     * @return état initial de la partie
     * @throws IllegalArgumentException si la liste des noms n'a pas la bonne taille
     */
    public EtatPartie creerNouvellePartie(ConfigurationPartie config, List<String> nomsJoueurs) {
        int n = config.getNombreJoueurs();
        if (nomsJoueurs == null || nomsJoueurs.size() != n) {
            throw new IllegalArgumentException("nomsJoueurs doit contenir exactement " + n + " noms.");
        }

        ReglesJeu regles = config.getRegles();

        // 1) Créer les joueurs (les derniers sont IA)
        List<Joueur> joueurs = new ArrayList<>();
        int nombreIA = config.getNombreIA();
        int nombreHumains = n - nombreIA;

        for (int i = 0; i < n; i++) {
            boolean estIA = i >= nombreHumains;
            joueurs.add(new Joueur(nomsJoueurs.get(i), estIA));
        }

        // 2) Construire le deck (base + extension optionnelle)
        List<Carte> deck = construireDeck(config.isExtensionActivee());

        // 3) Mélanger
        Collections.shuffle(deck, alea);

        // 4) Tirer les trophées
        int nbTrophees = regles.nombreTrophees(n);
        List<Carte> cartesTrophees = new ArrayList<>();
        for (int i = 0; i < nbTrophees; i++) {
            cartesTrophees.add(deck.remove(deck.size() - 1));
        }
        List<Trophee> trophees = creerTrophees(cartesTrophees);

        // 5) Mettre le reste en pioche (pile)
        Deque<Carte> pioche = new ArrayDeque<>();
        for (Carte c : deck) {
            pioche.push(c);
        }

        EtatPartie etatPartie = new EtatPartie(
                joueurs,
                pioche,
                trophees,
                regles,
                config.isExtensionActivee(),
                config.getNombreIA()
        );
        return etatPartie;
    }

    /**
     * Construit les trophees a partir des cartes tirees.
     *
     * @param cartesTrophees cartes tirees comme trophees
     * @return liste de trophees
     */
    private List<Trophee> creerTrophees(List<Carte> cartesTrophees) {
        List<Trophee> trophees = new ArrayList<>();

        for (Carte carte : cartesTrophees) {
            trophees.add(creerTropheePourCarte(carte));
        }

        return trophees;
    }

    /**
     * Construit le trophee conforme au pictogramme de la carte.
     *
     * @param carte carte tiree
     * @return trophee correspondant
     */
    private Trophee creerTropheePourCarte(Carte carte) {
        if (carte.estJoker()) {
            return new Trophee(carte, ConditionTrophee.MEILLEUR_JEST);
        }

        Couleur couleur = carte.getCouleur();
        Rang rang = carte.getRang();

        switch (couleur) {
            case COEURS:
                return new Trophee(carte, ConditionTrophee.JOKER);
            case PIQUES:
                switch (rang) {
                    case AS:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FORTE_CARTE_COULEUR,
                                Couleur.TREFLES,
                                null);
                    case DEUX:
                        return new Trophee(carte,
                                ConditionTrophee.MAJORITE_RANG,
                                null,
                                Rang.TROIS);
                    case TROIS:
                        return new Trophee(carte,
                                ConditionTrophee.MAJORITE_RANG,
                                null,
                                Rang.DEUX);
                    case QUATRE:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FAIBLE_CARTE_COULEUR,
                                Couleur.TREFLES,
                                null);
                    default:
                        return new Trophee(carte, ConditionTrophee.MAJORITE_RANG, null, rang);
                }
            case TREFLES:
                switch (rang) {
                    case AS:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FORTE_CARTE_COULEUR,
                                Couleur.PIQUES,
                                null);
                    case DEUX:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FAIBLE_CARTE_COULEUR,
                                Couleur.COEURS,
                                null);
                    case TROIS:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FORTE_CARTE_COULEUR,
                                Couleur.COEURS,
                                null);
                    case QUATRE:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FAIBLE_CARTE_COULEUR,
                                Couleur.PIQUES,
                                null);
                    default:
                        return new Trophee(carte, ConditionTrophee.MAJORITE_RANG, null, rang);
                }
            case CARREAUX:
                switch (rang) {
                    case AS:
                        return new Trophee(carte,
                                ConditionTrophee.MAJORITE_RANG,
                                null,
                                Rang.QUATRE);
                    case DEUX:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FORTE_CARTE_COULEUR,
                                Couleur.CARREAUX,
                                null);
                    case TROIS:
                        return new Trophee(carte,
                                ConditionTrophee.PLUS_FAIBLE_CARTE_COULEUR,
                                Couleur.CARREAUX,
                                null);
                    case QUATRE:
                        return new Trophee(carte, ConditionTrophee.MEILLEUR_JEST_SANS_JOKER);
                    default:
                        return new Trophee(carte, ConditionTrophee.MAJORITE_RANG, null, rang);
                }
            default:
                return new Trophee(carte, ConditionTrophee.MAJORITE_RANG, null, rang);
        }
    }

    /**
     * Construit le deck complet à partir :
     * <ul>
     *     <li>du deck de base (AS, 2, 3, 4 sur 4 couleurs + Joker)</li>
     *     <li>éventuellement de l'extension (ajout du rang CINQ sur 4 couleurs)</li>
     * </ul>
     *
     * @param extensionActivee true si l'extension est activée
     * @return liste de cartes représentant le deck
     */
    private List<Carte> construireDeck(boolean extensionActivee) {
        List<Carte> deck = construireDeckDeBase();

        if (extensionActivee) {
            // Extension : ajout des 4 cartes CINQ (une par couleur)
            for (Couleur couleur : Couleur.values()) {
                deck.add(new Carte(couleur, Rang.CINQ));
            }
        }

        return deck;
    }

    /**
     * Construit le deck de base :
     * <ul>
     *     <li>16 cartes normales : 4 couleurs x (AS, 2, 3, 4)</li>
     *     <li>1 Joker</li>
     * </ul>
     *
     * @return deck de base
     */
    private List<Carte> construireDeckDeBase() {
        List<Carte> deck = new ArrayList<>();

        for (Couleur couleur : Couleur.values()) {
            deck.add(new Carte(couleur, Rang.AS));
            deck.add(new Carte(couleur, Rang.DEUX));
            deck.add(new Carte(couleur, Rang.TROIS));
            deck.add(new Carte(couleur, Rang.QUATRE));
        }

        deck.add(new Joker());
        return deck;
    }
}
