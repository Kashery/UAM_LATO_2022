package pl.psi.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pl.psi.GameEngine;
import pl.psi.Hero;
import pl.psi.Point;

import java.io.IOException;
import java.util.List;

public class MainBattleController {
    private final GameEngine gameEngine;
    @FXML
    private GridPane gridMap;
    @FXML
    private Button waitButton;
    @FXML
    private Button defendButton;
    @FXML
    private Button passButton;
    @FXML
    private Button spellButton;
    @FXML
    private Label console;
    @FXML
    private Label roundNumber;

    public MainBattleController(final Hero aHero1, final Hero aHero2) {
        gameEngine = new GameEngine(aHero1, aHero2);
    }

    @FXML
    private void initialize() {
        refreshGui();

        console.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        console.setEllipsisString("");

        passButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            gameEngine.pass();
            refreshGui();
        });

        spellButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                    Scene scene = null;
                    Stage stage = null;
                    try {
                        final FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(Start.class.getClassLoader()
                                .getResource("fxml/spell-battle.fxml"));
                        loader.setController(new SpellBattleController());
                        scene = new Scene(loader.load());
                        stage = new Stage();
                        stage.setScene(scene);
                        stage.show();
                    } catch (final IOException aE) {
                        aE.printStackTrace();
                    }
                }
        );

        waitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (gameEngine.allActionsLeft()) {
                gameEngine.waitAction();
            } else {
                throw new RuntimeException("Action already performed.");
            }
            refreshGui();
        });

        defendButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (gameEngine.allActionsLeft()) {
                gameEngine.defendAction();
            } else {
                throw new RuntimeException("Action already performed.");
            }
            refreshGui();
        });

        gameEngine.addObserver(GameEngine.CREATURE_MOVED, (e) -> refreshGui());
    }

    private void renderSpecialFields(MapTile mapTile, int x, int y) {
        if (x == 0 && y == 0) {
            Image img = new Image("/images/cracked_ice.png");
            mapTile.setBackground(img);
        }

        if (x == 4 && y == 8) {
            Image img = new Image("/images/evilFog.png");
            mapTile.setBackground(img);
        }

        if (x == 5 && y == 5) {
            Image img = new Image("/images/holyGround.png");
            mapTile.setBackground(img);
        }
        if (x == 3 && y == 4) {
            Image img = new Image("/images/fields_of_Glory.png");
            mapTile.setBackground(img);
        }
        if (x == 4 && y == 6) {
            Image img = new Image("/images/fiery_Fields.png");
            mapTile.setBackground(img);
        }
    }

    private static void showStage(final String information, final boolean hasSpecial) {
        final int SMALL_WIDTH = 250;
        final int SMALL_HEIGHT = 225;
        final int BIG_WIDTH = 360;
        final int BIG_HEIGHT = 355;
        Stage newStage = new Stage();
        VBox comp = new VBox();
        Text text = new Text(information);
        text.setStyle("-fx-font: 24 arial;");
        comp.getChildren().add(text);
        final int width;
        final int height;
        if (hasSpecial) {
            width = BIG_WIDTH;
            height = BIG_HEIGHT;
        } else {
            width = SMALL_WIDTH;
            height = SMALL_HEIGHT;
        }
        Scene stageScene = new Scene(comp, width, height);
        newStage.setScene(stageScene);
        newStage.show();
    }

    private void refreshGui() {
        roundNumber.setText(gameEngine.getRoundNumber());
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 10; y++) {
                final int x1 = x;
                final int y1 = y;
                final MapTile mapTile = new MapTile("");

                if (gameEngine.canMove(new Point(x1, y1))) {
                    mapTile.setBackground(Color.DARKGREY);
                    List<Point> path = gameEngine.getPath(new Point(x1, y1));

                    if (gameEngine.getCreature(new Point(x1, y1)).isEmpty() || gameEngine.getCreature(new Point(x1, y1)).isPresent()) {
                        mapTile.setOnMouseEntered(mouseEvent -> {
                            if (gameEngine.getCreature(new Point(x1, y1)).isEmpty()) {
                                mapTile.setBackground(Color.GREY);
                            }
                        });

                        mapTile.setOnMouseExited(mouseEvent -> {
                            if (gameEngine.getCreature(new Point(x1, y1)).isEmpty()) {
                                mapTile.setBackground(Color.DARKGREY);
                                renderSpecialFields(mapTile, x1, y1);
                            }
                        });
                    }
                    mapTile.setOnMousePressed(
                            e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    path.forEach(point -> {
                                        if (!point.equals(path.get(path.size() - 1))) {
                                            gameEngine.move(point);
                                        } else {
                                            gameEngine.lastMove(point);
                                        }
                                    });
                                }
                            });
                }

                if (gameEngine.getCreature(new Point(x1, y1)).isPresent()) {
                    if (gameEngine.getCreature(new Point(x, y)).get().isAlive()) {
                        mapTile.setName("\n\n" + gameEngine.getCreature(new Point(x, y)).get().getAmount());
                        Image img = new Image(gameEngine.getCreature(new Point(x1, y1)).get().getBasicStats().getImagePath());
                        mapTile.setBackground(img);
                    } else {
                        Image img = new Image("/images/dead.jpg");
                        mapTile.setBackground(img);
                    }
                }


                if (gameEngine.canHeal(new Point(x1, y1))) {
                    mapTile.setBackground(Color.YELLOW);

                    mapTile.addEventHandler(MouseEvent.MOUSE_CLICKED,
                            e -> gameEngine.heal(new Point(x1, y1)));
                }


                renderSpecialFields(mapTile, x1, y1);

                if (gameEngine.getCreature(new Point(x1, y1)).isPresent()) {
                    mapTile.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.SECONDARY) {
                            showStage(gameEngine.getCreatureInformation(new Point(x1, y1)), gameEngine.getCreature(new Point(x1, y1)).get().hasSpecial());
                        }
                    });
                }
                if (gameEngine.canAttack(new Point(x, y))) {
                    mapTile.setOnMouseEntered(e -> {
                        Image img = new Image(gameEngine.getCreature(new Point(x1, y1)).get().getBasicStats().getCanAttackImagePath());
                        mapTile.setBackground(img);
                    });
                    mapTile.setOnMouseExited(e -> {
                        Image img = new Image(gameEngine.getCreature(new Point(x1, y1)).get().getBasicStats().getImagePath());
                        mapTile.setBackground(img);
                    });

                    mapTile.setOnMousePressed(
                            e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    gameEngine.attack(new Point(x1, y1));
                                }
                            });
                }

                if (gameEngine.canCastSpell(new Point(x, y))) {
                    mapTile.setOnMouseEntered(e -> {
                        Image img = new Image(gameEngine.getCreature(new Point(x1, y1)).get().getBasicStats().getCanBuffImagePath());
                        mapTile.setBackground(img);

                    });
                    mapTile.setOnMouseExited(e -> {
                        Image img = new Image(gameEngine.getCreature(new Point(x1, y1)).get().getBasicStats().getImagePath());
                        if (gameEngine.getCreature(new Point(x1, y1)).get().equals(gameEngine.getCurrentCreature())) {
                            img = new Image(gameEngine.getCreature(new Point(x1, y1)).get().getBasicStats().getCurrentImagePath());
                        }
                        mapTile.setBackground(img);
                    });

                    mapTile.setOnMousePressed(
                            e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    gameEngine.castCurrentCreatureSpell(new Point(x1, y1));
                                }
                            });
                }

                if (gameEngine.isCurrentCreature(new Point(x, y)) && gameEngine.isCurrentCreatureAlive()) {
                    var img = new Image(gameEngine.getCreature(new Point(x, y)).get().getBasicStats().getCurrentImagePath());
                    mapTile.setBackground(img);
                }

                console.setText(gameEngine.getAttackInformation());

                waitButton.setDisable(!gameEngine.allActionsLeft());
                defendButton.setDisable(!gameEngine.allActionsLeft());

                gridMap.add(mapTile, x1, y1);
            }
        }
    }
}

