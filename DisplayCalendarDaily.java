import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    JPanel p_center; // 날짜 박스 처리할 영역
    JButton bt_create_event;
    JButton bt_RVSP;
    JButton bt_update_event;
    JButton bt_delete_event;
    JButton bt_eventList;
    JButton bt_modeChange;
    JButton bt_notification;
    JButton bt_createUser;
    JButton bt_updateUser;
    JFrame frame5;

    public DisplayCalendarDaily() {

        frame5 = new JFrame("Daily Calendar by Justin");
        p_north = new JPanel();
        p_south = new JPanel();
        bt_prev = new JButton("previous");
        lb_title = new JLabel("upcomming year", SwingConstants.CENTER);
        bt_next = new JButton("next");

        bt_create_event = new JButton("Create an event");
        bt_update_event = new JButton("Modify an event");
        bt_delete_event = new JButton("Delete an event");
        // bt_RVSP = new JButton("RSVP");
        bt_notification = new JButton("Notifications");
        bt_eventList = new JButton("View event list");
        bt_createUser = new JButton("Create user");
        bt_modeChange = new JButton("Mode change");
        bt_updateUser = new JButton("Update user");

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
        JPanel bottomPanel = new JPanel(new BorderLayout());

        topPanel.add(prevDayButton, BorderLayout.WEST);
        topPanel.add(dateLabel, BorderLayout.CENTER);
        topPanel.add(nextDayButton, BorderLayout.EAST);
        bottomPanel.add(bt_create_event);
        // p_north.add(bt_RVSP);
        // p_south.add(bt_notification);
        // p_south.add(bt_update_event);
        bottomPanel.add(bt_delete_event);
        bottomPanel.add(bt_eventList);
        bottomPanel.add(bt_modeChange);
        bottomPanel.add(bt_createUser, BorderLayout.NORTH);
        bottomPanel.add(bt_updateUser, BorderLayout.SOUTH);

        // Create an event
        bt_create_event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.createEvent();
            }
        });

        // Update an event
        bt_update_event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.updateEvent();
            }
        });

        // Delete an event
        bt_delete_event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.deleteEvent();
            }
        });

        // Notification
        // bt_notification.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // Helpers.notification();
        // }
        // });

        // RVSP
        // bt_RVSP.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // sendRSVP();
        // }
        // });

        // Event list
        bt_eventList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.eventList();
            }
        });

        // Mode change
        bt_modeChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Before disposing");
                System.out.println("After disposing");
                Helpers.modeChange(frame5);
            }
        });

        // User create
        bt_createUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.createUser();
            }
        });

        bt_updateUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.updateUser(PersonalCalendar.name);
            }
        });

        frame5.setLayout(new BorderLayout());
        frame5.add(topPanel, BorderLayout.NORTH);
        frame5.add(new JScrollPane(calendarPanel), BorderLayout.CENTER);
        frame5.add(bottomPanel, BorderLayout.SOUTH);
        frame5.setVisible(true);
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

            // Fetch events for the current time slot from the database
            java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());
            String events = fetchEventsFromDatabase(sqlDate, hour, hour + 1);

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
        // TODO: Replace the following code with your database connection logic

        try (Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd)) {
            String query = "SELECT title FROM events WHERE ((DATE(start_time) = ? AND start_time <= ? AND end_time > ?) OR (DATE(end_time) = ? AND start_time < ? AND end_time >= ?)) AND creator_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, date);
                preparedStatement.setTimestamp(2, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(startHour)));
                preparedStatement.setTimestamp(3, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(endHour)));
                preparedStatement.setDate(4, date);
                preparedStatement.setTimestamp(5, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(startHour)));
                preparedStatement.setTimestamp(6, new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(endHour)));
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
        // Move the current calendar by the specified number of days
        currentCalendar.add(Calendar.DAY_OF_MONTH, days);

        // Update the date label and calendar panel
        updateDateLabel(currentCalendar.getTime());
        updateCalendarPanel(currentCalendar.getTime());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayCalendarDaily().setVisible(true));
    }
}
