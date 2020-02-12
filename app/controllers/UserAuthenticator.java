package controllers;

import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Handle the user authentication and validation.
 * @author Rodrigo Zanini
 * @version 1.0
 */
public class UserAuthenticator extends Security.Authenticator {
	private final MessagesApi messagesApi;

	@Inject public UserAuthenticator(MessagesApi messagesApi) {this.messagesApi = messagesApi;}

	@Override
	public Optional<String> getUsername(Http.Request req) {return super.getUsername(req);}

	@Override
	public Result onUnauthorized(Http.Request req) {return super.onUnauthorized(req);}
}