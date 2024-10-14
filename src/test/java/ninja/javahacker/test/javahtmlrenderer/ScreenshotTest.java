package ninja.javahacker.test.javahtmlrenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.image.BufferedImage;
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
    private BufferedImage getImage1() {
        return LoadResource.load("Hi-1.png");
    }

    private BufferedImage getImage2() {
        return LoadResource.load("Hi-2.png");
    }

    @Test
    public void testScreenshotComponent() throws Exception {
        var a = new AtomicReference<BufferedImage>();
        EventQueue.invokeAndWait(() -> {
            var b = new JButton("Hi");
            b.setForeground(Color.BLACK);
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setMinimumSize(new Dimension(150, 30));
            b.setPreferredSize(new Dimension(150, 30));
            b.setMaximumSize(new Dimension(150, 30));
            var jf = new JFrame();
            jf.add(b);
            jf.pack();
            a.set(Screenshot.screenshot(b));
        });
        //LoadResource.saveAs(a.get(), "testScreenshotComponent-Hi.png");
        var i1 = ImageCompare.equals(a.get(), getImage1());
        var i2 = ImageCompare.equals(a.get(), getImage2());
        Assertions.assertTrue(i1 || i2);
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
