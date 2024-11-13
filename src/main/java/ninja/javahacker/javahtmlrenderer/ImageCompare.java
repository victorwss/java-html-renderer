package ninja.javahacker.javahtmlrenderer;

import java.awt.image.BufferedImage;

/**
 * Compares two {@link BufferedImage}s.
 *
 * <p>Two images are considered equals only if they both have the same sizes and
 * have the same pixel-by-pixel colors.</p>
 *
 * <p>This should be used as such:</p>
 * <pre>
 *     BufferedImage a = ...;
 *     BufferedImage b = ...;
 *     boolean ImageCompare.equals(a, b);
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
public final class ImageCompare {

    /**
     * Instantiating this class in not allowed.
     */
    private ImageCompare() {
        throw new UnsupportedOperationException();
    }

    /**
     * Compares two {@link BufferedImage}s.
     *
     * <p>Two images are considered equals only if they both have the same sizes and have
     * the same pixel-by-pixel colors.</p>
     *
     * <p>This implementation accepts null values in the parameters. The {@code null} is
     * considered equals only to itself and unequal to everything else.</p>
     *
     * <p>This should be used as such:</p>
     *
     * <pre>
     *     BufferedImage a = ...;
     *     BufferedImage b = ...;
     *     boolean ImageCompare.equals(a, b);
     * </pre>
     *
     * @param a The first image to be compared.
     * @param b The second image to be compared.
     * @return {@code true} if both images have the same sizes and are equals in
     *     a pixel-by-pixel comparison or {@code false} otherwise.
     */
    public static boolean equals(BufferedImage a, BufferedImage b) {
        if (a == null) return b == null;
        if (b == null) return false;
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) return false;
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) return false;
            }
        }
        return true;
    }
}
