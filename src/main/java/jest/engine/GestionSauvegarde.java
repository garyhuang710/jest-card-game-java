package jest.engine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Gestionnaire de sauvegarde/chargement d'une partie.
 * <p>
 * La sauvegarde repose sur la sérialisation Java de l'objet {@link EtatPartie}.
 */
public class GestionSauvegarde {

    /**
     * Construit un gestionnaire de sauvegarde.
     */
    public GestionSauvegarde() {
    }

    /**
     * Sauvegarde l'état de partie dans un fichier.
     *
     * @param etat état à sauvegarder
     * @param cheminFichier chemin du fichier (ex: "sauvegarde.dat")
     * @throws IOException si erreur d'écriture
     */
    public void sauvegarder(EtatPartie etat, String cheminFichier) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(etat);
        }
    }

    /**
     * Charge un état de partie depuis un fichier.
     *
     * @param cheminFichier chemin du fichier (ex: "sauvegarde.dat")
     * @return état de partie chargé
     * @throws IOException si erreur de lecture
     * @throws ClassNotFoundException si une classe sérialisée est introuvable
     */
    public EtatPartie charger(String cheminFichier) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cheminFichier))) {
            Object obj = ois.readObject();
            return (EtatPartie) obj;
        }
    }
}
