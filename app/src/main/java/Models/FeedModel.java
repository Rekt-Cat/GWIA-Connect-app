package Models;

public class FeedModel {
    String userName;
    String PostImage;
    String Description;
    String likes;
    String comments;
    String Publisher;
    String PostId;


    public FeedModel(String userName, String PostImage, String Description, String likes, String comments, String Publisher) {
        this.userName = userName;
        this.PostImage = PostImage;
        this.Description = Description;
        this.likes = likes;
        this.comments = comments;
        this.Publisher = Publisher;
    }

    public FeedModel(String PostImage,String Description, String Publisher,String Postid) {
        this.PostImage = PostImage;
        this.Description = Description;
        this.Publisher = Publisher;
        this.PostId=Postid;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public FeedModel() {
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        this.Publisher = publisher;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return PostImage;
    }

    public void setImage(String image) {
        this.PostImage = image;
    }

    public String getCaption() {
        return Description;
    }

    public void setCaption(String caption) {
        this.Description = caption;
    }
}
