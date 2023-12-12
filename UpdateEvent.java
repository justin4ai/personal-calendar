import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateEvent extends JFrame {
    private DefaultListModel<String> eventListModel;
    private JList<String> eventList;

    private JTextField titleField;
    private JTextField locationField;
    private JTextField participantsField;
    private JTextArea descriptionArea;
    private JTextField startTimeField;
    private JTextField endTimeField;

    public UpdateEvent() {
        setTitle("Event List");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);

        JScrollPane scrollPane = new JScrollPane(eventList);

        JButton refreshButton = new JButton("Refresh");
        // JButton deleteButton = new JButton("Delete Selected Event");
        JButton updateButton = new JButton("Update Selected Event");

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshEventList();
            }
        });

        // deleteButton.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // deleteSelectedEvent();
        // }
        // });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedEvent();
            }
        });

        titleField = new JTextField(30);
        locationField = new JTextField(30);
        participantsField = new JTextField(50);
        descriptionArea = new JTextArea(5, 20);
        startTimeField = new JTextField(20);
        endTimeField = new JTextField(20);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        titleField.setText("TODAY(M)_" + (System.currentTimeMillis() / (60 * 1000)));
        locationField.setText("school");
        participantsField.setText("Dr.Bennett, Jiyeong Oh");

        // Set the start time to the current time
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, 2);
        calendar1.add(Calendar.MINUTE, 5);
        startTimeField.setText(dateFormat.format(calendar1.getTime()));

        // Set the end time to 2 hours later than the current time
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, 2);
        calendar2.add(Calendar.MINUTE, 6);
        endTimeField.setText(dateFormat.format(calendar2.getTime()));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(7, 2));
        detailsPanel.add(new JLabel("Title:"));
        detailsPanel.add(titleField);
        detailsPanel.add(new JLabel("Location:"));
        detailsPanel.add(locationField);
        detailsPanel.add(new JLabel("Participants:"));
        detailsPanel.add(participantsField);
        detailsPanel.add(new JLabel("Description:"));
        detailsPanel.add(new JScrollPane(descriptionArea));
        detailsPanel.add(new JLabel("Start Time:"));
        detailsPanel.add(startTimeField);
        detailsPanel.add(new JLabel("End Time:"));
        detailsPanel.add(endTimeField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        // buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        eventList.addListSelectionListener(e -> displaySelectedEventDetails());
    }

    private void refreshEventList() {
        eventListModel.clear();

        try {
            Connection connection = DriverManager.getConnection(
                    Helpers.dbURL,
                    Helpers.dbUser,
                    Helpers.dbPasswd);

            PreparedStatement preparedStatement = connection
                    .prepareStatement(
                            "SELECT event_id, title, location, participants, start_time, end_time FROM events WHERE creator_id = ?");
            preparedStatement.setInt(1, PersonalCalendar.userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                eventListModel.addElement("(" + resultSet.getString("event_id") + ")" + resultSet.getString("title") +
                        " at " + resultSet.getString("location") + " || from " + resultSet.getTimestamp("start_time")
                        + " to " + resultSet.getTimestamp("end_time") + " || with "
                        + resultSet.getString("participants"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateSelectedEvent() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedEvent = eventListModel.get(selectedIndex);
            // int eventId =
            // Integer.parseInt(selectedEvent.split(")")[0].split("(")[1].trim());
            int eventId = Integer.parseInt(selectedEvent.split("\\)")[0].split("\\(")[1].trim());

            try {
                Connection connection = DriverManager.getConnection(
                        Helpers.dbURL,
                        Helpers.dbUser,
                        Helpers.dbPasswd);

                String SQL_UPDATE = "UPDATE events SET title = ?, location = ?, participants = ?, description = ?, start_time = ?::date, end_time = ?::date WHERE event_id = ?";

                PreparedStatement preparedStatement = connection
                        .prepareStatement(SQL_UPDATE);

                preparedStatement.setString(1, titleField.getText());
                preparedStatement.setString(2, locationField.getText());
                preparedStatement.setString(3, participantsField.getText());
                preparedStatement.setString(4, descriptionArea.getText());
                preparedStatement.setString(5, startTimeField.getText());
                preparedStatement.setString(6, endTimeField.getText());
                preparedStatement.setInt(7, eventId);

                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();

                refreshEventList();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void displaySelectedEventDetails() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedEvent = eventListModel.get(selectedIndex);
            // int eventId =
            // Integer.parseInt(selectedEvent.split(")")[0].split("(")[1].trim());
            int eventId = Integer.parseInt(selectedEvent.split("\\)")[0].split("\\(")[1].trim());
            try {
                Connection connection = DriverManager.getConnection(
                        Helpers.dbURL,
                        Helpers.dbUser,
                        Helpers.dbPasswd);

                String SQL_SELECT = "SELECT title, location, participants, description, start_time, end_time FROM events WHERE event_id = ? AND creator_id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, eventId);
                preparedStatement.setInt(2, PersonalCalendar.userID);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    titleField.setText(resultSet.getString("title"));
                    locationField.setText(resultSet.getString("location"));
                    participantsField.setText(resultSet.getString("participants"));
                    descriptionArea.setText(resultSet.getString("description"));
                    startTimeField.setText(resultSet.getString("start_time"));
                    endTimeField.setText(resultSet.getString("end_time"));
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}