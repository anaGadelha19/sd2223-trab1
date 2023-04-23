package sd2223.trab1.api.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Result;

import java.util.List;

@WebService(serviceName = FeedsService.NAME, targetNamespace = FeedsService.NAMESPACE, endpointInterface = FeedsService.INTERFACE)
public interface FeedsService {


    static final String NAME = "feeds";
    static final String NAMESPACE = "http://sd2223";
    static final String INTERFACE = "sd2223.trab1.api.soap.FeedsService";


    @WebMethod
    Long postMessage(String user, String pwd, Message msg) throws FeedsException;

    @WebMethod
    void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException;

    @WebMethod
    Message getMessage(String user, long mid) throws FeedsException;

    @WebMethod
    List<Message> getMessages(String user, long time) throws FeedsException;


    @WebMethod
    void subUser(String user, String userSub, String pwd) throws FeedsException;


    @WebMethod
    void unsubscribeUser(String user, String userSub, String pwd) throws FeedsException;


    @WebMethod
    List<String> listSubs(String user) throws FeedsException;
}
