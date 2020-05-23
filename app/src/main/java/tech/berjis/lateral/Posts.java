package tech.berjis.lateral;

public class Posts {
    private long time;
    private String availability, post_id, user, status, text, type;

    public Posts(long time, String availability, String post_id, String user, String status, String text, String type) {
        this.time = time;
        this.availability = availability;
        this.post_id = post_id;
        this.user = user;
        this.status = status;
        this.text = text;
        this.type = type;
    }

    public Posts() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
