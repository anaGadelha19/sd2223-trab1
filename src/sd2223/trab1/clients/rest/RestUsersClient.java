package sd2223.trab1.clients.rest;


import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Users;
import sd2223.trab1.api.rest.UsersService;

public class RestUsersClient extends RestClient implements Users {

    final WebTarget target;


    public RestUsersClient(URI serverURI) {
        super(serverURI);
        target = client.target(serverURI).path(UsersService.PATH);
    }

    private Result<String> clt_createUser(User user) {

        Response r = target.request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return super.toJavaResult(r, String.class);
        else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return Result.error(getErrorCodeFrom(r.getStatus())); //????TODO: THIS WAY OR THE OTHER?
        }
    }

    private Result<User> clt_getUser(String name, String pwd) {

        Response r = target.path(name)
                //Used to retrieve optional values from the request
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
            return super.toJavaResult(r, User.class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus());

        return null;
    }

    private Result<User> clt_updateUser(String name, String pwd, User user) {

        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity()) {
            System.out.println("Success, update user with name: " + name);
            return super.toJavaResult(r, User.class);//r.readEntity(User.class);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return null;
        }
    }

    private Result<User> clt_deleteUser(String name, String pwd) {

        Response r = target.path(name)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity()) {
            System.out.println("Success, deleted user with name: " + name);
            return super.toJavaResult(r, User.class);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return null;
        }

    }

    private Result<List<User>> clt_searchUsers(String pattern) {

        Response r = target
                .queryParam(UsersService.QUERY, pattern).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity()) {
            return r.readEntity(new GenericType<Result<List<User>>>() {
            });
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return null;
        }
    }

    private Result<User> clt_getUserByName(String name) {

        Response r = target.path("byName/" + name).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity()) {
            return super.toJavaResult(r, User.class);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus());
            return null;
        }
    }

    @Override
    public Result<String> createUser(User user) {
        return super.reTry(() -> clt_createUser(user));
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        return super.reTry(() -> clt_getUser(name, pwd));
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User user) {
        return super.reTry(() -> clt_updateUser(name, pwd, user));
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        return super.reTry(() -> clt_deleteUser(name, pwd));
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return super.reTry(() -> clt_searchUsers(pattern));
    }

  /*  @Override
    public Result<User> getUserByName(String name) {
        return super.reTry(() -> clt_getUserByName(name));
    }

   /* @Override
    public boolean hasUser(String user) {
        // TODO: O QUE FAZER AQUI??

        return ;
    }*/
}

