import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
                    removeAll();
                    while (resultSet.next()) {
                        System.out.println("event fetched!");
                        // 일정 정보 가져오기
                        String title = resultSet.getString("title");
                        String location = resultSet.getString("location");
                        String description = resultSet.getString("description");
                        Date startTime = resultSet.getTimestamp("start_time");

                        // JButton 생성
                        JButton eventButton = new JButton(
                                String.format("%s - %s", title, dateFormat.format(startTime)));
                        eventButton.setAlignmentX(Component.LEFT_ALIGNMENT);

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
}