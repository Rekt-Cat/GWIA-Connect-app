package Models;

public class SearchModel {
    String id;
    String fullName;
    String imageUrl;
    String bio;

    public SearchModel(String id, String username, String imageUrl, String bio) {
        this.id = id;
        this.fullName = username;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    public SearchModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfullName() {
        return fullName;
    }

    public void setUsername(String username) {
        this.fullName = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
