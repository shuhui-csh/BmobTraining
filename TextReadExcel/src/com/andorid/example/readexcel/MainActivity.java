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
	

	// ��ʾ�������ݵ�ListView
	ListView lv;

	ArrayList list1;

	// ӵ���������ݵ�Adapter
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
				Log.i("MainActivity", "����Ŭ���ؿ�ʼ��ȡexel�������");
				// ��ȡassets�е�excel�ļ�
				ExcelReader er = new ExcelReader(MainActivity.this.getAssets()
						.open(filename));
				Log.i("MainActivity", "�Ѿ���ȡ��exel�������0");
				list = er.getExcelContent();
				Log.i("MainActivity", "�Ѿ���ȡ��exel�������1");
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
					Log.i("MainActivity", "�Ѿ���postStr��ӡ��exel�������");
					// �ӱ��ϻ�ȡ����
					User user = new User();
					user.No = list.get(i)[0].toString();
					user.knowledgeTyp = list.get(i)[1].toString();
					user.indication = list.get(i)[2].toString();
					user.type = list.get(i)[3].toString();
					user.level = list.get(i)[4].toString();
					user.answer = "��ȷ";
					user.subject = list.get(i)[6].toString();

					System.out.println("user������ֵΪ " + user.answer);
					Log.i("MainActivity", "��ӡuser������");
					// �������ݿ������
					DBHelper helper = new DBHelper(context);
					Log.i("MainActivity", "�Ѿ���ת�����ݿ������ȥ��");
					// �����ݿ�
					helper.openDatabase();
					// ��user�洢�����ݿ���
					long result = -1;
					result = helper.insert(user);

					if (result == -1) {
						/*Toast.makeText(MainActivity.this, "���ʧ��",
								Toast.LENGTH_LONG).show();*/
						System.out.println("���ʧ��");
					} else {
						/*Toast.makeText(MainActivity.this, "���ok",
								Toast.LENGTH_LONG).show();*/
						System.out.println("��ӳɹ�");
					}
					System.out.println("������ֵΪ " + result + " ��������");
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
			// ������ʾ
			DBHelper helper = new DBHelper(MainActivity.this);// ��������û���list
			helper.openDatabase(); // �����ݿ⣬�ʹ���һ�Σ���ΪHelper�е�SQLiteDatabase�Ǿ�̬�ġ�
			list1 = helper.getAllUser();// �õ����б���״̬Ϊprivacy���û���list
			//
			lv = (ListView) findViewById(R.id.listview); // ����ListView����
			if (list1.size() == 0) {
				// Drawable nodata_bg =
				// getResources().getDrawable(R.drawable.nodata_bg);
				// mainLinearLayout.setBackgroundDrawable(nodata_bg);
				setTitle("û�в鵽�κ�����");
				System.out.println("û�в鵽�κ�����");
			}
			if (list1.size() > 0) {
				// mainLinearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.contact_bg));
				setTitle("��������");
				System.out.println("���������κ�����");
				//
			}
			// ��������adapter��������
			adapter = new SimpleAdapter(MainActivity.this, list1,
					R.layout.list_item,
					new String[] { "name", "knowledgeTyp", "indication",
							"type", "level", "answer", "subject" }, new int[] {
							R.id.NO1, R.id.NO2, R.id.NO3, R.id.NO4, R.id.NO5,
							R.id.NO6, R.id.NO7 });

			lv.setAdapter(adapter);// �����Ϻõ�adapter����listview����ʾ���û���
			}}
	}
}
