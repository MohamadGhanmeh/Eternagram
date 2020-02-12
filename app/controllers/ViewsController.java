package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Handle the communication between actors. It's the interface that holds all methods available to the public.
 * @author TBA
 * @version 1.0
 */
public class ViewsController extends Controller {
	public Result index() {
		return ok(views.html.index.render());
	}
}
