package ninja.javahacker.test.javahtmlrenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.imageio.ImageIO;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class LoadResource {

    private LoadResource() {
        throw new UnsupportedOperationException();
    }

    public static BufferedImage load(String source) {
        try {
            return ImageIO.read(LoadResource.class.getResource(source));
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }

    public static File save(BufferedImage image) {
        try {
            File temp = Files.createTempFile("img", ".png").toAbsolutePath().toFile();
            ImageIO.write(image, "png", temp);
            return temp;
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }

    public static void save2(BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File("temp.png"));
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }
}
