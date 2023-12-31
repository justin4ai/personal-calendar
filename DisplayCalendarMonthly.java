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

public class DisplayCalendarMonthly extends JFrame {

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
    JButton bt_viewEvents;
    JButton bt_createUser;
    JButton bt_updateUser;
    JPanel dayPanel;
    JLabel weekLabel;
    JFrame frame2;
    JFrame frame3;
    String month;
    List<DateBox> dateBoxList;

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

    public DisplayCalendarMonthly() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 1분마다 checkReminders 메서드를 실행
        scheduler.scheduleAtFixedRate(ReminderPopup::checkReminders, 0, 1, TimeUnit.MINUTES);

        JOptionPane.showMessageDialog(null,
                String.format("Hello %s, welcome to Justin's family calendar.", PersonalCalendar.name),
                String.format("Your user id is '%d'", PersonalCalendar.userID), JOptionPane.INFORMATION_MESSAGE);
        frame2 = new JFrame("Family Calendar by Justin");

        // 디자인
        dateBoxList = new ArrayList<>();

        p_north = new JPanel();
        p_south = new JPanel();
        bt_prev = new JButton("previous");
        lb_title = new JLabel("upcomming year", SwingConstants.CENTER);
        bt_next = new JButton("next");

        bt_create_event = new JButton("Create an event");
        bt_update_event = new JButton("Modify an event");
        bt_delete_event = new JButton("Delete an event");
        // bt_RVSP = new JButton("RSVP");
        bt_viewEvents = new JButton("View all events");
        bt_eventList = new JButton("Search events");
        bt_createUser = new JButton("Create user");
        bt_modeChange = new JButton("Mode change");
        bt_updateUser = new JButton("Update user");

        p_center = new JPanel();

        // 라벨에 폰트 설정
        lb_title.setFont(new Font("Arial-Black", Font.BOLD, 25));
        lb_title.setPreferredSize(new Dimension(300, 30));

        p_north.add(bt_prev);
        p_north.add(lb_title);
        p_north.add(bt_next);
        p_south.add(bt_create_event);
        // p_north.add(bt_RVSP);
        p_south.add(bt_viewEvents);
        p_south.add(bt_update_event);
        p_south.add(bt_delete_event);
        p_south.add(bt_eventList);
        p_south.add(bt_modeChange);
        p_north.add(bt_createUser, BorderLayout.NORTH);
        p_north.add(bt_updateUser, BorderLayout.SOUTH);
        frame2.add(p_north, BorderLayout.NORTH);
        frame2.add(p_south, BorderLayout.SOUTH);
        frame2.add(p_center);

