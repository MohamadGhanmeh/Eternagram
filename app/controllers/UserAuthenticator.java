package controllers;

import models.User;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Optional;


public class UserAuthenticator extends Security.Authenticator {
	@Override
	public Optional<String> getUsername(Http.Request request) {
		Optional<String> userId = request.session().get("user");
		String sessionID = request.session().get("sessionID").orElse("");
		User user = User.find.byId(parsers.StringParsers.parseLong(userId.orElse("0")));
		if (user == null) return Optional.empty();      // user with specified userName does not exist or is inactive
		int answer = SessionController.verifySession(userId.orElse("0"), sessionID);
		if (answer == 1) return userId;                                   // session is valid
		return Optional.empty();                                            // session is invalid
	}

	@Override
	public Result onUnauthorized(Http.Request request) {
		String userId = request.session().get("userId").orElse("0");
		String sessionID = request.session().get("sessionID").orElse("");
		int answer = SessionController.verifySession(userId, sessionID);
		switch (answer){
			case 0:		return redirect(routes.ViewsController.index()).flashing("error", "You session is invalid.");
			case -1:	return redirect(routes.ViewsController.index()).flashing("error", "You logged in from a different browser.");
			case -2:	return redirect(routes.ViewsController.index()).flashing("warning", "Your session has expired.");
			default:	return redirect(routes.ViewsController.index());
		}
	}
}