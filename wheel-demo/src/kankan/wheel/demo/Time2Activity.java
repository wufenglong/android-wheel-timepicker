package kankan.wheel.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kankan.wheel.R;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Time2Activity extends Activity {
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mWeekday;
    private int mHour;
    private int mMins;
    LinearLayout timePickerLayout;
    private Animation animDownIn;
    private Animation animDownOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time2_layout);
        animDownIn = AnimationUtils.loadAnimation(this, R.anim.slide_down_in);
        animDownOut = AnimationUtils.loadAnimation(this, R.anim.slide_down_out);
        timePickerLayout = (LinearLayout) findViewById(R.id.time_picker_layout);
        timePickerLayout.startAnimation(animDownIn);
        final TextView show = (TextView) findViewById(R.id.show);
        final WheelView hoursView = (WheelView) findViewById(R.id.hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 0, 23, "%02d时");
        hourAdapter.setItemResource(R.layout.wheel_text_item);
        hourAdapter.setItemTextResource(R.id.text);
        hoursView.setViewAdapter(hourAdapter);
        hoursView.setCyclic(true);
        hoursView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mHour = newValue;
                show.setText(mYear + "年" + mMonth + "月" + mDay + "日" + mWeekday + "  " + mHour + "时" + mMins + "分");
            }
        });
        final WheelView minsView = (WheelView) findViewById(R.id.mins);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 59, "%02d分");
        minAdapter.setItemResource(R.layout.wheel_text_item);
        minAdapter.setItemTextResource(R.id.text);
        minsView.setViewAdapter(minAdapter);
        minsView.setCyclic(true);
        minsView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mMins = newValue;
                show.setText(mYear + "年" + mMonth + "月" + mDay + "日" + mWeekday + "  " + mHour + "时" + mMins + "分");
            }
        });

        // set current time
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        hoursView.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        minsView.setCurrentItem(calendar.get(Calendar.MINUTE));
        //ampm.setCurrentItem(calendar.get(Calendar.AM_PM));
        final WheelView day = (WheelView) findViewById(R.id.day);
        final DayArrayAdapter dayArrayAdapter = new DayArrayAdapter(this, calendar);
        day.setViewAdapter(dayArrayAdapter);
        day.setCurrentItem(dayArrayAdapter.getItemsCount() / 2);
        day.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mYear = dayArrayAdapter.getSelectedYear(newValue);
                mMonth = dayArrayAdapter.getSelectedMonth(newValue);
                mDay = dayArrayAdapter.getSelectedDay(newValue);
                mWeekday = dayArrayAdapter.getSelectedWeekday(newValue);
                show.setText(mYear + "年" + mMonth + "月" + mDay + "日" + mWeekday + "  " + mHour + "时" + mMins + "分");
            }
        });
        mYear = dayArrayAdapter.getSelectedYear(dayArrayAdapter.getItemsCount() / 2);
        mMonth = dayArrayAdapter.getSelectedMonth(dayArrayAdapter.getItemsCount() / 2);
        mDay = dayArrayAdapter.getSelectedDay(dayArrayAdapter.getItemsCount() / 2);
        mWeekday = dayArrayAdapter.getSelectedWeekday(dayArrayAdapter.getItemsCount() / 2);
        mHour = hoursView.getCurrentItem();
        mMins = minsView.getCurrentItem();
        show.setText(mYear + "年" + mMonth + "月" + mDay + "日" + mWeekday + "  " + mHour + "时" + mMins + "分");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            timePickerLayout.startAnimation(animDownOut);
            animDownOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Day adapter
     */
    private class DayArrayAdapter extends AbstractWheelTextAdapter {
        // Count of days to be shown
        private final int daysCount = 47481;
        // Calendar
        Calendar calendar;

        /**
         * Constructor
         */
        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.time2_day, NO_RESOURCE);
            this.calendar = calendar;

            setItemTextResource(R.id.time2_monthday);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            int dayoffset = -daysCount / 2 + index;
            Calendar newCalendar = getNewCalendar(index);
            View view = super.getItem(index, cachedView, parent);
            TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
            if (dayoffset == 0) {
                weekday.setText("");
            } else {
                DateFormat format = new SimpleDateFormat("EEE");
                weekday.setText(format.format(newCalendar.getTime()));
            }

            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            if (dayoffset == 0) {
                monthday.setText("今天");
                monthday.setTextColor(0xFF0000F0);
            } else {
                DateFormat format = new SimpleDateFormat("MMMd");
                monthday.setText(format.format(newCalendar.getTime()));
                monthday.setTextColor(0xFF111111);
            }
            return view;
        }

        private Calendar getNewCalendar(int index) {
            int dayoffset = -daysCount / 2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.DAY_OF_YEAR, dayoffset);
            int month = newCalendar.get(Calendar.MONTH) + 1;
            return newCalendar;
        }

        public int getSelectedYear(int index) {
            int dayoffset = -daysCount / 2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.DAY_OF_YEAR, dayoffset);
            return newCalendar.get(Calendar.YEAR);
        }

        public int getSelectedMonth(int index) {
            int dayoffset = -daysCount / 2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.DAY_OF_YEAR, dayoffset);
            return newCalendar.get(Calendar.MONTH) + 1;
        }

        public int getSelectedDay(int index) {
            int dayoffset = -daysCount / 2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.DAY_OF_YEAR, dayoffset);
            return newCalendar.get(Calendar.DAY_OF_MONTH);
        }

        public String getSelectedWeekday(int index) {
            int dayoffset = -daysCount / 2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.DAY_OF_YEAR, dayoffset);
            DateFormat format = new SimpleDateFormat("EEE");
            return format.format(newCalendar.getTime());
        }

        @Override
        public int getItemsCount() {
            return daysCount;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return "";
        }
    }
}
