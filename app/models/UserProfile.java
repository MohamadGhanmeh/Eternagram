package models;


import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class UserProfile extends Model {
    @Id
    private long profileId;
    private String firstName;
    private String lastName;
    private int userGender;
    private String userBio;
    private String urlFacebook;
    private String urlLinkedin;
    private String urlYoutube;
    private String urlTwitch;
    private String urlTwitter;
    private String urlPersonal;

    public UserProfile(User profileOwner, String firstName, String lastName, int userGender, String userBio, String urlFacebook, String urlLinkedin, String urlYoutube, String urlTwitch, String urlTwitter, String urlPersonal) {
        this.profileId = profileOwner.getUserId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.userGender = userGender;
        this.userBio = userBio;
        this.urlFacebook = urlFacebook;
        this.urlLinkedin = urlLinkedin;
        this.urlYoutube = urlYoutube;
        this.urlTwitch = urlTwitch;
        this.urlTwitter = urlTwitter;
        this.urlPersonal = urlPersonal;
    }

    public static Finder<Long, UserProfile> find = new Finder<>(UserProfile.class);

    public long getProfileId() {return profileId;}
    public void setProfileId(User profileOwner) {this.profileId = profileOwner.getUserId();}
    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public int getUserGender() {return userGender;}
    public void setUserGender(int userGender) {this.userGender = userGender;}
    public String getUserBio() {return userBio;}
    public void setUserBio(String userBio) {this.userBio = userBio;}
    public String getUrlFacebook() {return urlFacebook;}
    public void setUrlFacebook(String urlFacebook) {this.urlFacebook = urlFacebook;}
    public String getUrlLinkedin() {return urlLinkedin;}
    public void setUrlLinkedin(String urlLinkedin) {this.urlLinkedin = urlLinkedin;}
    public String getUrlYoutube() {return urlYoutube;}
    public void setUrlYoutube(String urlYoutube) {this.urlYoutube = urlYoutube;}
    public String getUrlTwitch() {return urlTwitch;}
    public void setUrlTwitch(String urlTwitch) {this.urlTwitch = urlTwitch;}
    public String getUrlTwitter() {return urlTwitter;}
    public void setUrlTwitter(String urlTwitter) {this.urlTwitter = urlTwitter;}
    public String getUrlPersonal() {return urlPersonal;}
    public void setUrlPersonal(String urlPersonal) {this.urlPersonal = urlPersonal;}
}
