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
    private static final BufferedImage TEST_1 = LoadResource.load("/test1.png");
    private static final BufferedImage HI = LoadResource.load("/hi.png");

    @Test
    public void testImageComparisonOk() {
        Assertions.assertTrue(ImageCompare.equals(TEST_1, TEST_1));
        Assertions.assertTrue(ImageCompare.equals(HI, HI));
    }

    @Test
    public void testImageComparisonNegative() {
        Assertions.assertFalse(ImageCompare.equals(TEST_1, HI));
        Assertions.assertFalse(ImageCompare.equals(HI, TEST_1));
    }

    @Test
    public void testImageComparisonWithNull() {
        Assertions.assertFalse(ImageCompare.equals(null, HI));
        Assertions.assertFalse(ImageCompare.equals(HI, null));
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
