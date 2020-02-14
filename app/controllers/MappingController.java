package controllers;

import models.User;

import javax.inject.Singleton;
import java.util.SortedMap;
import java.util.TreeMap;

@Singleton
public class MappingController {
	public static SortedMap<String, User> mapUser() {
		SortedMap<String, User> answer = new TreeMap<>();
		for (User user : User.find.all()) {answer.put(user.getUserName(), user);}
		return answer;
	}
}
