package com.andorid.example.readexcel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.yingo.dbtext.DBHelper;
import com.yingo.dbtext.User;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button btnTest;

	private List<String[]> list;
	private final String FILE_NAME = "position2013.xls";
	

	// 显示所有数据的ListView
	ListView lv;

	ArrayList list1;

	// 拥有所有数据的Adapter
	SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		list = new ArrayList<String[]>();

		btnTest = (Button) findViewById(R.id.btnTest);

		btnTest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					ServiceTask task = new ServiceTask(MainActivity.this);
					task.execute(FILE_NAME);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class ServiceTask extends AsyncTask<String, Void, String> {

		private Context context;

		private ServiceTask(Context context) {
			this.context = context;
		}

		@Override
		protected  String doInBackground(String... params) {

			String filename = params[0];

			try {
				Log.i("MainActivity", "正在努力地开始读取exel表格数据");
				// 读取assets中的excel文件
				ExcelReader er = new ExcelReader(MainActivity.this.getAssets()
						.open(filename));
				Log.i("MainActivity", "已经读取了exel表格数据0");
				list = er.getExcelContent();
				Log.i("MainActivity", "已经读取了exel表格数据1");
				for (int i = 0; i < list.size(); i++) {
					// for (int i = 0; i < 1; i++) {
					StringBuffer postStr = new StringBuffer();
					postStr.append("No=");
					postStr.append(list.get(i)[0]);
					postStr.append("&knowledgeType=");
					postStr.append(list.get(i)[1]);
					postStr.append("&indication=");
					postStr.append(list.get(i)[2]);
					postStr.append("&type=");
					postStr.append(list.get(i)[3]);
					postStr.append("&level=");
					postStr.append(list.get(i)[4]);
					postStr.append("&answer=");
					postStr.append(list.get(i)[5]);
					postStr.append("&subject=");
					postStr.append(list.get(i)[6]);
					
					System.out.println(postStr);
					Log.i("MainActivity", "已经用postStr打印了exel表格数据");
					// 从表单上获取数据
					User user = new User();
					user.No = list.get(i)[0].toString();
					user.knowledgeTyp = list.get(i)[1].toString();
					user.indication = list.get(i)[2].toString();
					user.type = list.get(i)[3].toString();
					user.level = list.get(i)[4].toString();
					user.answer = "正确";
					user.subject = list.get(i)[6].toString();

					System.out.println("user输出结果值为 " + user.answer);
					Log.i("MainActivity", "打印user的数据");
					// 创建数据库帮助类
					DBHelper helper = new DBHelper(context);
					Log.i("MainActivity", "已经跳转到数据库操作中去了");
					// 打开数据库
					helper.openDatabase();
					// 把user存储到数据库里
					long result = -1;
					result = helper.insert(user);

					if (result == -1) {
						/*Toast.makeText(MainActivity.this, "添加失败",
								Toast.LENGTH_LONG).show();*/
						System.out.println("添加失败");
					} else {
						/*Toast.makeText(MainActivity.this, "添加ok",
								Toast.LENGTH_LONG).show();*/
						System.out.println("添加成功");
					}
					System.out.println("输出结果值为 " + result + " 结束程序");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return "OK";

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result=="OK"){
			// 数据显示
			DBHelper helper = new DBHelper(MainActivity.this);// 获得所有用户的list
			helper.openDatabase(); // 打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
			list1 = helper.getAllUser();// 拿到所有保密状态为privacy的用户的list
			//
			lv = (ListView) findViewById(R.id.listview); // 创建ListView对象
			if (list1.size() == 0) {
				// Drawable nodata_bg =
				// getResources().getDrawable(R.drawable.nodata_bg);
				// mainLinearLayout.setBackgroundDrawable(nodata_bg);
				setTitle("没有查到任何数据");
				System.out.println("没有查到任何数据");
			}
			if (list1.size() > 0) {
				// mainLinearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.contact_bg));
				setTitle("各种名单");
				System.out.println("各种名单任何数据");
				//
			}
			// 将数据与adapter集合起来
			adapter = new SimpleAdapter(MainActivity.this, list1,
					R.layout.list_item,
					new String[] { "name", "knowledgeTyp", "indication",
							"type", "level", "answer", "subject" }, new int[] {
							R.id.NO1, R.id.NO2, R.id.NO3, R.id.NO4, R.id.NO5,
							R.id.NO6, R.id.NO7 });

			lv.setAdapter(adapter);// 将整合好的adapter交给listview，显示给用户看
			}}
	}
}
