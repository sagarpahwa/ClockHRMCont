package com.clockhr.attendeceCalender;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import com.clockhr.*;
//import com.clockhr.R;

/**
 * Created by a7med on 28/06/2015.
 */
public class CalendarView extends LinearLayout
{
	// for logging
	private static final String LOGTAG = "Calendar View";

	// how many days to show, defaults to six weeks, 42 days
	private static final int DAYS_COUNT = 42;

	// default date format
	private static final String DATE_FORMAT = "MMM yyyy";

	// date format
	private String dateFormat;

	// current displayed month
	private Calendar currentDate = Calendar.getInstance();

	//event handling
	private EventHandler eventHandler = null;

	// internal components
	private LinearLayout header;
	private ImageView btnPrev;
	private ImageView btnNext;
	private TextView txtDate;
	private GridView grid;

	// seasons' rainbow
	int[] rainbow = new int[] {
			R.color.primarytrslucent,
			R.color.primarytrslucent,
			R.color.primarytrslucent,
			R.color.primarytrslucent
	};

	// month-season association (northern hemisphere, sorry australia :)
	int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

	public CalendarView(Context context)
	{
		super(context);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initControl(context, attrs);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initControl(context, attrs);
	}

	/**
	 * Load control xml layout
	 */
	private void initControl(Context context, AttributeSet attrs)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.control_calendar, this);

		loadDateFormat(attrs);
		assignUiElements();
		assignClickHandlers();

		updateCalendar();
	}

	private void loadDateFormat(AttributeSet attrs)
	{
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

		try
		{
			// try to load provided date format, and fallback to default otherwise
			dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
			if (dateFormat == null)
				dateFormat = DATE_FORMAT;
		}
		finally
		{
			ta.recycle();
		}
	}
	private void assignUiElements()
	{
		// layout is inflated, assign local variables to components
		header = (LinearLayout)findViewById(R.id.calendar_header);
		btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
		btnNext = (ImageView)findViewById(R.id.calendar_next_button);
		txtDate = (TextView)findViewById(R.id.calendar_date_display);
		grid = (GridView)findViewById(R.id.calendar_grid);
	}

	private void assignClickHandlers()
	{
		// add one month and refresh UI
		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentDate.add(Calendar.MONTH, 1);
				updateCalendar();
				eventHandler.onNextButtonClick();

			}
		});

		// subtract one month and refresh UI
		btnPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentDate.add(Calendar.MONTH, -1);
				updateCalendar();
				eventHandler.onPreviousButtonClick();
			}
		});

		// long-pressing a day
		grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
			{
				// handle long-press
				if (eventHandler == null)
					return false;

				eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));
				return true;
			}
		});
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View cell, int position, long id) {
				// handle long-press
				if (eventHandler == null)
					return ;

				eventHandler.onDayPressed((Date)view.getItemAtPosition(position));
				return ;

			}
		});
	}

	/**
	 * Display dates correctly in grid
	 */
	public void updateCalendar()
	{
		updateCalendar(null,null);
	}

	/**
	 * Display dates correctly in grid
	 */
	public void updateCalendar(ArrayList<Date> events,ArrayList<String> eventType)
	{
		ArrayList<Date> cells = new ArrayList<>();
		Calendar calendar = (Calendar)currentDate.clone();

		// determine the cell for current month's beginning
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		// move calendar backwards to the beginning of the week
		calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

		// fill cells
		while (cells.size() < DAYS_COUNT)
		{
			cells.add(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		// update grid
		grid.setAdapter(new CalendarAdapter(getContext(), cells, events,eventType));

		// update title
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		txtDate.setText(sdf.format(currentDate.getTime()));

		// set header color according to current season
		int month = currentDate.get(Calendar.MONTH);
		int season = monthSeason[month];
		int color = rainbow[season];

		header.setBackgroundColor(getResources().getColor(color));
	}


	private class CalendarAdapter extends ArrayAdapter<Date>
	{
		// days with events
		private ArrayList<Date> eventDays;
		private ArrayList<String>eventType;
		private int myear,mmonth,mday;
		// for view inflation
		private LayoutInflater inflater;

		public CalendarAdapter(Context context, ArrayList<Date> days, ArrayList<Date> eventDays,ArrayList<String> eventType)
		{
			super(context, R.layout.control_calendar_day, days);
			Calendar cal = Calendar.getInstance();
			myear = cal.get(Calendar.YEAR);
			mmonth = cal.get(Calendar.MONTH);
			mday = cal.get(Calendar.DAY_OF_MONTH);
			this.eventDays = eventDays;
			this.eventType=eventType;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			// day in question
			Date date = getItem(position);
			int day = date.getDate();
			int month = date.getMonth();
			int year = date.getYear();

			// today
			Date today = new Date();
			// inflate item if it does not exist yet
			if (view == null)
				view = inflater.inflate(R.layout.control_calendar_day, parent, false);

			// if this day has an event, specify event image
			//view.setBackgroundResource(0);
			//view.setBackgroundColor(0xffffffff);
			int counter = 0;
			if (eventDays != null)
			{
				for (Date eventDate : eventDays)
				{
					if (eventDate.getDate() == day &&
							eventDate.getMonth() == month &&
							eventDate.getYear() == year){
						int year1=year+1900;
						if((year1<myear)||(year1==myear&&month<mmonth)||(year1==myear&&month==mmonth&&day<=mday)) {
						// mark this day for event
						//view.setBackgroundResource(R.drawable.reminder);
						if (eventType.get(counter).equals("P")) {
							//Log.e("p:", "" + counter+" "+myear+" "+mmonth+" "+mday+" "+year+" "+month+" "+day);
							//view.setBackgroundColor(0x99009688);

							view.setBackgroundResource(R.drawable.border);
						}/* else if (eventType.get(counter).equals("A")) {
//							view.setBackgroundResource(R.drawable.border_red);
							//view.setBackgroundColor(0x88ff0000);
						}*/
						else if (eventType.get(counter).equals("PN")) {
							view.setBackgroundResource(R.drawable.border_beforehalfday);

							//view.setBackgroundColor(0x88ff0000);
						} else if (eventType.get(counter).equals("W/O")) {
							view.setBackgroundResource(R.drawable.border_red);

							//view.setBackgroundColor(0x88ff0000);
						}
						else if (eventType.get(counter).equals("L")) {
							view.setBackgroundResource(R.drawable.border);

							//view.setBackgroundColor(0x88ff0000);
						}
						else if (eventType.get(counter).equals("H")) {
							view.setBackgroundResource(R.drawable.border_holiday);

							//view.setBackgroundColor(0x88ff0000);
						}
					}
						break;
					}
					counter++;
				}
			}
			// clear styling
			((TextView)view).setTypeface(null, Typeface.NORMAL);
			((TextView)view).setTextColor(Color.BLACK);

			if (month != today.getMonth() || year != today.getYear())
			{
				// if this day is outside current month, grey it out
				((TextView)view).setTextColor(getResources().getColor(R.color.list_item_title));
			}
			else if (day == today.getDate())
			{
				// if it is today, set it to blue/bold
				((TextView)view).setTypeface(null, Typeface.BOLD);
				((TextView)view).setTextColor(getResources().getColor(R.color.primary_text));
			}

			// set text
			((TextView)view).setText(String.valueOf(date.getDate()));

			return view;
		}
	}

	/**
	 * Assign event handler to be passed needed events
	 */
	public void setEventHandler(EventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}

	/**
	 * This interface defines what events to be reported to
	 * the outside world
	 */
	public interface EventHandler
	{
		void onPreviousButtonClick();
		void onNextButtonClick();
		void onDayLongPress(Date date);
		void onDayPressed(Date date);
	}
}
