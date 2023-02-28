package Models;

public class AcademicsModel2 {
    String subject;
    int percent;

    public AcademicsModel2(String subject, int percent) {
        this.subject = subject;
        this.percent = percent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
