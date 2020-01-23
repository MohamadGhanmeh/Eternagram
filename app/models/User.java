package models;

import io.ebean.Model;

import javax.persistence.Id;

public class User extends Model {
	@Id
	private String userId;
}
