package cn.jxufe.timetable;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TableActivity extends Activity {
	private SQLiteDatabase database;
	private String databaseFilename = Environment
			.getExternalStorageDirectory() + "/timetable/timetable.db";

	private int colors[] = { Color.rgb(0xee, 0xff, 0xff),
			Color.rgb(0xf0, 0x96, 0x09), Color.rgb(0x8c, 0xbf, 0x26),
			Color.rgb(0x00, 0xab, 0xa9), Color.rgb(0x99, 0x6c, 0x33),
			Color.rgb(0x3b, 0x92, 0xbc), Color.rgb(0xd5, 0x4d, 0x34),
			Color.rgb(0xcc, 0xcc, 0xcc) };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 切换语言
		Locale.setDefault(Locale.CHINESE);
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		config.locale = Locale.CHINESE;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		setContentView(R.layout.layout_timetable);

		database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
		TextView t_addClass = (TextView) findViewById(R.id.t_addClass);

		// 分别表示周一到周日
		LinearLayout monday = (LinearLayout) findViewById(R.id.monday);
		LinearLayout tuesday = (LinearLayout) findViewById(R.id.tuesday);
		LinearLayout wednesday = (LinearLayout) findViewById(R.id.wednesday);
		LinearLayout thursday = (LinearLayout) findViewById(R.id.thursday);
		LinearLayout friday = (LinearLayout) findViewById(R.id.friday);
		LinearLayout saturday = (LinearLayout) findViewById(R.id.saturday);
		LinearLayout sunday = (LinearLayout) findViewById(R.id.sunday);

		LinearLayout layout = null;
		Cursor cursor = database.rawQuery("SELECT * FROM t_course", null);
		while (cursor.moveToNext()) {
			String week = cursor.getString(cursor.getColumnIndex("week"));
			String course = cursor.getString(cursor.getColumnIndex("course"));
			String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
			String place = cursor.getString(cursor.getColumnIndex("place"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			int color = cursor.getInt(cursor.getColumnIndex("color"));
			switch (week) {
			case "monday":
				layout = monday;
				break;
			case "tuesday":
				layout = tuesday;
				break;
			case "wednesday":
				layout = wednesday;
				break;
			case "thursday":
				layout = thursday;
				break;
			case "friday":
				layout = friday;
				break;
			case "saturday":
				layout = saturday;
				break;
			case "sunday":
				layout = sunday;
				break;
			default:
				break;
			}
			setClass(layout, course, teacher, place, num, color);
		}
		cursor.close();

		t_addClass.setClickable(true);
		t_addClass.setFocusable(true);
		t_addClass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TableActivity.this, AddClassActivity.class);
				TableActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				TableActivity.this.finish();
			}
		});
	}

	/**
	 * 设置课程的方法
	 * 
	 * @param layout
	 * @param title
	 *            课程名称
	 * @param place
	 *            地点
	 * @param classes
	 *            节数
	 * @param color
	 *            背景色
	 */
	void setClass(LinearLayout layout, String course, String teacher,
			String place, int num, int color) {
		int classes = 0;
		if (num == 1 || num == 2) {
			classes = 2;
		} else {
			classes = 3;
		}
		if(course!=null){
			
		}
		View view = LayoutInflater.from(this).inflate(R.layout.layout_timetable_item, null);
		view.setMinimumHeight(dip2px(this, classes * 48));
		view.setBackgroundColor(colors[color]);
		((TextView) view.findViewById(R.id.course)).setText(course);
		((TextView) view.findViewById(R.id.teacher)).setText("教师："+teacher);
		((TextView) view.findViewById(R.id.place)).setText("地点："+place);
		// 为课程View设置点击的监听器
		layout.addView(view);
		TextView blank = new TextView(this);
		blank.setHeight(dip2px(this, classes));
		layout.addView(blank);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/** * 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}