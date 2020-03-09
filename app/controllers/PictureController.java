package controllers;

import formatters.Scalr;
import models.Comment;
import models.Picture;
import models.Tag;
import models.User;
import models.relationships.Likes;
import models.relationships.Tags;
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
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
	public Result initializeTags(){
		Tag toCreate = Tag.findByTagContent("Selfie");
		if (toCreate == null){
			toCreate = new Tag(1,"Selfie");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("Portrait");
		if (toCreate == null){
			toCreate = new Tag(2,"Portait");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("NSFW");
		if (toCreate == null){
			toCreate = new Tag(3,"NSFW");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("WCW");
		if (toCreate == null){
			toCreate = new Tag(4,"WCW");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("OTD");
		if (toCreate == null){
			toCreate = new Tag(5,"OTD");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("Love");
		if (toCreate == null){
			toCreate = new Tag(6,"Love");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("Kawaii");
		if (toCreate == null){
			toCreate = new Tag(9,"Kawaii");
			toCreate.save();
		}
		toCreate = Tag.findByTagContent("Food");
		if (toCreate == null){
			toCreate = new Tag(10,"Food");
			toCreate.save();
		}
		return redirect(routes.ViewsController.index());
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
		String thumbnailAddress = fileAddress + "/" + pictureId + "_thumbnail.jpg";
		File picture = new File(fileAddress + "/" + pictureId + fileExtension);
		File thumbnail = new File(thumbnailAddress);
		try {
			BufferedImage toConvert = ImageIO.read(picture);
			BufferedImage converted = new BufferedImage(toConvert.getWidth(), toConvert.getHeight(), BufferedImage.TYPE_INT_RGB);
			converted.createGraphics().drawImage(toConvert, 0, 0, Color.white, null);
			BufferedImage resized = Scalr.resize(converted, Scalr.Method.ULTRA_QUALITY, 640, Scalr.OP_ANTIALIAS);
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			thumbnail.createNewFile();
			writer.setOutput(ImageIO.createImageOutputStream(new FileOutputStream(thumbnail)));
			ImageWriteParam writeParam = writer.getDefaultWriteParam();
			writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writeParam.setCompressionQuality(0.3f);
			writer.write(null, new IIOImage(resized, null, null), writeParam);
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Result viewPicturePage(Request request, String pictureId){
		User user = User.findById(request.session().get("user").orElse("0"));
		DynamicForm commentForm = formFactory.form();
		Picture picture = Picture.find.byId(pictureId);
		if (picture==null) return ok(views.html.layouts.pictureModal_failure.render(commentForm, request));

		return ok(views.html.layouts.pictureModal.render(picture, user, commentForm, request));
	}
	public Result getPicture(Request request, String pictureId, boolean isFullSize) {
		Picture toLoad = Picture.find.byId(pictureId);
		if (toLoad == null) return ok(new File(fileDirectory + "/Default.jpg"), Optional.of("Default.jpg"));
		String pictureAddress = getPictureLocation(toLoad);
		String pictureExtension = (isFullSize)? toLoad.getFileExtension() : "_thumbnail.jpg";
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
		return ok(); // TODO we have to complete the picture/comment page first
	}
	public Result deleteCommentAction(Request request, String commentId){
		Comment toDelete = Comment.find.byId(commentId);

		if (toDelete==null) return redirect(""); // TODO we have to complete the picture/comment page first
		Picture commentedPicture = toDelete.getCommentedPicture();
		toDelete.delete();
		commentedPicture.removeCommentFromPicture(toDelete);
		return redirect(""); // TODO we have to complete the picture/comment page first
	}
	public Result getPictureComments(Request request, String pictureId) {
		Picture picture = Picture.find.byId(pictureId);
		if (picture==null) return ok();
		return ok(views.html.layouts.contentBox.comments.render(picture));
	}
	public Result tagPictureAction(Request request, long tagId, String pictureId){
		Picture taggedPicture = Picture.find.byId(pictureId);
		Tag tagOfPicture = Tag.find.byId(tagId);

		if (taggedPicture==null || tagOfPicture==null) return redirect(routes.ViewsController.index()).flashing("error", "There was an error when tagging the picture.");
		if (Tags.find.byId(tagId + ";" + taggedPicture.getPictureId()) != null) {return redirect(routes.ViewsController.index()).flashing("error","you are already tagged that photo");}

		Tags newTags = new Tags(taggedPicture, tagOfPicture);
		newTags.save();
		return ok();
	}

	public Result untagPictureAction(Request request, long tagId, String pictureId){
		Picture taggedPicture = Picture.find.byId(pictureId);
		Tag tagOfPicture = Tag.find.byId(tagId);

		if (taggedPicture==null || tagOfPicture==null) return redirect(routes.ViewsController.index()).flashing("error", "There was an error when untagging the picture.");
		Tags toDelete = Tags.find.byId(tagId + ";" + taggedPicture.getPictureId());
		if (toDelete == null) {return redirect(routes.ViewsController.index()).flashing("error","That photo is not tagged");}

		toDelete.delete();
		return ok();
	}

	public Result likePictureAction(Request request, String pictureId) {
		User liker = User.findById(request.session().get("user").orElse("0"));
		Picture liked = Picture.find.byId(pictureId);

		if (liker==null || liked==null) return redirect(routes.ViewsController.index()).flashing("error", "There was an error when liking that picture.");

		Likes like = new Likes(liker, liked);
		like.save();
		liked.addLikeToPicture();
		liked.update();
		return ok();    // TODO we have to complete the picture/comment page
	}
	public Result unlikePictureAction(Request request, String pictureId) {
		User liker = User.findById(request.session().get("user").orElse("0"));
		Picture liked = Picture.find.byId(pictureId);

		if (liker==null || liked==null) return redirect(routes.ViewsController.index()).flashing("error", "There was an error when unliking that picture.");
		Likes like = Likes.find.byId(liker.getUserId() + ";" + liked.getPictureId());
		if (like==null) return redirect(routes.ViewsController.index()).flashing("error", "You don't like that picture.");

		like.delete();
		liked.removeLikeFromPicture();
		liked.update();
		return ok();    // TODO we have to complete the picture/comment page
	}
}
