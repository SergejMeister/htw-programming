package com.htw.master.prog.broker.rest;

import com.sun.net.httpserver.HttpServer;
import de.sb.java.TypeMetadata;
import de.sb.java.net.HttpModuleHandler;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * This class is used within a Java-SE VM to deploy REST services.
 * Note that for LAZY fetching of entities with EclipseLink (dynamic weaving) the following
 * has to be added to the JVM start parameters: -javaagent:[path]eclipselink.jar
 */
@TypeMetadata(copyright = "2013-2015 Sascha Baumeister, all rights reserved", version = "1.0.0", authors = "Sascha Baumeister")
public class ApplicationContainer {

    private final Logger LOG = LoggerFactory.getLogger(ApplicationContainer.class);

    private final int servicePort;

    public ApplicationContainer(int servicePort) {
        this.servicePort = servicePort;
    }

    /**
     * Application entry point. The given argument is expected to be a service port.
     *
     * @param args the runtime arguments
     *
     * @throws NumberFormatException if the given port is not a number
     * @throws IOException           if there is an I/O related problem
     */
    static public void main(final String[] args) throws NumberFormatException, IOException {
        final int servicePort = args.length == 0 ? 80 : Integer.parseInt(args[0]);
        new ApplicationContainer(servicePort).run();
    }

    public void run() throws NumberFormatException, IOException {
        final URI serviceURI;
        try {
            serviceURI =
                    new URI("http", null, InetAddress.getLocalHost().getCanonicalHostName(), servicePort, "/services",
                            null, null);
        } catch (final URISyntaxException exception) {
            throw new AssertionError();
        }

        // Note that server-startup is only required in Java-SE, as any Java-EE engine must ship a built-in HTTP server
        // implementation and XML-based configuration. The Factory-Class used is Jersey-specific, while the HTTP server
        // type used is Oracle/OpenJDK-specific. Other HTTP server types more suitable for production environments are
        // available, such as Apache Tomcat, Grizzly, Simple, etc.
        final ResourceConfig configuration = new ResourceConfig()
                .packages(ApplicationContainer.class.getPackage().toString())
                .register(MoxyJsonFeature.class)    // edit "network.http.accept.default" in Firefox's "about:config"
                .register(MoxyXmlFeature.class)        // to make "application/json" preferable to "application/xml"
                .register(EntityFilteringFeature.class);

        try {
            final HttpServer container = JdkHttpServerFactory.createHttpServer(serviceURI, configuration);
            final HttpModuleHandler resourceHandler = new HttpModuleHandler("/resources");
            container.createContext(resourceHandler.getContextPath(), resourceHandler);
            try {
                System.out.format("HTTP container running on service address %s:%s, enter \"quit\" to stop.\n",
                        serviceURI.getHost(), serviceURI.getPort());
                System.out.format("Service path \"%s\" is configured for REST service access.\n", serviceURI.getPath());
                System.out.format("Service path \"%s\" is configured for JAR resource access.\n",
                        resourceHandler.getContextPath());
                final BufferedReader charSource = new BufferedReader(new InputStreamReader(System.in));
                while (!"quit".equals(charSource.readLine())) ;
            } finally {
                container.stop(0);
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }


}