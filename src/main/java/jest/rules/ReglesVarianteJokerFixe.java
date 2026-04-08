package jest.rules;

/**
 * Variante ou le joker a un comportement fixe.
 */
public class ReglesVarianteJokerFixe extends ReglesDeBase {
    /**
     * Construit les regles de la variante joker fixe.
     */
    public ReglesVarianteJokerFixe() {
    }

    /**
     * Retourne le nom de la variante.
     *
     * @return nom de la variante
     */
    @Override
    public String nomVariante() {
        return "VARIANTE_JOKER_FIXE";
    }
}
