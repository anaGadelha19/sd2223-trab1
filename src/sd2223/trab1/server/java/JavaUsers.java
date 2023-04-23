package sd2223.trab1.server.java;

import sd2223.trab1.Discovery;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.api.java.Result.ErrorCode;
import sd2223.trab1.clients.UsersClientFactory;
import sd2223.trab1.server.rest.RestUsersServer;


import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaUsers implements Users {


    private final Map<String, User> users = new ConcurrentHashMap<>();

    private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

    private Feeds feeds;

    private final String serviceName;
    private final Discovery discovery;


    public JavaUsers(String serviceName, Discovery discovery) {
        this.serviceName = serviceName;
        this.discovery = discovery;
    }

    @Override
    public Result<String> createUser(User user) {
        Log.info("createUser : " + user);

        // Check if user data is valid
        if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
            Log.info("User object invalid.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        // Insert user, checking if name already exists
        if (users.putIfAbsent(user.getName(), user) != null) {
            Log.info("User already exists.");
            return Result.error(ErrorCode.CONFLICT);
        }

        return Result.ok(user.getName() + "@" + user.getDomain());
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if (name == null || pwd == null) {
            Log.info("Name or Password null.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        User user = users.get(name);
        // Check if user exists
        if (user == null) {
            Log.info("User does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);

        }

        //Check if the password is correct
        if (!user.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            return Result.error(ErrorCode.FORBIDDEN);

        }

        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User user) {

        Log.info("updateUser : user = " + name + "; newUser = " + user);

        if (!user.getName().equals(name))
            return Result.error(ErrorCode.BAD_REQUEST);

        User oldUser;
        //getUser method checks the BAD_REQUEST, NOT_FOUND and FORBIDDEN exceptions
        Result<User> getRes = getUser(name, pwd);

        if (getRes.isOK())
            oldUser = getRes.value();
        else
            return getRes;


        if (user.getPwd() != null)
            oldUser.setPwd(user.getPwd());
        if (user.getDomain() != null)
            oldUser.setDomain(user.getDomain());
        if (user.getDisplayName() != null)
            oldUser.setDisplayName(user.getDisplayName());

        users.put(oldUser.getName(), oldUser);

        //oldUser is now updated
        return Result.ok(oldUser);
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) throws InterruptedException {

        Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

        feeds = UsersClientFactory.getFeedService(discovery.knownUrisOf(serviceName, 1)[0]);

        User user;
        //getUser method checks the BAD_REQUEST, NOT_FOUND and FORBIDDEN exceptions
        Result<User> getRes = getUser(name, pwd);

        if (getRes.isOK())
            user = getRes.value();
        else
            return getRes;

        feeds.removeDeletedUserFeed(user);
        users.remove(name);

        return Result.ok(user);
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {

        Log.info("searchUsers : user = " + pattern);

        List<User> usersList = new LinkedList<>();

        for (String userName : users.keySet()) {
            if (userName.toUpperCase().contains(pattern.toUpperCase())) {
                User user = users.get(userName);
                usersList.add(user);
            }
        }
        return Result.ok(usersList);
    }

   /* @Override
    public Result<User> getUserByName(String name) {
        Log.info("getUser : user = " + name);

        User user = users.get(name);

        if (user == null) {// Check if user exists
            Log.info("User does not exist.");
            return Result.error(ErrorCode.NOT_FOUND);
        } else {
            return Result.ok(user);
        }

    }*/

    //  @Override
   private boolean hasUser(String user) {
        String[] aux = user.split("@");
        return users.get(aux[0]) != null;
    }


}



