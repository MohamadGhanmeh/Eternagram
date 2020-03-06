package controllers;

import io.ebean.Expr;
import models.Comment;
import models.Picture;
import models.User;
import models.UserProfile;
import models.relationships.Follows;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

@Singleton
public class MappingController {
	public static class Triplet<A,B,C> {
		public final A first;
		public final B second;
		public final C third;
		public Triplet(A first, B second, C third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

	}
	// relationships.FOLLOWS

	// USER
	public static SortedMap<String, User> mapUsers() {
		SortedMap<String, User> answer = new TreeMap<>();
		for (User user : User.find.all()) {answer.put(user.getUserName(), user);}
		return answer;
	}
	public static SortedMap<LocalDateTime, Follows> mapFollowedUsers(User user) {
		SortedMap<LocalDateTime, Follows> answer = new TreeMap<>(Collections.reverseOrder());
		for (Follows follows : Follows.findFollowees(user)) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(follows.getFollowee().getLastUploadDate(), follows);
		}
		return answer;
	}
	public static SortedMap<String, Follows> mapFollowingUsers(User user) {
		SortedMap<String, Follows> answer = new TreeMap<>(Collections.reverseOrder());
		for (Follows follows : Follows.findFollowers(user)) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(follows.getFollower().getUserName(), follows);
		}
		return answer;
	}
	public static SortedMap<String, Triplet<User, UserProfile, Boolean>> mapVisibleUsers(User user) {
		SortedMap<String, Triplet<User, UserProfile, Boolean>> answer = new TreeMap<>();
		for (User target : User.find.all()) {
			if (!user.equals(target)) {
				UserProfile profile = target.getUserProfile();
				if (profile == null) profile = new UserProfile(target);
				Boolean isFollowing = Follows.find.byId(user.getUserId() + ";" + target.getUserId()) != null;
				answer.put(target.getUserName(), new Triplet<>(target, profile, isFollowing));
			}
		}
		return answer;
	}
	// PICTURE
	public static SortedMap<LocalDateTime, Triplet<User, Follows, SortedMap<LocalDateTime, Picture>>> mapHomeFeed(User user){
		SortedMap<LocalDateTime, Triplet<User, Follows, SortedMap<LocalDateTime, Picture>>> answer = new TreeMap<>();
		for (Follows follows : Follows.findFollowees(user)) {
			User followedUser = follows.getFollowee();
			SortedMap<LocalDateTime, Picture> visiblePictures = new TreeMap<>();
			for (Picture picture : Picture.find.query().where().and(Expr.startsWith("pictureId", followedUser.getUserId() + ";"),Expr.ge("uploadTime",follows.getLastProfileView())).findList()){
				visiblePictures.put(picture.getUploadTime(), picture);
			}
			if (visiblePictures.size()>0){
				Triplet<User, Follows, SortedMap<LocalDateTime, Picture>> triplet = new Triplet<>(followedUser, follows, visiblePictures);
				answer.put(followedUser.getLastUploadDate(), triplet);
			}
		}
		return answer;
	}

	public static SortedMap<LocalDateTime, Picture> mapVisiblePictures(User user, User target) {
		SortedMap<LocalDateTime, Picture> answer = new TreeMap<>(Collections.reverseOrder());
		for (Picture picture : Picture.find.query().where().startsWith("pictureId", target.getUserId() + ";").findList()) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(picture.getUploadTime(), picture);
		}
		return answer;
	}
	public static SortedMap<LocalDateTime, Picture> mapNewVisiblePictures(User user, User target, LocalDateTime lastProfileView) {
		SortedMap<LocalDateTime, Picture> answer = new TreeMap<>(Collections.reverseOrder());
		for (Picture picture : Picture.find.query().where().startsWith("pictureId", target.getUserId() + ";").findList()) {
			//IF user has a filter on, or privacy system is active, do the check here
			if(picture.getUploadTime().isAfter(lastProfileView)) answer.put(picture.getUploadTime(), picture);
		}
		return answer;
	}
	// COMMENT
	public static SortedMap<LocalDateTime, Comment> mapPictureComments(Picture picture) {
		SortedMap<LocalDateTime, Comment> answer = new TreeMap<>();
		for (Comment comment : PictureController.getComments(picture)) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(comment.getPostingTime(), comment);
		}
		return answer;
	}


}
