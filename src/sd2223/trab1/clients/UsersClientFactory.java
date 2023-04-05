package sd2223.trab1.clients;


import sd2223.trab1.Discovery;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.clients.rest.RestUsersClient;
import sd2223.trab1.clients.soap.SoapUsersClient;

import java.net.URI;


public class UsersClientFactory {

    private static final String REST = "/rest";
    private static final String SOAP = "/soap";

    private Discovery discovery = Discovery.getInstance();

    public static Users get(URI serverURI) {
        var uriString = serverURI.toString(); //use discovery to find it

        if (uriString.endsWith(REST))
            return new RestUsersClient(serverURI);
        else if (uriString.endsWith(SOAP))
            return new SoapUsersClient(serverURI);
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}
