package controllers;

import formatters.Scalr;
import models.Comment;
import models.Picture;
import models.User;
import play.Environment;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Handle actions related to pictures (posting, commenting, deleting, etc).
 * @author TBA
 * @version 1.0
 */
@Singleton
public class PictureController extends Controller {
	private final Environment environment;
	private FormFactory formFactory;
	private final String fileDirectory;
	@Inject
	public PictureController(FormFactory formFactory, Environment environment) {
		this.environment = environment;
		this.formFactory = formFactory;
		this.fileDirectory = initialize();
	}

	public static List<Comment> getComments(Picture picture) {return Comment.find.query().where().eq("commentedPicture", picture).findList();}

	private String initialize() {
		String directoryRoot = environment.rootPath().toString().concat("/data");
		String directoryFiles = directoryRoot.concat("/uploadedFiles");
		File directory = new File(directoryRoot);
		if (!directory.exists()) {directory.mkdir();}
		directory = new File(directoryFiles);
		if (!directory.exists()) {directory.mkdir();}
		return directory.getAbsolutePath();
	}
	private boolean isCaptionValid(String toCheck) {
		if ((toCheck == null) || toCheck.trim().equals("")) return false;
		return (toCheck.length() <= 50);
	}
	private boolean isCommentValid(String toCheck) {
		if ((toCheck == null) || toCheck.trim().equals("")) return false;
		int length = toCheck.trim().length();
		return (length >= 4 && length <= 250);
	}
	private String getPictureLocation(Picture picture){ return fileDirectory + "/" + picture.getPictureOwner().getUserId();}

	public Result newPicturePage(Request request){
		DynamicForm pictureForm = formFactory.form();
		return ok(views.html.navUpload.render(pictureForm, request));
	}
	public Result newPictureAction(Request request) {
		DynamicForm pictureForm = formFactory.form().bindFromRequest(request);
		String pictureCaption = pictureForm.get("pictureCaption").trim();
		User pictureOwner = User.findById(request.session().get("user").orElse("0"));

		if (!isCaptionValid(pictureCaption)) return badRequest();
		if (pictureOwner == null) return badRequest();
		Picture newPicture = new Picture(pictureOwner);
		newPicture.setPictureCaption(pictureCaption);

		Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
		Http.MultipartFormData.FilePart<Files.TemporaryFile> toUpload = body.getFile("file");
		if (toUpload != null) {
			String fileName = toUpload.getFilename();
			newPicture.setFileExtension(fileName.substring(fileName.lastIndexOf('.')));
			Files.TemporaryFile file = toUpload.getRef();
			// Verify that the User's directory exists
			String fileAddress = fileDirectory + "/" + pictureOwner.getUserId();
			File directory = new File(fileAddress);
			if (!directory.exists()) {directory.mkdir();}
			// Open the file to upload into
			newPicture.save();
			newPicture.refresh();
			// Update tje user's picture count and last post time
			pictureOwner.addUploadedPicture(newPicture);
			fileAddress = getPictureLocation(newPicture) + "/" + newPicture.getPictureId() + newPicture.getFileExtension();
			directory = new File(fileAddress);
			file.copyTo(directory, true);
			newPictureThumbnail(newPicture);
			return redirect(routes.ViewsController.userProfile(pictureOwner.getUserName(), pictureOwner.getUserId())).flashing("success", "You have uploaded a new picture, titled \"" + pictureCaption + "\"");
		} else {
			return badRequest().flashing("error", "Missing file");
		}
	}
	public void newPictureThumbnail(Picture uploadedPicture) {
		String fileExtension = uploadedPicture.getFileExtension();
		String pictureId = uploadedPicture.getPictureId();
		String fileAddress = getPictureLocation(uploadedPicture);
		String thumbnailAddress = fileAddress + "/" + pictureId + "_thumbnail" + fileExtension;
		try {
			BufferedImage picture = ImageIO.read(new File(fileAddress + "/" + pictureId + fileExtension));
			BufferedImage scaledImg = Scalr.resize(picture, Scalr.Method.ULTRA_QUALITY, 640, Scalr.OP_ANTIALIAS);
			File thumbnail = new File(thumbnailAddress);
			ImageIO.write(scaledImg, fileExtension.substring(1), thumbnail);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Result viewPicturePage(Request request, String pictureId){
		DynamicForm commentForm = formFactory.form();
		Picture picture = Picture.find.byId(pictureId);
		if (picture==null) return ok(views.html.layouts.pictureModal_failure.render(commentForm, request));

		return ok(views.html.layouts.pictureModal.render(picture, commentForm, request));
	}
	public Result getPicture(Request request, String pictureId, boolean isFullSize) {
		Picture toLoad = Picture.find.byId(pictureId);
		if (toLoad == null) return ok(new File(fileDirectory + "/Default.jpg"), Optional.of("Default.jpg"));
		String pictureAddress = getPictureLocation(toLoad);
		String pictureExtension = (isFullSize)? toLoad.getFileExtension() : "_thumbnail" + toLoad.getFileExtension();
		String pictureName = toLoad.getPictureId() + toLoad.getFileExtension();
		//if (isFullSize) return ok(new File(pictureAddress + "/" + pictureName), Optional.of(pictureName));
		return ok(new File(pictureAddress + "/" + pictureId + pictureExtension), Optional.of(pictureName));
	}

	public Result newCommentAction(Request request, String pictureId){
		DynamicForm commentForm = formFactory.form().bindFromRequest(request);
		String commentContent = commentForm.get("commentContent");
		User commentator = User.findById(request.session().get("user").orElse("0"));
		Picture picture = Picture.find.byId(pictureId);

		if (picture==null || commentator==null) return redirect(routes.ViewsController.index()).flashing("error", "There was an error when posting the comment.");
		if (!isCommentValid(commentContent)) return badRequest(); // TODO we have to complete the picture/comment page first

		Comment newComment = new Comment(commentator, picture, commentContent.trim());
		newComment.save();
		picture.addCommentToPicture(newComment);
		return redirect(""); // TODO we have to complete the picture/comment page first
	}
	public Result deleteCommentAction(Request request, String commentId){
		Comment toDelete = Comment.find.byId(commentId);

		if (toDelete==null) return redirect(""); // TODO we have to complete the picture/comment page first
		Picture commentedPicture = toDelete.getCommentedPicture();
		toDelete.delete();
		commentedPicture.removeCommentFromPicture(toDelete);
		return redirect(""); // TODO we have to complete the picture/comment page first
	}
}
