package models.relationships;

import io.ebean.Finder;
import io.ebean.Model;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Follows extends Model {

    @Id
    String followsId;
    @Constraints.Required
    @OneToMany
    private User follower;
    @Constraints.Required
    @OneToMany
    private User followee;
    private String note;
    public static Finder<String, Follows> find = new Finder<>(Follows.class);

    public Follows( @Constraints.Required User follower, @Constraints.Required User followee, String note){
        this.follower = follower;
        this.followee = followee;
        this.followsId = follower.getUserId() + "," + followee.getUserId();
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
