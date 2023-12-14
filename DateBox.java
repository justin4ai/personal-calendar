import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.*;
import java.text.SimpleDateFormat;

public class DateBox extends JPanel {
    String day;
    Color color;
    int width;
    int height;
    String month;
    Boolean flag;
    String year;

    public DateBox(String day, Color color, int width, int height) {
        this.day = day;
        this.color = color;
        this.width = width;
        this.height = height;
        this.month = "";
        this.flag = false;
        this.year = "";
        setPreferredSize(new Dimension(width, height));

        // if ((flag == true) && (day != "")) {
        // System.out.println(String.format("Datebox for %s-%s-%s", year, month, day));
        // fetchEvents();
        // }

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(color);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.yellow);
        g.drawString(day, 10, 20);

        if ((flag == true) && (day != "")) {
            System.out.println(String.format("Datebox for %s-%s-%s", year, month, day));
            // Helpers.clearButtons(this);
            fetchEvents();
        }
    }

    private void fetchEvents() {
        // JDBC 연결 정보 설정

        try (Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd)) {
            // 날짜 형식 설정
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            System.out.println("fetch Events");
            // 주어진 년월일에 해당하는 일정을 가져오는 SQL 쿼리
            String sql = "SELECT * FROM events " +
                    "WHERE EXTRACT(YEAR FROM start_time) = ? " +
                    "AND EXTRACT(MONTH FROM start_time) = ? " +
                    "AND EXTRACT(DAY FROM start_time) = ? AND creator_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(year));
                preparedStatement.setInt(2, Integer.parseInt(month));
                preparedStatement.setInt(3, Integer.parseInt(day));
                preparedStatement.setInt(4, PersonalCalendar.userID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Helpers.clearButtons(this);
                    System.out.println(this);
                    while (resultSet.next()) {
                        System.out.println("event fetched!");
                        // 일정 정보 가져오기
                        int eventID = resultSet.getInt("event_id");
                        String title = resultSet.getString("title");
                        String location = resultSet.getString("location");
                        String description = resultSet.getString("description");
                        Date startTime = resultSet.getTimestamp("start_time");
                        Date endTime = resultSet.getTimestamp("end_time");
                        String participants = resultSet.getString("participants");

                        // JButton 생성
                        JButton eventButton = new JButton(
                                String.format("%s", title));
                        eventButton.setAlignmentX(Component.LEFT_ALIGNMENT);

                        eventButton.addActionListener(e -> {

                            showEventDetails(eventID, title, startTime, endTime, location, participants, description);
                        });

                        // JButton을 DateBox에 추가
                        add(eventButton);
                    }
                    // revalidate();
                    // repaint();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showEventDetails(int event_id, String title, Date startTime, Date endTime, String location,
            String participants, String description) {
        // 상세 정보를 표시하는 다이얼로그 생성
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Event Details");

        // 각 정보에 대한 JTextField 생성 및 기본값 설정
        JTextField titleField = new JTextField(title);
        JTextField startTimeField = new JTextField(formatTimestamp(startTime));
        JTextField endTimeField = new JTextField(formatTimestamp(endTime));
        JTextField locationField = new JTextField(location);
        JTextField participantsField = new JTextField(participants);
        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setRows(5);
        descriptionArea.setColumns(20);

        // 수정 버튼 생성
        JButton modifyButton = new JButton("");
        modifyButton.setIcon(new ImageIcon(getResizedImage(Helpers.modifyImg, 50, 50)));
        modifyButton.addActionListener(modifyEvent -> {
            // 수정 여부를 묻는 다이얼로그 생성
            int response = JOptionPane.showConfirmDialog(detailsDialog,
                    "Are you sure you want to modify the event?", "Modify Event",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {

                try {
                    Connection connection = DriverManager.getConnection(
                            Helpers.dbURL,
                            Helpers.dbUser,
                            Helpers.dbPasswd);

                    String SQL_UPDATE = "UPDATE events SET title = ?, location = ?, participants = ?, description = ?, start_time = ?, end_time = ? WHERE event_id = ?";

                    PreparedStatement preparedStatement = connection
                            .prepareStatement(SQL_UPDATE);

                    preparedStatement.setString(1, titleField.getText());
                    preparedStatement.setString(2, locationField.getText());
                    preparedStatement.setString(3, participantsField.getText());
                    preparedStatement.setString(4, descriptionArea.getText());
                    preparedStatement.setString(5, startTimeField.getText());
                    preparedStatement.setString(6, endTimeField.getText());
                    preparedStatement.setInt(7, event_id);

                    int res = preparedStatement.executeUpdate();

                    preparedStatement.close();
                    connection.close();

                    if (res > 0) {
                        JOptionPane.showMessageDialog(null, "Event has been well updated!", "Event Updated",
                                JOptionPane.DEFAULT_OPTION);
                        detailsDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 삭제 버튼 생성
        JButton deleteButton = new JButton();
        deleteButton.setIcon(new ImageIcon(getResizedImage(Helpers.deleteImg, 50, 50)));
        deleteButton.addActionListener(deleteEvent -> {

            int response = JOptionPane.showConfirmDialog(detailsDialog,
                    "Are you sure you want to delete the event?", "Delete Event",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {

                try {
                    Connection connection = DriverManager.getConnection(
                            Helpers.dbURL,
                            Helpers.dbUser,
                            Helpers.dbPasswd);

                    String SQL_DELETE_Rs = "DELETE FROM reminders WHERE event_id = ?";
                    String SQL_DELETE_E = "DELETE FROM events WHERE event_id = ?";
                    String SQL_DELETE_R = "DELETE FROM reminderinfo WHERE event_id = ?";

                    PreparedStatement preparedStatement1 = connection.prepareStatement(SQL_DELETE_Rs);
                    preparedStatement1.setInt(1, event_id);
                    PreparedStatement preparedStatement2 = connection.prepareStatement(SQL_DELETE_E);
                    preparedStatement2.setInt(1, event_id);
                    PreparedStatement preparedStatement3 = connection.prepareStatement(SQL_DELETE_R);
                    preparedStatement3.setInt(1, event_id);

                    int res1 = preparedStatement1.executeUpdate();
                    int res3 = preparedStatement3.executeUpdate();
                    int res2 = preparedStatement2.executeUpdate();

                    preparedStatement1.close();
                    preparedStatement2.close();
                    preparedStatement3.close();
                    connection.close();

                    if ((res1 > 0) && (res2 > 0) && (res3 > 0)) {
                        JOptionPane.showMessageDialog(null, "Event has been well deleted!", "Event Deleted",
                                JOptionPane.DEFAULT_OPTION);
                        detailsDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 패널에 컴포넌트들 추가
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
        detailsPanel.add(modifyButton);
        detailsPanel.add(deleteButton);

        modifyButton.setPreferredSize(new Dimension(50, 50));
        deleteButton.setPreferredSize(new Dimension(50, 50));

        // 다이얼로그에 패널 추가
        detailsDialog.add(detailsPanel);
        detailsDialog.setSize(400, 300);
        detailsDialog.setLocationRelativeTo(null);
        detailsDialog.setVisible(true);
    }

    // Timestamp를 문자열로 형식화하는 메서드
    private String formatTimestamp(Date timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(timestamp);
    }

    private Image getResizedImage(String filePath, int width, int height) {
        try {
            Image originalImage = new ImageIcon(filePath).getImage();
            return originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}