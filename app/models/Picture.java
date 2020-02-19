package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
public class Picture extends Model {
	@Id
	private String pictureId;
	@Constraints.Required
	private String location;
	@Constraints.Required
	@ManyToOne
	private User pictureOwner;

	
}
