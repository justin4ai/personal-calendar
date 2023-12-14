import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewEvents extends JFrame {
    private JTable eventTable;
    private DefaultTableModel tableModel;

    public ViewEvents() {
        setTitle("View all the events");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create columns and data model
        String[] columns = { "Event ID", "Title", "Participants", "Location", "Description", "Creator ID", "Start Time",
                "End Time" };
        tableModel = new DefaultTableModel(columns, 0);

        // Create table
        eventTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(eventTable);

        // Add components to the frame
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Fetch and display data
        fetchData();

        // Add mouse listener to handle row clicks
        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = eventTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEventDetailsDialog(selectedRow);
                }
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void fetchData() {
        try {
            Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);

            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM events WHERE creator_id = ?");

            preparedStatement.setInt(1, PersonalCalendar.userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("event_id"),
                        resultSet.getString("title"),
                        resultSet.getString("participants"),
                        resultSet.getString("location"),
                        resultSet.getString("description"),
                        resultSet.getInt("creator_id"),
                        resultSet.getTimestamp("start_time"),
                        resultSet.getTimestamp("end_time")
                };

                tableModel.addRow(rowData);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showEventDetailsDialog(int rowIndex) {
        JDialog detailsDialog = new JDialog(this, "Event Details", true);
        detailsDialog.setSize(400, 300);

        // Fetch details of the selected event
        int eventId = (int) eventTable.getValueAt(rowIndex, 0);
        EventDetailsPanel detailsPanel = fetchEventDetails(eventId);

        // Add details panel to the dialog
        detailsDialog.add(detailsPanel);

        // Set the dialog to be visible
        detailsDialog.setVisible(true);
    }

    private EventDetailsPanel fetchEventDetails(int eventId) {
        EventDetailsPanel detailsPanel = new EventDetailsPanel();

        try {
            Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);

            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM events WHERE event_id = ?");

            preparedStatement.setInt(1, eventId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                detailsPanel.setTitle(resultSet.getString("title"));
                detailsPanel.setLocation(resultSet.getString("location"));
                detailsPanel.setParticipants(resultSet.getString("participants"));
                detailsPanel.setDescription(resultSet.getString("description"));
                detailsPanel.setStartTime(resultSet.getTimestamp("start_time"));
                detailsPanel.setEndTime(resultSet.getTimestamp("end_time"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return detailsPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewEvents viewEvents = new ViewEvents();
            viewEvents.setVisible(true);
        });
    }

}
