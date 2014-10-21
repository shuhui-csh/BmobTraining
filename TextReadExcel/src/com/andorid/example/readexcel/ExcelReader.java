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
 * ��ȡExcel���
 */
public class ExcelReader {
	
	/** �ļ������������� */
	private InputStream is = null;
	
	/** ��ǰ��Sheet��� */
	private int currSheet;
	
	/** ��ǰλ�� */
	private int currPosition;
	
	/** Sheet������� */
	private int numOfSheets;
	
	/** HSSFWorkbook */
	HSSFWorkbook workbook = null;
	
	/** �������ڸ�ʽ */
	private final String DATE_FORMAT = "yyyy-MM-dd";
	
	/**
	 * ���캯������һ��ExcelReader
	 * 
	 * @param path
	 * @throws IOException
	 * @throws Exception
	 */
	public ExcelReader(String path) throws IOException, Exception {
		// �жϲ����Ƿ�Ϊ�ջ���û������
		if (path == null || path.trim().equals("")) {
			throw new IOException("no input file specified");
		}
		// ���ÿ�ʼ��Ϊ0
		currPosition = 0;
		// ���õ�ǰλ��Ϊ0
		currSheet = 0;
		// �����ļ�������
		is = new FileInputStream(path);

		workbook = new HSSFWorkbook(is);
		// ����Sheet��
		numOfSheets = workbook.getNumberOfSheets();
	}

	/**
	 * ���캯������һ��ExcelReader
	 * 
	 * @param inputStream
	 * @throws IOException
	 * @throws Exception
	 */
	public ExcelReader(InputStream inputStream) throws IOException, Exception {
		// �жϲ����Ƿ�Ϊ�ջ���û������
		if (inputStream == null) {
			throw new IOException("no inputStream specified");
		}
		// ���ÿ�ʼ��Ϊ0
		currPosition = 0;
		// ���õ�ǰλ��Ϊ0
		currSheet = 0;
		// �����ļ�������
		this.is = inputStream;

		workbook = new HSSFWorkbook(is);
		// ����Sheet��
		numOfSheets = workbook.getNumberOfSheets();

	}

	/**
	 * ���Excel�������
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

			//��������б�ĩβ��ʼɾ������
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
				else{//����elseѭ�����б������һ�������ݵ�һ�о�ֹͣ������else���б���Ϊ�յ���ȫ��ȥ��
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * ����readLine��ȡ�ļ���һ��
	 * 
	 * @return
	 * @throws IOException
	 */
	public String[] readLine() throws IOException {

		// ͨ��POI�ṩ��API��ȡ�ļ�
		// ����currSheet��ֵ��ȡ��ǰ��sheet
		HSSFSheet sheet = workbook.getSheetAt(currSheet);
		
		//�жϵ�ǰ���Ƿ񵽵�ǰSheet�Ľ�β
		//����������ȡһ��excel�ļ��еĶ���sheet���
		if (currPosition > sheet.getLastRowNum()) {
			// ��ǰ��λ������
			currPosition = 0;
			// �ж��Ƿ���Sheet
			while (++currSheet < numOfSheets) {
				// �õ���һ��sheet
				sheet = workbook.getSheetAt(currSheet);

				// ��ǰ�����Ƿ��Ѵﵽ�ļ�ĩβ
				if (currPosition == sheet.getLastRowNum()) {
					continue;
				} else {
					// ��ȡ��ǰ����
					int row = currPosition;
					currPosition++;
					// ��ȡ��ǰ������
					return getLine(sheet, row);
				}
			}
			return null;
		}
		// ��ȡ��ǰ����
		int row = currPosition;
		currPosition++;
		// ��ȡ��ǰ������
		return getLine(sheet, row);
	}

	/**
	 * ����getLine����Sheet��һ������
	 * 
	 * @param sheet
	 * @param row
	 * @return
	 */
	private String[] getLine(HSSFSheet sheet, int row) {
		// ��������ȡ��Sheet��һ��
		HSSFRow rowline = sheet.getRow(row);
		
		// ��ȡ��ǰ�е�����
		int fieldColumns = rowline.getLastCellNum();
		
		// ������
		String[] str = new String[fieldColumns];
				
		HSSFCell cell = null;
		// ����������
		for (int i = 0; i < fieldColumns; i++) {
			// ȡ�õ�ǰCell
			cell = rowline.getCell(i);
			String cellvalue = null;
			if (cell != null) {
				
				cellvalue = getCellValue(cell);
				
			} else {
				cellvalue = "";
			}
			// ��������
			str[i] = cellvalue;
		}
		
		return str;
	}

	/**
	 * ͨ��excel fx������ȡֵ
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
				// �жϵ�ǰ��Cell��Type�Ƿ�ΪDate
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// �����Date���ͣ�ȡ�ø�Cell��Dateֵ
					try {
						value = new SimpleDateFormat(DATE_FORMAT).format(cell
								.getDateCellValue());
					} catch (IllegalArgumentException ex) {
						ex.printStackTrace();
					}

				} else {// ����Ǵ�����
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
	 * close����ִ�����Ĺرղ���
	 */
	public void close() {
		// ���is��Ϊ�գ���ر�InputStream�ļ�������
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
