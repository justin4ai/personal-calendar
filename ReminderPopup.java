import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderPopup {

    public static void checkReminders() {
        System.out.println("Executed!");

        try (Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd)) {
            // 현재 시간 가져오기
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date currentDate = new Date();
            String currentDateTimeString = dateFormat.format(currentDate);

            // 현재 시간과 일치하는 이벤트 찾기
            String sql = "SELECT e.title, e.location, e.participants, e.start_time, e.end_time " +
                    "FROM events e " +
                    "INNER JOIN reminders r ON e.event_id = r.event_id " +
                    "WHERE e.creator_id = ? AND r.time_to_send = ?::timestamp";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, PersonalCalendar.userID);
                preparedStatement.setString(2, currentDateTimeString);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        System.out.println("Gotcha");
                        String title = resultSet.getString("title");
                        String location = resultSet.getString("location");
                        String participants = resultSet.getString("participants");
                        Timestamp startTime = resultSet.getTimestamp("start_time");
                        Timestamp endTime = resultSet.getTimestamp("end_time");

                        // TODO: 팝업 창 표시 논리 구현
                        showPopup(title, location, participants, startTime, endTime);
                        // 이제 지워야함
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO: 팝업 창 표시 메서드 구현
    private static void showPopup(String title, String location, String participants, Timestamp startTime,
            Timestamp endTime) {
        // 여기에 팝업 창을 표시하는 논리를 추가하세요.
        // 예를 들어 JOptionPane 또는 다른 GUI 팝업 컴포넌트를 사용하여 정보를 표시할 수 있습니다.

        JDialog popup = new JDialog();

        String text = String.format(
                "<html>The following event is upcomming!<br>Title : %s<br>Location : %s<br>Participants : %s<br>Start Time : %s<br>End Time : %s</html>",
                title, location, participants, startTime, endTime);

        JPanel panel = new JPanel();
        panel.add(new JLabel(text));

        popup.add(panel);
        popup.pack();
        popup.setVisible(true);
        popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}