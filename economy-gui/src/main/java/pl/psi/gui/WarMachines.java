package pl.psi.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import pl.psi.ProductType;
import pl.psi.creatures.EconomyCreature;

import java.util.function.BiConsumer;

public class WarMachines extends AbstractButton<EconomyCreature> {

    public WarMachines(BiConsumer<ProductType, EconomyCreature> ecoController, EconomyCreature economyCreature, boolean canBuy) {
        super(ecoController, economyCreature, canBuy);
        PATH = "/machines/" + economyCreature.getStats().toString() + ".png";
        DESCRIPTION = economyCreature.getStats().getName() + " | " + economyCreature.getGoldCost().getPrice();
    }

    @Override
    void acceptProduct(BiConsumer<ProductType, EconomyCreature> buy) {
        buy.accept(ProductType.CREATURE,product);
    }

    @Override
    void prepareTop(FlowPane aTopPane) {
        aTopPane.getChildren().add(new Label("Single Cost: " + product.getGoldCost().getPrice()));
        aTopPane.getChildren().add(new Label("Statistics : attack - " + product.getStats().getAttack() + " , armor - "+
                product.getStats().getArmor() + " , maxHP - " + product.getStats().getMaxHp()));
    }
}
