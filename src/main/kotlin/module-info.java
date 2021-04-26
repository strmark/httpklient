module httpklient {
    // required modules
    requires kotlin.stdlib;

    // optional modules
    requires static java.xml.bind;
    requires static java.xml.soap;
    requires static java.datatransfer;
    requires static com.fasterxml.jackson.databind;
    requires static io.opentracing.api;
    requires static org.apache.commons.io;

    // packages to export
    exports com.github.lion7.httpklient;
}
