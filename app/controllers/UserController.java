package controllers;

import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

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

    private boolean isEmailValid(String toCheck) {
        if (toCheck == null) return false;
        toCheck = toCheck.toLowerCase();
        if (!toCheck.matches("^[a-z0-9@._%+-]{6,254}$")) return false;
        if (!toCheck.matches("^[a-z0-9._%+-]+@(?:[a-z0-9-]+\\.)+[a-z]{2,63}$")) return false;
        return (User.findByEmail(toCheck) == null);
    }
    private boolean isUserNameValid(String toCheck) {
        if (toCheck == null) return false;
        if (!toCheck.matches("^([a-zA-Z0-9_])+$")) return false;
        if (User.findByUserName(toCheck) != null) return false;
        int size = toCheck.length();
        return (size <= 18 && size >= 6);
    }
    private boolean isUserPasswordValid(String toCheck) {
        if (toCheck == null) return false;
        if (!toCheck.matches("^([a-zA-Z0-9])+$")) return false;
        if (!toCheck.matches("\\S*[a-z]\\S*") || !toCheck.matches("\\S*[A-Z]\\S*") || !toCheck.matches("\\S*[0-9]\\S*")) return false;
        int size = toCheck.length();
        return (size <= 18 && size >= 6);
    }
    private boolean isPhoneNumberValid(String toCheck) {
        if (toCheck == null || toCheck.trim().equals("")) return true;
        toCheck = toCheck.replaceAll("\\s", "");
        if (!toCheck.matches("^[0-9]{10,}$")) return false;
        return (User.find.query().where().eq("phoneNumber", toCheck) != null);
    }

    public Result newUserAction(Request request) {
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        String userEmail = userForm.get("userEmail");
        String userName = userForm.get("userName");
        String userPassword = userForm.get("userPassword");
        String phoneNumber = userForm.get("phoneNumber");
        LocalDateTime userDOB = parsers.StringParsers.parseDate(userForm.get("userDOB"));

        if (!isEmailValid(userEmail)) return badRequest(views.html.startPage.render(userForm.withError("userEmail", "That email is invalid."), true, request));
        if (!isUserNameValid(userName)) return badRequest(views.html.startPage.render(userForm.withError("userName", "That username is invalid."), true, request));
        if (!isUserPasswordValid(userPassword)) return badRequest(views.html.startPage.render(userForm.withError("userPassword", "That password is invalid."), true, request));
        if (!isPhoneNumberValid(phoneNumber)) return badRequest(views.html.startPage.render(userForm.withError("phoneNumber", "That phone number is invalid."), true, request));
        if (userDOB == null) return badRequest(views.html.startPage.render(userForm.withError("userDOB", "That date of birth is invalid."), true, request));
        User newUser = new User(0,userName,userPassword,userEmail,phoneNumber,userDOB,0L,0L);
        newUser.save();
        newUser.refresh();
        return redirect(routes.ViewsController.index()).flashing("success", newUser.getUserName() + ", your Eternagram account was created successfully. You can now log in.");
    }
    public Result logIn(Request request) {
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        String userEmail = userForm.get("userEmail");
        String userPassword = userForm.get("userPassword");
        User toLogin = User.findByEmail(userEmail);
        if (toLogin == null || !toLogin.logIn(userPassword)) return redirect(routes.ViewsController.index()).flashing("error", "The email and password combination entered are invalid");
        return redirect(routes.ViewsController.index()).withSession(SessionController.loginUser(toLogin.getUserId()));
    }
    public Result logOut(Request request) {
        SessionController.logoutUser(request.session().get("user").orElse("0"));
        return redirect(routes.ViewsController.index()).withNewSession();
    }

}
