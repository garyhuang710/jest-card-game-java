package jest.rules;

/**
 * Variante ou les carreaux sont positifs au score.
 */
public class ReglesVarianteCarreauxPositifs extends ReglesDeBase {
    /**
     * Construit les regles de la variante carreaux positifs.
     */
    public ReglesVarianteCarreauxPositifs() {
    }

    /**
     * Retourne le nom de la variante.
     *
     * @return nom de la variante
     */
    @Override
    public String nomVariante() {
        return "VARIANTE_CARREAUX_POSITIFS";
    }
}
