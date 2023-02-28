package Models;

public class NewsModel {
    String cat,title,date,text;

    public NewsModel(String cat, String title, String date, String text) {
        this.cat = cat;
        this.title = title;
        this.date = date;
        this.text = text;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
