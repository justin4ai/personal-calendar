import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DisplayCalendarDaily extends JFrame {
    private static final int START_HOUR = 0;
    private static final int END_HOUR = 23;
    private static final int TIME_INTERVAL = 60;

    private JPanel calendarPanel;
    private JLabel dateLabel;
    private Calendar currentCalendar;

    JPanel p_north;
    JPanel p_south;
    JButton bt_prev;
    JLabel lb_title;
    JButton bt_next;
    JButton bt_create_event;
    JButton bt_RVSP;
    JButton bt_update_event;
    JButton bt_delete_event;
    JButton bt_eventList;
    JButton bt_modeChange;
    JButton bt_notification;
    JButton bt_createUser;
    JButton bt_updateUser;
    JButton bt_viewEvents;
    JFrame frame5;

    public DisplayCalendarDaily() {
        frame5 = new JFrame("Daily Calendar by Justin");
        p_north = new JPanel();
        p_south = new JPanel();
        bt_prev = new JButton("previous");
        lb_title = new JLabel("upcoming year", SwingConstants.CENTER);
        bt_next = new JButton("next");

        bt_create_event = new JButton("Create an event");
        bt_update_event = new JButton("Modify an event");
        bt_delete_event = new JButton("Delete an event");
        bt_viewEvents = new JButton("View all events");
        bt_eventList = new JButton("View event list");
        bt_modeChange = new JButton("Mode change");
        bt_createUser = new JButton("Create user");
        bt_updateUser = new JButton("Update user");

        currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY, START_HOUR);
        currentCalendar.set(Calendar.MINUTE, 0);

        calendarPanel = new JPanel(new GridLayout(END_HOUR - START_HOUR + 1, 2));
        updateCalendarPanel(currentCalendar.getTime());

        JButton prevDayButton = new JButton("Previous Day");
        JButton nextDayButton = new JButton("Next Day");

        prevDayButton.addActionListener(e -> navigate(-1));
        nextDayButton.addActionListener(e -> navigate(1));

        dateLabel = new JLabel();
        updateDateLabel(currentCalendar.getTime());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel();

        topPanel.add(prevDayButton, BorderLayout.WEST);
        topPanel.add(dateLabel, BorderLayout.CENTER);
        topPanel.add(nextDayButton, BorderLayout.EAST);
        bottomPanel.add(bt_create_event);
        bottomPanel.add(bt_delete_event);
        bottomPanel.add(bt_eventList);
        bottomPanel.add(bt_modeChange);
        bottomPanel.add(bt_createUser);
        bottomPanel.add(bt_updateUser);
        bottomPanel.add(bt_viewEvents);

        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        bt_create_event.addActionListener(e -> Helpers.createEvent());
        bt_update_event.addActionListener(e -> Helpers.updateEvent());
        bt_delete_event.addActionListener(e -> Helpers.deleteEvent());
        bt_viewEvents.addActionListener(e -> Helpers.viewEvents());

        bt_eventList.addActionListener(e -> Helpers.eventList());

        bt_modeChange.addActionListener(e -> Helpers.modeChange(frame5));

        bt_createUser.addActionListener(e -> Helpers.createUser());

        bt_updateUser.addActionListener(e -> Helpers.updateUser(PersonalCalendar.name));

        frame5.setLayout(new BorderLayout());
        frame5.add(topPanel, BorderLayout.NORTH);
        frame5.add(new JScrollPane(calendarPanel), BorderLayout.CENTER);
        frame5.add(bottomPanel, BorderLayout.SOUTH);
        frame5.pack();
        frame5.setVisible(true);

        updateCalendarPanel(currentCalendar.getTime());
    }

    private void updateCalendarPanel(Date selectedDate) {
        calendarPanel.removeAll();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, START_HOUR);
        calendar.set(Calendar.MINUTE, 0);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
            JLabel timeLabel = new JLabel(timeFormat.format(calendar.getTime()));
            timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            calendarPanel.add(timeLabel);

            JPanel timeSlotPanel = new JPanel();
            timeSlotPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());
            String events = "<html>"
                    + fetchEventsFromDatabase(sqlDate, hour -  1, hour) + "</html>";

            JLabel eventLabel = new JLabel(events);
            eventLabel.setHorizontalAlignment(SwingConstants.CENTER);
            timeSlotPanel.add(eventLabel);

            calendarPanel.add(timeSlotPanel);

            calendar.add(Calendar.MINUTE, TIME_INTERVAL);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private String fetchEventsFromDatabase(java.sql.Date date, int startHour, int endHour) {
        try (Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd)) {
            String query = "SELECT title FROM events WHERE ((DATE(start_time) = ? AND start_time < ? AND end_time > ?) OR (DATE(end_time) = ? AND start_time < ? AND end_time > ?)) AND creator_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, date);
                preparedStatement.setTimestamp(2, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(endHour)));
                preparedStatement.setTimestamp(3, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(startHour)));
                preparedStatement.setDate(4, date);
                preparedStatement.setTimestamp(5, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(endHour)));
                preparedStatement.setTimestamp(6, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(startHour)));
                preparedStatement.setInt(7, PersonalCalendar.userID);

                ResultSet resultSet = preparedStatement.executeQuery();
                StringBuilder events = new StringBuilder();

                while (resultSet.next()) {
                    events.append(resultSet.getString("title")).append("<br>");
                }

                return events.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void updateDateLabel(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateLabel.setText(dateFormat.format(date));
    }

    private void navigate(int days) {
        currentCalendar.add(Calendar.DAY_OF_MONTH, days);
        updateDateLabel(currentCalendar.getTime());
        updateCalendarPanel(currentCalendar.getTime());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayCalendarDaily().setVisible(true));
    }
}
