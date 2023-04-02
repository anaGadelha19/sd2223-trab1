package sd2223.trab1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.clients.RestUsersClient;
import sd2223.trab1.server.FeedsServer;
import sd2223.trab1.server.UsersServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Singleton
public class FeedsResource implements FeedsService {

    //TODO: Esta bom a variavel feeds?
    //Map<name@domain, HashMap<messageId, Message>> feeds
    private final Map<String, HashMap<Long, Message>> feeds = new ConcurrentHashMap<>();

    private final Map<User, LinkedList<String>> subscribers = new ConcurrentHashMap<>();

    private static Logger Log = Logger.getLogger(UsersResource.class.getName());

    private UsersResource usrRes = new UsersResource();

    private AtomicLong messagesIdGenerator = new AtomicLong(0);

    private RestUsersClient restUsersClient;

    private Discovery discovery;

    public FeedsResource() {
        discovery = FeedsServer.discovery;
        //this.restUsersClient = new RestUsersClient(discovery.knownUrisOf(UsersServer.SERVICE, 5)); TODO ¯\_(ツ)_/¯
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + "password = " + pwd + "; message = " + msg);

        String[] userSplit = user.split("@");

        User u = usrRes.getUser(userSplit[0], pwd);

        if(!userSplit[1].equals(msg.getDomain())){
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        long mid = messagesIdGenerator.incrementAndGet();
        Message newMsg = new Message(mid, userSplit[0], userSplit[1], msg.getText());

        addMessageToFeed(user, mid, newMsg);

        for(String sub: subscribers.get(u)){
            if(sub.contains(userSplit[1])){
                addMessageToFeed(sub, mid, newMsg);
            } //else {
                // TODO: use discovery?!?!
            //}
        }


        return mid;
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {

    }

    @Override
    public Message getMessage(String user, long mid) {
        Log.info("getMessage : user = " + user + "; messageId = " + mid);

        if(!usrRes.hasUser(user)){
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Message msg = feeds.get(user).get(mid);
        if(msg == null) {
            Log.info("Message does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return msg;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return null;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

    }

    @Override
    public List<String> listSubs(String user) {
        return null;
    }

    private void addMessageToFeed(String user, long mid, Message msg){
        // Checks if the user has already a "feed"
        if(feeds.get(user) == null) {
            feeds.put(user, new HashMap<>());
        }
        // Adds the message to the feed
        feeds.get(user).put(mid, msg);
    }
}
