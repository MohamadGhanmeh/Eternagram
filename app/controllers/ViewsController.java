package controllers;

import models.User;
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
		User user = User.findById(request.session().get("user").orElse("0"));
		if (user == null) {
			DynamicForm form = formFactory.form();
			return ok(views.html.startPage.render(form, false, request));
		}
		return ok(views.html.navHome.render(request, user));
	}
	public Result userList(Request request) {
		User user = User.findById(request.session().get("user").orElse("0"));
		return ok(views.html.navUsers.render(user, request));
	}
	public Result userProfile(Request request, String userName, Long userId){
		User user = User.findById(request.session().get("user").orElse("0"));
		return ok(views.html.navProfile.render(user, request));
	}
}
