package sd2223.trab1.server.rest;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.Discovery;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Logger;

public class RestFeedsServer {


    //TODO: Probably like the usersServer but I have to see

    private static Logger Log = Logger.getLogger(RestFeedsServer.class.getName());

    static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);


    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "FeedsService";
    public static final String SERVICE_NAME = "feeds";
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static Discovery discovery;

    public static void main(String[] args) {
        try {
            //Used to register resources in a server
            ResourceConfig config = new ResourceConfig();
            config.register(RestFeedsResource.class);
            // config.register(CustomLoggingFilter.class);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            //Launches HTTP server in a separate thread
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

            discovery = Discovery.getInstance();
            discovery.announce(SERVICE_NAME, serverURI);

            //TODO: Add Discovery
            Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

            // More code can be executed here...
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}



