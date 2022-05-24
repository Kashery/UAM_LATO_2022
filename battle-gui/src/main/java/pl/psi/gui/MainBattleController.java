package pl.psi.gui;

import javafx.scene.image.Image;
import pl.psi.GameEngine;
import pl.psi.Hero;
import pl.psi.Point;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MainBattleController
{
    private final GameEngine gameEngine;
    @FXML
    private GridPane gridMap;
    @FXML
    private Button passButton;

    public MainBattleController( final Hero aHero1, final Hero aHero2 )
    {
        gameEngine = new GameEngine( aHero1, aHero2 );
    }

    @FXML
    private void initialize()
    {
        refreshGui();

        passButton.addEventHandler( MouseEvent.MOUSE_CLICKED, ( e ) -> {
            gameEngine.pass();
            refreshGui();
        } );

        gameEngine.addObserver( GameEngine.CREATURE_MOVED, ( e ) -> refreshGui() );
    }

    private void renderSpecialFields(MapTile mapTile, int x, int y)
    {
        if (x == 0 && y == 0)
        {
            Image img = new Image("/images/cracked_ice.png");
            mapTile.setBackground(img);
        }

        if(x == 4 && y == 8) {
            var img = new Image("/images/evilFog.png");
            mapTile.setBackground(img);
        }

        if(x == 5 && y == 5) {
            var img = new Image("/images/holyGround.png");
            mapTile.setBackground(img);
        }
    }

    private void refreshGui()
    {
        for( int x = 0; x < 15; x++ )
        {
            for( int y = 0; y < 10; y++ )
            {
                final int x1 = x;
                final int y1 = y;
                final MapTile mapTile = new MapTile( "" );
                gameEngine.getCreature( new Point( x, y ) )
                    .ifPresent( c -> mapTile.setName( c.getName() ) );

                if( gameEngine.canMove( new Point( x, y ) ) )
                {
                    mapTile.setBackground( Color.GREY );

                    mapTile.addEventHandler( MouseEvent.MOUSE_CLICKED,
                        e -> gameEngine.move( new Point( x1, y1 ) ) );
                }
                if( gameEngine.canAttack( new Point( x, y ) ) )
                {
                    mapTile.setBackground( Color.RED );

                    mapTile.addEventHandler( MouseEvent.MOUSE_CLICKED,
                        e -> gameEngine.attack( new Point( x1, y1 ) ) );
                }
                if( gameEngine.canHeal( new Point( x, y ) ) )
                {
                    mapTile.setBackground( Color.YELLOW );

                    mapTile.addEventHandler( MouseEvent.MOUSE_CLICKED,
                            e -> gameEngine.heal( new Point( x1, y1 ) ) );
                }

                renderSpecialFields( mapTile, x, y );
                gridMap.add( mapTile, x, y );
            }
        }
    }
}
