package com.yingo.dbtext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.yingo.dbtext.User;

public class DBHelper {
	
	public static final String DB_DBNAME="contact";
	
	public static final String DB_TABLENAME="user";
	
	public static final int VERSION = 4;
	
	public static SQLiteDatabase dbInstance; 
	
	private MyDBHelper myDBHelper;
	
	private StringBuffer tableCreate;
	
	private Context context;
	
	public DBHelper(Context context) {
		this.context = context;
	}
	
	public void openDatabase() { //SQLiteOpenHelper中的一个方法，通过这个方法得到数据库的实例
		if(dbInstance == null) {
			myDBHelper = new MyDBHelper(context,DB_DBNAME,VERSION);
			dbInstance = myDBHelper.getWritableDatabase();//查系统的资源，没有就调用OnCreate的方法，通过getWritableDatabase方法获得实例
		}
	}
	/**
	 * 往数据库里面的user表插入一条数据，若失败返回-1
	 * @param user
	 * @return   失败返回-1
	 */
	
	public long insert(User user) {
		ContentValues values = new ContentValues();
		values.put("name", user.No);
		values.put("knowledgeTyp", user.knowledgeTyp);
		values.put("indication", user.indication);
		values.put("type", user.type);
		values.put("level", user.level);
		values.put("answer", user.answer);
		values.put("subject",user.subject);
		
		return dbInstance.insert(DB_TABLENAME, null, values);
	}
	
	/**
	 * 获得数据库中所有的用户，将每一个用户放到一个map中去，然后再将map放到list里面去返回
	 * @param privacy 
	 * @return list
	 */
	
	public ArrayList getAllUser() {
		ArrayList list = new ArrayList();
		Cursor cursor = null;
	
			cursor = dbInstance.query(DB_TABLENAME, 
					new String[]{"_id","name","knowledgeTyp","indication","type","level","answer","subject",}, 
					null,
					null, 
					null, 
					null, 
					null);
		
		
		while(cursor.moveToNext()) {
			HashMap item = new HashMap();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("name", cursor.getString(cursor.getColumnIndex("name")));
			item.put("knowledgeTyp", cursor.getString(cursor.getColumnIndex("knowledgeTyp")));
			item.put("indication", cursor.getString(cursor.getColumnIndex("indication")));
			item.put("type", cursor.getString(cursor.getColumnIndex("type")));
			item.put("level", cursor.getString(cursor.getColumnIndex("level")));
			item.put("answer", cursor.getString(cursor.getColumnIndex("answer")));
			item.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
			
			list.add(item);
		}
		
		return list;
	}

	
	public void modify(User user) {
		ContentValues values = new ContentValues();
		values.put("name", user.No);
		values.put("knowledgeTyp", user.knowledgeTyp);
		values.put("indication", user.indication);
		values.put("type", user.type);
		values.put("level", user.level);
		values.put("answer", user.answer);
		values.put("subject",user.subject);
		
		
		dbInstance.update(DB_TABLENAME, values, "_id=?", new String[]{String.valueOf(user._id)});
	}
	
	public void delete(int _id) {
		dbInstance.delete(DB_TABLENAME, "_id=?", new String[]{String.valueOf(_id)});
	}
	public void deleteAll(int privacy) {
		dbInstance.delete(DB_TABLENAME, "privacy=?", new String[]{String.valueOf(privacy)});
	}
	
	public int getTotalCount() {
		Cursor cursor = dbInstance.query(DB_TABLENAME, new String[]{"count(*)"}, null, null, null, null, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}
	
	
	public ArrayList getUsers(String condition, boolean privacy) {
		ArrayList list = new ArrayList();
		String strSelection = "";
		if(privacy) {
			strSelection = "and privacy = 1";
		} else {
			strSelection = "and privacy = 0";
		}
		String sql = "select * from " + DB_TABLENAME + " where 1=1 and (name like '%" + condition + "%' " +
				"or knowledgeTyp like '%" + condition + "%' or type like '%" + condition + "%' " +
						"or indication like '%" + condition + "%')" + strSelection;
		Cursor cursor = dbInstance.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			HashMap item = new HashMap();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("name", cursor.getString(cursor.getColumnIndex("name")));
			item.put("knowledgeTyp", cursor.getString(cursor.getColumnIndex("knowledgeTyp")));
			item.put("indication", cursor.getString(cursor.getColumnIndex("indication")));
			item.put("type", cursor.getString(cursor.getColumnIndex("type")));
			item.put("level", cursor.getString(cursor.getColumnIndex("level")));
			item.put("answer", cursor.getString(cursor.getColumnIndex("answer")));
			item.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
			list.add(item);
		}
		return list;
	}
	
