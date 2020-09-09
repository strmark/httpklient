module httpklient {
    // required modules
    requires kotlin.stdlib;

    // optional modules
    requires static java.xml.bind;
    requires static java.xml.ws;
    requires static com.fasterxml.jackson.databind;

    // packages to export
    exports com.github.lion7.httpklient;
    exports com.github.lion7.httpklient.exception;
    exports com.github.lion7.httpklient.impl;
    exports com.github.lion7.httpklient.multipart;
    exports com.github.lion7.httpklient.readers;
    exports com.github.lion7.httpklient.writers;
}