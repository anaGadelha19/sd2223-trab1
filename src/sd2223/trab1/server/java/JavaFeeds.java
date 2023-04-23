package sd2223.trab1.server.java;


import sd2223.trab1.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.clients.UsersClientFactory;
import sd2223.trab1.clients.rest.RestUsersClient;
import sd2223.trab1.server.rest.RestUsersServer;
import sd2223.trab1.api.java.Result.ErrorCode;


import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {
//TODO: Complete this Class

    //TODO: Esta bom a variavel feeds?
    //ConcurrentHashMap<name@domain, ConcurrentHashMap<messageId, Message>> feeds
    private final Map<String, Map<Long, Message>> feeds = new ConcurrentHashMap<>();

    // User -> user that has a linkedList of subscribers
    private final Map<String, LinkedList<String>> subscribers = new ConcurrentHashMap<>();

    //User -> user that has a linkedList of users that were subscribed by the user
    private final Map<String, LinkedList<String>> subscribedTo = new ConcurrentHashMap<>();

    private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

    private AtomicLong messagesIdGenerator = new AtomicLong(-1);

    private Users users;

    private final String serviceName;
    private final Discovery discovery;

    public JavaFeeds(String serviceName, Discovery discovery) throws InterruptedException {
        this.serviceName = serviceName;
        this.discovery = discovery;

        URI[] usersUris = discovery.knownUrisOf(serviceName, 1);
        users = UsersClientFactory.getUserService(usersUris[0]);
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + " password = " + pwd + "; message = " + msg);

        String[] userSplit = user.split("@");

        User u;
        Result<User> getRes = users.getUser(userSplit[0], pwd);

        //  TODO: VERIFY this
        if (getRes == null || !getRes.isOK()) {//TODO: verify if user exists? ????
            Log.info("User does not exist.");
            return Result.error(ErrorCode.FORBIDDEN);
        } else {
            u = getRes.value();

        }

        if (!u.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        if (!userSplit[1].equals(msg.getDomain())) {
            Log.info("User does not exist in this domain.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        messagesIdGenerator.incrementAndGet();
        long mid = messagesIdGenerator.get();
        Message newMsg = new Message(mid, userSplit[0], userSplit[1], msg.getText());

        addMessageToFeed(user, mid, newMsg);

        if (subscribers.get(u) != null) {
            for (String sub : subscribers.get(u)) {
                addMessageToFeed(sub, mid, newMsg);
            }
        }
        return Result.ok(mid);
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        Log.info("removeFromPersonalFeed : user = " + user + " password = " + pwd + "; messageId = " + mid);

        String[] userSplit = user.split("@");

        User u;
        Result<User> getRes = users.getUser(userSplit[0], pwd);

        // If the user does not exist
        if (getRes.isOK())
            u = getRes.value();
        else {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        // If password is incorrect
        if (!u.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        //If the message does not exist
        Result<Message> msg = getMessage(user, mid);
        if (!msg.isOK()) {
            Log.info("Message does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);

        } else {
            feeds.get(user).remove(mid);
            Log.info("Message removed successfully");
            return Result.ok();
        }


    }

    @Override
    public Result<Void> removeDeletedUserFeed(User user) {
        Log.info("removeDeletedUserFeed : user = " + user.getName());

        LinkedList<String> followers = subscribers.get(user);
        Set<Long> userMessagesId = feeds.get(user.getName()).keySet();

        for (String name : followers) {
            for (Long mid : userMessagesId) {
                feeds.get(name).remove(mid);
            }
        }

        feeds.remove(user.getName() + "@" + user.getDomain());

        return Result.ok();
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage : user = " + user + "; messageId = " + mid);
//TODO: verify if user exists?
        if (feeds.get(user) == null) {
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
        Log.info("getMessages : user = " + user + "; time = " + time);
//TODO: verify if user exists?
        //TODO: I do not know if it is 100% in accordance with what is requested
        if (feeds.get(user) == null) {
            Log.info("User either does not exist or has no messages.");
            return Result.error(ErrorCode.NOT_FOUND);

        }

        List<Message> msgList = new LinkedList<>();
        for (Message msg : feeds.get(user).values()) {
            if (msg.getCreationTime() >= time) {
                msgList.add(msg);
            }
        }

        return Result.ok(msgList);
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        Log.info("subUser : user = " + user + "; userSub = " + userSub + "; pwd = " + pwd);


        String[] userSplit = user.split("@");
        //String[] userSubSplit = userSub.split("@");

        User u;
        Result<User> getUser = users.getUser(userSplit[0], pwd);

        // If the user does not exist
        if (getUser.isOK())
            u = getUser.value();
        else {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        // If password is incorrect
        if (!u.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);
        }

        //If user to be subscribed does not exist //TODO: verify this
       /* if (users.getUserByName(userSub) == null) {
            Log.info("User to be subscribe does not exist.");TODO: verify if user exists?
            return Result.error(ErrorCode.FORBIDDEN);
        }*/

        // Add to the map of the users that subscribed the userSub
        if (subscribedTo.get(user) != null) {
            subscribedTo.get(user).add(userSub);
        } else {
            LinkedList<String> firstSubscriber = new LinkedList<>();
            firstSubscriber.add(userSub);
            subscribedTo.put(user, firstSubscriber);
        }

        // Add to the map of the subscribers of the userSub
        if (subscribers.get(userSub) != null) {
            //Adding user to the userSub list of subscribers
            subscribers.get(userSub).add(user);
            Log.info("Subscribers IF IT EXISTSSS" + subscribers.get(userSub));

        } else {
            // When it is the first subscriber we create a new list and add the first one
            LinkedList<String> firstSubscriber = new LinkedList<>();
            firstSubscriber.add(user);
            subscribers.put(userSub, firstSubscriber);

            Log.info("Subscribers IF DIDNT EXIST" + subscribers.get(userSub));
        }

        return Result.ok();
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {


        return null;
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        Log.info("listSubs : user = " + user);
        /*String[] userSplit = user.split("@"); //TODO: verify if user exists?
        //String[] userSubSplit = userSub.split("@");

        Result<User> getUser = users.getUser(userSplit[0], "");

        // If the user does not exist
        if (getUser == null || !getUser.isOK()) {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.FORBIDDEN);
        }
*/
        if (subscribedTo.get(user) == null) {
            Log.info("User does not have subscribers.");
            return Result.ok(subscribedTo.get(user));
        }

        return Result.ok(subscribedTo.get(user));
    }

    private void addMessageToFeed(String user, long mid, Message msg) {
        feeds.putIfAbsent(user, new ConcurrentHashMap<>());
        feeds.get(user).putIfAbsent(mid, msg);
    }

    private boolean hasUser(String user) {

        Log.info("final User = " + users.getUser(user, ""));

        var result = users.getUser(user, "");
        return result.error() == ErrorCode.FORBIDDEN;
    }
}


