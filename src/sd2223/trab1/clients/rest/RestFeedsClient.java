package sd2223.trab1.clients.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;

import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.api.rest.UsersService;

import javax.print.attribute.standard.Media;
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

    private Result<Long> clt_postMessage(String user, String pwd, Message msg) {
        Response r = target.path(user)
                .queryParam(FeedsService.PWD, pwd).request() // UsersService or FeedsService??
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            return super.toJavaResult(r, Long.class);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }
    }

    private Result<Void> clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path(user).path(String.valueOf(mid)) //IDK?? TODO
                .queryParam(FeedsService.PWD, pwd).request()
                .delete();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            System.out.println("User removed from personal Feed");
            return Result.ok(); // TODO: What happens when void??
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }
    }

    private Result<Message> clt_getMessage(String user, long mid) {
        Response r = target.path(user)
                .path(String.valueOf(mid))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            return super.toJavaResult(r, Message.class);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }
    }

    private Result<List<Message>> clt_getMessages(String user, long time) {
        Response r = target.path(user)
                .path(String.valueOf(time))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            return r.readEntity(new GenericType<Result<List<Message>>>() {
            }); // TODO: IDK IF THIS IS RIGHT
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }
    }

    private Result<Void> clt_subUser(String user, String userSub, String pwd) {
        Response r = target.path(user)
                .path(userSub)
                .queryParam(FeedsService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            System.out.println("User subscribed Successfully");
            return Result.ok(); // TODO: What happens when void??
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }
    }

    private Result<Void> clt_unsubscribeUser(String user, String userSub, String pwd) {
        Response r = target.path(user)
                .path(userSub)
                .queryParam(FeedsService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            System.out.println("User unsubscribed Successfully");
            return Result.ok(); // TODO: What happens when void??
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }
    }

    private Result<List<String>> clt_listSubs(String user) {
        Response r = target.path(user)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            return r.readEntity(new GenericType<Result<List<String>>>() {
            }); // TODO: IDK IF THIS IS RIGHT
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus()));
        }

    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        return super.reTry(() -> clt_postMessage(user, pwd, msg));
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {

        return super.reTry(() -> clt_removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return super.reTry(() -> clt_getMessage(user, mid));
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return super.reTry(() -> clt_getMessages(user, time));
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {

        return super.reTry(() -> clt_subUser(user, userSub, pwd));
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {

        return super.reTry(() -> clt_unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return super.reTry(() -> clt_listSubs(user));
    }
}
