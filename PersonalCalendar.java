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

public class PersonalCalendar extends JFrame {

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

	JPanel calendarPanel;
	public static Calendar cal; // 날짜 객체
	int currentWeek;
	int currentDay;
	int dayOfMonth;
	int today;
	String cellValue;
	Calendar calendar;

	public static int yy; // 기준점이 되는 년도
	public static int mm; // 기준점이 되는 월
	public static int ww;
	public static int dd;
	public static int startDay; // 월의 시작 요일
	public static int lastDate; // 월의 마지막 날
	public static int userID = -1; // Current user's ID
	public static String name = new String(""); // Current user's name

	///////////////////////////////////////// Log-in panel
	///////////////////////////////////////// ///////////////////////////////////////////////

	//////////////////////////////////////// Monthly
	//////////////////////////////////////// calendar//////////////////////////////////////////////

	//////////////////////////////////////// Weekly calendar
	//////////////////////////////////////// //////////////////////////////////////////////

	///////////////////////////////////////// Helper methods
	///////////////////////////////////////// ///////////////////////////////////////////////

	class Event {
		private int eventId;
		private String title;
		private Timestamp startTime;
		private Timestamp endTime;
		private String location;

		public Event(int eventId, String title, Timestamp startTime, Timestamp endTime, String location) {
			this.eventId = eventId;
			this.title = title;
			this.startTime = startTime;
			this.endTime = endTime;
			this.location = location;
		}

		public int getEventId() {
			return eventId;
		}

		public String getTitle() {
			return title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	public static void main(String[] args) {
		// PersonalCalendar diary = new PersonalCalendar();
		// diary.displayLoginPanel();
		DisplayLoginPanel x = new DisplayLoginPanel();
	}
}