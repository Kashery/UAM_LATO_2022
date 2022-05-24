package pl.psi.artifacts;

import pl.psi.shop.ProductPrice;

public class EconomyArtifactFactory {

    private static final String EXCEPTION_MESSAGE = "We don't have artifact with this name. Try again.";

    public Artifact create(final String name ){

        switch (name){

            case "Cape of Conjuring":
                return new Artifact(ArtifactPlacement.SHOULDERS,name,new ProductPrice(300));
            case "Crown of Dragontooth":
                return new Artifact(ArtifactPlacement.HEAD,name,new ProductPrice(300));
            case "Blackshard of the Dead Knight":
                return new Artifact(ArtifactPlacement.RIGHT_HAND,name,new ProductPrice(300));
            default:
                throw new IllegalArgumentException( EXCEPTION_MESSAGE );
        }

    }
}