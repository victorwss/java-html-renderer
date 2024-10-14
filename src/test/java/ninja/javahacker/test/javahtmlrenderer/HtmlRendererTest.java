package ninja.javahacker.test.javahtmlrenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import ninja.javahacker.javahtmlrenderer.HtmlRenderer;
import ninja.javahacker.javahtmlrenderer.ImageCompare;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class HtmlRendererTest {
    private static final String HTML = ""
            + "<h1 style=\"color: blue; font-family: 'Arial'; font-size: 20; font-weight: bold;\">Hello World!</h1>"
            + "<p style=\"color: black; font-family: 'Arial'; font-size: 12; font-weight: normal;\">Lorem ipsum dolor</p>"
            + "<div><img src='file:///XXX' /></div>";

    private BufferedImage loadTestImage() {
        return LoadResource.load("test1.png");
    }

    private BufferedImage loadExpectedImage1() {
        return LoadResource.load("testhtml-1.png");
    }

    private BufferedImage loadExpectedImage2() {
        return LoadResource.load("testhtml-2.png");
    }

    private void assertImageExpected(String x, BufferedImage image) {
        //LoadResource.saveAs(image, x);
        var i1 = ImageCompare.equals(image, loadExpectedImage1());
        var i2 = ImageCompare.equals(image, loadExpectedImage2());
        if (!i1 && !i2) {
            LoadResource.saveAs(image, "FAIL-" + x);
            throw new AssertionError("Image compare failed: " + x);
        }
    }

    private String makeHtml() {
        File f = LoadResource.saveTemp(loadTestImage());
        return HTML.replace("XXX", f.getAbsolutePath());
    }

    @Test
    public void testRender() throws InterruptedException {
        assertImageExpected("testRender.png", HtmlRenderer.render(makeHtml()));
    }

    @Test
    public void testRenderInterrupted() throws InterruptedException {
        Thread.currentThread().interrupt();
        Assertions.assertThrows(InterruptedException.class, () -> HtmlRenderer.render(makeHtml()));
    }

    @Test
    public void testPreparedRender() throws InterruptedException {
        var r = HtmlRenderer.prepare(makeHtml());
        Assertions.assertFalse(r.isDone());
        Assertions.assertFalse(r.getResultIfDone().isPresent());
        assertImageExpected("testPreparedRender.png", r.getResult());
    }

    @Test
    public void testPreparedInterruptedRender() throws InterruptedException {
        Thread.currentThread().interrupt();
        var r = HtmlRenderer.prepare(makeHtml());
        Assertions.assertFalse(r.isDone());
        Assertions.assertFalse(r.getResultIfDone().isPresent());
        Assertions.assertThrows(InterruptedException.class, r::getResult);
    }

    @Test
    public void testRenderLongSleep() throws Exception {
        var x = new AtomicReference<BufferedImage>();
        var f = new AtomicReference<Throwable>();
        Thread t = new Thread(() -> {
            try {
                x.set(HtmlRenderer.render(makeHtml(), 1000));
            } catch (InterruptedException e) {
                f.set(e);
            }
        });
        t.start();
        Thread.sleep(500);
        Assertions.assertTrue(t.isAlive());
        t.join();
        assertImageExpected("testRenderLongSleep.png", x.get());
        Assertions.assertNull(f.get());
    }

    @Test
    public void testPreparedRenderLongSleep() throws Exception {
        var x = new AtomicReference<BufferedImage>();
        var f = new AtomicReference<Throwable>();
        Thread t = new Thread(() -> {
            try {
                x.set(HtmlRenderer.prepare(makeHtml(), 1000).getResult());
            } catch (InterruptedException e) {
                f.set(e);
            }
        });
        t.start();
        Thread.sleep(500);
        Assertions.assertTrue(t.isAlive());
        t.join();
        assertImageExpected("testPreparedRenderLongSleep.png", x.get());
        Assertions.assertNull(f.get());
    }
}
