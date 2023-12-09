import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.Locale;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayLoginPanel extends JFrame {
    public DisplayLoginPanel() {
        // Log-in panel here
        JFrame frame = new JFrame("Log-in to family calendar");
        ImageIcon imageIcon = new ImageIcon("./calendar.png");
        JLabel label = new JLabel(imageIcon);
        frame.setLocationRelativeTo(null);

        // Create JTextField components for name and password
        JTextField nameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        // Create JButton to submit the input
        JButton submitButton = new JButton("Log-in");

        // Create ActionListener for the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputName = nameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String inputPasswd = new String(passwordChars);

                // Now you can use inputName and inputPasswd as needed
                System.out.println("Name: " + inputName);
                System.out.println("Password: " + inputPasswd);

                if (loginIsSuccessful(inputName, inputPasswd)) {
                    // Close the JFrame
                    frame.dispose();

                    DisplayCalendarMonthly y = new DisplayCalendarMonthly();

                }

                else {
                    JOptionPane.showMessageDialog(null, "Check if your user name and password are correct.",
                            "Log-in Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create a JPanel to hold the components
        JPanel panelforimg = new JPanel();
        JPanel panel = new JPanel(new GridLayout(3, 2));

        panelforimg.add(label);
        // Add components to the panel
        panel.add(new JLabel("Name: "));
        panel.add(nameField);
        panel.add(new JLabel("Password: "));
        panel.add(passwordField);
        panel.add(submitButton, BorderLayout.CENTER);

        // Add the panel to the frame
        frame.add(panelforimg, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.SOUTH);

        // Set frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public boolean loginIsSuccessful(String username, String userpassword) {

        System.out.println("Here you are");
        System.out.println(username);
        System.out.println(userpassword);
        String SQL_SELECT = String.format("SELECT user_id, name FROM users WHERE name='%s' and password='%s'", username,
                userpassword);

        try (Connection conn = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);
                PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                PersonalCalendar.userID = resultSet.getInt("user_id");
                PersonalCalendar.name = resultSet.getString("name");
                System.out.println(String.format("Verified ID is %s", PersonalCalendar.userID));

                JOptionPane.showMessageDialog(null,
                        String.format("Log-in Success!\nYour user name :%s", PersonalCalendar.name),
                        "Log-in Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            System.out.print(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