        // 이전 버튼을 눌렀을 때 전 월로 이동해야함
        bt_prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateMonth(-1);
            }
        });

        // 다음 버튼을 눌렀을 때 다음 달로 이동해야함
        bt_next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateMonth(1);
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
        bt_viewEvents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Helpers.viewEvents();
            }
        });

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
                System.out.println("After disposing");
                Helpers.modeChange(frame2);
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

        getCurrentDate(); // 현재 날짜 객체 생성
        getDateInfo(); // 날짜 객체로부터 정보들 구하기
        setDateTitle(); // 타이틀 라벨에 날짜 표시하기
        createDay(); // 요일 박스 생성
        createDate(); // 날짜 박스 생성
        printDate(); // 상자에 날짜 그리기

        frame2.setVisible(true);
        frame2.setBounds(100, 100, 1200, 1200);
        frame2.setResizable(false);

        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void getCurrentDate() {
        cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

    }

    // 시작 요일, 끝 날 등 구하기
    public void getDateInfo() {
        yy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH);
        startDay = getFirstDayOfMonth(yy, mm);
        lastDate = getLastDate(yy, mm);
    }

    // 요일 생성
    public void createDay() {

        for (int i = 0; i < 7; i++) {
            DateBox dayBox = new DateBox(dayAr[i], Color.gray, 150, 70);
            p_center.add(dayBox);
        }
    }

    // 날짜 생성
    public void createDate() {
        for (int i = 0; i < dayAr.length * 6; i++) {
            DateBox dateBox = new DateBox("",
                    Color.LIGHT_GRAY, 150, 100);
            p_center.add(dateBox);
            dateBoxAr[i] = dateBox;
            // p_center.add(new JButton());
        }
    }

    // 해당 월의 시작 요일 구하기
    // 개발 원리 : 날짜 객체를 해당 월의 1일로 조작한 후, 요일 구하기
    // 사용 방법 : 2021년 2월을 구할시 2021, 1을 넣으면 됨
    public int getFirstDayOfMonth(int yy, int mm) {
        System.out.println("getFirstDayOfMonth called");
        Calendar cal = Calendar.getInstance(); // 날짜 객체 생성
        cal.set(yy, mm, 1);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;// 요일은 1부터 시작으로 배열과 쌍을 맞추기 위해 -1
    }

    // 사용 방법 : 2021년 2월을 구할시 2021, 1을 넣으면 됨
    public int getLastDate(int yy, int mm) {
        System.out.println("getLastDate called");
        Calendar cal = Calendar.getInstance();
        cal.set(yy, mm + 1, 0);
        // 마지막 날을 의미한다.
        return cal.get(Calendar.DATE);
    }

    // 날짜 박스에 날짜 출력하기
    public void printDate() {
        // System.out.println("printDate called");
        System.out.println("시작 요일" + startDay);
        System.out.println("마지막 일" + lastDate);
        Calendar cal = Calendar.getInstance();
        month = (cal.get(Calendar.MONTH) + 1) + "";
        int n = 1;
        for (int i = 0; i < dateBoxAr.length; i++) {
            if (i >= startDay && n <= lastDate) {

                System.out.println(
                        String.format("Date  : %s-%s-%s", cal.get(Calendar.YEAR) + "", month, Integer.toString(n)));

                dateBoxAr[i].day = Integer.toString(n);
                dateBoxAr[i].month = month;
                dateBoxAr[i].flag = true;
                dateBoxAr[i].year = cal.get(Calendar.YEAR) + "";
                // dateBoxAr[i].repaint();
                n++;
            } else {

                // System.out.println(
                // String.format("Date : %s-%s-%s", cal.get(Calendar.YEAR) + "", month,
                // Integer.toString(n)));
                dateBoxAr[i].day = "";
                // dateBoxAr[i].repaint();
            }
        }

        // p_center.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                p_center.revalidate();
                p_center.repaint();
            }
        });
    }

    // 달력을 넘기거나 전으로 이동할 때 날짜 객체에 대한 정보도 변경
    public void updateWeek(int data) {
        // 캘린더 객체에 들어있는 날짜를 기준으로 월 정보를 바꿔준다.
        cal.set(Calendar.WEEK_OF_MONTH, ww + data);
        // dateBoxAr = new DateBox[dayAr.length * 6];
        // createDay();
        // createDate();
        getDateInfo();
        printDate();
        setDateTitle();
    }

    // 달력을 넘기거나 전으로 이동할 때 날짜 객체에 대한 정보도 변경
    public void updateMonth(int data) {
        // cal.set(Calendar.MONTH, mm + data);
        cal.add(Calendar.MONTH, data);
        getDateInfo();
        recreateDateBoxes();
        printDate();
        setDateTitle();

        SwingUtilities.invokeLater(() -> {
            frame2.repaint();
            frame2.revalidate();
        });
    }

    private void recreateDateBoxes() {
        frame2.getContentPane().remove(p_center);
        // p_center.removeAll();
        p_center = new JPanel();
        dateBoxList.clear();

        createDay();

        for (int i = 0; i < dayAr.length * 6; i++) {
            DateBox dateBox = createDateBox(i);
            p_center.add(dateBox);
            dateBoxList.add(dateBox);
        }

        // SwingUtilities.invokeLater(() -> {
        // p_center.revalidate();
        // p_center.repaint();
        // });
        frame2.getContentPane().add(p_center, BorderLayout.CENTER);
        // frame2.revalidate();
        // frame2.repaint();
    }

    // Create a DateBox with the given index
    private DateBox createDateBox(int index) {
        int n = index - startDay + 1;
        DateBox dateBox = new DateBox("", Color.LIGHT_GRAY, 150, 100);

        if (n > 0 && n <= lastDate) {
            dateBox.day = Integer.toString(n);
            dateBox.month = month;
            dateBox.flag = true;
            dateBox.year = cal.get(Calendar.YEAR) + "";
        }

        return dateBox;
    }

    // 몇년도 몇월인지를 보여주는 타이틀 라벨의 값을 변경
    public void setDateTitle() {
        lb_title.setText(yy + "-" + StringManager.getZeroString(mm + 1));
        lb_title.updateUI();
    }
}
