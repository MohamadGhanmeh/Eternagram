package models.relationships;

import io.ebean.Finder;
import io.ebean.Model;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Follows extends Model {

    @Id
    String followsId;
    @Constraints.Required
    @ManyToOne
    private User follower;
    @Constraints.Required
    @ManyToOne
    private User followee;
    private String FollowingNote;
    private LocalDateTime lastProfileView;
    public static Finder<String, Follows> find = new Finder<>(Follows.class);
    public static List<Follows> findFollowers(User followee) {return find.query().where().eq("followee", followee).findList();}
    public static List<Follows> findFollowees(User follower) {return find.query().where().eq("follower", follower).findList();}

    public Follows( @Constraints.Required User follower, @Constraints.Required User followee, String FollowingNote){
        this.follower = follower;
        this.followee = followee;
        this.followsId = follower.getUserId() + ";" + followee.getUserId();
        this.FollowingNote = FollowingNote;
        this.lastProfileView = LocalDateTime.now();
    }

    public User getFollower() {
        return follower;
    }
    public void setFollower(User follower) {
        this.follower = follower;
        this.followsId = follower.getUserId() + "," + followee.getUserId();
    }
    public User getFollowee() {
        return followee;
    }
    public void setFollowee(User followee) {
        this.followee = followee;
        this.followsId = follower.getUserId() + "," + followee.getUserId();
    }
    public String getFollowingNote() {
        return FollowingNote;
    }
    public void setFollowingNote(String FollowingNote) {
        this.FollowingNote = FollowingNote;
    }

    public LocalDateTime getLastProfileView() {
        return lastProfileView;
    }

    public void setLastProfileView(LocalDateTime lastProfileView) {
        this.lastProfileView = lastProfileView;
    }
}
