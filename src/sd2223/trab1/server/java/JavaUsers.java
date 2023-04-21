package sd2223.trab1.server.java;

import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.api.java.Result.ErrorCode;
import sd2223.trab1.server.rest.RestUsersServer;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaUsers  implements Users {



        private final Map<String, User> users = new ConcurrentHashMap<>();

        private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());


        public JavaUsers(){

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
                return Result.error( ErrorCode.CONFLICT);
            }
            //TODO: Change this
            return Result.ok(user.getName() + "@" + user.getDomain());
        }

        @Override
        public Result<User> getUser(String name, String pwd) {
            Log.info("getUser : user = " + name + "; pwd = " + pwd);

            // Check if user is valid
            if (name == null || pwd == null) {
                Log.info("Name or Password null.");
                return Result.error( ErrorCode.BAD_REQUEST);

            }

            User user = users.get(name);
            // Check if user exists
            if (user == null) {
                Log.info("User does not exist.");
                return Result.error( ErrorCode.NOT_FOUND);

            }

            //Check if the password is correct
            if (!user.getPwd().equals(pwd)) {
                Log.info("Password is incorrect.");
                return Result.error( ErrorCode.FORBIDDEN);

            }

            return Result.ok(user);
        }

        @Override
        public Result<User> updateUser(String name, String pwd, User user) {

            Log.info("updateUser : user = " + name + "; newUser = " + user);

            if(!user.getName().equals(name))
                return Result.error(ErrorCode.BAD_REQUEST);

            User oldUser;
            //getUser method checks the BAD_REQUEST, NOT_FOUND and FORBIDDEN exceptions
            Result<User> getRes = getUser(name, pwd);

            if(getRes.isOK())
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
            // TODO Auto-generated method stub
        }

        @Override
        public Result<User> deleteUser(String name, String pwd) {

            Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

            User user;
            //getUser method checks the BAD_REQUEST, NOT_FOUND and FORBIDDEN exceptions
            Result<User> getRes = getUser(name, pwd);

            if(getRes.isOK())
                user = getRes.value();
            else
                return getRes;

            users.remove(name);

            return Result.ok(user);
            // TODO: Remove the feed and its messages
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

    @Override
    public Result<Void> verifyPassword(String name, String pwd) {
        return null;
    }

    @Override
        public boolean hasUser(String user) {
            String[] aux = user.split("@");
            return users.get(aux[0]) != null;
        }

    }



