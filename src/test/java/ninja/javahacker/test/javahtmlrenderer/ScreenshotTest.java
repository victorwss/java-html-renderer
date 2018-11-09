/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.javahacker.test.javahtmlrenderer;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
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
    private static final BufferedImage TEST_HI = LoadResource.load("/hi.png");

    @Test
    public void testScreenshotComponent() throws Exception {
        var a = new AtomicReference<BufferedImage>();
        EventQueue.invokeAndWait(() -> {
            var jf = new JFrame();
            var b = new JButton("Hi");
            jf.add(b);
            jf.pack();
            a.set(Screenshot.screenshot(b));
        });
        Assertions.assertTrue(ImageCompare.equals(a.get(), TEST_HI));
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
