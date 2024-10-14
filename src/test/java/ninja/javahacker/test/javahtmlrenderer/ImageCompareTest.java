package ninja.javahacker.test.javahtmlrenderer;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import ninja.javahacker.javahtmlrenderer.ImageCompare;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ImageCompareTest {

    private BufferedImage loadImage1() {
        return LoadResource.load("testhtml-1.png");
    }

    private BufferedImage loadImage2() {
        return LoadResource.load("Hi-1.png");
    }

    @Test
    public void testImageComparisonSame() {
        var i = loadImage1();
        var j = loadImage2();
        Assertions.assertTrue(ImageCompare.equals(i, i));
        Assertions.assertTrue(ImageCompare.equals(j, j));
    }

    @Test
    public void testImageComparisonIdentical() {
        var i = loadImage1();
        var j = loadImage2();
        var x = loadImage1();
        var y = loadImage2();
        Assertions.assertTrue(i != x);
        Assertions.assertTrue(j != y);
        Assertions.assertTrue(ImageCompare.equals(i, x));
        Assertions.assertTrue(ImageCompare.equals(j, y));
    }

    @Test
    public void testImageComparisonNegative() {
        var i = loadImage1();
        var j = loadImage2();
        Assertions.assertFalse(ImageCompare.equals(i, j));
        Assertions.assertFalse(ImageCompare.equals(j, i));
    }

    @Test
    public void testImageComparisonWithNull() {
        var j = loadImage2();
        Assertions.assertFalse(ImageCompare.equals(null, j));
        Assertions.assertFalse(ImageCompare.equals(j, null));
    }

    @Test
    public void testImageComparisonNulls() {
        Assertions.assertTrue(ImageCompare.equals(null, null));
    }

    @Test
    public void testNoNewInstance() {
        var x = Assertions.assertThrows(InvocationTargetException.class, () -> {
            var ctor = ImageCompare.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
        });
        Assertions.assertTrue(x.getCause() instanceof UnsupportedOperationException);
    }
}
