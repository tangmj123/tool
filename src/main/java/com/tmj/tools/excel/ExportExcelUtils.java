package com.tmj.tools.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import com.tmj.tools.file.FileUtils;
/**
 * 导出excel工具
 * @author Tangmj
 *
 * @param <T>
 */
public class ExportExcelUtils<T> {
	private static int SIZE_OF_EACH_SHEET = 100;
	
	private static int SIZE_OF_EACH_WORKBOOK = 10000;
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final int _3_M = 3  *1024*1024;
	/**
	 *  <HSSFWorkbook>写入<HttpServletResponse>
	 * @param workbook
	 * @param response
	 * @param fileName excel文件名
	 * @throws IOException
	 */
	public void exportExcel(HSSFWorkbook workbook,HttpServletResponse response,String fileName) throws IOException{
		fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
		response.setContentType("octets/stream");
        response.addHeader("Content-Disposition",
                "attachment;filename="+java.net.URLEncoder.encode(fileName, "UTF-8")+".xls");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
	}
	/**
	 * List<T> 转成<HSSFWorkbook> ,并写入<HttpServletResponse>
	 * @param datas 数据
	 * @param columns 要导出的项
	 * @param sheetSize 每个sheet页尺寸
	 * @param response 响应
	 * @param fileName 文件名
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public void exportExcel(List<T> datas,List<Column> columns,int sheetSize,HttpServletResponse response,String fileName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
		exportExcel(createWorkbook(datas, columns, sheetSize), response, fileName);
	}
	
	/**
	 *  List<T> 转成多个<HSSFWorkbook> ,打成zip包并写入<HttpServletResponse>
	 * @param datas 数据
	 * @param columns 导出项
	 * @param workbookSize 每个workbook尺寸
	 * @param sheetSize 每个sheet页尺寸
	 * @param req 
	 * @param res
	 * @param fileName
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public void exportExcelWithZip(List<T> datas,List<Column> columns,int workbookSize,int sheetSize,HttpServletRequest req,HttpServletResponse res,String fileName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
		if(workbookSize>0) SIZE_OF_EACH_WORKBOOK = workbookSize;
		OutputStream out = res.getOutputStream();
		res.setContentType("application/octet-stream;charset=UTF-8");
		res.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8") + ".zip");
		List<String> fileNames = new ArrayList();// 用于存放生成的文件名称s 
		File zip = new File(req.getRealPath("/") +java.io.File.separator+ fileName + ".zip");// 压缩文件  
		int totalSize = datas.size();
		int size = totalSize%SIZE_OF_EACH_WORKBOOK==0?totalSize/SIZE_OF_EACH_WORKBOOK:totalSize/SIZE_OF_EACH_WORKBOOK+1;
		File[] files = new File[size];
		for(int i=0;i<size;i++){
			HSSFWorkbook workbook = createWorkbook(datas.subList(i*SIZE_OF_EACH_WORKBOOK, i<sheetSize-1?(i+1)*SIZE_OF_EACH_WORKBOOK:totalSize), columns, sheetSize);
			String file = req.getRealPath("/") + java.io.File.separator + fileName + "-" + i + ".xls";
			fileNames.add(file);
			try(FileOutputStream fos = new FileOutputStream(file)){
				workbook.write(fos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < files.length; i++) {
			files[i] = new File(fileNames.get(i));
		}
		FileUtils.zipFiles(files, zip);
		try (FileInputStream fis = new FileInputStream(zip)){
			byte[] buffer = new byte[1024];
			int len = 0;
			while(((len=fis.read(buffer)))!=-1){
				out.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//删除临时文件
		if(zip.exists()) zip.delete();
		for (File f:files) {
			if(f.exists()) f.delete();
		}
	}
	/**
	 * 用 List<T> 创建<HSSFWorkbook>
	 * @param datas 数据
	 * @param columns 导出项
	 * @param sheetSize 每个sheet页尺寸
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public HSSFWorkbook createWorkbook(List<T> datas,List<Column> columns,int sheetSize) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(sheetSize > 0) SIZE_OF_EACH_SHEET = sheetSize;
		HSSFWorkbook workbook = new HSSFWorkbook();                // workbook
		CellStyle headStyle = createCellStyel(workbook, 12, true); // 12号字，加粗
		CellStyle bodyStyle = createCellStyel(workbook, 10, false);// 10号字，不加粗
		Collections.sort(columns);				                   // 列排序
		List<String> methodNames = new ArrayList<String>();
		for(Column c : columns){
			String methodName = c.getName();
			methodName = "get"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1,methodName.length());
			methodNames.add(methodName);
		}
		multiSheet(workbook, datas, columns,methodNames, headStyle, bodyStyle);
		return workbook;
	}
	
	private void multiSheet(HSSFWorkbook workbook,List<T> datas,List<Column> columns,List<String> methodNames,CellStyle headStyle,CellStyle bodyStyle) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		int dataSize = datas.size();
		int sheetSize = dataSize%SIZE_OF_EACH_SHEET==0?dataSize/SIZE_OF_EACH_SHEET:dataSize/SIZE_OF_EACH_SHEET+1;
		for(int i=0;i<sheetSize;i++)
			eachSheet(workbook,datas.subList(i*SIZE_OF_EACH_SHEET, i<sheetSize-1?(i+1)*SIZE_OF_EACH_SHEET:dataSize), columns,methodNames,headStyle,bodyStyle);
	}
	
	private void eachSheet(HSSFWorkbook workbook,List<T> datas,List<Column> columns,List<String> methodNames,CellStyle headStyle,CellStyle bodyStyle) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		HSSFSheet sheet =  workbook.createSheet();    // sheet
		int rowSize = 0;
		Row head = sheet.createRow(rowSize++);        // excel 表头
		
		int totalColumn = columns.size();
		
		//设置表头信息
		for (int i = 0; i < totalColumn; i++) {
			HSSFCell cell = (HSSFCell) head.createCell(i);
			cell.setCellValue(columns.get(i).getAlias());
			cell.setCellStyle(headStyle);
		}
		
		for(T t : datas){                                       // 处理集合中每一个元素
			Row body = sheet.createRow(rowSize++);              // 创建下一行
			Class<T> clazz = (Class<T>) t.getClass();
			int columnSize = 0;
			for(String m : methodNames){
				Method method = clazz.getMethod(m);              // 得到getXXX() 方法
				Object value = method.invoke(t);                 // 反射得到值
				setCellValue(body, columnSize++, value,bodyStyle);
			}
		}
	}
	
	/***
	 * 给<Cell>设置值和样式
	 * @param row
	 * @param columnSize
	 * @param value
	 * @param cellStyle
	 */
	private void setCellValue(Row row, int columnSize, Object value,CellStyle cellStyle) {
		HSSFCell cell;
		cell = (HSSFCell) row.createCell(columnSize); // 创建下一列
		//Integer Long Float Double Date Boolean         // 填充值
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if(value instanceof Long) {
			cell.setCellValue((Integer) value);
		} else if(value instanceof Float) {
			cell.setCellValue((Integer) value);
		} else if(value instanceof Double) {
			cell.setCellValue((Integer) value);
		} else if(value instanceof String){
			cell.setCellValue((String) value);
		} else if(value instanceof Date){
			cell.setCellValue(format.format(value));
		} else if(value instanceof Boolean){
			cell.setCellValue((Boolean) value);
		}
		cell.setCellStyle(cellStyle);
	}
	
	private CellStyle createCellStyel(HSSFWorkbook workbook,int fontSize,boolean bold){
		CellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        // 把字体应用到当前的样式
        style.setFont(font);
		return style;
	}
	public static class Column implements Comparable<Column>{
		private final String name;    //导出项列属性名
		private final String alias;   //导出项列别名
		private final int order;      //导出项列顺序
		
		public String getName() {
			return name;
		}
		public String getAlias() {
			return alias;
		}
		public int getOrder() {
			return order;
		}
		public Column(String name,String alias,int order){
			this.name = name;
			this.alias = alias;
			this.order = order;
		}
		@Override
		public int compareTo(Column that) {
			return this.order-that.order;
		}
	}
	
}
