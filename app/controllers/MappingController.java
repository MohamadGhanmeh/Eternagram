package controllers;

import models.Comment;
import models.Picture;
import models.User;
import models.relationships.Follows;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

@Singleton
public class MappingController {
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
	// PICTURE
	public static SortedMap<LocalDateTime, Picture> mapVisiblePictures(User user, User target) {
		SortedMap<LocalDateTime, Picture> answer = new TreeMap<>(Collections.reverseOrder());
		for (Picture picture : Picture.find.query().where().startsWith("pictureId", target.getUserId() + ";").findList()) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(picture.getUploadTime(), picture);
		}
		return answer;
	}
	// COMMENT
	public static SortedMap<LocalDateTime, Comment> mapPictureComments(Picture picture) {
		SortedMap<LocalDateTime, Comment> answer = new TreeMap<>(Collections.reverseOrder());
		for (Comment comment : PictureController.getComments(picture)) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(comment.getPostingTime(), comment);
		}
		return answer;
	}
}
