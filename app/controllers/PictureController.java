package controllers;

import formatters.DateTimeFormats;
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

import javax.inject.Inject;
import java.io.File;
import java.time.LocalDateTime;

/**
 * Handle actions related to pictures (posting, commenting, deleting, etc).
 * @author TBA
 * @version 1.0
 */
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

	public Result newPicturePage(Request request){
		return ok();
	}
	public Result newPictureAction(Request request) {
		DynamicForm pictureForm = formFactory.form().bindFromRequest(request);
		String pictureCaption = pictureForm.get("pictureCaption");
		User pictureOwner = User.findById(request.session().get("user").orElse("0"));

		if (!isCaptionValid(pictureCaption)) return badRequest();
		if (pictureOwner == null) return badRequest();
		Picture newPicture = new Picture();
		newPicture.setPictureOwner(pictureOwner);
		newPicture.setPictureId(pictureOwner.getUserId() + "-" + newPicture.getUploadTime().format(DateTimeFormats.ID));
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
			newPicture.setPictureLocation(fileAddress);
			newPicture.save();
			fileAddress = newPicture.getPictureLocation() + newPicture.getPictureId() + newPicture.getFileExtension();
			directory = new File(fileAddress);
			file.copyTo(directory, true);
			return redirect(routes.PictureController.viewPicturePage(newPicture.getPictureId()));
		} else {
			return badRequest().flashing("error", "Missing file");
		}
	}
	public Result viewPicturePage(Request request, String pictureId){
		return ok();
	}
}
