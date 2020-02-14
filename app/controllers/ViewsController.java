package controllers;

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
		DynamicForm form = formFactory.form();
		return ok(views.html.navProfile.render(request));
		//return ok(views.html.index.render(request));
		//return ok(views.html.startPage.render(form, request));
	}
}
