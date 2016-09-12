package com.tmj.tools.excel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
 * 处理excel和csv工具类
 *
 * @param <T>
 * @author Tangmj
 */
@SuppressWarnings({"unused","unchecked"})
public class FileExportUtils<T> {
    private  int SIZE_OF_EACH_SHEET = 65534;

    private  int SIZE_OF_EACH_WORKBOOK = 10000;

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

//    private  final int _3_M = 3 * 1024 * 1024;
    
    private static final String SYSTEMP = FileUtils.sysTemp();

    private String fileName;
    
    private FileType fileType;
    
    public String getFileName() {
        return fileName;
    }

//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }

    
    private int lastSheetIndex;

    public FileExportUtils() {

    }

    public FileExportUtils(String fileName) {
        this.fileName = fileName + fileFormat.format(new Date());
        this.fileType = FileType.excel;           //默认用excel导出
    }

    
    public FileExportUtils(String fileName,FileType fileType){
    	this.fileName = fileName + fileFormat.format(new Date());
    	this.fileType = fileType;
    }
    /**
     * List<T> 转成<HSSFWorkbook> ,并写入<HttpServletResponse>
     *
     * @param datas     数据
     * @param columns   要导出列
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
    public void exportFile(List<T> datas, List<Column> columns, int sheetSize, HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException {
    	if(fileType.equals(FileType.excel)) {
    		if(sheetSize < 0 || sheetSize > SIZE_OF_EACH_SHEET) sheetSize = SIZE_OF_EACH_SHEET;
        	exportExcel(createWorkbook(datas, columns, sheetSize,filePath()), request, response, fileName);
    	}
        else if(fileType.equals(FileType.csv)){
        	exportCSV(datas, columns, request, response);
        }
    }
    /**
     * 导出csv文件
     * @author Tangmj
     * @param datas
     * @param columns
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    private void exportCSV(List<T> datas, List<Column> columns,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
    	fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition",
                "attachment;filename=" + fileName + fileExtension());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/x-excel");
    	try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));) {
			Collections.sort(columns);
			// 处理文件头
			int totalColumn = columns.size();
			StringBuffer header = new StringBuffer();
			for (int i = 0; i < totalColumn; i++) {
				header.append(columns.get(i).getAlias());
				if (i < (totalColumn - 1))
					header.append(",");
			}
			bw.write("\n");
			
			for (T t : datas) { // 处理集合中每一个元素
				Class<T> clazz = (Class<T>) t.getClass();
				StringBuffer line = new StringBuffer();
				for (int i = 0; i < totalColumn; i++) {
					Column c = columns.get(i);
					Field field = t.getClass().getDeclaredField(c.getName());
					Class<?> type = field.getType();
					String methodName = c.getName();
					if (type == boolean.class /* || type == Boolean.class */)
						methodName = "is"+ methodName.substring(0, 1).toUpperCase()+ methodName.substring(1, methodName.length());
					else
						methodName = "get"+ methodName.substring(0, 1).toUpperCase()+ methodName.substring(1, methodName.length());
					Method method = clazz.getMethod(methodName);
					Object value = method.invoke(t);
					Map<Object, Object> converts = c.getConverts();
					if (converts != null)
						value = converts.get(value) != null ? converts.get(value): value;
					line.append(value);
					if (i < (totalColumn - 1)) line.append(",");
				}
				bw.write(line.toString());
				bw.write("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	
    }

	/**
	 * 添加数据到文件中
     * 之后需要调用#FileExportUtils.finish()
	 * @author Tangmj
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
    public void addDatas(List<T> datas, List<Column> columns, int sheetSize) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
    	if(fileType.equals(FileType.excel)) {
    		if(sheetSize < 0 || sheetSize > SIZE_OF_EACH_SHEET) sheetSize = SIZE_OF_EACH_SHEET;
	    	addExcelDatas(datas, columns, sheetSize);
    	}else if(fileType.equals(FileType.csv)){
    		addCSVDatas(datas, columns);
    	}
    }
    /**
     * 向csv文件中添加数据
     * @author Tangmj
     * @param datas
     * @param columns
     */
	private void addCSVDatas(List<T> datas, List<Column> columns) {
    	Path path = Paths.get(tmpFilePath());
    	try(BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE);
    			BufferedReader br = Files.newBufferedReader(path)) {
			Collections.sort(columns);
			int totalColumn = columns.size();
			if(br.lines().count()==0){
				// 处理文件头
				StringBuffer header = new StringBuffer();
				for (int i = 0; i < totalColumn; i++) {
					header.append(columns.get(i).getAlias());
					if (i < (totalColumn - 1))
						header.append(",");
				}
				bw.write(header.toString());
				bw.write("\n");
			}
			for (T t : datas) { // 处理集合中每一个元素
				Class<T> clazz = (Class<T>) t.getClass();
				StringBuffer line = new StringBuffer();
				for (int i = 0; i < totalColumn; i++) {
					Column c = columns.get(i);
					Field field = t.getClass().getDeclaredField(c.getName());
					Class<?> type = field.getType();
					String methodName = c.getName();
					if (type == boolean.class /* || type == Boolean.class */)
						methodName = "is"+ methodName.substring(0, 1).toUpperCase()+ methodName.substring(1, methodName.length());
					else
						methodName = "get"+ methodName.substring(0, 1).toUpperCase()+ methodName.substring(1, methodName.length());
					Method method = clazz.getMethod(methodName);
					Object value = method.invoke(t);
					Map<Object, Object> converts = c.getConverts();
					if (converts != null)
						value = converts.get(value) != null ? converts.get(value): value;
					line.append(value);
					if (i < (totalColumn - 1)) line.append(",");
				}
				bw.write(line.toString());
				bw.write("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	} 
	/**
	 * 向excel中添加数据
	 * @author Tangmj
	 * @param datas
	 * @param columns
	 * @param sheetSize
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	private void addExcelDatas(List<T> datas, List<Column> columns,int sheetSize) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		HSSFWorkbook workbook = createWorkbook(datas, columns, sheetSize,tmpFilePath());
		try {
		    Path path = Paths.get(tmpFilePath());
		    Files.delete(path);
		} catch (IOException e) {
		}
		write2Path(workbook, tmpFilePath());
	}
	private String tmpFilePath(){
		return SYSTEMP + fileName + tmpFileExtension();
	}
	private String filePath(){
		return SYSTEMP + fileName + fileExtension();
	}
    /**
     * 下载分页添加的数据
     *
     * @param response
     */
    public boolean download(String fileName ,FileType fileType, HttpServletResponse response) throws IOException, URISyntaxException {
        this.fileName = fileName;
        this.fileType = fileType==null?FileType.excel:fileType;
        File file = new File(filePath());
        if(!file.exists()) return false;
        if(fileType==FileType.excel){
        	try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                response.addHeader("Content-Disposition",
                        "attachment;filename=" + fileName + fileExtension());
                OutputStream out = response.getOutputStream();
                HSSFWorkbook workbook = readFromPath(file.getPath());
                workbook.write(out);
                fileName = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(fileType==FileType.csv){
        	fileName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + fileName + fileExtension());
            response.setContentType("application/x-excel");
            Path path = Paths.get(filePath());
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
            		BufferedReader br = Files.newBufferedReader(path,Charset.forName("UTF-8")) ){
            	String line = null;
				while((line=br.readLine())!=null){
					bw.write(line);
					bw.write("\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return true;
    }

    /**
     * 导出excel
     * <HSSFWorkbook>写入<HttpServletResponse>
     *
     * @param workbook
     * @param request
     * @param response
     * @param fileName
     * @throws IOException
     */
    private void exportExcel(HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response, String fileName) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition",
                "attachment;filename=" + fileName + fileExtension());
        OutputStream out = response.getOutputStream();
        workbook.write(out);
    }

    /**
     * List<T> 转成多个<HSSFWorkbook> ,打成zip包并写入<HttpServletResponse>
     *
     * @param datas        数据
     * @param columns      导出项
     * @param workbookSize 每个workbook尺寸
     * @param sheetSize    每个sheet页尺寸
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
    public void exportExcelWithZip(List<T> datas, List<Column> columns, int workbookSize, int sheetSize, HttpServletRequest req, HttpServletResponse res) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException {
        if (workbookSize > 0) SIZE_OF_EACH_WORKBOOK = workbookSize;
        OutputStream out = res.getOutputStream();
        res.setContentType("application/octet-stream;charset=UTF-8");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".zip");
        List<String> fileNames = new ArrayList<String>();// 用于存放生成的文件名称
        File zip = new File(SYSTEMP + URLEncoder.encode(fileName, "UTF-8") + ".zip");// 压缩文件
        int totalSize = datas.size();
        int size = totalSize % SIZE_OF_EACH_WORKBOOK == 0 ? totalSize / SIZE_OF_EACH_WORKBOOK : totalSize / SIZE_OF_EACH_WORKBOOK + 1;//workbook的数量
        File[] files = new File[size];
        for (int i = 0; i < size; i++) {
            HSSFWorkbook workbook = createWorkbook(datas.subList(i * SIZE_OF_EACH_WORKBOOK, i < size - 1 ? (i + 1) * SIZE_OF_EACH_WORKBOOK : totalSize), columns, sheetSize,filePath());
            String file = SYSTEMP + fileName + "-" + i + fileExtension();
            fileNames.add(file);
            write2Path(workbook, file);
        }

        for (int i = 0; i < files.length; i++) {
            files[i] = new File(fileNames.get(i));
        }
        FileUtils.zipFiles(files, zip);
        try (FileInputStream fis = new FileInputStream(zip)) {
            byte[] buffer = new byte[1024];
			int len = 0;
            while (((len = fis.read(buffer))) != -1) {
                out.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //删除临时文件
        if (zip.exists()) zip.delete();
        for (File f : files) {
            if (f.exists()) f.delete();
        }
    }

    /**
     * 创建workbook
     *
     * @param datas     数据
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
    private HSSFWorkbook createWorkbook(List<T> datas, List<Column> columns, int sheetSize,String filePath) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        if (sheetSize > 0) SIZE_OF_EACH_SHEET = sheetSize;
        HSSFWorkbook workbook = null;                // workbook
        if (fileName != null) workbook = readFromPath(filePath);
        if (workbook == null) workbook = new HSSFWorkbook();
        CellStyle headStyle = createCellStyel(workbook, 12, true); // 12号字，加粗
        CellStyle bodyStyle = createCellStyel(workbook, 10, false);// 10号字，不加粗
        multiSheet(workbook, datas, columns, headStyle, bodyStyle);
        return workbook;
    }

    /**
     * 分多个sheet
     *
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
    private void multiSheet(HSSFWorkbook workbook, List<T> datas, List<Column> columns, CellStyle headStyle, CellStyle bodyStyle) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
    	HSSFSheet lastSheet = null;
        if (lastSheetIndex == 0) {
            lastSheet = workbook.createSheet();
            lastSheetIndex++;
        } else lastSheet = workbook.getSheetAt(lastSheetIndex - 1);
        int lastRowNum = lastSheet.getLastRowNum();
//        if (lastRowNum == SIZE_OF_EACH_SHEET) {
//            lastSheet = workbook.createSheet();//最后一页已经写满
//            lastRowNum = 0;
//            lastSheetIndex++;
//        }
        int dataNeeded = SIZE_OF_EACH_SHEET; //某个sheet页上需要的数据条数
        int fromIndex = 0;
        int toIndex = 0;
//        if (lastRowNum > 0) dataNeeded -= lastRowNum;
        int allDataSize = datas.size();
        int remainDataSize = allDataSize;   //保留的数据
        List<T> subList = null;             //子集合
        while (toIndex < allDataSize) {
            if (dataNeeded > remainDataSize) dataNeeded = remainDataSize;
            
            lastRowNum = lastSheet.getLastRowNum();
            
            if (lastRowNum == SIZE_OF_EACH_SHEET) { //最后一页已经写满
                lastSheet = workbook.createSheet();
                lastRowNum = 0;
                lastSheetIndex++;
            }
            dataNeeded =  SIZE_OF_EACH_SHEET - lastRowNum;
            
            toIndex += dataNeeded;         //更新终了位置
            if(toIndex>allDataSize){
            	toIndex = allDataSize;
            	dataNeeded = allDataSize - fromIndex;
            }
            
            subList = datas.subList(fromIndex, toIndex);
            eachSheet(lastSheet, subList, columns, headStyle, bodyStyle);
            fromIndex = toIndex;          //更新起始位置
            remainDataSize -= dataNeeded; //更新剩余数据条数
        }
    }

    /**
     * 处理每一个sheet
     *
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
    private void eachSheet(HSSFSheet sheet, List<T> datas, List<Column> columns, CellStyle headStyle, CellStyle bodyStyle) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        int lastRowNum = sheet.getLastRowNum();
        int rowSize = lastRowNum;
        Collections.sort(columns);
        if (lastRowNum == 0) {
            Row head = sheet.createRow(rowSize++);        // excel 表头
            //设置表头信息
            int totalColumn = columns.size();
            for (int i = 0; i < totalColumn; i++) {
                HSSFCell cell = (HSSFCell) head.createCell(i);
                cell.setCellValue(columns.get(i).getAlias());
                cell.setCellStyle(headStyle);
            }
        }
        if(lastRowNum>0) rowSize++;                      // 避免最后一行被覆盖导致导出数据混乱
        for (T t : datas) {                              // 处理集合中每一个元素
        	Row body = sheet.createRow(rowSize++);       // 创建下一行
            Class<T> clazz = (Class<T>) t.getClass();
            int columnSize = 0;
            for (Column c : columns) {
                Field field = t.getClass().getDeclaredField(c.getName());
                Class<?> type = field.getType();
                String methodName = c.getName();
                if (type == boolean.class /*|| type == Boolean.class*/)
                    methodName = "is" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1, methodName.length());
                else
                    methodName = "get" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1, methodName.length());
                Method method = clazz.getMethod(methodName);
                Object value = method.invoke(t);
                Map<Object, Object> converts = c.getConverts();
                if (converts != null) value = converts.get(value) != null ? converts.get(value) : value;
                setCellValue(body, columnSize++, value, bodyStyle);
            }
        }
    }

    /**
     * 给<Cell>设置值和样式
     *
     * @param row
     * @param columnSize
     * @param value
     * @param cellStyle
     */
    private void setCellValue(Row row, int columnSize, Object value, CellStyle cellStyle) {
        HSSFCell cell;
        cell = (HSSFCell) row.createCell(columnSize); // 创建下一列
        //Integer Long Float Double Date Boolean         // 填充值
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Date) {
            cell.setCellValue(format.format(value));
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        cell.setCellStyle(cellStyle);
    }

    /**
     * 设置单元格样式
     *
     * @param workbook
     * @param fontSize
     * @param bold
     * @return
     */
    private CellStyle createCellStyel(HSSFWorkbook workbook, int fontSize, boolean bold) {
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
     *
     * @param workbook
     * @param filePath
     */
    private void write2Path(HSSFWorkbook workbook, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);) {
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件得到HSSFWorkbook
     *
     * @param filePath
     * @return
     */
    private HSSFWorkbook readFromPath(String filePath) {
        HSSFWorkbook workbook = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            workbook = new HSSFWorkbook(fis);
        } catch (Exception e) {
        }
        return workbook;
    }

    /**
     * 导出Excel辅助类
     *
     * @author Tangmj
     */
    public static class Column implements Comparable<Column> {
        private final String name;                                            //导出项列属性名
        private final String alias;                                           //导出项列别名
        private final int order;                                              //导出项列顺序
        private Map<Object, Object> converts = new HashMap<Object, Object>(); //列转换

        public String getName() {
            return name;
        }

        public String getAlias() {
            return alias;
        }

        public int getOrder() {
            return order;
        }

        public Map<Object, Object> getConverts() {
            return converts;
        }

        public Column(String name, String alias, int order, Map<Object, Object> converts) {
            this.name = name;
            this.alias = alias == null ? name.toUpperCase() : alias;
            this.order = order;
            this.converts = converts;
        }

        @Override
        public int compareTo(Column that) {
            return this.order - that.order;
        }
    }
    /**
     * 文件类型
     * @author Tangmj
     *
     */
    public static enum FileType{
    	csv(".csv"),
    	excel(".xls");
    	
    	private String extension;
    	FileType(String extension){
    		this.extension = extension;
    	}
    	
    	public String getExtension(){
    		return extension;
    	}
    }
    /**
     * 获取文件后缀
     * @author Tangmj
     * @return
     */
    private String fileExtension(){
    	return fileType.getExtension();
    }
    /**
     * 
     * @author Tangmj
     * @return
     */
    private String tmpFileExtension(){
    	return fileType.getExtension()+".tmp";
    }
    /**
     * 文件生成完毕将临时文件名改正过来
     * @author Tangmj
     * @param fileName
     */
    public void finish(){
    	String tmpFilePath = tmpFilePath();
    	File tmpFile = new File(tmpFilePath);
    	if(tmpFile.exists()){
    		String finalFilePath = tmpFilePath.substring(0,tmpFilePath.lastIndexOf(".tmp"));
    		File finalFile = new File(finalFilePath);
    		tmpFile.renameTo(finalFile);
    	}
    }
    
}
