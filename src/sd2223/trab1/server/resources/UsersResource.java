package sd2223.trab1.server.resources;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;

@Singleton
public class UsersResource implements UsersService {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    private static Logger Log = Logger.getLogger(UsersResource.class.getName());

    public UsersResource() {
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);

        // Check if user data is valid
        if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
            Log.info("User object invalid.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        // Insert user, checking if name already exists
        if (users.putIfAbsent(user.getName(), user) != null) {
            Log.info("User already exists.");
            throw new WebApplicationException(Status.CONFLICT);
        }
        //TODO: Change this
        return user.getName() + "@" + user.getDomain();
    }

    @Override
    public User getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);

        // Check if user is valid
        if (name == null || pwd == null) {
            Log.info("Name or Password null.");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        User user = users.get(name);
        // Check if user exists
        if (user == null) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        //Check if the password is correct
        if (!user.getPwd().equals(pwd)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        return user;
    }

    @Override
    public User updateUser(String name, String pwd, User user) {

        Log.info("updateUser : user = " + name + "; newUser = " + user);

        //getUser method checks the BAD_REQUEST, NOT_FOUND and FORBIDDEN exceptions
        User oldUser = getUser(name, pwd);

        if (user.getName() != null)
            oldUser.setName(user.getName());
        if (user.getPwd() != null)
            oldUser.setPwd(user.getPwd());
        if (user.getDomain() != null)
            oldUser.setDomain(user.getDomain());
        if (user.getDisplayName() != null)
            oldUser.setDisplayName(user.getDisplayName());

        users.put(oldUser.getName(), oldUser);

        //oldUser is now updated
        return oldUser;
        // TODO Auto-generated method stub
    }

    @Override
    public User deleteUser(String name, String pwd) {

        Log.info("deleteUser : user = " + name);

        //getUser method checks the BAD_REQUEST, NOT_FOUND and FORBIDDEN exceptions
        User user = getUser(name, pwd);

        users.remove(name);

        return user;
        // TODO: Remove the feed and its messages
    }

    @Override
    public List<User> searchUsers(String pattern) {
        // TODO Auto-generated method stub
        return null;
    }

}

