package ninja.javahacker.test.javahtmlrenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class LoadResource {

    private LoadResource() {
        throw new UnsupportedOperationException();
    }

    private static List<URL> urlsFromClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            return List.of(((URLClassLoader) classLoader).getURLs());
        }
        return Stream
                .of(ManagementFactory.getRuntimeMXBean().getClassPath().split(File.pathSeparator))
                .map(LoadResource::url).toList();
    }

    private static URL url(String classPathEntry) {
        try {
            return new File(classPathEntry).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("URL could not be created from '" + classPathEntry + "'", ex);
        }
    }

    public static BufferedImage load(String source) {
        var r = urlsFromClassLoader(LoadResource.class.getClassLoader());
        var u = r.stream().filter(x -> x.toString().endsWith("/test/")).findFirst();
        var q = u.orElseThrow(() -> new AssertionError(r.toString()));
        var f = q.getFile() + source;
        if (f.charAt(0) == '/') f = f.substring(1);
        var s = new File(f);
        try {
            return ImageIO.read(s);
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }

    public static File saveTemp(BufferedImage image) {
        try {
            var temp = Files.createTempFile("img", ".png").toAbsolutePath().toFile();
            ImageIO.write(image, "png", temp);
            return temp;
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }

    public static void saveX(BufferedImage image) {
        saveAs(image, "temp.png");
    }

    public static void saveAs(BufferedImage image, String name) {
        try {
            ImageIO.write(image, "png", new File(name));
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }
}