	public void deleteMarked(ArrayList<Integer> deleteId) {
		StringBuffer  strDeleteId = new StringBuffer();
		strDeleteId.append("_id=");
		for(int i=0;i<deleteId.size();i++) {
			if(i!=deleteId.size()-1) {
				strDeleteId.append(deleteId.get(i) + " or _id=");
			} else {
				strDeleteId.append(deleteId.get(i));
			}
			
			
		}
		dbInstance.delete(DB_TABLENAME, strDeleteId.toString(), null);
		System.out.println(strDeleteId.toString());
		
	}
	
	public void backupData(boolean privacy) {
		StringBuffer sqlBackup = new StringBuffer();
		Cursor cursor = null;
		if(privacy) {
			cursor = dbInstance.query(DB_TABLENAME, 
					new String[]{"_id","name","knowledgeTyp","indication","type","level","answer","subject",}, 
					"privacy=1", 
					null, 
					null, 
					null, 
					null);
		} else {
			cursor = dbInstance.query(DB_TABLENAME, 
					new String[]{"_id","name","knowledgeTyp","indication","type","level","answer","subject",}, 
					"privacy=0",
					null, 
					null, 
					null, 
					null);
		}
		 
		
		while(cursor.moveToNext()) {
			sqlBackup.append("insert into " + DB_TABLENAME + "(name,knowledgeTyp,indication,type,level,answer,subject,position,company,zipcode,remark,imageid,privacy)")
			.append(" values ('")
			.append(cursor.getString(cursor.getColumnIndex("name"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("knowledgeTyp"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("indication"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("type"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("level"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("answer"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("subject"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("position"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("company"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("zipcode"))).append("','")
			.append(cursor.getString(cursor.getColumnIndex("remark"))).append("',")
			.append(cursor.getInt(cursor.getColumnIndex("imageid"))).append(",")
			.append(cursor.getInt(cursor.getColumnIndex("privacy")))
			.append(");").append("\n");
		}
		saveDataToFile(sqlBackup.toString(),privacy);
		
	}

	
	private void saveDataToFile(String strData,boolean privacy) {
		String fileName = "";
		if(privacy) {
			fileName = "priv_data.bk";
		} else {
			fileName = "comm_data.bk";
		}
		try {
		String SDPATH = Environment.getExternalStorageDirectory() + "/";
		File fileParentPath = new File(SDPATH + "zpContactData/");
		fileParentPath.mkdirs();
		File file = new File(SDPATH + "zpContactData/" + fileName);
		System.out.println("the file previous path = " + file.getAbsolutePath());
		
		file.createNewFile();
		System.out.println("the file next path = " + file.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(file);
		
		fos.write(strData.getBytes());
		fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void restoreData(String fileName) {
		try {
		String SDPATH = Environment.getExternalStorageDirectory() + "/";
		File file = null;
		if(fileName.endsWith(".bk")) {
			file = new File(SDPATH + "zpContactData/"+ fileName);
		} else {
			file = new File(SDPATH + "zpContactData/"+ fileName + ".bk");
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = "";
		while((str=br.readLine())!=null) {
			System.out.println(str);
			dbInstance.execSQL(str);
		}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean findFile(String fileName) {
		String SDPATH = Environment.getExternalStorageDirectory() + "/";
		File file = null;
		if(fileName.endsWith(".bk")) {
			file = new File(SDPATH + "zpContact/"+fileName);
		} else {
			file = new File(SDPATH + "zpContact/"+fileName + ".bk");
		}
		
		if(file.exists()) {
			return true;
		} else {
			return false;
		}
		
		
	}


	class MyDBHelper extends SQLiteOpenHelper {

		public MyDBHelper(Context context, String name,
				int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {//没有数据库的情况下只执行一次，只执行一次
			tableCreate = new StringBuffer();
			tableCreate.append("create table ")
					   .append(DB_TABLENAME)
					   .append(" (")
					   .append("_id integer primary key autoincrement,")
					   .append("name text,")
					   .append("knowledgeTyp text,")
					   .append("indication text,")
					   .append("type text,")
					   .append("level text,")
					   .append("answer text,")
					   .append("subject text ")
					   
					   .append(")");
			System.out.println(tableCreate.toString());
			db.execSQL(tableCreate.toString());
		}

		@Override//，主要跟版本比较，比较版本，当一样时什么都不做，不一样时数据清除在重新建表
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "drop table if exists " + DB_TABLENAME;
			db.execSQL(sql);
			myDBHelper.onCreate(db);
		}
		
	}


	


	
	
}
