package models.relationships;

import io.ebean.Finder;
import io.ebean.Model;
import models.Picture;
import models.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class Likes extends Model {
	@Id
	private String likesId;
	@OneToOne
	private User liker;
	@OneToOne
	private Picture liked;

	public static Finder<String, Likes> find = new Finder<>(Likes.class);
	public static List<Likes> findByPicture(Picture picture) {return find.query().where().eq("liked", picture).findList();}
	public static List<Likes> findByUser(User user) {return find.query().where().eq("liker", user).findList();}

	public Likes(User liker, Picture liked) {
		this.likesId = liker.getUserId() + ";" + liked.getPictureId();
		this.liker = liker;
		this.liked = liked;
	}

	public String getLikesId() {return likesId;}
	public void setLikesId(String likesId) {this.likesId = likesId;}
	public User getLiker() {return liker;}
	public void setLiker(User liker) {this.liker = liker;}
	public Picture getLiked() {return liked;}
	public void setLiked(Picture liked) {this.liked = liked;}
}
