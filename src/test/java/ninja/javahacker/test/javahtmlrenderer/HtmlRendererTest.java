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
    private static final String HTML =
            "<h1 style='color: blue'>Hello World!</h1><p>Lorem ipsum dolor</p><div><img src='file:///XXX' /></div>";

    private static final BufferedImage TEST_IMG = LoadResource.load("/test1.png");
    private static final BufferedImage EXPECTED = LoadResource.load("/testhtml.png");

    private void assertImageExpected(BufferedImage image) {
        Assertions.assertTrue(ImageCompare.equals(image, EXPECTED));
    }

    private String makeHtml() {
        File f = LoadResource.save(TEST_IMG);
        return HTML.replace("XXX", f.getAbsolutePath());
    }

    @Test
    public void testRender() {
        assertImageExpected(HtmlRenderer.render(makeHtml()));
    }

    @Test
    public void testRenderInterrupted() {
        Thread.currentThread().interrupt();
        assertImageExpected(HtmlRenderer.render(makeHtml()));
    }

    @Test
    public void testRenderInterruptibly() throws Exception {
        assertImageExpected(HtmlRenderer.renderInterruptibly(makeHtml()));
    }

    @Test
    public void testRenderInterruptiblyInterrupted() {
        Thread.currentThread().interrupt();
        Assertions.assertThrows(InterruptedException.class, () -> HtmlRenderer.renderInterruptibly(makeHtml()));
    }

    @Test
    public void testPreparedRender() {
        var r = HtmlRenderer.prepare(makeHtml());
        Assertions.assertFalse(r.isDone());
        Assertions.assertFalse(r.getResultIfDone().isPresent());
        assertImageExpected(r.getResult());
    }

    @Test
    public void testPreparedInterruptedRender() {
        Thread.currentThread().interrupt();
        var r = HtmlRenderer.prepare(makeHtml());
        Assertions.assertFalse(r.isDone());
        Assertions.assertFalse(r.getResultIfDone().isPresent());
        assertImageExpected(r.getResult());
    }

    @Test
    public void testPreparedInterruptiblyRender() throws Exception {
        var r = HtmlRenderer.prepare(makeHtml());
        Assertions.assertFalse(r.isDone());
        Assertions.assertFalse(r.getResultIfDone().isPresent());
        assertImageExpected(r.getResultInterruptibly());
    }

    @Test
    public void testPreparedInterruptiblyInterruptedRender() {
        Thread.currentThread().interrupt();
        var r = HtmlRenderer.prepare(makeHtml());
        Assertions.assertFalse(r.isDone());
        Assertions.assertFalse(r.getResultIfDone().isPresent());
        Assertions.assertThrows(InterruptedException.class, r::getResultInterruptibly);
    }

    @Test
    public void testRenderLongSleep() throws Exception {
        var x = new AtomicReference<BufferedImage>();
        Thread t = new Thread(() -> {
            x.set(HtmlRenderer.render(makeHtml(), 1000));
        });
        t.start();
        Thread.sleep(500);
        Assertions.assertTrue(t.isAlive());
        t.join();
        assertImageExpected(x.get());
    }

    @Test
    public void testPreparedRenderLongSleep() throws Exception {
        var x = new AtomicReference<BufferedImage>();
        Thread t = new Thread(() -> {
            x.set(HtmlRenderer.prepare(makeHtml(), 1000).getResult());
        });
        t.start();
        Thread.sleep(500);
        Assertions.assertTrue(t.isAlive());
        t.join();
        assertImageExpected(x.get());
    }
}
