package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class User extends Model {
	@Id
	private long userId;
	@Constraints.Required
	@Column(unique = true)
	private String userName;
	@Constraints.Required
	private String userPassword;
	@Constraints.Required
	@Column(unique = true)
	private String userEmail;
	private String phoneNumber;
	@Constraints.Required
	private LocalDateTime userDOB;


	public User(long userId, @Constraints.Required String userName, @Constraints.Required String userPassword, @Constraints.Required String userEmail, String phoneNumber, @Constraints.Required LocalDateTime userDOB) {
		this.userId = userId;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
		this.phoneNumber = phoneNumber;
		this.userDOB = userDOB;
	}

	public static Finder<Long, User> find = new Finder<>(User.class);
	public static User findByUserName(String userName) {return find.query().where().eq("userName", userName).findOne();}
	public static User findByEmail(String email) {return find.query().where().eq("userEmail", email).findOne();}

	public long getUserId() {return userId;}
	public void setUserId(long userId) {this.userId = userId;}
	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}
	public String getUserPassword() {return userPassword;}
	public void setUserPassword(String userPassword) {this.userPassword = userPassword;}
	public String getUserEmail() {return userEmail;}
	public void setUserEmail(String userEmail) {this.userEmail = userEmail;}
	public String getPhoneNumber() {return phoneNumber;}
	public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
	public LocalDateTime getUserDOB() {return userDOB;}
	public void setUserDOB(LocalDateTime userDOB) {this.userDOB = userDOB;}
	public UserProfile getUserProfile() {return UserProfile.find.byId(userId);}

	public boolean logIn(String password) {return password.equals(this.userPassword);}
}
