import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewEvents extends JFrame {
    private JTable eventTable;
    private DefaultTableModel tableModel;

    public ViewEvents() {
        setTitle("Event Table View");
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

}
