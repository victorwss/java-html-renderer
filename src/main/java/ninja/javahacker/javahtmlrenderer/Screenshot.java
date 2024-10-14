package ninja.javahacker.javahtmlrenderer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 * Takes screenshots of AWT {@link Component}s.
 * <p>This should be used as such:</p>
 * <pre>
 *     java.awt.Component c = ...;
 *     BufferedImage img = Screenshot.screenshot(c);
 * </pre>
 * <p>Note that this code should never be used outside the AWT's Event Dispatch Thread
 * because AWT's component are not thread-safe and should never be used outside that thread.
 * Doing so is very likely to result in race-conditions, or data corruption.
 * However, this implementation does not checks if it is being run in AWT's Event Dispatch Thread.</p>
 * @author Victor Williams Stafusa da Silva
 */
public class Screenshot {

    /**
     * Instantiating this class in not allowed.
     */
    private Screenshot() {
        throw new UnsupportedOperationException();
    }

    /**
     * Takes an screenshot of some AWT's component.
     * @param c The component to be screenshot'd.
     * @return An image representing the screenshot of the component.
     * @throws IllegalArgumentException If the {@code c} is {@code null}.
     */
    public static BufferedImage screenshot(Component c) {
        if (c == null) throw new IllegalArgumentException("The component can't be null.");
        BufferedImage image = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(c.getWidth(), c.getHeight());
        Graphics graphics = image.createGraphics();
        c.print(graphics);
        graphics.dispose();
        return image;
    }
}
