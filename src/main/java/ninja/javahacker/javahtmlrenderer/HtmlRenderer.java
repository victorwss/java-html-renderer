package ninja.javahacker.javahtmlrenderer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 * Renders HTML pages into {@link BufferedImage}s.
 * <p>Instances of this class are immutable and represents the ongoing process of
 * the rendering of an HTML document.</p>
 * <p>Normally, you would just want to produce the image directly as such:</p>
 * <pre>
 *     String html = "&lt;p&gt;Hello&lt;/p&gt;";
 *     BufferedImage my = HtmlRenderer.render(html);
 * </pre>
 * <p>Internally, this class uses two other threads to perform the render
 * and wait for it to finish. The purpose of that wait is to allow the loading of
 * embedded images complete. The default wait time is 200 ms, but it can be
 * overrode by using the overloaded methods that take the wait time as a parameter.</p>
 * <p>Since the HTML document is renderer asynchronously, the {@link #render(String)} method is blocking.
 * If you are interested in a blocking-free behavior, you should construct instances of {@code HtmlRender}
 * by using the {@link #prepare(String)} method and monitor the asynchronous render
 * process by using the instance methods {@link #isDone()} or {@link #getResultIfDone()}.</p>
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class HtmlRenderer {

    /**
     * The default wait time for allowing the document be completed.
     * <p>This is used most for the purposes of fully rendering images.</p>
     * <p>This is defined as 200 milliseconds.</p>
     */
    private static final int DEFAULT_WAIT_TIME = 200;

    /**
     * The wait time for allowing the document be completed.
     */
    private final int sleepTime;

    /**
     * The thread that is responsible for performing the render.
     */
    private final Thread worker;

    /**
     * The image that will eventually be asynchronously produced as the result
     * of the render process.
     */
    private final AtomicReference<BufferedImage> result;

    /**
     * Creates an instance of a renderer for a given HTML source and a given wait sleep time.
     * <p>This constructor is private and exists solely for instantiation of the {@code HtmlRenderer}.</p>
     * @param html The HTML that should be rendered.
     * @param sleepTime The wait time.
     * @throws IllegalArgumentException If the {@code sleepTime} is negative or the {@code html} is {@code null}.
     */
    private HtmlRenderer(String html, int sleepTime) {
        if (html == null) throw new IllegalArgumentException("The html can't be null.");
        if (sleepTime < 0) throw new IllegalArgumentException("The sleepTime can't be negative.");
        this.result = new AtomicReference<>();
        this.worker = new Thread(() -> inBackground(html));
        this.sleepTime = sleepTime;
    }

    /**
     * Creates an instance of a renderer for a given HTML source and starts its render thread.
     * <p>Uses the default wait time of 200 ms.</p>
     * @param html The HTML that should be rendered.
     * @return The created renderer.
     * @throws IllegalArgumentException If the {@code html} is {@code null}.
     */
    public static HtmlRenderer prepare(String html) {
        return prepare(html, DEFAULT_WAIT_TIME);
    }

    /**
     * Creates an instance of a renderer for a given HTML source and a given
     * wait sleep time and starts its render thread.
     * @param html The HTML that should be rendered.
     * @param sleepTime The wait time.
     * @return The created renderer.
     * @throws IllegalArgumentException If the {@code sleepTime} is negative or the {@code html} is {@code null}.
     */
    public static HtmlRenderer prepare(String html, int sleepTime) {
        HtmlRenderer h = new HtmlRenderer(html, sleepTime);
        h.worker.start();
        return h;
    }

    /**
     * Does the heavy work of rendering the given HTML source in a background thread.
     * @param html The HTML source.
     */
    private void inBackground(String html) {
        if (EventQueue.isDispatchThread()) throw new AssertionError();
        AtomicReference<JFrame> frame = new AtomicReference<>();
        AtomicReference<JEditorPane> pane = new AtomicReference<>();
        try {
            invokeAndWait(() -> {
                JFrame jf = new JFrame();
                jf.setUndecorated(true);
                JEditorPane jep = new JEditorPane("text/html", html);
                jf.add(jep);
                jf.pack();
                jf.setResizable(false);
                jf.setLocationRelativeTo(null);
                frame.set(jf);
                pane.set(jep);
            });
            sleep();
            invokeAndWait(() -> {
                JFrame jf = frame.get();
                JEditorPane jep = pane.get();
                jf.pack();
                result.set(Screenshot.screenshot(jep));
                jf.dispose();
            });
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Returns {@code true} if the asynchronously rendered image is done or {@code false} otherwise.
     * @return {@code true} if the asynchronously rendered image is done or {@code false} otherwise.
     */
    public boolean isDone() {
        return result.get() != null;
    }

    /**
     * Returns an {@code Optional} containing the asynchronously rendered image if it is available.
     * @return An {@code Optional} containing the asynchronously rendered image if it is available.
     */
    public Optional<BufferedImage> getResultIfDone() {
        return Optional.ofNullable(result.get());
    }

    /**
     * Returns the rendered image.
     * <p>If the asynchronously loaded image is not available yet, waits until it is.</p>
     * @return The rendered image.
     * @throws InterruptedException If this thread is interrupted and the image was not available yet.
     */
    @SuppressFBWarnings("MDM_WAIT_WITHOUT_TIMEOUT")
    public BufferedImage getResult() throws InterruptedException {
        worker.join();
        return result.get();
    }

    /**
     * Sleeps for the {@code sleepTime}.
     * @throws InterruptedException If another thread interrupts the current one.
     */
    @SuppressFBWarnings("MDM_THREAD_YIELD")
    private void sleep() throws InterruptedException {
        if (EventQueue.isDispatchThread()) throw new AssertionError();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException x) {
            // Ignore.
        }
    }

    /**
     * Calls {@link EventQueue#invokeAndWait(Runnable)}.
     * @param run The {@code Runnable} that should be run with
     *     {@link EventQueue#invokeAndWait(Runnable)}.
     * @throws InterruptedException If another thread interrupts the current one.
     */
    private static void invokeAndWait(Runnable run) throws InterruptedException {
        if (EventQueue.isDispatchThread()) throw new AssertionError();
        try {
            EventQueue.invokeAndWait(run);
        } catch (InvocationTargetException x) {
            throw new AssertionError(x.getCause());
        }
    }

    /**
     * Renders a given HTML source and waits it to be rendered.
     * <p>Uses the default wait sleep time.</p>
     * @param html The HTML that should be rendered.
     * @return The rendered image.
     * @throws IllegalArgumentException If the {@code html} is {@code null}.
     * @throws InterruptedException If this thread is interrupted before the image become available.
     */
    public static BufferedImage render(String html) throws InterruptedException {
        return prepare(html).getResult();
    }

    /**
     * Renders a given HTML source using a given wait sleep time.
     * @param html The HTML that should be rendered.
     * @param sleepTime The wait time.
     * @return The rendered image.
     * @throws IllegalArgumentException If the {@code sleepTime} is negative or the {@code html} is {@code null}.
     * @throws InterruptedException If this thread is interrupted before the image become available.
     */
    public static BufferedImage render(String html, int sleepTime) throws InterruptedException {
        return prepare(html, sleepTime).getResult();
    }
}
