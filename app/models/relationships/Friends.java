package models.relationships;

import io.ebean.Finder;
import io.ebean.Model;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Friends extends Model {

    @Id
    String friendsId;
    @Constraints.Required
    private User friendRequester;
    @Constraints.Required
    private User friendReceiver;
    private RequestStatus requestStatus;
    private LocalDateTime requestDate;
    private LocalDateTime acceptedDate;

    public static Finder<String, Friends> find = new Finder<>(Friends.class);
    public static List<Friends> findFriends(User friend) {return find.query().where().eq("friend", friend).findList();}

    public enum RequestStatus {
        ACCEPTED,
        DENIED,
        UNFRIENDEDBYREQUESTER,
        UNFRIENDEDBYRECEIVER,
        PENDING
    }

    public Friends( @Constraints.Required User friendRequester, @Constraints.Required User friendReceiver, RequestStatus requestStatus) {
        this.friendsId = friendRequester.getUserId() + ";" + friendReceiver.getUserId();
        this.friendRequester = friendRequester;
        this.friendReceiver = friendReceiver;
        this.requestStatus = requestStatus;
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
            case PENDING: case UNFRIENDEDBYRECEIVER:
                if (!user.equals(friendReceiver)) break;
                requestStatus = RequestStatus.ACCEPTED;
                acceptedDate = LocalDateTime.now();
                friendReceiver.addFriend();
                friendRequester.addFriend();
                this.save();
                break;
            case UNFRIENDEDBYREQUESTER:
                if (!user.equals(friendRequester)) break;
                requestStatus = RequestStatus.ACCEPTED;
                acceptedDate = LocalDateTime.now();
                friendReceiver.addFriend();
                friendRequester.addFriend();
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
                friendReceiver.removeFriend();
                this.acceptedDate = LocalDateTime.now();
                this.save();
        }
    }

}
