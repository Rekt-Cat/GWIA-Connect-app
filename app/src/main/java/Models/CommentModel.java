package Models;

public class CommentModel {

    String comment;
    String publisher;



    public CommentModel(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public CommentModel() {
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public String getUserComment() {
        return comment;
    }

    public void setUserComment(String userComment) {
        this.comment = userComment;
    }
}
