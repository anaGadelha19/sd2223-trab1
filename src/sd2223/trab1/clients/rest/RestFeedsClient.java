package sd2223.trab1.clients.rest;

import jakarta.ws.rs.client.WebTarget;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;

import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.rest.UsersService;

import java.net.URI;
import java.util.List;

public class RestFeedsClient extends RestClient implements Feeds {

    //TODO: Retry methods, clt_methods
    final WebTarget target;


    RestFeedsClient(URI serverURI, WebTarget target) {
        super(serverURI);
        this.target = client.target(serverURI).path(UsersService.PATH);
        ;
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        return null;
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {

        return null;
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return null;
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
}
