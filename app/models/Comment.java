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
    @ManyToOne
    @Constraints.Required
    private User commentator;
    @Constraints.Required
    @ManyToOne
    private Picture commentedPicture;
    @Constraints.Required
    private String commentContent;
    @Constraints.Required
    private LocalDateTime postingTime;

    public Comment(String commentId, @Constraints.Required User commentator, @Constraints.Required Picture commentedPicture, @Constraints.Required String commentContent, @Constraints.Required LocalDateTime postingTime) {
        this.commentId = commentId;
        this.commentator = commentator;
        this.commentedPicture = commentedPicture;
        this.commentContent = commentContent;
        this.postingTime = postingTime;
    }
    public Comment(@Constraints.Required User commentator, @Constraints.Required Picture commentedPicture, @Constraints.Required String commentContent) {
        this.commentator = commentator;
        this.commentedPicture = commentedPicture;
        this.commentContent = commentContent;
        this.postingTime = LocalDateTime.now();
        this.commentId = commentator.getUserId() + ";" + commentedPicture.getPictureId() + ";" + this.postingTime;
    }

    public static Finder<String, Comment> find = new Finder<>(Comment.class);
    
    public String getCommentId() {return commentId;}
    public void setCommentId(String commentId) {this.commentId = commentId;}
    public void setCommentId() {this.commentId = commentator.getUserId() + ";" + commentedPicture.getPictureId() + ";" + (commentedPicture.getPictureComments());}
    public User getCommentator() {return commentator;}
    public void setCommentator(User commentator) {this.commentator = commentator;}
    public Picture getCommentedPicture() {return commentedPicture;}
    public void setCommentedPicture(Picture commentedPicture) {this.commentedPicture = commentedPicture;}
    public String getCommentContent() {return commentContent;}
    public void setCommentContent(String commentContent) {this.commentContent = commentContent;}
    public LocalDateTime getPostingTime() {return postingTime;}
    public void setPostingTime(LocalDateTime postingTime) {this.postingTime = postingTime;}
}
