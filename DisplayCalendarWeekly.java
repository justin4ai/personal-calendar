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
    int dd;
    int startDay; // 월의 시작 요일
    int lastDate; // 월의 마지막 날

    public DisplayCalendarWeekly() {

        frame3 = new JFrame("Family Calendar by Justin (Weekly Mode)");
        frame3.setTitle("Weekly Calendar");
        frame3.setSize(1600, 800);
        frame3.setLayout(new BorderLayout());
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTable weeklyTable = new JTable();
        JPanel p_north = new JPanel();
        JPanel p_south = new JPanel();

        JPanel weekPanel = new JPanel(new BorderLayout());

        weeklyTable.setGridColor(Color.LIGHT_GRAY);

        DefaultTableModel weeklyModel = new DefaultTableModel(
                new Object[] { "Date/day", "Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6" }, 0);
        weeklyTable.setModel(weeklyModel);

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
        bt_notification = new JButton("Notifications");
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
                System.out.println("Before disposing");
                frame2.dispose();
                System.out.println("After disposing");
                Helpers.modeChange(3);
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

        p_north.add(prevWeekButton);
        p_north.add(nextWeekButton);
        p_south.add(bt_create_event);
        p_north.add(bt_RVSP);
        p_north.add(bt_notification);
        p_south.add(bt_update_event);
        p_south.add(bt_delete_event);
        p_south.add(bt_eventList);
        p_south.add(bt_modeChange);
        p_south.add(bt_createUser);

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
        System.out.println("Inside the method!");
        DefaultTableModel weeklyModel = (DefaultTableModel) weeklyTable.getModel();
        weeklyModel.setRowCount(0);

        int rowHeight = 100;
        weeklyTable.setRowHeight(rowHeight);

        Calendar currentCalendar = calendar.getInstance();
        currentCalendar.setTime(startDate);

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

    }
}
