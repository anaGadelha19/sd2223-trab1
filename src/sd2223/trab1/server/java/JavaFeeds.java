package sd2223.trab1.server.java;


import sd2223.trab1.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.clients.rest.RestUsersClient;
import sd2223.trab1.server.rest.RestUsersServer;
import sd2223.trab1.api.java.Result.ErrorCode;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {
//TODO: Complete this Class

    //TODO: Esta bom a variavel feeds?
    //Map<name@domain, HashMap<messageId, Message>> feeds
    private final Map<String, HashMap<Long, Message>> feeds = new ConcurrentHashMap<>();

    private final Map<User, LinkedList<String>> subscribers = new ConcurrentHashMap<>();

    private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

    private JavaUsers usrRes = new JavaUsers();

    private AtomicLong messagesIdGenerator = new AtomicLong(0);

    private RestUsersClient restUsersClient; // TODO: add get ethod from factory

    private Discovery discovery = Discovery.getInstance();


    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + "password = " + pwd + "; message = " + msg);

        String[] userSplit = user.split("@");

        User u = (User) usrRes.getUser(userSplit[0], pwd); // Cast??

        if (!userSplit[1].equals(msg.getDomain())) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);

        }

        long mid = messagesIdGenerator.incrementAndGet();
        Message newMsg = new Message(mid, userSplit[0], userSplit[1], msg.getText());

        addMessageToFeed(user, mid, newMsg);

        for (String sub : subscribers.get(u)) {
            if (sub.contains(userSplit[1])) {
                addMessageToFeed(sub, mid, newMsg);
            } else {
                //URI[] uris = discovery.knownUrisOf(RestUsersServer.SERVICE, 1);

                   /* for(URI uri: uris){
                        String responseSub;
                    }*/
                // TODO: use discovery?!?!
            }
        }


        return Result.ok(mid);
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {

        return null;
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage : user = " + user + "; messageId = " + mid);

        if (!usrRes.hasUser(user)) {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);

        }

        Message msg = feeds.get(user).get(mid);
        if (msg == null) {
            Log.info("Message does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);

        }

        return Result.ok(msg);
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return null;
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {

        return null;
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {

        return null;
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return null;
    }

    private void addMessageToFeed(String user, long mid, Message msg) {
        // Checks if the user has already a "feed"
        if (feeds.get(user) == null) {
            feeds.put(user, new HashMap<>());
        }
        // Adds the message to the feed
        feeds.get(user).put(mid, msg);
    }
}


