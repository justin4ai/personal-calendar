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
import java.util.Map;
import java.util.List;
import java.sql.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;

public class Helpers {

    public static String dbURL = "jdbc:postgresql://127.0.0.1:5432/dob";
    public static String dbUser = "dob";
    public static String dbPasswd = "dobstudio";
    public static Map<String, List<String>> eventDataMap = new HashMap<>();

    // After clicking create an event button
    public static void createEvent() {
        JDialog createEventDialog = new JDialog();
        createEventDialog.setTitle("Create an event");

        // Create JTextField components for name and password
        JTextField title = new JTextField(30);
        JTextField startTime = new JTextField(30);
        // startTime.setText("2023-12-08 03:00:00");
        JTextField location = new JTextField(30);
        JTextField endTime = new JTextField(30);
        // endTime.setText("2023-12-09 03:00:00");
        JTextField participant = new JTextField(50);
        JTextField description = new JTextField(100);
        JTextField timeframe = new JTextField(2);
        JTextField interval = new JTextField(2);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        title.setText("TODAY_" + (System.currentTimeMillis() / (60 * 1000)));
        location.setText("home");
        participant.setText("Justin, YH");

        // Set the start time to the current time
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, 1);
        calendar1.add(Calendar.MINUTE, 2);
        startTime.setText(dateFormat.format(calendar1.getTime()));

