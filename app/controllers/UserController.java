package controllers;

import models.Picture;
import models.User;
import models.UserProfile;
import models.relationships.Follows;
import models.relationships.Friends;
import play.mvc.Http.Request;

import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;

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
    public UserController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

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
        if (!toCheck.matches("\\S*[a-z]\\S*") || !toCheck.matches("\\S*[A-Z]\\S*") || !toCheck.matches("\\S*[0-9]\\S*"))
            return false;
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

        if (!isEmailValid(userEmail))
            return badRequest(views.html.startPage.render(userForm.withError("userEmail", "That email is invalid."), true, request));
        if (!isUserNameValid(userName))
            return badRequest(views.html.startPage.render(userForm.withError("userName", "That username is invalid."), true, request));
        if (!isUserPasswordValid(userPassword))
            return badRequest(views.html.startPage.render(userForm.withError("userPassword", "That password is invalid. It must have an uppercase and a lowercase character, a number and be between 6 and 18 characters long."), true, request));
        if (!isPhoneNumberValid(phoneNumber))
            return badRequest(views.html.startPage.render(userForm.withError("phoneNumber", "That phone number is invalid."), true, request));
        if (userDOB == null)
            return badRequest(views.html.startPage.render(userForm.withError("userDOB", "That date of birth is invalid."), true, request));
        User newUser = new User(0, userName, userPassword, userEmail, phoneNumber, userDOB, 0L, 0L, 0L, LocalDateTime.now(), 0L);
        newUser.save();
        newUser.refresh();
        return redirect(routes.ViewsController.index()).flashing("success", newUser.getUserName() + ", your Eternagram account was created successfully. You can now log in.");
    }

    public Result logIn(Request request) {
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        String userEmail = userForm.get("userEmail");
        String userPassword = userForm.get("userPassword");
        User toLogin = User.findByEmail(userEmail);
        if (toLogin == null || !toLogin.logIn(userPassword))
            return redirect(routes.ViewsController.index()).flashing("error", "The email and password combination entered are invalid");
        return redirect(routes.ViewsController.index()).withSession(SessionController.loginUser(toLogin.getUserId()));
    }

    public Result logOut(Request request) {
        SessionController.logoutUser(request.session().get("user").orElse("0"));
        return redirect(routes.ViewsController.index()).withNewSession();
    }

    public Result followUser(Request request, Long userId) {
        User userToFollow = User.find.byId(userId);
        User userThatFollows = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));
        DynamicForm form = formFactory.form().bindFromRequest(request);
        String note = form.get("followingNote");

        if (userThatFollows == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem.");
        }
        if (userToFollow == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem trying to follow that user");
        }
        if (Follows.find.byId(userThatFollows.getUserId() + ";" + userToFollow.getUserId()) != null) {
            return redirect(routes.ViewsController.index()).flashing("error", "you are already following that user");
        }

        Follows toCreate = new Follows(userThatFollows, userToFollow, note);
        toCreate.save();
        userToFollow.addFollower();
        userToFollow.update();
        userThatFollows.addFollowing();
        userThatFollows.update();
        return redirect(routes.ViewsController.userProfile(userToFollow.getUserName(), userToFollow.getUserId())).flashing("success", "you are now following user " + userToFollow.getUserName());

    }

    public Result unfollowUser(Request request, Long userId) {
        User userToFollow = User.find.byId(userId);
        User userThatFollows = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));

        if (userThatFollows == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem.");
        }
        if (userToFollow == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem trying to stop following that user");
        }
        String followingId = userThatFollows.getUserId() + ";" + userToFollow.getUserId();
        Follows toDelete = Follows.find.byId(followingId);
        if (toDelete == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "you are not following that user");
        }

        toDelete.delete();
        userToFollow.removeFollower();
        userToFollow.update();
        userThatFollows.removeFollowing();
        userThatFollows.update();
        return redirect(routes.ViewsController.userProfile(userToFollow.getUserName(), userToFollow.getUserId())).flashing("success", "you are not following user " + userToFollow.getUserName() + " anymore");

    }

    public Result friendRequestAction(Request request, Long userId) {
        User userToAdd = User.find.byId(userId);
        User userThatRequests = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));
        DynamicForm form = formFactory.form().bindFromRequest(request);

        if (userToAdd == null || userToAdd.equals(userThatRequests)) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem.");
        }
        if (userThatRequests == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem trying to friend that user");
        }
        Friends friendRequest = Friends.findRequest(userThatRequests, userToAdd);
        if (friendRequest != null) {
            switch(friendRequest.getRequestStatus()){
                case ACCEPTED:
                    return redirect(routes.ViewsController.userProfile(userToAdd.getUserName(), userToAdd.getUserId())).flashing("error", "you are already friends with " + userToAdd.getUserName());
                case DENIED: case UNFRIENDEDBYRECEIVER:
                    if(friendRequest.getFriendRequester().equals(userThatRequests)) return redirect(routes.ViewsController.userProfile(userToAdd.getUserName(), userToAdd.getUserId())).flashing("error",  userToAdd.getUserName() + " has denied your request");
                case PENDING:
                    if(friendRequest.getFriendRequester().equals(userThatRequests)) return redirect(routes.ViewsController.userProfile(userToAdd.getUserName(), userToAdd.getUserId())).flashing("error", "your request to add " + userToAdd.getUserName() + "is pending");
            }
            friendRequest.acceptRequest(userThatRequests);
            return redirect(routes.ViewsController.userProfile(userToAdd.getUserName(), userToAdd.getUserId())).flashing("success", "you are now friends with " + userToAdd.getUserName());
        }

        Friends toAdd = new Friends(userThatRequests, userToAdd);
        toAdd.save();
        return redirect(routes.ViewsController.userProfile(userToAdd.getUserName(), userToAdd.getUserId())).flashing("success", "your friend request was sent to " + userToAdd.getUserName());
    }
    public Result unfriendRequestAction(Request request, Long userId) {
        User userToRefuse = User.find.byId(userId);
        User userThatRequests = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));
        DynamicForm form = formFactory.form().bindFromRequest(request);

        if (userToRefuse == null || userToRefuse.equals(userThatRequests)) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem.");
        }
        if (userThatRequests == null) {
            return redirect(routes.ViewsController.index()).flashing("error", "There was a problem trying to friend that user");
        }
        Friends friendRequest = Friends.findRequest(userThatRequests, userToRefuse);
        if (friendRequest != null) {
            switch(friendRequest.getRequestStatus()){
                case ACCEPTED: break;
                case PENDING:
                    if (friendRequest.getFriendRequester().equals(userThatRequests)){
                        friendRequest.delete();
                        return redirect(routes.ViewsController.userProfile(userToRefuse.getUserName(), userToRefuse.getUserId())).flashing("success", "you cancelled your friend request to " + userToRefuse.getUserName());
                    }
                    break;
                default:
                    return redirect(routes.ViewsController.userProfile(userToRefuse.getUserName(), userToRefuse.getUserId())).flashing("error", "you are not friends with " + userToRefuse.getUserName());
            }
            friendRequest.refuseRequest(userThatRequests);
            return redirect(routes.ViewsController.userProfile(userToRefuse.getUserName(), userToRefuse.getUserId())).flashing("success", "you are now friends with " + userToRefuse.getUserName());
        }
        return redirect(routes.ViewsController.userProfile(userToRefuse.getUserName(), userToRefuse.getUserId())).flashing("error", "you are not friends with " + userToRefuse.getUserName());
    }

    public Result setProfilePicture(Request request, String pictureId) {
        User user = User.findById(request.session().get("user").orElse("0"));
        Picture picture = Picture.find.byId(pictureId);
        if (!pictureId.equals("0") && picture == null) return redirect(routes.ViewsController.userSelfProfile()).flashing("error", "There was an error setting that picture as Profile Picture.");

        UserProfile userProfile = user.getUserProfile();
        if (userProfile==null) {
            userProfile = new UserProfile(user);
            userProfile.setUserProfilePicture(picture);
            userProfile.save();
        } else {
            userProfile.setUserProfilePicture(picture);
            userProfile.save();
        }
        return redirect(routes.ViewsController.userSelfProfile()).flashing("success", "The new profile picture has been set");
    }
    public Result editProfilePage(Request request) {
        User user = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));
        UserProfile profile = user.getUserProfile();
        if(profile == null) profile = new UserProfile(user);
        Form<UserProfile> form = formFactory.form(UserProfile.class).fill(profile);
        DynamicForm dynamicForm = formFactory.form();
        return ok(views.html.editProfile.render(user, form, dynamicForm, request));
    }
    public Result editProfileAction(Request request) {
        User user = User.find.byId(Long.parseLong(request.session().get("user").orElse("0")));
        UserProfile profile = user.getUserProfile();
        if(profile == null) {
            profile = new UserProfile(user);
            profile.save();
        }
        Form<UserProfile> form = formFactory.form(UserProfile.class).bindFromRequest(request);
        DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
        if (dynamicForm.hasErrors()) {
            form = formFactory.form(UserProfile.class).fill(form.get());
            return badRequest(views.html.editProfile.render(user, form, dynamicForm, request));
        }
        if (!user.getUserPassword().equals(dynamicForm.get("oldPassword"))) {
            form = formFactory.form(UserProfile.class).fill(form.get());
            return badRequest(views.html.editProfile.render(user, form.withError("oldPassword", "The password entered is invalid"), dynamicForm, request));
        }
        String newPassword = dynamicForm.get("newPassword1");
        if (!newPassword.equals("")) {
            if (newPassword.equals(dynamicForm.get("newPassword2"))) {
                if(isUserPasswordValid(newPassword)){
                    user.setUserPassword(newPassword);
                    user.update();
                } else {
                    form = formFactory.form(UserProfile.class).fill(form.get());
                    return badRequest(views.html.editProfile.render(user, form.withError("newPassword1", "That password is invalid. It must have an uppercase and a lowercase character, a number and be between 6 and 18 characters long."), dynamicForm, request));
                }
            } else {
                form = formFactory.form(UserProfile.class).fill(form.get());
                return badRequest(views.html.editProfile.render(user, form.withError("newPassword2", "The passwords don't match"), dynamicForm, request));
            }
        }
        profile.updateProfile(form.get());
        return redirect(routes.ViewsController.userSelfProfile());
    }
}
