package models.relationships;

import io.ebean.Finder;
import io.ebean.Model;
import models.Picture;
import models.Tag;
import play.data.validation.Constraints;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

public class Tags extends Model {

    @Id
    private String TagsId;

    @Constraints.Required
    @ManyToOne
    private Picture taggedPicture;

    @Constraints.Required
    @ManyToOne
    private Tag tagOfPicture;

    public static Finder<String, Tags> find = new Finder<>(Tags.class);

    /*Finds tags relations of a tag*/
    public static List<Tags> findByTag(Tag tagOfPicture) {return find.query().where().eq("taggedPicture", tagOfPicture).findList();}

    /*Finds tags relations of picture*/
    public static List<Tags> findByPicture(Picture taggedPicture) {return find.query().where().eq("taggedPicture", taggedPicture).findList();}

    public Tags(@Constraints.Required Picture taggedPicture, @Constraints.Required Tag tagOfPicture) {
        TagsId = tagOfPicture.getTagId() + ";" +taggedPicture.getPictureId();
        this.taggedPicture = taggedPicture;
        this.tagOfPicture = tagOfPicture;
    }

    public String getTagsId() {
        return TagsId;
    }

    public void setTagsId(String tagsId) {
        TagsId = tagsId;
    }

    public Picture getTaggedPicture() {
        return taggedPicture;
    }

    public void setTaggedPicture(Picture taggedPicture) {
        this.taggedPicture = taggedPicture;
    }

    public Tag getTagOfPicture() {
        return tagOfPicture;
    }

    public void setTagOfPicture(Tag tagOfPicture) {
        this.tagOfPicture = tagOfPicture;
    }
}
