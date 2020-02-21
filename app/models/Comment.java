package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Comment extends Model {

    @Id
    private String commentId;

    private int commentNumber;

    @ManyToOne
    @Constraints.Required
    private User commentator;

    @Constraints.Required
    @ManyToOne
    private Picture commentedPicture;

    @Constraints.Required
    private String comment;

    @Constraints.Required
    private LocalDateTime postingTime;

    public static Finder<String, Comment> find = new Finder<>(Comment.class);

    public Comment(@Constraints.Required User commentator, @Constraints.Required Picture commentedPicture,String comment, int commentNumber, LocalDateTime postingTime){
        this.commentator = commentator;
        this.commentedPicture = commentedPicture;
        this.commentNumber = commentNumber;
        this.comment = comment;
        this.postingTime = postingTime;
        this.commentId = commentator.getUserId() + "," + commentedPicture.getPictureId() + "," + Integer.toString(commentNumber);
    }



    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public User getCommentator() {
        return commentator;
    }

    public void setCommentator(User commentator) {
        this.commentator = commentator;
    }

    public Picture getCommentedPicture() {
        return commentedPicture;
    }

    public void setCommentedPicture(Picture commentedPicture) {
        this.commentedPicture = commentedPicture;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(LocalDateTime postingTime) {
        this.postingTime = postingTime;
    }
}
