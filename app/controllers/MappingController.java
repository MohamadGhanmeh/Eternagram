package controllers;

import models.Picture;
import models.User;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

@Singleton
public class MappingController {
	public static SortedMap<String, User> mapUser() {
		SortedMap<String, User> answer = new TreeMap<>();
		for (User user : User.find.all()) {answer.put(user.getUserName(), user);}
		return answer;
	}

	public static SortedMap<LocalDateTime, Picture> mapVisiblePictures(User user, User target) {
		SortedMap<LocalDateTime, Picture> answer = new TreeMap<>();
		for (Picture picture : Picture.find.query().where().startsWith("pictureId", target.getUserId() + ";").findList()) {
			//IF user has a filter on, or privacy system is active, do the check here
			answer.put(picture.getUploadTime(), picture);
		}
		return answer;
	}
}
