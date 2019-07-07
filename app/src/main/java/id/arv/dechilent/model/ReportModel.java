package id.arv.dechilent.model;

public class ReportModel {
    public String name;
    public String email;
    public int score;
    public long created_at;

    public ReportModel() {
    }

    public ReportModel(String name, String email, int score, long created_at) {
        this.name = name;
        this.email = email;
        this.score = score;
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
