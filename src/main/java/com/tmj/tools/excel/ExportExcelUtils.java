package com.tmj.tools.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static int SIZE_OF_EACH_SHEET = 500;
	
	private static int SIZE_OF_EACH_WORKBOOK = 10000;
	
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	private static final int _3_M = 3  *1024*1024;
	
	private static final String SYSTEMP = FileUtils.sysTemp();

	private String fileName ;
	
	private int lastSheetIndex;
	
	public ExportExcelUtils(String fileName){
		this.fileName  = fileName + fileFormat.format(new Date());
	}
	/**
	 * List<T> 转成<HSSFWorkbook> ,并写入<HttpServletResponse>
	 * @param datas 数据
	 * @param columns 要导出列
	 * @param sheetSize 每个sheet页尺寸
	 * @param request 
	 * @param response
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 */
	public void exportExcel(List<T> datas,List<Column> columns,int sheetSize,HttpServletRequest request,HttpServletResponse response) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException{
		exportExcel(createWorkbook(datas, columns, sheetSize),request, response, fileName);
	}
	/**
	 * 分页添加数据到<HSSFWorkbook>
	 * @param datas
	 * @param columns
	 * @param sheetSize
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public void addDatas(List<T> datas,List<Column> columns,int sheetSize) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		HSSFWorkbook workbook = createWorkbook(datas, columns, sheetSize);
		try {
			Path path = Paths.get(SYSTEMP+fileName+".xls");
			Files.delete(path);
		} catch (IOException e) {
		}
		write2Path(workbook, SYSTEMP+fileName+".xls");
	}
	/**
	 * 下载分页添加的数据
	 * @param response
	 */
	public void download(HttpServletResponse response){
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition",
			        "attachment;filename="+fileName+".xls");
			OutputStream out = response.getOutputStream();
			HSSFWorkbook workbook = readFromPath(SYSTEMP+fileName+".xls");
			workbook.write(out);
			fileName = null;
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * <HSSFWorkbook>写入<HttpServletResponse>
	 * @param workbook
	 * @param request
	 * @param response
	 * @param fileName
	 * @throws IOException
	 */
	private void exportExcel(HSSFWorkbook workbook,HttpServletRequest request, HttpServletResponse response,String fileName) throws IOException{
		fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition",
                "attachment;filename="+fileName+".xls");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
	}

	/**
	 * List<T> 转成多个<HSSFWorkbook> ,打成zip包并写入<HttpServletResponse>
	 * @param datas 数据
	 * @param columns 导出项
	 * @param workbookSize 每个workbook尺寸
	 * @param sheetSize 每个sheet页尺寸
	 * @param req
	 * @param res
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 */
	public void exportExcelWithZip(List<T> datas,List<Column> columns,int workbookSize,int sheetSize,HttpServletRequest req,HttpServletResponse res) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException{
		if(workbookSize>0) SIZE_OF_EACH_WORKBOOK = workbookSize;
		OutputStream out = res.getOutputStream();
		res.setContentType("application/octet-stream;charset=UTF-8");
		res.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8") + ".zip");
		List<String> fileNames = new ArrayList<String>();// 用于存放生成的文件名称
		File zip = new File(SYSTEMP+ URLEncoder.encode(fileName, "UTF-8") + ".zip");// 压缩文件  
		int totalSize = datas.size();
		int size = totalSize%SIZE_OF_EACH_WORKBOOK==0?totalSize/SIZE_OF_EACH_WORKBOOK:totalSize/SIZE_OF_EACH_WORKBOOK+1;//workbook的数量
		File[] files = new File[size];
		for(int i=0;i<size;i++){
			HSSFWorkbook workbook = createWorkbook(datas.subList(i*SIZE_OF_EACH_WORKBOOK, i<size-1?(i+1)*SIZE_OF_EACH_WORKBOOK:totalSize), columns, sheetSize);
			String file = SYSTEMP+ fileName + "-" + i + ".xls";
			fileNames.add(file);
			write2Path(workbook, file);
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
	 * 创建workbook
	 * @param datas 数据
	 * @param columns
	 * @param sheetSize
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	private HSSFWorkbook createWorkbook(List<T> datas,List<Column> columns,int sheetSize) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		if(sheetSize > 0) SIZE_OF_EACH_SHEET = sheetSize;
		HSSFWorkbook workbook = null;                // workbook
		if(fileName!=null) workbook = readFromPath(SYSTEMP+fileName+".xls");
		if(workbook==null) workbook = new HSSFWorkbook();
		CellStyle headStyle = createCellStyel(workbook, 12, true); // 12号字，加粗
		CellStyle bodyStyle = createCellStyel(workbook, 10, false);// 10号字，不加粗
		multiSheet(workbook, datas, columns, headStyle, bodyStyle);
		return workbook;
	}
	/**
	 * 分多个sheet
	 * @param workbook
	 * @param datas
	 * @param columns
	 * @param headStyle
	 * @param bodyStyle
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	private void multiSheet(HSSFWorkbook workbook,List<T> datas,List<Column> columns,CellStyle headStyle,CellStyle bodyStyle) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		HSSFSheet lastSheet = null;
		if(lastSheetIndex==0){
			lastSheet = workbook.createSheet();
			lastSheetIndex++;
		}
		else lastSheet = workbook.getSheetAt(lastSheetIndex-1);
		int lastRowNum = lastSheet.getLastRowNum();
		if(lastRowNum==SIZE_OF_EACH_SHEET){
			lastSheet = workbook.createSheet();//最后一页已经写满
			lastSheetIndex++;
		}
		int dataNeeded = SIZE_OF_EACH_SHEET;
		int fromIndex = 0;
		int toIndex = 0;
		if(lastRowNum>0) dataNeeded -= lastRowNum;
		int allDataSize = datas.size();
		int remainDataSize = allDataSize;
		List<T> subList = null;
		while(toIndex<allDataSize){
			if(dataNeeded>remainDataSize) dataNeeded = remainDataSize;
			toIndex += dataNeeded;
			subList = datas.subList(fromIndex, toIndex);
			eachSheet(lastSheet,subList, columns,headStyle,bodyStyle);
			lastRowNum = lastSheet.getLastRowNum();
			if(lastRowNum==SIZE_OF_EACH_SHEET) {
				lastSheet = workbook.createSheet();//最后一页已经写满
				lastSheetIndex++;
			}
			fromIndex = toIndex;
			remainDataSize -= dataNeeded;
		}
	}	
	/**
	 * 处理每一个sheet
	 * @param sheet
	 * @param datas
	 * @param columns
	 * @param headStyle
	 * @param bodyStyle
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	private void eachSheet(HSSFSheet sheet,List<T> datas,List<Column> columns,CellStyle headStyle,CellStyle bodyStyle) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		int lastRowNum = sheet.getLastRowNum();
		int rowSize = lastRowNum;
		Collections.sort(columns);		
		if(lastRowNum==0){
			Row head = sheet.createRow(rowSize++);        // excel 表头
			//设置表头信息
			int totalColumn = columns.size();
			for (int i = 0; i < totalColumn; i++) {
				HSSFCell cell = (HSSFCell) head.createCell(i);
				cell.setCellValue(columns.get(i).getAlias());
				cell.setCellStyle(headStyle);
			}
		}
		for(T t : datas){                                       // 处理集合中每一个元素
			Row body = sheet.createRow(rowSize++);              // 创建下一行
			Class<T> clazz = (Class<T>) t.getClass();
			int columnSize = 0;
			for(Column c : columns){
				Field field = t.getClass().getDeclaredField(c.getName());
				Class<?> type = field.getType();
				String methodName = c.getName();
				if(type == boolean.class /*|| type == Boolean.class*/)
					methodName = "is"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1,methodName.length());
				else 
					methodName = "get"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1,methodName.length());
				Method method = clazz.getMethod(methodName); 
				Object value = method.invoke(t);
				Map<Object,Object> converts = c.getConverts();
				if(converts!=null)  value = converts.get(value)!=null?converts.get(value):value;
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
	/**
	 * 设置单元格样式
	 * @param workbook
	 * @param fontSize
	 * @param bold
	 * @return
	 */
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
	/**
	 * 将workbook写到文件中
	 * @param workbook
	 * @param filePath
	 */
	private void write2Path(HSSFWorkbook workbook,String filePath){
		try(FileOutputStream fos = new FileOutputStream(filePath);){
			workbook.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从文件得到HSSFWorkbook
	 * @param filePath
	 * @return
	 */
	private HSSFWorkbook readFromPath(String filePath){
		HSSFWorkbook workbook = null;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			workbook = new HSSFWorkbook(fis);
		} catch (Exception e) {
		}
		return workbook;
	}
	//根据不同浏览器设置不同编码 not work
//	private void encodeFileName(HttpServletRequest request, String fileName) {
//		final String userAgent = request.getHeader("USER-AGENT");
//		try {
//			if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
//				fileName = URLEncoder.encode(fileName,"UTF8");
//			}else if(StringUtils.contains(userAgent, "Mozilla")){//火狐浏览器
//				fileName = new String(fileName.getBytes(), "ISO8859-1");
//			}else{
//				fileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
//			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 导出Excel辅助类
	 * @author Tangmj
	 */
	public static class Column implements Comparable<Column>{
		private final String name;    //导出项列属性名
		private final String alias;   //导出项列别名
		private final int order;      //导出项列顺序
		private Map<Object,Object> converts = new HashMap<Object,Object>(); //列转换
		
		public String getName() {
			return name;
		}
		public String getAlias() {
			return alias;
		}
		public int getOrder() {
			return order;
		}
		public Map<Object,Object> getConverts(){
			return converts;
		}
		public Column(String name,String alias,int order,Map<Object,Object> converts){
			this.name = name;
			this.alias = alias==null?name.toUpperCase():alias;
			this.order = order;
			this.converts = converts;
		}
		@Override
		public int compareTo(Column that) {
			return this.order-that.order;
		}
	}
}
