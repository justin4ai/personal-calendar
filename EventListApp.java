import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventListApp extends JFrame {
    private DefaultListModel<String> eventListModel;
    private JList<String> eventList;

    public EventListApp() {
        setTitle("Event List");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        JScrollPane scrollPane = new JScrollPane(eventList);

        JButton refreshButton = new JButton("Refresh");
        JButton deleteButton = new JButton("Delete Selected Event");

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshEventList();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedEvent();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
                            "SELECT event_id, title, location, participants, start_time, end_time FROM events");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                eventListModel.addElement("(" + resultSet.getString("event_id") + ")" + resultSet.getString("title") +
                        " at " + resultSet.getString("location") + " || from " + resultSet.getTimestamp("start_time")
                        + " to " + resultSet.getTimestamp("start_time") + " || with "
                        + resultSet.getString("participants"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSelectedEvent() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedEvent = eventListModel.get(selectedIndex);
            int eventId = Integer.parseInt(selectedEvent.split(")")[0].split("(")[1].trim());

            try {
                Connection connection = DriverManager.getConnection(
                        Helpers.dbURL,
                        Helpers.dbUser,
                        Helpers.dbPasswd);

                String SQL_DELETE_E = "DELETE FROM events WHERE event_id = ?";
                String SQL_DELETE_R = "DELETE FROM reminderinfo WHERE event_id = ?";

                PreparedStatement preparedStatement = connection
                        .prepareStatement(SQL_DELETE_E);
                PreparedStatement preparedStatement2 = connection
                        .prepareStatement(SQL_DELETE_R);

                preparedStatement2.setInt(1, eventId);
                preparedStatement2.executeUpdate();

                preparedStatement.setInt(1, eventId);
                preparedStatement.executeUpdate();

                preparedStatement2.close();
                preparedStatement.close();
                connection.close();

                refreshEventList();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
