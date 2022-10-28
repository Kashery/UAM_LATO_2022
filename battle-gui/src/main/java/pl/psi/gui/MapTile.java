package pl.psi.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.image.BufferedImage;

class MapTile extends StackPane {

    private final Rectangle rect;
    private final Text text;

    MapTile(final String aName) {
        rect = new Rectangle(60, 60);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.RED);
        getChildren().add(rect);
        text = new Text(aName);
        getChildren().add(text);
        text.setFill(Color.WHITE);
        Font font = Font.font("Arial", FontWeight.BOLD, 15);
        text.setFont(font);
    }

    void setName(final String aName) {
        text.setText(aName);
    }

    void setBackground(final Color aColor) {
        rect.setFill(aColor);
    }

    void setBackground(final Image img) {
        rect.setFill(new ImagePattern(img));
    }

    /**
     * Prepares and sets background for MapTile with ARGB format image
     * For backgrounds without alpha channels please use other setBackground methods since this particular one is significantly slower due to manual pixel writing
     * @param img ARGB image
     * @param bgcolor background color for pixels with complete transparency
     */
    void setBackground(final Image img, java.awt.Color bgcolor){


        BufferedImage image = new BufferedImage((int)img.getWidth(), (int)img.getHeight(), BufferedImage.TYPE_INT_RGB);
        WritableImage write = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter writepixel = write.getPixelWriter();
        for (int h = 0; h < image.getHeight(); h++) {
            for (int w = 0; w < image.getWidth(); w++) {
                if (img.getPixelReader().getArgb(w, h)==0){
                    writepixel.setArgb(w, h, bgcolor.getRGB());
                }else{
                    writepixel.setArgb(w, h, img.getPixelReader().getArgb(w, h));
                }
            }

        }

        for (int i = 0; i < (int)img.getHeight(); i++){
            for (int k = 0; k < (int)img.getWidth(); k++){
                image.setRGB(k, i, img.getPixelReader().getArgb(k, i));
            }
        }
        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);


        //Image img = new Image(gameEngine.getField(new Point(x1, y1)).get().getImagePath());

        Image imgr = SwingFXUtils.toFXImage(result, null);
        rect.setFill(new ImagePattern(write));
    }

}
