package controllers;

import models.User;
import models.UserProfile;
import models.relationships.Follows;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;

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
		User user = User.findById(request.session().get("user").orElse("0"));
		DynamicForm form = formFactory.form();
		if (user == null) {return ok(views.html.startPage.render(form, false, request));}
		return ok(views.html.navHome.render(user, form, request));
	}
	public Result userList(Request request) {
		User user = User.findById(request.session().get("user").orElse("0"));
		DynamicForm form = formFactory.form();
		return ok(views.html.navUsers.render(user, form, request));
	}
	public Result userSelfProfile(Request request) {
		User user = User.findById(request.session().get("user").orElse("0"));
		if (user == null) return redirect(routes.ViewsController.index());
		return redirect(routes.ViewsController.userProfile(user.getUserName(), user.getUserId()));
	}
	public Result userProfile(Request request, String userName, Long userId){
		User user = User.findById(request.session().get("user").orElse("0"));
		User target = User.find.byId(userId);
		if(target==null || !target.getUserName().equals(userName)){
			return redirect(routes.ViewsController.index()).flashing("error","The user you're trying to access doesn't exist");
		}
		Follows follows = Follows.find.byId(user.getUserId() + ";" + target.getUserId());
		if (follows!=null) {
			follows.setLastProfileView(LocalDateTime.now());
			follows.update();
		}

		DynamicForm form = formFactory.form();
		UserProfile userProfile = target.getUserProfile();
		if(userProfile == null) userProfile = new UserProfile(target);
		return ok(views.html.navProfile.render(user, target, userProfile, form, request));
	}
	public Result socialPage(Request request) {
		User user = User.findById(request.session().get("user").orElse("0"));
		DynamicForm form = formFactory.form();
		return ok(views.html.navSocial.render(user, form, request));
	}
}
