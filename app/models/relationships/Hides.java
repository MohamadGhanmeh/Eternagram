package models.relationships;

import io.ebean.Finder;
import io.ebean.Model;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Hides extends Model {
	@Id
	String hidesId;
	@Constraints.Required
	@ManyToOne
	private User hider;
	@Constraints.Required
	@ManyToOne
	private User hid;
	public static Finder<String, Hides> find = new Finder<>(Hides.class);
	public static List<Hides> findByHider(User hider) {return find.query().where().eq("hider", hider).findList();}
	public static List<Hides> findByHid(User hid) {return find.query().where().eq("hid", hid).findList();}

	public Hides(@Constraints.Required User hider, @Constraints.Required User hid){
		this.hider = hider;
		this.hid = hid;
		this.hidesId = hider.getUserId() + ";" + hid.getUserId();
	}

	public String getHidesId() {return hidesId;}
	public void setHidesId(String hidesId) {this.hidesId = hidesId;}
	public User getHider() {return hider;}
	public void setHider(User hider) {this.hider = hider;}
	public User getHid() {return hid;}
	public void setHid(User hid) {this.hid = hid;}
}
