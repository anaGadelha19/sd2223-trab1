package sd2223.trab1.server.soap;

import jakarta.xml.ws.Endpoint;
import sd2223.trab1.Discovery;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoapFeedsServer {


    public static final int PORT = 8081;
    public static final String SERVICE_NAME = "feeds";
    public static String SERVER_BASE_URI = "http://%s:%s/soap";
    public static Discovery discovery = null;

    private static Logger Log = Logger.getLogger(sd2223.trab1.server.soap.SoapFeedsServer.class.getName());

    public static void main(String[] args) throws Exception {

//		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
//		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
//		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
//		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

        Log.setLevel(Level.INFO);

        String ip = InetAddress.getLocalHost().getHostAddress();
        String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

        Discovery.getInstance().announce(SERVICE_NAME, serverURI);

        Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new SoapFeedsWebService());

        Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE_NAME, serverURI));
    }
}



