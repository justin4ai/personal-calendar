import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyCalendarApp extends JFrame {
    private static final int START_HOUR = 0;
    private static final int END_HOUR = 23;
    private static final int TIME_INTERVAL = 60;

    private JPanel calendarPanel;
    private JLabel dateLabel;
    private Calendar currentCalendar;

    public DailyCalendarApp() {
        setTitle("Daily Calendar");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the current calendar to the current date
        currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY, START_HOUR);
        currentCalendar.set(Calendar.MINUTE, 0);

        // Create calendar panel
        calendarPanel = new JPanel(new GridLayout(END_HOUR - START_HOUR + 1, 2));
        updateCalendarPanel(currentCalendar.getTime());

        // Create navigation buttons
        JButton prevDayButton = new JButton("Previous Day");
        JButton nextDayButton = new JButton("Next Day");

        prevDayButton.addActionListener(e -> navigate(-1));
        nextDayButton.addActionListener(e -> navigate(1));

        // Create date label
        dateLabel = new JLabel();
        updateDateLabel(currentCalendar.getTime());

        // Create layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(prevDayButton, BorderLayout.WEST);
        topPanel.add(dateLabel, BorderLayout.CENTER);
        topPanel.add(nextDayButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(calendarPanel), BorderLayout.CENTER);
    }

    private void updateCalendarPanel(Date selectedDate) {
        calendarPanel.removeAll();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, START_HOUR);
        calendar.set(Calendar.MINUTE, 0);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
            // Add time label
            JLabel timeLabel = new JLabel(timeFormat.format(calendar.getTime()));
            timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            calendarPanel.add(timeLabel);

            JPanel timeSlotPanel = new JPanel();
            timeSlotPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // TODO: Add your logic to fetch and display events for the current time slot
            JLabel eventLabel = new JLabel("");
            eventLabel.setHorizontalAlignment(SwingConstants.CENTER);
            timeSlotPanel.add(eventLabel);

            calendarPanel.add(timeSlotPanel);

            calendar.add(Calendar.MINUTE, TIME_INTERVAL);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void updateDateLabel(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateLabel.setText(dateFormat.format(date));
    }

    private void navigate(int days) {
        // Move the current calendar by the specified number of days
        currentCalendar.add(Calendar.DAY_OF_MONTH, days);

        // Update the date label and calendar panel
        updateDateLabel(currentCalendar.getTime());
        updateCalendarPanel(currentCalendar.getTime());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DailyCalendarApp().setVisible(true));
    }
}
