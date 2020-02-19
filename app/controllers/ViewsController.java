package controllers;

import models.User;
import models.UserProfile;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handle the communication between actors. It's the interface that holds all methods available to the public.
 * @author TBA
 * @version 1.0
 */
@Singleton
public class ViewsController extends Controller {
	private final FormFactory formFactory;
	@Inject
	public ViewsController(FormFactory formFactory) {this.formFactory = formFactory;}

	public Result index(Request request) {
		Long userId = parsers.StringParsers.parseLong(request.session().get("user").orElse("0"));
		User user = User.find.byId(userId);
		if (user == null) {
			DynamicForm form = formFactory.form();
			return ok(views.html.startPage.render(form, false, request));
		}
		return ok(views.html.index.render(request));
	}
	public Result userList(Request request) {
		User user = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));
		return ok(views.html.navUsers.render(user, request));
	}
	public Result userProfile(Request request, String userName, Long userId){
		User user = User.find.byId(userId);
		if(user==null || !user.getUserName().equals(userName)){
			return redirect(routes.ViewsController.index()).flashing("error","The user you're trying to access doesn't exist");
		}
		UserProfile userProfile = user.getUserProfile();
		if(userProfile == null) userProfile = new UserProfile(user);
		return ok(views.html.navProfile.render(user, userProfile, request));
	}
}