        // Set the end time to 2 hours later than the current time
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, 1);
        calendar2.add(Calendar.MINUTE, 2);
        endTime.setText(dateFormat.format(calendar2.getTime()));

        // Set default values for time frame and interval
        timeframe.setText("60");
        interval.setText("15");
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
                String timeframeCreated = timeframe.getText();
                String intervalCreated = interval.getText();
                int creatorId = PersonalCalendar.userID;

                try {
                    if (isEventAvailable(new java.sql.Timestamp(dateFormat.parse(startCreated).getTime()),
                            new java.sql.Timestamp(dateFormat.parse(endCreated).getTime()))) {
                        String SQL_INSERT1 = "INSERT INTO events (title, start_time, end_time, description, creator_id, location, participants) VALUES (?, ?, ?, ?, ?, ?, ?);";
                        String SQL_INSERT2 = "INSERT INTO reminderinfo (event_id, time_frame, interval_q) VALUES (?, ?, ?::interval_enum);"; // for
                        // reminder_info

                        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPasswd);
                                PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT1,
                                        Statement.RETURN_GENERATED_KEYS);
                                PreparedStatement preparedStatement2 = conn.prepareStatement(SQL_INSERT2);) {

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
                            // ResultSet res = preparedStatement.executeQuery();
                            preparedStatement.executeUpdate();
                            ResultSet res = preparedStatement.getGeneratedKeys();

                            if (res.next()) {

                                JOptionPane.showMessageDialog(null, "Event has been well created!", "Event created",
                                        JOptionPane.INFORMATION_MESSAGE);
                                System.out.println(String.format("res is %s", res.getInt(1)));
                                int eventID = res.getInt(1);
                                preparedStatement2.setInt(1, eventID);
                                preparedStatement2.setInt(2, Integer.parseInt(timeframeCreated));
                                preparedStatement2.setString(3, intervalCreated);

                                int rowInsertion = preparedStatement2.executeUpdate();
                                System.out.println(rowInsertion);
                                if (rowInsertion > 0) {
                                    System.out.println("Reminderinfo well inserted");
                                }

                                /////////////////////
                                long startTime = dateFormat.parse(startCreated).getTime();

                                long timeFrameInMillis = Integer.parseInt(timeframeCreated) * 60 * 1000; // time_frame을
                                                                                                         // 밀리초로
                                                                                                         // 변환
                                long intervalInMillis = Integer.parseInt(intervalCreated) * 60 * 1000; // interval을
                                                                                                       // 밀리초로
                                                                                                       // 변환

                                long endTime = startTime - timeFrameInMillis; // 시작 시간에서 time_frame 전의 시간

                                // 현재 시각 가져오기
                                long currentMillis = System.currentTimeMillis();

                                // reminder 생성 및 데이터베이스에 추가

                                System.out.println(String.format("endTime : %s", endTime));
                                System.out.println(String.format("startTime : %s", startTime));
                                while (endTime < startTime) {
                                    System.out.println(String.format("endTime : %s", endTime));
                                    System.out.println(String.format("startTime : %s", startTime));
                                    // reminder 생성
                                    if (endTime > currentMillis) { // reminder에 넣을 시간이 지금보다 나중일 때만
                                        Timestamp reminderTime = new Timestamp(endTime);

                                        // 데이터베이스에 추가
                                        String SQL_INSERT3 = "INSERT INTO reminders (event_id, time_to_send) VALUES (?, ?);"; // for
                                                                                                                              // reminders

                                        try (Connection conn2 = DriverManager.getConnection(dbURL, dbUser,
                                                dbPasswd);
                                                PreparedStatement preparedStatement3 = conn2
                                                        .prepareStatement(SQL_INSERT3);) {

                                            preparedStatement3.setInt(1, eventID);
                                            preparedStatement3.setTimestamp(2, reminderTime);

                                            preparedStatement3.executeUpdate();
                                        }

                                    }

                                    // 다음 reminder의 시간으로 이동
                                    endTime += intervalInMillis;
                                }
                                /////////////////////

                                createEventDialog.dispose();
                            }

                            else {
                                JOptionPane.showMessageDialog(null, "Event has not been created!",
                                        "Event not created",
                                        JOptionPane.WARNING_MESSAGE);
                            }

                        } catch (SQLException ee) {
                            System.out.print(ee.getMessage());
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }

                    else {
                        JOptionPane.showMessageDialog(null, "The event time overlaps pre-existing events.",
                                "Availability Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ParseException eee) {
                    System.out.println(eee.getMessage());
                }

            }
        });

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new GridLayout(10, 2));

        // Add components to the panel
        panel.add(new JLabel("Title: "));
        panel.add(title);
        panel.add(new JLabel("Location: "));
        panel.add(location);
        panel.add(new JLabel("<html>Participants: (ex. Justin, YH) </html>"));
        panel.add(participant);
        panel.add(new JLabel("<html>Start time:<br>(ex. 2023-12-25 16:00:00) </html>"));
        panel.add(startTime);
        panel.add(new JLabel("<html>End time<br> (ex. 2023-12-25 17:00:00): </html>"));
        panel.add(endTime);
        panel.add(new JLabel("Description: "));
        panel.add(description);

        panel.add(new JLabel("Reminder settings are below"));
        panel.add(new JLabel(""));

        panel.add(new JLabel("Time Frame (in minutes) : "));
        panel.add(timeframe);
        panel.add(new JLabel("<html>Interval (in minutes)<br>0 means no reminder.</html>"));
        panel.add(interval);

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
        EventListApp app = new EventListApp();
        app.setVisible(true);
    }

    public static void eventList() {
        SwingUtilities.invokeLater(() -> {
            JDialog viewEventsDialog = new JDialog();
            viewEventsDialog.setTitle("Search events");

            // Create JTextField components for name and password
            JTextField title = new JTextField(30);
            JTextField participants = new JTextField(50);
            JTextField location = new JTextField(30);
            JTextField startDate = new JTextField(20);
            JTextField endDate = new JTextField(20);

            title.setText("TODAY");
            participants.setText("Justin");
            location.setText("home");

            // Create JButton to submit the input
            JButton searchButton = new JButton("Search");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar calendar = Calendar.getInstance();
            startDate.setText(dateFormat.format(calendar.getTime()));

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DAY_OF_MONTH, 1);
            endDate.setText(dateFormat.format(calendar2.getTime()));

            // Create ActionListener for the submit button
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JDialog events = new JDialog();
                    events.setLayout(new BorderLayout());
                    // DefaultListModel<String> eventListModel = new DefaultListModel<>();
                    DefaultTableModel tableModel = new DefaultTableModel();
                    tableModel.addColumn("날짜");
                    tableModel.addColumn("시간");
                    tableModel.addColumn("제목");

                    JTable eventTable = new JTable(tableModel);

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

                    try (Connection conn = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);
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

                        while (resultSet.next()) {
                            // 여기에서 곧바로 이어서.
                            Timestamp startTime = resultSet.getTimestamp("start_time");
                            Timestamp endTime = resultSet.getTimestamp("end_time");
                            String eventTitle = resultSet.getString("title");
                            System.out.println(startTime);
                            System.out.println(endTime);
                            System.out.println(eventTitle);

                            // 날짜, 시간 정보 추출
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy년 MM월 dd일");
                            // SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

                            Vector<String> rowData = new Vector<>();
                            rowData.add(dateFormatter.format(startTime) + "부터 " + dateFormatter.format(endTime) + "까지");
                            // rowData.add(timeFormatter.format(startTime) + "부터 " +
                            // timeFormatter.format(endTime) + "까지");
                            rowData.add(eventTitle);

                            tableModel.addRow(rowData);

                            String itemText = "날짜: " +
                                    dateFormatter.format(startTime) +
                                    "부터 " +
                                    dateFormatter.format(endTime) +
                                    "까지\n" +
                                    "시간: "; // +
                            // timeFormatter.format(startTime) +
                            // "부터 " +
                            // timeFormatter.format(endTime) +
                            // "까지\n" +
                            // "제목: " + eventTitle;

                            // tableModel.addRow(rowData);
                            // eventListModel.addElement(itemText);
                            // eventListModel.addElement("separator");
                        }

                        // JList<String> eventList = new JList<>(eventListModel);
                        // JScrollPane scrollPane = new JScrollPane(eventList);
                        JScrollPane scrollPane = new JScrollPane(eventTable);
                        events.add(scrollPane);
                        events.setVisible(true);
                        events.setSize(800, 800);

                        events.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        });
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

                try (Connection conn = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);
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

                    try (Connection conn = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);
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

                try (Connection conn = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);
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
        try (Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd)) {
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
                    return " AND location=" + "'" + location + "'" + ";";
                }

            } else {
                if (location.equals("")) {
                    return String.format(" AND participants LIKE '%%%s%%'", participant) + ";";
                } else {
                    return String.format(" AND participants LIKE '%%%s%%'", participant) + " AND location=" + "'"
                            + location + "'"
                            + ";";
                }
            }
        } else {
            if (participant.equals("")) {

                if (location.equals("")) {
                    return String.format(" AND title LIKE '%%%s%%'", title + ";");
                } else {
                    return String.format(" AND title LIKE '%%%s%%'", title) + " AND location=" + "'" + location + "'"
                            + ";";
                }

            } else {
                if (location.equals("")) {
                    return String.format(" AND title LIKE '%%%s%%'", title)
                            + String.format(" AND participants LIKE '%%%s%%'", participant) + ";";
                } else {
                    return String.format(" AND title LIKE '%%%s%%'", title)
                            + String.format(" AND participants LIKE '%%%s%%'", participant)
                            + " AND location=" + "'" + location + "'" + ";";
                }
            }
        }
    }

    public static void clearButtons(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                panel.remove(component);
            }
        }
    }
}