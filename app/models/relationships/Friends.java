package models.relationships;

import io.ebean.Expr;
import io.ebean.Finder;
import io.ebean.Model;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Friends extends Model {
    @Id
    String friendsId;
    @Constraints.Required
    @OneToOne
    private User friendRequester;
    @Constraints.Required
    @OneToOne
    private User friendReceiver;
    private RequestStatus requestStatus;
    private LocalDateTime requestDate;
    private LocalDateTime acceptedDate;

    public static Finder<String, Friends> find = new Finder<>(Friends.class);
    public static List<Friends> findFriends(User friend) {return find.query().where().eq("friend", friend).findList();}
    public static Friends findRequest(User user1, User user2){
        return find.query().where().or(
                Expr.and(
                        Expr.eq("friendReceiver", user1),
                        Expr.eq("friendRequester", user2)
                ),
                Expr.and(
                        Expr.eq("friendReceiver", user2),
                        Expr.eq("friendRequester", user1)
                )
        ).findOne();
    }

    public enum RequestStatus {
        ACCEPTED,
        DENIED,
        UNFRIENDEDBYREQUESTER,
        UNFRIENDEDBYRECEIVER,
        PENDING
    }

    public Friends( @Constraints.Required User friendRequester, @Constraints.Required User friendReceiver) {
        this.friendsId = friendRequester.getUserId() + ";" + friendReceiver.getUserId();
        this.friendRequester = friendRequester;
        this.friendReceiver = friendReceiver;
        this.requestStatus = RequestStatus.PENDING;
        this.requestDate = LocalDateTime.now();
        this.acceptedDate = null;
    }

    public String getFriendsId() {return friendsId;}
    public void setFriendsId(String friendsId) {this.friendsId = friendsId;}
    public User getFriendRequester() {return friendRequester;}
    public void setFriendRequester(User friendRequester) {this.friendRequester = friendRequester;}
    public User getFriendReceiver() {return friendReceiver;}
    public void setFriendReceiver(User friendReceiver) {this.friendReceiver = friendReceiver;}
    public RequestStatus getRequestStatus() {return requestStatus;}
    public void setRequestStatus(RequestStatus requestStatus) {this.requestStatus = requestStatus;}
    public LocalDateTime getRequestDate() {return requestDate;}
    public void setRequestDate(LocalDateTime requestDate) {this.requestDate = requestDate;}
    public LocalDateTime getAcceptedDate() {return acceptedDate;}
    public void setAcceptedDate(LocalDateTime acceptedDate) {this.acceptedDate = acceptedDate;}

    public void acceptRequest(User user){
        switch (requestStatus) {
            case PENDING: case UNFRIENDEDBYRECEIVER: case DENIED:
                if (!user.equals(friendReceiver)) break;
                requestStatus = RequestStatus.ACCEPTED;
                acceptedDate = LocalDateTime.now();
                friendReceiver.addFriend();
                friendReceiver.save();
                friendRequester.addFriend();
                friendRequester.save();
                this.save();
                break;
            case UNFRIENDEDBYREQUESTER:
                if (!user.equals(friendRequester)) break;
                requestStatus = RequestStatus.ACCEPTED;
                acceptedDate = LocalDateTime.now();
                friendReceiver.addFriend();
                friendReceiver.save();
                friendRequester.addFriend();
                friendRequester.save();
                this.save();
                break;
        }
    }
    public void refuseRequest(User user){
        switch (requestStatus){
            case PENDING:
                if(!user.equals(friendReceiver)) break;
                requestStatus = RequestStatus.DENIED;
                this.save();
                break;
            case ACCEPTED:
                if(user.equals(friendReceiver)) requestStatus = RequestStatus.UNFRIENDEDBYRECEIVER;
                else if (user.equals(friendRequester)) requestStatus = RequestStatus.UNFRIENDEDBYREQUESTER;
                else break;
                friendRequester.removeFriend();
                friendRequester.save();
                friendReceiver.removeFriend();
                friendReceiver.save();
                this.acceptedDate = LocalDateTime.now();
                this.save();
        }
    }
}
