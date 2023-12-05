import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
import java.text.ParseException;

public class Helpers {

    // After clicking create an event button
    public static void createEvent() {
        JDialog createEventDialog = new JDialog();
        createEventDialog.setTitle("Create an event");

        // Create JTextField components for name and password
        JTextField title = new JTextField(30);
        JTextField startTime = new JTextField(20);
        JTextField location = new JTextField(20);
        JTextField endTime = new JTextField(20);
        JTextField participant = new JTextField(20);
        JTextField description = new JTextField(100);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Create JButton to submit the input
        JButton createButton = new JButton("Create");

        // Create ActionListener for the submit button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titleCreated = title.getText();
                String locationCreated = location.getText();
                String startCreated = startTime.getText();
                String endCreated = endTime.getText();
                String descCreated = description.getText();
                String participantCreated = participant.getText();
                int creatorId = PersonalCalendar.userID;

                try {
                    if (isEventAvailable(new java.sql.Timestamp(dateFormat.parse(startCreated).getTime()),
                            new java.sql.Timestamp(dateFormat.parse(endCreated).getTime()))) {
                        String SQL_INSERT1 = "INSERT INTO events (title, start_time, end_time, description, creator_id, location, participants) VALUES (?, ?, ?, ?, ?, ?, ?);";

                        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
                                "dob", "dobstudio");
                                PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT1)) {

                            preparedStatement.setString(1, titleCreated);
                            preparedStatement.setTimestamp(2,
                                    new java.sql.Timestamp(dateFormat.parse(startCreated).getTime()));
                            preparedStatement.setTimestamp(3,
                                    new java.sql.Timestamp(dateFormat.parse(endCreated).getTime()));
                            preparedStatement.setString(4, descCreated);
                            preparedStatement.setInt(5, creatorId);
                            preparedStatement.setString(6, locationCreated);
                            preparedStatement.setString(7, participantCreated);

                            System.out.println(preparedStatement);
                            Integer rowInserted = preparedStatement.executeUpdate();
                            System.out.println(rowInserted);
                            // Redundant name-passwd pair will be denied

                            if (rowInserted > 0) {
                                System.out.println("User created successfully");
                                createEventDialog.dispose();
                            }

                        } catch (SQLException ee) {
                            System.out.print(ee.getMessage());
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }

