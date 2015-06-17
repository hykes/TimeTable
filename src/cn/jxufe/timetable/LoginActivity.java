package cn.jxufe.timetable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	Button rebackBtn, loginBtn, forgetPasswdBtn;
	EditText userEdit, passwdEdit;
	private CheckBox lookPwd;
	PopupWindow popup;
	RelativeLayout loginLayout;
	private SQLiteDatabase database;
	private final String DATABASE_PATH = Environment
			.getExternalStorageDirectory() + "/timetable";
	private final String DATABASE_FILENAME = "timetable.db";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 1.DEFAULT_FEATURES：系统默认状态，一般不需要指定
		// 2.FEATURE_CONTEXT_MENU：启用ContextMenu，默认该项已启用，一般无需指定
		// 3.FEATURE_CUSTOM_TITLE：自定义标题。当需要自定义标题时必须指定。如：标题是一个按钮时
		// 4.FEATURE_INDETERMINATE_PROGRESS：不确定的进度
		// 5.FEATURE_LEFT_ICON：标题栏左侧的图标
		// 6.FEATURE_NO_TITLE：无标题
		// 7.FEATURE_OPTIONS_PANEL：启用“选项面板”功能，默认已启用。
		// 8.FEATURE_PROGRESS：进度指示器功能
		// 9.FEATURE_RIGHT_ICON:标题栏右侧的图标
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_login);
		database = openDatabase();
		loginBtn = (Button) findViewById(R.id.login_login_btn);
		loginBtn.setOnClickListener(this);// 注册监听器 一定不能忘
		passwdEdit = (EditText) findViewById(R.id.login_passwd_edit);
		userEdit = (EditText) findViewById(R.id.login_user_edit);
		forgetPasswdBtn = (Button) findViewById(R.id.forget_passwd);
		forgetPasswdBtn.setOnClickListener(this);
		loginLayout = (RelativeLayout) findViewById(R.id.login_layout);

		// 显示密码
		lookPwd = (CheckBox) findViewById(R.id.lookPwd);
		lookPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 如果选中，显示密码
					passwdEdit
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				} else {
					// 否则隐藏密码
					passwdEdit
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.login_login_btn:// 点击登录按钮 进行判断 用户名和密码是否为空
			String userEditStr = userEdit.getText().toString().trim();
			String passwdEditStr = passwdEdit.getText().toString().trim();
			if (("".equals(userEditStr) || null == userEditStr)
					|| ("".equals(passwdEditStr) || null == passwdEditStr)) {// 只要用户名和密码有一个为空
				Toast.makeText(getApplicationContext(),
						"微课表账号或密码不能为空，请输入微课表账号或密码", Toast.LENGTH_SHORT).show();
			} else {
				int count = 0;
				Cursor cursor = database
						.rawQuery(
								"SELECT COUNT(*) FROM t_user WHERE username=? AND password=?",
								new String[] { userEditStr, passwdEditStr });
				if (cursor.moveToFirst()) {
					count = cursor.getInt(0);
				}
				cursor.close();
				if (count == 1) {
					Toast.makeText(getApplicationContext(), "登录成功",
							Toast.LENGTH_SHORT).show();
					/* 新建一个Intent对象 */
					Intent intent = new Intent();
					// 传参
					// intent.putExtra("msg","hello");
					/* 指定intent要启动的类 */
					intent.setClass(LoginActivity.this, TableActivity.class);
					/* 启动一个新的Activity */
					LoginActivity.this.startActivity(intent);
					overridePendingTransition(R.anim.fade, R.anim.hold);
					/* 关闭当前的Activity */
					LoginActivity.this.finish();
					// 获取参数
					// String name=intent.getStringExtra("msg");

				} else {
					Toast.makeText(getApplicationContext(), "登录失败",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
			loginLayout.setBackgroundColor(Color.WHITE);
			forgetPasswdBtn.setBackgroundColor(Color.WHITE);
			forgetPasswdBtn.setEnabled(true);
		}
		return super.onTouchEvent(event);
	}

	protected void onDestroy() {
		super.onDestroy();
		if (database != null) {
			database.close();
		}
	}

	private SQLiteDatabase openDatabase() {
		try {
			// 获得dictionary.db文件的绝对路径
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			File dir = new File(DATABASE_PATH);
			// 如果DATABASE_PATH目录不存在，创建这个目录
			if (!dir.exists())
				dir.mkdir();
			// 如果在DATABASE_PATH目录中不存在
			// DATABASE_FILENAME文件，则从res\raw目录中复制这个文件到
			// SD卡的目录（DATABASE_PATH）
			if (!(new File(databaseFilename)).exists()) {
				System.out.println("正在复制数据库文件！");
				// 获得封装文件的InputStream对象
				InputStream is = getResources()
						.openRawResource(R.raw.timetable);
				System.out.println("获得数据库文件数据流！");
				// true就是追加文字，false就是替换文字。而不写就默认替换。
				FileOutputStream fos = new FileOutputStream(databaseFilename,
						false);
				System.out.println("获得数据库文件输出流！");
				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				System.out.println("数据库文件复制完成！");
			}
			// 打开/sdcard/dictionary目录中的dictionary.db文件
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		} catch (Exception e) {
			System.out.println("数据库加载出错！" + e.getMessage());
		}
		return null;
	}

}