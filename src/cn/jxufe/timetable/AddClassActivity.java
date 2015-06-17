package cn.jxufe.timetable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddClassActivity extends Activity {
	private SQLiteDatabase database;
	private String databaseFilename = Environment
			.getExternalStorageDirectory() + "/timetable/timetable.db";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_addclass);

		database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
		TextView t_timetable = (TextView) findViewById(R.id.t_timetable);
		Button btn_addClass=(Button) findViewById(R.id.btn_addClass);

		t_timetable.setClickable(true);
		t_timetable.setFocusable(true);
		t_timetable.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AddClassActivity.this, TableActivity.class);
				AddClassActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				AddClassActivity.this.finish();
			}
		});
		
		btn_addClass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				EditText e_teacher = (EditText) findViewById(R.id.addClass_teacher);
				EditText e_place = (EditText) findViewById(R.id.addClass_place);
				EditText e_course = (EditText) findViewById(R.id.addClass_course);
				Spinner s_week = (Spinner) findViewById(R.id.spinner_week);
				Spinner s_color = (Spinner) findViewById(R.id.spinner_color);
				Spinner s_classes = (Spinner) findViewById(R.id.spinner_classes);
				ContentValues values = new ContentValues();
				values.put("course", e_course.getText().toString());
				values.put("teacher", e_teacher.getText().toString());
				values.put("place", e_place.getText().toString());
				values.put("color", returnColor(s_color.getSelectedItem().toString()));
				// 参数1为表名，参数2为更新后的值，参数3表示满足条件的列名称，参数4为该列名下的值
				String week=returnWeek(s_week.getSelectedItem().toString());
				String num=returnClasses(s_classes.getSelectedItem().toString());
				database.update("t_course", values, "week=? and num=?", new String[]{ week, num });
				
				
				Intent intent = new Intent();
				intent.setClass(AddClassActivity.this, TableActivity.class);
				AddClassActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				AddClassActivity.this.finish();
			}
		});
		
	}
	
	public String returnWeek(String week){
		switch (week) {
		case "星期一":
			 return "monday";
		case "星期二":
			return "tuesday";
		case "星期三":
			return "wednesday";
		case "星期四":
			return "thursday";
		case "星期五":
			return "friday";
		case "星期六":
			return "saturday";
		case "星期天":
			return "sunday";
		default:
			return "";
		}
	}
	
	public String returnClasses(String classes){
		switch (classes) {
		case "第1/2节":
			return "1";
		case "第3/4节":
			return "2";
		case "第5/6/7节":
			return "3";
		case "第8/9/A节":
			return "4";
		default:
			return "0";
		}
	}
	
	public int returnColor(String color){
		switch (color) {
		case "白色":
			return 0;
		case "橙色":
			return 1;
		case "绿色":
			return 2;
		case "浅蓝色":
			return 3;
		case "棕色":
			return 4;
		case "天蓝色":
			return 5;
		case "红色":
			return 6;
		case "灰色":
			return 7;
		default:
			return 0;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}