                    else {
                        JOptionPane.showMessageDialog(null, "The given time frame overlaps pre-existing events.",
                                "Availability Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ParseException eee) {
                    System.out.println(eee.getMessage());
                }

            }
        });

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new GridLayout(6, 2));

        // Add components to the panel
        panel.add(new JLabel("Title: "));
        panel.add(title);
        panel.add(new JLabel("Location: "));
        panel.add(location);
        panel.add(new JLabel("Participants: "));
        panel.add(participant);
        panel.add(new JLabel("Start time: "));
        panel.add(startTime);
        panel.add(new JLabel("End time: "));
        panel.add(endTime);
        panel.add(new JLabel("Description: "));
        panel.add(description);
        panel.add(createButton);

        // Add the panel to the frame
        createEventDialog.add(panel);

        // Set frame properties
        createEventDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        createEventDialog.setSize(400, 800);
        createEventDialog.setVisible(true);
    }

    // After clicking modify an event button
    public static void updateEvent() {
        JDialog updateEventDialog = new JDialog();
        updateEventDialog.setTitle("Update an event");

        // Create JTextField components for name and password

        // Create JButton to submit the input
        JButton updateButton = new JButton("Update");

        // Create ActionListener for the submit button
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Add components to the panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JPanel event1 = new JPanel();
        JPanel event2 = new JPanel();

        event1.add(new JLabel("Interim event 1"));
        event2.add(new JLabel("Interim event 2"));

        listPanel.add(event1);
        listPanel.add(event2);

        JScrollPane scrollPanel = new JScrollPane(listPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Add the scrollPanel to the dialogPanel
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.add(scrollPanel, BorderLayout.CENTER);

        // Add the updateButton to the dialogPanel
        dialogPanel.add(updateButton, BorderLayout.SOUTH);

        // Add the dialogPanel to the updateEventDialog
        updateEventDialog.add(dialogPanel);

        // Add the panel to the frame

        // Set frame properties
        updateEventDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        updateEventDialog.setSize(400, 800);
        updateEventDialog.setVisible(true);
    }

    // After clicking delete an event button
    public static void deleteEvent() {
        JDialog deleteEventDialog = new JDialog();
        deleteEventDialog.setTitle("Delete an event");

        // Create JTextField components for name and password

        // Create JButton to submit the input
        JButton deleteButton = new JButton("Delete");
        // Create ActionListener for the submit button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Add components to the panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JPanel event1 = new JPanel();
        JPanel event2 = new JPanel();

        event1.add(new JLabel("Interim event 1"));
        event2.add(new JLabel("Interim event 2"));

        listPanel.add(event1);
        listPanel.add(event2);

        JScrollPane scrollPanel = new JScrollPane(listPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Add the scrollPanel to the dialogPanel
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.add(scrollPanel, BorderLayout.CENTER);

        // Add the updateButton to the dialogPanel
        dialogPanel.add(deleteButton, BorderLayout.SOUTH);

        // Add the dialogPanel to the updateEventDialog
        deleteEventDialog.add(dialogPanel);

        // Add the panel to the frame

        // Set frame properties
        deleteEventDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        deleteEventDialog.setSize(400, 800);
        deleteEventDialog.setVisible(true);

        // If the user is the creator, delete the event itself

        // Else, delete the user from invited people list
    }

    public static void eventList() {
        JDialog viewEventsDialog = new JDialog();
        viewEventsDialog.setTitle("Search events");

        // Create JTextField components for name and password
        JTextField title = new JTextField(20);
        JTextField participants = new JTextField(20);
        JTextField location = new JTextField(20);
        JTextField startDate = new JTextField(20);
        JTextField endDate = new JTextField(20);

        // Create JButton to submit the input
        JButton searchButton = new JButton("Search");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Create ActionListener for the submit button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JDialog events = new JDialog();
                events.setLayout(new BorderLayout());
                DefaultListModel<String> eventListModel = new DefaultListModel<>();

                String titleCreated = title.getText();
                String participantsCreated = participants.getText();
                String locationCreated = location.getText();
                String startDateCreated = startDate.getText();
                String endDateCreated = endDate.getText();
                System.out.println(String.format("titleCreated : %s", titleCreated));
                System.out.println(String.format("locationCreated : %s", locationCreated));
                System.out.println(String.format("participantsCreated : %s", participantsCreated));

                String SQL_SELECT = "SELECT * FROM events WHERE start_time BETWEEN ? AND ?"
                        + converter(titleCreated, participantsCreated, locationCreated);

                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
                        "dob", "dobstudio");
                        PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
                    System.out.println(preparedStatement);
                    preparedStatement.setTimestamp(1,
                            new java.sql.Timestamp(dateFormat.parse(startDateCreated).getTime()));
                    preparedStatement.setTimestamp(2,
                            new java.sql.Timestamp(
                                    dateFormat.parse(endDateCreated).getTime() + 24 * 60 * 60 * 1000 - 1)); // 23시
                    // 59분
                    // 59초
                    System.out.println(preparedStatement);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    // Redundant name-passwd pair will be denied

                    if (resultSet.next()) {
                        // 여기에서 곧바로 이어서.
                        Timestamp startTime = resultSet.getTimestamp("start_time");
                        Timestamp endTime = resultSet.getTimestamp("end_time");
                        String eventTitle = resultSet.getString("title");

                        // 날짜, 시간 정보 추출
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy년 MM월 dd일");
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

                        String itemText = "날짜: " +
                                dateFormatter.format(startTime) +
                                "부터 " +
                                dateFormatter.format(endTime) +
                                "까지\n" +
                                "시간: " +
                                timeFormatter.format(startTime) +
                                "부터 " +
                                timeFormatter.format(endTime) +
                                "까지\n" +
                                "제목: " + eventTitle;

                        eventListModel.addElement(itemText);
                        eventListModel.addElement("separator");
                    }

                    JList<String> eventList = new JList<>(eventListModel);
                    JScrollPane scrollPane = new JScrollPane(eventList);
                    events.add(scrollPane);
                    events.setVisible(true);
                    events.setSize(800, 800);

                    events.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                } catch (SQLException ee) {
                    System.out.print(ee.getMessage());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new GridLayout(7, 2));

        // Add components to the panel
        panel.add(new JLabel("title: "));
        panel.add(title);
        panel.add(new JLabel("participants: "));
        panel.add(participants);

        panel.add(new JLabel("location:  "));
        panel.add(location);
        panel.add(new JLabel("From (date):  "));
        panel.add(startDate);
        panel.add(new JLabel("To (date):  "));
        panel.add(endDate);
        // Add the JDatePicker to the panel
        panel.add(searchButton);

        // Add the panel to the frame
        viewEventsDialog.add(panel);

        // Set frame properties
        viewEventsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        viewEventsDialog.setSize(400, 800);
        viewEventsDialog.setVisible(true);
    }

    // After clicking notification button
    public static void notification() {
        JDialog notificationDialog = new JDialog();
        notificationDialog.setTitle("Notification");

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new GridLayout(7, 2));

        // Add the panel to the frame
        notificationDialog.add(panel);

        // Set frame properties
        notificationDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        notificationDialog.setSize(400, 800);
        notificationDialog.setVisible(true);
    }

    // For changing mode between monthly/weekly
    public static void modeChange(int flag) {
        if (flag == 3) { // Monthly
            System.out.println("KEKE");
            // frame2.dispose();
            new DisplayCalendarWeekly();
        } else {
            // frame3.dispose();
            DisplayCalendarMonthly tmp1 = new DisplayCalendarMonthly();
        }
    }

    // After clicking create an user button
    public static void createUser() {
        JDialog createUserDialog = new JDialog();
        createUserDialog.setTitle("Create an account");

        // Create JTextField components for name and password
        JTextField name = new JTextField(20);
        JPasswordField passwd = new JPasswordField(20);
        JTextField phone = new JTextField(20);
        JTextField email = new JTextField(20);

        // Create JButton to submit the input
        JButton createButton = new JButton("Create");

        // Create ActionListener for the submit button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameCreated = name.getText();
                String passwdCreated = new String(passwd.getPassword());
                String phoneCreated = phone.getText();
                String emailCreated = email.getText();

                String SQL_INSERT = String.format(
                        "INSERT INTO users (name, password, phone, email) VALUES ('%s', '%s', '%s', '%s');",
                        nameCreated,
                        passwdCreated, phoneCreated, emailCreated);

                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
                        "dob", "dobstudio");
                        PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {
                    System.out.println(preparedStatement);
                    Integer rowInserted = preparedStatement.executeUpdate();
                    System.out.println(rowInserted);
                    // Redundant name-passwd pair will be denied

                    if (rowInserted > 0) {
                        System.out.println("User created successfully");
                        createUserDialog.dispose();
                    }

                } catch (SQLException ee) {
                    System.out.print(ee.getMessage());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new GridLayout(5, 2));

        // Add components to the panel
        panel.add(new JLabel("Name: "));
        panel.add(name);
        panel.add(new JLabel("Password: "));
        panel.add(passwd);
        panel.add(new JLabel("Phone:  "));
        panel.add(phone);
        panel.add(new JLabel("Email: "));
        panel.add(email);
        panel.add(createButton);

        // Add the panel to the frame
        createUserDialog.add(panel);

        // Set frame properties
        createUserDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        createUserDialog.setSize(400, 800);
        createUserDialog.setVisible(true);
    }

    public static void updateUser(String myName) {

        AtomicBoolean flag = new AtomicBoolean(false);
        AtomicReference<String> password_res = new AtomicReference<>("initialValue");

        JLabel verifyResult = new JLabel("Access to database: ...");
        JDialog updateUserDialog = new JDialog();
        updateUserDialog.setTitle("Update an account");

        // Create JTextField components for name and password
        JPasswordField passwd_verify = new JPasswordField(20);

        JTextField name = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        JTextField phone = new JTextField(20);
        JTextField email = new JTextField(20);

        // Create JButton to submit the input
        JButton updateButton = new JButton("Create");
        JButton verifyButton = new JButton("Verify");

        // Create ActionListener for the submit button
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (flag.get()) {
                    String nameCreated = name.getText();
                    String passwdCreated = new String(password.getPassword());
                    String phoneCreated = phone.getText();
                    String emailCreated = email.getText();

                    String SQL_UPDATE = String.format(
                            /// SQL query 바꾸고, and flag==true로 확인 후 업데이트
                            "UPDATE users SET name='%s', password='%s', phone='%s', email='%s' WHERE name='%s' AND password='%s';",
                            nameCreated,
                            passwdCreated, phoneCreated, emailCreated, myName, password_res.get());

                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
                            "dob", "dobstudio");
                            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {
                        System.out.println(preparedStatement);
                        Integer rowAffected = preparedStatement.executeUpdate();
                        System.out.println(rowAffected);

                        if (rowAffected > 0) {
                            JOptionPane.showMessageDialog(null, "User information has been updated successfully!",
                                    "User Update", JOptionPane.NO_OPTION);
                            updateUserDialog.dispose();
                        }

                        else {
                            JOptionPane.showMessageDialog(null,
                                    "User information has not been updated. Probably your new user information is invalid.",
                                    "Update Fail",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (SQLException ee) {
                        System.out.print(ee.getMessage());
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

                else {
                    JOptionPane.showMessageDialog(null, "User password is not verified.", "Password Not Verified",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passwdToVerify = new String(passwd_verify.getPassword());

                String SQL_SELECT = String.format(
                        "SELECT * FROM users WHERE name='%s' AND password='%s';", myName, passwdToVerify);

                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
                        "dob", "dobstudio");
                        PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        verifyResult.setText("Access to database : Success!");
                        String name_res = resultSet.getString("name");
                        password_res.set(resultSet.getString("password"));
                        String phone_res = resultSet.getString("phone");
                        String email_res = resultSet.getString("email");
                        flag.set(true);

                        name.setText(name_res);
                        password.setText(password_res.get());
                        phone.setText(phone_res);
                        email.setText(email_res);
                    }

                    else {
                        verifyResult.setText("Access to database : Not correct. Try again.");
                        flag.set(true);
                    }

                } catch (SQLException er) {
                    System.out.print(er.getMessage());
                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
        });

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new GridLayout(7, 2));

        // Add components to the panel
        panel.add(new JLabel("Verify your password: "));
        panel.add(passwd_verify);
        panel.add(verifyButton);
        panel.add(verifyResult);

        panel.add(new JLabel("Name: "));
        panel.add(name);
        panel.add(new JLabel("Password: "));
        panel.add(password);
        panel.add(new JLabel("Phone:  "));
        panel.add(phone);
        panel.add(new JLabel("Email: "));
        panel.add(email);
        panel.add(updateButton);

        // Add the panel to the frame
        updateUserDialog.add(panel);

        // Set frame properties
        updateUserDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        updateUserDialog.setSize(600, 800);
        updateUserDialog.setLocationRelativeTo(null);
        updateUserDialog.setVisible(true);

    }

    private static boolean isEventAvailable(Date newStartTime, Date newEndTime) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
                "dob", "dobstudio")) {
            String query = "SELECT * FROM events WHERE " +
                    "(start_time, end_time) OVERLAPS (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, new Timestamp(newStartTime.getTime()));
                preparedStatement.setTimestamp(2, new Timestamp(newEndTime.getTime()));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return !resultSet.next(); // 겹치는 경우가 없으면 사용 가능
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 예외 처리 필요
        }
    }

    private static String converter(String title, String participant, String location) {
        if (title == "") {
            if (participant.equals("")) {

                if (location.equals("")) {
                    return ";";
                } else {
                    return " AND location=" + location + ";";
                }

            } else {
                if (location.equals("")) {
                    return String.format(" AND participants LIKE '%%%s%%'", participant) + ";";
                } else {
                    return String.format(" AND participants LIKE '%%%s%%'", participant) + " AND location=" + location
                            + ";";
                }
            }
        } else {
            if (participant.equals("")) {

                if (location.equals("")) {
                    return String.format(" AND title LIKE '%%%s%%'", title + ";");
                } else {
                    return String.format(" AND title LIKE '%%%s%%'", title) + " AND location=" + location + ";";
                }

            } else {
                if (location.equals("")) {
                    return String.format(" AND title LIKE '%%%s%%'", title)
                            + String.format(" AND participants LIKE '%%%s%%'", participant) + ";";
                } else {
                    return String.format(" AND title LIKE '%%%s%%'", title)
                            + String.format(" AND participants LIKE '%%%s%%'", participant)
                            + " AND location=" + location + ";";
                }
            }
        }
    }
}