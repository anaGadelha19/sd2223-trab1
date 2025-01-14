package sd2223.trab1.api.java;


import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;

import java.util.List;

public interface Feeds { //it has all the methods that the feedsService has


    Result<Long> postMessage(String user, String pwd, Message msg);

    Result<Void> removeFromPersonalFeed(String user, long mid, String pwd);

    Result<Void> removeDeletedUserFeed(User user);

    Result<Message> getMessage(String user, long mid);

    Result<List<Message>> getMessages(String user, long time);

    Result<Void> subUser(String user, String userSub, String pwd);

    Result<Void> unsubscribeUser(String user, String userSub, String pwd);

    Result<List<String>> listSubs(String user);
}
