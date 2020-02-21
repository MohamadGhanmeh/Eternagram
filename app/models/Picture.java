package models;

import formatters.DateTimeFormats;
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
	private String pictureCaption;
	@Constraints.Required
	@ManyToOne
	private User pictureOwner;
	private String fileExtension;
	private int pictureComments;

	public Picture(@Constraints.Required LocalDateTime uploadTime, @Constraints.Required String pictureCaption, @Constraints.Required User pictureOwner, int pictureComments) {
		this.uploadTime = uploadTime;
		this.pictureCaption = pictureCaption;
		this.pictureOwner = pictureOwner;
		this.pictureComments = pictureComments;
		this.pictureId = pictureOwner.getUserId() + ";" + uploadTime.format(DateTimeFormats.ID);
	}
	public Picture(User pictureOwner){
		this.pictureOwner = pictureOwner;
		this.uploadTime = LocalDateTime.now();
		this.pictureComments = 0;
		this.pictureId = pictureOwner.getUserId() + ";" + uploadTime.format(DateTimeFormats.ID);
	}

	public static Finder<String, Picture> find = new Finder<>(Picture.class);

	public String getPictureId() {return pictureId;}
	public void setPictureId(String pictureId) {this.pictureId = pictureId;}
	public LocalDateTime getUploadTime() {return uploadTime;}
	public void setUploadTime(LocalDateTime uploadTime) {this.uploadTime = uploadTime;}
	public String getPictureCaption() {return pictureCaption;}
	public void setPictureCaption(String pictureCaption) {this.pictureCaption = pictureCaption;}
	public User getPictureOwner() {return pictureOwner;}
	public void setPictureOwner(User pictureOwner) {this.pictureOwner = pictureOwner;}
	public String getFileExtension() {return fileExtension;}
	public void setFileExtension(String fileExtension) {this.fileExtension = fileExtension;}
	public int getPictureComments() {return pictureComments;}
	public void setPictureComments(int pictureComments) {this.pictureComments = pictureComments;}
}
