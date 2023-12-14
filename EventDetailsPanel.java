import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class EventDetailsPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel locationLabel;
    private JLabel participantsLabel;
    private JLabel descriptionLabel;
    private JLabel startTimeLabel;
    private JLabel endTimeLabel;

    public EventDetailsPanel() {
        setLayout(new GridLayout(6, 1));

        titleLabel = new JLabel("Title:");
        locationLabel = new JLabel("Location:");
        participantsLabel = new JLabel("Participants:");
        descriptionLabel = new JLabel("Description:");
        startTimeLabel = new JLabel("Start Time:");
        endTimeLabel = new JLabel("End Time:");

        add(titleLabel);
        // add(new JLabel()); // Placeholder for title text
        add(locationLabel);
        // add(new JLabel()); // Placeholder for location text
        add(participantsLabel);
        // add(new JLabel()); // Placeholder for participants text
        add(descriptionLabel);
        // add(new JLabel()); // Placeholder for description text
        add(startTimeLabel);
        /// add(new JLabel()); // Placeholder for start time text
        add(endTimeLabel);
        // add(new JLabel()); // Placeholder for end time text
    }

    public void setTitle(String title) {
        titleLabel.setText("Title: " + title);
    }

    public void setLocation(String location) {
        locationLabel.setText("Location: " + location);
    }

    public void setParticipants(String participants) {
        participantsLabel.setText("Participants: " + participants);
    }

    public void setDescription(String description) {
        descriptionLabel.setText("Description: " + description);
    }

    public void setStartTime(Timestamp startTime) {
        startTimeLabel.setText("Start Time: " + startTime.toString());
    }

    public void setEndTime(Timestamp endTime) {
        endTimeLabel.setText("End Time: " + endTime.toString());
    }
}
