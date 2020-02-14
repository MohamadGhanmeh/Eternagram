package models;

import io.ebean.Finder;
import io.ebean.Model;
import io.ebeaninternal.server.lib.util.Str;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;

@Entity
public class Picture extends Model {
	@Id
	private String pictureId;
	@Constraints.Required
	private String location;
	@Constraints.Required
	@ManyToOne
	private User pictureOwner;

	public Picture(String pictureId, @Constraints.Required String location, @Constraints.Required User pictureOwner){
		this.pictureId = pictureId;
		this.location = location;
		this.pictureOwner = pictureOwner;
	}

	public static Finder<String, Picture> find = new Finder<>(Picture.class);
	public static Picture findByPictureOwner(String pictureId) {return find.query().where().eq("pictureOwner", pictureOwner).findOne();}

	public String getPictureId() {return pictureId;}
	public void setPictureId(String pictureId) {this.pictureId = pictureId;}
	public String getLocation() {return location;}
	public void setLocation(String location) {this.location = location;}
	public User getPictureOwner() {return pictureOwner;}
	public void setPictureOwner(User pictureOwner) {this.pictureOwner = pictureOwner;}
}
