import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.Locale;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayCalendarWeekly extends JFrame {
    String[] dayAr = { "Sun", "Mon", "Tue", "Wen", "Thur", "Fri", "Sat" };
    DateBox[] dateBoxAr = new DateBox[dayAr.length * 6];
    JPanel p_north;
    JPanel p_south;
    JButton bt_prev;
    JLabel lb_title;
    JButton bt_next;
    JPanel p_center; // 날짜 박스 처리할 영역
    JButton bt_create_event;
    JButton bt_RVSP;
    JButton bt_update_event;
    JButton bt_delete_event;
    JButton bt_eventList;
    JButton bt_modeChange;
    JButton bt_notification;
    JButton bt_createUser;
    JPanel dayPanel;
    JLabel weekLabel;
    JFrame frame2;
    JFrame frame3;
    String month;
    JButton bt_updateUser;
    JTable weeklyTable;

    JPanel calendarPanel;
    Calendar cal; // 날짜 객체
    int currentWeek;
    int currentDay;
    int dayOfMonth;
    int today;
    String cellValue;
    Calendar calendar;

    int yy; // 기준점이 되는 년도
    int mm; // 기준점이 되는 월
    int ww;
    int titleYear;
    int titleMonth;
    int dd;
    int startDay; // 월의 시작 요일
    int lastDate; // 월의 마지막 날
    JLabel yearMonthLabel = new JLabel();

    public DisplayCalendarWeekly() {

        frame3 = new JFrame("Family Calendar by Justin (Weekly Mode)");
        frame3.setTitle("Weekly Calendar");
        frame3.setSize(1600, 800);
        frame3.setLayout(new BorderLayout());
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        weeklyTable = new JTable();
        // JTable weeklyTable = new JTable();
        JPanel p_north = new JPanel();
        JPanel p_south = new JPanel();

        JPanel weekPanel = new JPanel(new BorderLayout());

        weeklyTable.setGridColor(Color.LIGHT_GRAY);

        // weeklyTable의 컬럼 구성 변경
        DefaultTableModel weeklyModel = new DefaultTableModel(
                new Object[] { "Sun", "Mon", "Tue", "Wen", "Thur", "Fri", "Sat" }, 0);
        weeklyTable.setModel(weeklyModel);

        // yearMonthLabel 초기화
        yearMonthLabel.setText(titleYear + "-" + titleMonth);

        cal = Calendar.getInstance();
        // currentWeek = calendar.get(Calendar.WEEK_OF_MONTH);
        currentDay = cal.get(Calendar.DAY_OF_MONTH);

        // JPanel mainPanel = new JPanel(new BorderLayout());

        JButton prevWeekButton = new JButton("Previous week");
        JButton nextWeekButton = new JButton("Next week");

        bt_create_event = new JButton("Create an event");
        bt_update_event = new JButton("Modify an event");
        bt_delete_event = new JButton("Delete an event");
        // bt_RVSP = new JButton("RSVP");
        // bt_notification = new JButton("Notifications");
        bt_eventList = new JButton("View event list");
        bt_createUser = new JButton("Create user");
        bt_modeChange = new JButton("Mode change");
        bt_updateUser = new JButton("Update user");

        // calendarPanel = new JPanel(new GridLayout(0, 1));

        prevWeekButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // currentWeek--;
                cal.add(Calendar.DATE, -7);
                updateWeeklyCalendar(weeklyTable, cal.getTime());
            }
        });
        nextWeekButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentWeek++;
                cal.add(Calendar.DATE, 7);
                updateWeeklyCalendar(weeklyTable, cal.getTime());
            }
        });

        // Create an event
        bt_create_event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.createEvent();
            }
        });

        // Update an event
        bt_update_event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.updateEvent();
            }
        });

        // Delete an event
        bt_delete_event.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.deleteEvent();
            }
        });

        // Notification
        // bt_notification.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // Helpers.notification();
        // }
        // });

        // RVSP
        // bt_RVSP.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // sendRSVP();
        // }
        // });

        // Event list
        bt_eventList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.eventList();
            }
        });

        // Mode change
        bt_modeChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.modeChange(frame3);
            }
        });

        // User create
        bt_createUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.createUser();
            }
        });

        bt_updateUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.updateUser(PersonalCalendar.name);
            }
        });

        // System.out.println(String.format("Cal.getTime : %s", cal.getTime()));
        updateWeeklyCalendar(weeklyTable, cal.getTime());

        weeklyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = weeklyTable.rowAtPoint(evt.getPoint());
                int col = weeklyTable.columnAtPoint(evt.getPoint());

                // 일정을 클릭하면 상세 정보를 표시
                if (weeklyTable.getValueAt(row, col) != null) {
                    showEventDetails(row, col);
                }
            }
        });

        p_north.add(prevWeekButton);

        yearMonthLabel.setText("" + titleYear + "-" + titleMonth);

        p_north.add(yearMonthLabel);

        p_north.add(nextWeekButton);
        p_south.add(bt_create_event);
        // p_north.add(bt_RVSP);
        // p_north.add(bt_notification);
        p_south.add(bt_update_event);
        p_south.add(bt_delete_event);
        p_south.add(bt_eventList);
        p_south.add(bt_modeChange);
        p_south.add(bt_createUser);
        p_south.add(bt_updateUser);

        // JScrollPane scrollPane = new JScrollPane(calendarPanel);
        // comboBox

        // mainPanel.add(scrollPane, BorderLayout.CENTER);

        weekPanel.add(p_north, BorderLayout.NORTH);
        weekPanel.add(new JScrollPane(weeklyTable), BorderLayout.CENTER); // What is JScrollPane exactly for?
        weekPanel.add(p_south, BorderLayout.SOUTH);

        frame3.add(weekPanel, BorderLayout.CENTER);
        frame3.setVisible(true);

        // // Update an event
        // bt_update_event.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // updateEvent();
        // }
        // });

        // // Delete an event
        // bt_delete_event.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // deleteEvent();
        // }
        // });

        // // RVSP
        // bt_RVSP.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // sendRSVP();
        // }
        // });

        // // Event list
        // bt_eventList.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // eventList();
        // }
        // });

        // // Mode change
        // bt_modeChange.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // modeChange(2);
        // }
        // });
        // }
    }

    public void updateWeeklyCalendar(JTable weeklyTable, Date startDate) {
        DefaultTableModel weeklyModel = (DefaultTableModel) weeklyTable.getModel();
        weeklyModel.setRowCount(0);

        int rowHeight = 100;
        weeklyTable.setRowHeight(rowHeight);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(startDate);

        titleYear = currentCalendar.get(Calendar.YEAR);
        titleMonth = currentCalendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 1을 더해줍니다.

        for (int i = 0; i < 7; i++) {
            int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);

            String dayName = currentCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            String cellValue = dayName + " " + dayOfMonth;

            if (dayOfMonth == today
                    && currentCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                cellValue = "Today: " + cellValue;
            }

            weeklyModel.addRow(new Object[] { cellValue });
            currentCalendar.add(Calendar.DATE, 1);
        }

        yearMonthLabel.setText(titleYear + "-" + titleMonth); // 년월 정보 업데이트
    }

    private void showEventDetails(int row, int col) {
        // 해당 셀의 일정을 불러옴
        String day = weeklyTable.getValueAt(row, col).toString();

        // 데이터베이스에서 해당 날짜의 일정을 불러옴
        List<Event> events = loadEventsFromDatabase(titleYear, titleMonth, day);

        // 상세 정보를 표시할 다이얼로그 생성
        JFrame detailsFrame = new JFrame("Event Details");
        detailsFrame.setSize(400, 300);
        detailsFrame.setLayout(new BorderLayout());

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        // 불러온 일정 정보를 텍스트 영역에 추가
        for (Event event : events) {
            detailsArea.append("Title: " + event.getTitle());
        }

        detailsFrame.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        detailsFrame.setVisible(true);
    }

    // 데이터베이스에서 해당 날짜의 일정을 불러옴
    private List<Event> loadEventsFromDatabase(int year, int month, String day) {
        List<Event> events = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(Helpers.dbURL, Helpers.dbUser, Helpers.dbPasswd);

            String sql = "SELECT * FROM events " +
                    "WHERE EXTRACT(YEAR FROM start_time) = ? " +
                    "AND EXTRACT(MONTH FROM start_time) = ? " +
                    "AND EXTRACT(DAY FROM start_time) = ? AND creator_id = ?";

            // SQL 쿼리 작성 (예시: events 테이블에서 해당 날짜의 일정을 불러오기)
            // String sql = "SELECT * FROM events WHERE DATE(start_time) = ? AND creator_id
            // = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, year);
                preparedStatement.setInt(2, month);
                preparedStatement.setInt(3, Integer.parseInt(day.split(" ")[1]));

                preparedStatement.setInt(4, PersonalCalendar.userID);

                // 쿼리 실행
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {

                        int eventID = resultSet.getInt("event_id");
                        String title = resultSet.getString("title");
                        String location = resultSet.getString("location");
                        String description = resultSet.getString("description");
                        Timestamp startTime = resultSet.getTimestamp("start_time");
                        Timestamp endTime = resultSet.getTimestamp("end_time");
                        String participants = resultSet.getString("participants");

                        events.add(new Event(eventID, title, startTime, endTime, location, participants, description));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

}