package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Picture extends Model {
	@Id
	private String pictureId;
	@Constraints.Required
	private LocalDateTime uploadTime;
	@Constraints.Required
	private String pictureLocation;
	@Constraints.Required
	private String pictureCaption;
	@Constraints.Required
	@ManyToOne
	private User pictureOwner;
	private String fileExtension;

	public Picture(String pictureId, @Constraints.Required LocalDateTime uploadTime, @Constraints.Required String pictureLocation, @Constraints.Required String pictureCaption, @Constraints.Required User pictureOwner) {
		this.pictureId = pictureId;
		this.uploadTime = uploadTime;
		this.pictureLocation = pictureLocation;
		this.pictureCaption = pictureCaption;
		this.pictureOwner = pictureOwner;
	}
	public Picture(){this.uploadTime = LocalDateTime.now();}

	public static Finder<String, Picture> find = new Finder<>(Picture.class);

	public String getPictureId() {return pictureId;}
	public void setPictureId(String pictureId) {this.pictureId = pictureId;}
	public LocalDateTime getUploadTime() {return uploadTime;}
	public void setUploadTime(LocalDateTime uploadTime) {this.uploadTime = uploadTime;}
	public String getPictureLocation() {return pictureLocation;}
	public void setPictureLocation(String pictureLocation) {this.pictureLocation = pictureLocation;}
	public String getPictureCaption() {return pictureCaption;}
	public void setPictureCaption(String pictureCaption) {this.pictureCaption = pictureCaption;}
	public User getPictureOwner() {return pictureOwner;}
	public void setPictureOwner(User pictureOwner) {this.pictureOwner = pictureOwner;}
	public String getFileExtension() {return fileExtension;}
	public void setFileExtension(String fileExtension) {this.fileExtension = fileExtension;}
}
