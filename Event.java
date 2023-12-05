import java.sql.Timestamp;

public class Event {
    private int eventId;
    private String title;
    private Timestamp startTime;
    private Timestamp endTime;
    private String location;

    public Event(int eventId, String title, Timestamp startTime, Timestamp endTime, String location) {
        this.eventId = eventId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public int getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
