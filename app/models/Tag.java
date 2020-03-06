package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag extends Model {
    @Id
    private int tagId;
    @Constraints.Required
    private String tagContent;

    public Tag(int tagId, @Constraints.Required String tagContent) {
        this.tagId = tagId;
        this.tagContent = tagContent;
    }

    public static Finder<Long, Tag> find = new Finder<>(Tag.class);
    public static Tag findByTagContent(String tagContent) {return find.byId(parsers.StringParsers.parseLong(tagContent));}

    public int getTagId() {return tagId;}
    public void setTagId(int tagId) {this.tagId = tagId;}
    public String getTagContent() {return tagContent;}
    public void setTagContent(String tagContent) {this.tagContent = tagContent;}
}
