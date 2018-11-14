package ninja.javahacker.test.javahtmlrenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JFrame;
import ninja.javahacker.javahtmlrenderer.ImageCompare;
import ninja.javahacker.javahtmlrenderer.Screenshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ScreenshotTest {
    private static final BufferedImage HI = LoadResource.load("/Hi.png");

    @Test
    public void testScreenshotComponent() throws Exception {
        var a = new AtomicReference<BufferedImage>();
        EventQueue.invokeAndWait(() -> {
            var jf = new JFrame();
            var b = new JButton("Hi");
            b.setForeground(Color.BLACK);
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setMinimumSize(new Dimension(150, 30));
            b.setPreferredSize(new Dimension(150, 30));
            b.setMaximumSize(new Dimension(150, 30));
            jf.add(b);
            jf.pack();
            a.set(Screenshot.screenshot(b));
        });
        Assertions.assertTrue(ImageCompare.equals(a.get(), HI));
    }

    @Test
    public void testScreenshotNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Screenshot.screenshot(null), "The component can't be null.");
    }

    @Test
    public void testNoNewInstance() {
        var x = Assertions.assertThrows(InvocationTargetException.class, () -> {
            var ctor = Screenshot.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
        });
        Assertions.assertTrue(x.getCause() instanceof UnsupportedOperationException);
    }
}
