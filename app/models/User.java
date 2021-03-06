package models;

import controllers.MappingController;
import controllers.ReverseAssets;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import router.Routes;

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
	private Long pictureAmount;
	private Long followerAmount;
	private Long followingAmount;
	private Long friendsAmount;
	private LocalDateTime lastUploadDate;

	public User(long userId, @Constraints.Required String userName, @Constraints.Required String userPassword, @Constraints.Required String userEmail, String phoneNumber, @Constraints.Required LocalDateTime userDOB, Long pictureAmount, Long followerAmount,Long friendsAmount, LocalDateTime lastUploadDate, Long followingAmount) {
		this.userId = userId;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
		this.phoneNumber = phoneNumber;
		this.userDOB = userDOB;
		this.pictureAmount = pictureAmount;
		this.followerAmount = followerAmount;
		this.friendsAmount = friendsAmount;
		this.lastUploadDate = lastUploadDate;
		this.followingAmount = followingAmount;
	}

	public static Finder<Long, User> find = new Finder<>(User.class);
	public static User findByUserName(String userName) {return find.query().where().eq("userName", userName).findOne();}
	public static User findByEmail(String email) {return find.query().where().eq("userEmail", email).findOne();}
	public static User findById(String userId) {return find.byId(parsers.StringParsers.parseLong(userId));}

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
	public Long getPictureAmount() {return pictureAmount;}
	public void setPictureAmount(Long pictureAmount) {this.pictureAmount = pictureAmount;}
	public Long getFollowerAmount() {return followerAmount;}
	public void setFollowerAmount(Long followerAmount) {this.followerAmount = followerAmount;}
	public LocalDateTime getLastUploadDate() {return lastUploadDate;}
	public void setLastUploadDate(LocalDateTime lastUploadDate) {this.lastUploadDate = lastUploadDate;}
	public Long getFollowingAmount() {return followingAmount; }
	public void setFollowingAmount(Long followingAmount) { this.followingAmount = followingAmount; }

	public UserProfile getUserProfile() {
		UserProfile profile = UserProfile.find.byId(userId);
		if (profile==null) profile = new UserProfile(this);
		return profile;
	}
	public boolean logIn(String password) {return password.equals(this.userPassword);}
	public Picture getProfilePicture(){
		UserProfile userProfile = getUserProfile();
		if ((userProfile == null) || (userProfile.getUserProfilePicture() == null)) return new Picture(this);
		return userProfile.getUserProfilePicture();
	}
	public void addFollower(){this.followerAmount++;}
	public void addFollowing(){this.followingAmount++;}
	public void addFriend(){this.friendsAmount++;}
	public void removeFollower(){this.followerAmount--;}
	public void removeFollowing(){this.followingAmount--;}
	public void removeFriend(){this.friendsAmount--;}
	public void addUploadedPicture(Picture picture) {
		if (picture.getPictureOwner().equals(this)) {
			pictureAmount += 1;
			if (picture.getUploadTime().isAfter(lastUploadDate)) {lastUploadDate = picture.getUploadTime();}
			this.update();
		}
	}
	public void removeUploadedPicture(Picture picture) {
		if (picture.getPictureOwner().equals(this)) {
			pictureAmount-=1;
			if (picture.getUploadTime().equals(lastUploadDate)) {
				lastUploadDate = MappingController.mapVisiblePictures(this,this).firstKey();
			}
			this.update();
		}
	}
}
