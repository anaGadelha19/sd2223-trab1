package sd2223.trab1.server;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.server.resources.FeedsResource;


import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;


public class FeedsServer {

    //TODO: Probably like the usersServer but I have to see

    private static Logger Log = Logger.getLogger(sd2223.trab1.server.UsersServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "FeedsService";
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {
        try {
            //Used to register resources in a server
            ResourceConfig config = new ResourceConfig();
            config.register(FeedsResource.class);
            // config.register(CustomLoggingFilter.class);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            //Launches HTTP server in a separate thread
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);



            //TODO: Add Discovery
            Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

            // More code can be executed here...
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}



