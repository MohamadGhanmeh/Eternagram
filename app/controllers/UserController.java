package controllers;

import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handle actions related to users (register, follow, friend, etc).
 * @author TBA
 * @version 1.0
 */
@Singleton
public class UserController extends Controller {
    private final FormFactory formFactory;

    @Inject
    public UserController(FormFactory formFactory) {this.formFactory = formFactory;}

    public Result newUserRequest(Request request) {
        Form<User> userForm = formFactory.form(User.class);
        return ok();
    }

    public Result newUserAction(Request request) {
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        return ok();
    }
}
