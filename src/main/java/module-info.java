import ninja.javahacker.javahtmlrenderer.HtmlRenderer;
import ninja.javahacker.javahtmlrenderer.Screenshot;
import ninja.javahacker.javahtmlrenderer.ImageCompare;

/**
 * Defines the {@link HtmlRenderer}, {@link Screenshot} and {@link ImageCompare} classes.
 */
@SuppressWarnings({
    "module", // opens
    "requires-automatic", "requires-transitive-automatic" // com.github.spotbugs.annotations
})
module ninja.javahacker.javahtmlrenderer {
    requires transitive java.desktop;
    requires transitive static lombok;
    requires transitive static com.github.spotbugs.annotations;
    exports ninja.javahacker.javahtmlrenderer;
    opens ninja.javahacker.javahtmlrenderer to ninja.javahacker.test.javahtmlrenderer;
}