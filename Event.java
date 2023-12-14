import java.sql.Timestamp;

public class Event {
    private int eventId;
    private String title;
    private Timestamp startTime;
    private Timestamp endTime;
    private String location;
    private String participants;
    private String description;

    public Event(int eventId, String title, Timestamp startTime, Timestamp endTime, String location,
            String participants, String description) {
        this.eventId = eventId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.participants = participants;
        this.description = description;
    }

    public int getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getParticipants() {
        return participants;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title;
    }
}
