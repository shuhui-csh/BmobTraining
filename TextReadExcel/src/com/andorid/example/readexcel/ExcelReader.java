package com.andorid.example.readexcel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/**
 * 读取Excel表格
 */
public class ExcelReader {
	
	/** 文件二进制输入流 */
	private InputStream is = null;
	
	/** 当前的Sheet表格 */
	private int currSheet;
	
	/** 当前位置 */
	private int currPosition;
	
	/** Sheet表格数量 */
	private int numOfSheets;
	
	/** HSSFWorkbook */
	HSSFWorkbook workbook = null;
	
	/** 设置日期格式 */
	private final String DATE_FORMAT = "yyyy-MM-dd";
	
	/**
	 * 构造函数创建一个ExcelReader
	 * 
	 * @param path
	 * @throws IOException
	 * @throws Exception
	 */
	public ExcelReader(String path) throws IOException, Exception {
		// 判断参数是否为空或者没有意义
		if (path == null || path.trim().equals("")) {
			throw new IOException("no input file specified");
		}
		// 设置开始行为0
		currPosition = 0;
		// 设置当前位置为0
		currSheet = 0;
		// 创建文件输入流
		is = new FileInputStream(path);

		workbook = new HSSFWorkbook(is);
		// 设置Sheet数
		numOfSheets = workbook.getNumberOfSheets();
	}

	/**
	 * 构造函数创建一个ExcelReader
	 * 
	 * @param inputStream
	 * @throws IOException
	 * @throws Exception
	 */
	public ExcelReader(InputStream inputStream) throws IOException, Exception {
		// 判断参数是否为空或者没有意义
		if (inputStream == null) {
			throw new IOException("no inputStream specified");
		}
		// 设置开始行为0
		currPosition = 0;
		// 设置当前位置为0
		currSheet = 0;
		// 创建文件输入流
		this.is = inputStream;

		workbook = new HSSFWorkbook(is);
		// 设置Sheet数
		numOfSheets = workbook.getNumberOfSheets();

	}

	/**
	 * 获得Excel表格内容
	 * @return
	 */
	public List<String[]> getExcelContent(){
		
		List<String[]> list = new ArrayList<String[]>();
		try {
			String[] line = readLine();
			while (line != null) {
				
				list.add(line);
				line = readLine();
			}
			close();

			//在这里从列表末尾开始删除空行
			boolean isNull = false;
			
			for(int i=list.size()-1; i>=0; i--){
				
				for(int j=0; j<list.get(i).length; j++){
					if(!isEmpty(list.get(i)[j])){
						isNull = false;
						break;
					}else{
						isNull = true;
					}
				}
				if(isNull){
					list.remove(i);
				}
				else{//加上else循环到列表中最后一条有内容的一行就停止，不加else将列表中为空的行全部去掉
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 函数readLine读取文件的一行
	 * 
	 * @return
	 * @throws IOException
	 */
	public String[] readLine() throws IOException {

		// 通过POI提供的API读取文件
		// 根据currSheet的值获取当前的sheet
		HSSFSheet sheet = workbook.getSheetAt(currSheet);
		
		//判断当前行是否到当前Sheet的结尾
		//这里用来读取一个excel文件中的多张sheet表格
		if (currPosition > sheet.getLastRowNum()) {
			// 当前行位置清零
			currPosition = 0;
			// 判断是否还有Sheet
			while (++currSheet < numOfSheets) {
				// 得到下一张sheet
				sheet = workbook.getSheetAt(currSheet);

				// 当前行数是否已达到文件末尾
				if (currPosition == sheet.getLastRowNum()) {
					continue;
				} else {
					// 获取当前行数
					int row = currPosition;
					currPosition++;
					// 读取当前行数据
					return getLine(sheet, row);
				}
			}
			return null;
		}
		// 获取当前行数
		int row = currPosition;
		currPosition++;
		// 读取当前行数据
		return getLine(sheet, row);
	}

	/**
	 * 函数getLine返回Sheet的一行数据
	 * 
	 * @param sheet
	 * @param row
	 * @return
	 */
	private String[] getLine(HSSFSheet sheet, int row) {
		// 根据行数取得Sheet的一行
		HSSFRow rowline = sheet.getRow(row);
		
		// 获取当前行的列数
		int fieldColumns = rowline.getLastCellNum();
		
		// 保存结果
		String[] str = new String[fieldColumns];
				
		HSSFCell cell = null;
		// 遍历所有列
		for (int i = 0; i < fieldColumns; i++) {
			// 取得当前Cell
			cell = rowline.getCell(i);
			String cellvalue = null;
			if (cell != null) {
				
				cellvalue = getCellValue(cell);
				
			} else {
				cellvalue = "";
			}
			// 保存数据
			str[i] = cellvalue;
		}
		
		return str;
	}

	/**
	 * 通过excel fx函数获取值
	 * 
	 * @param cell
	 * @return
	 */
	public String getCellValue(HSSFCell cell) {
		String value = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_FORMULA:
				// cell.getCellFormula();
				try {
					value = String.valueOf(cell.getNumericCellValue());
				} catch (IllegalStateException e) {
					value = String.valueOf(cell.getRichStringCellValue());
				}
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				// value = String.valueOf(cell.getNumericCellValue());
				// 判断当前的Cell的Type是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型，取得该Cell的Date值
					try {
						value = new SimpleDateFormat(DATE_FORMAT).format(cell
								.getDateCellValue());
					} catch (IllegalArgumentException ex) {
						ex.printStackTrace();
					}

				} else {// 如果是纯数字
					value = String.valueOf(cell.getNumericCellValue());
				}
				break;
			case HSSFCell.CELL_TYPE_STRING:
				value = String.valueOf(cell.getRichStringCellValue());
				break;
			}
		}
//		System.out.println(value);

		return value;
	}
	
	/**
	 * close函数执行流的关闭操作
	 */
	public void close() {
		// 如果is不为空，则关闭InputStream文件输入流
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				is = null;
			}
		}
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
}
