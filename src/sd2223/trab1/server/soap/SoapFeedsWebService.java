
package sd2223.trab1.server.soap;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.soap.FeedsException;
import sd2223.trab1.api.soap.FeedsService;
import sd2223.trab1.server.java.JavaFeeds;

import java.util.List;

public class SoapFeedsWebService extends SoapWebService<FeedsException> implements FeedsService {

    final Feeds impl;

    public SoapFeedsWebService() throws InterruptedException {
        super((result) -> new FeedsException(result.error().toString()));
        this.impl = new JavaFeeds(SoapUsersServer.SERVICE_NAME, SoapFeedsServer.discovery);

    }

    @Override
    public Long postMessage(String user, String pwd, Message msg) throws FeedsException {
        return super.fromJavaResult(impl.postMessage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) throws FeedsException {
        super.fromJavaResult(impl.removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public void removeDeletedUserFeed(User user) throws FeedsException {
        super.fromJavaResult(impl.removeDeletedUserFeed(user));
    }

    @Override
    public Message getMessage(String user, long mid) throws FeedsException {
        return super.fromJavaResult(impl.getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) throws FeedsException {
        return super.fromJavaResult(impl.getMessages(user, time));
    }


    @Override
    public void subUser(String user, String userSub, String pwd) throws FeedsException {
        //super.fromJavaResult(impl.subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) throws FeedsException {
        super.fromJavaResult(impl.unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) throws FeedsException {
        return super.fromJavaResult(impl.listSubs(user));
    }
}

