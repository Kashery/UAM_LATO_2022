package pl.psi.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.psi.Hero;
import pl.psi.hero.HeroStatistics;
import pl.psi.spells.SpellFactory;

import java.io.IOException;
import java.util.List;

import static pl.psi.spells.SpellNames.*;
import static pl.psi.spells.SpellRang.BASIC;

public class Start extends Application {

    public Start() {

    }

    static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        Scene scene = null;
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Start.class.getClassLoader()
                    .getResource("fxml/main-battle.fxml"));
            loader.setController(new MainBattleController(createP1(), createP2()));
            scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.setX(5);
            primaryStage.setY(5);
            primaryStage.show();
        } catch (final IOException aE) {
            aE.printStackTrace();
        }
    }

    private Hero createP1() {
        final Hero ret =
                new Hero(List.of(
                        new NecropolisFactory().create(false, 1, 5),
                        new NecropolisFactory().create(true, 2, 5),
                        new NecropolisFactory().create(false,3,5),
                        new NecropolisFactory().create(true,4,5),
                        new NecropolisFactory().create(false, 5, 100),
                        new NecropolisFactory().create(true, 6, 1000),
                        new NecropolisFactory().create(true,7,5)
                        ),HeroStatistics.NECROMANCER);
        ret.getCreatures().forEach(creature -> creature.setHeroNumber(1));
        return ret;
    }

    private Hero createP2() {
        final Hero ret = new Hero(List.of(
                new StrongholdFactory().create(true, 1, 100),
                new StrongholdFactory().create(true, 2, 50),
                new StrongholdFactory().create(false, 3, 25),
                new StrongholdFactory().create(true, 4, 15),
                new StrongholdFactory().create(false, 5, 10),
                new StrongholdFactory().create(true, 6, 5),
                new StrongholdFactory().create(false, 7, 100)
        ),HeroStatistics.KNIGHT);
        ret.getCreatures().forEach(creature -> creature.setHeroNumber(2));
        return ret;
    }

}
