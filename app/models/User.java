package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
	private String firstName;
	@Constraints.Required
	private String lastName;
	@Constraints.Required
	@Column(unique = true)
	private String userEmail;
	@Constraints.Required
	@Column(unique = true)
	private String phoneNumber;

	public User(long userId, @Constraints.Required String userName, @Constraints.Required String password, @Constraints.Required String firstName, @Constraints.Required String lastName, @Constraints.Required String email, @Constraints.Required String phoneNumber) {
		this.userId = userId;
		this.userName = userName;
		this.userPassword = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userEmail = email;
		this.phoneNumber = phoneNumber;
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
	public String getFirstName() {return firstName;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public String getLastName() {return lastName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public String getUserEmail() {return userEmail;}
	public void setUserEmail(String userEmail) {this.userEmail = userEmail;}
	public String getPhoneNumber() {return phoneNumber;}
	public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

	public boolean logIn(String password) {return password.equals(this.userPassword);}
}
