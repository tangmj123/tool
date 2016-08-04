package com.tmj.tools.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * 文件工具类
 * @author Tangmj
 *
 */
public class FileUtils {
	/**
	 * java.io.File[]压缩到java.io.File
	 * @param srcfiles
	 * @param zipfile
	 */
	public static void zipFiles(java.io.File[] srcfiles, java.io.File zipfile) {
		byte[] buf = new byte[1024];
		try(CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(zipfile), new CRC32());
				ZipOutputStream zos = new ZipOutputStream(cos);) {
			for (int i = 0; i < srcfiles.length; i++) {
				FileInputStream in = new FileInputStream(srcfiles[i]);
				zos.putNextEntry(new ZipEntry(srcfiles[i].getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到系统的临时文件目录
	 * @return
	 */
	public static String sysTemp(){
		String tmpDic = System.getProperty("java.io.tmpdir");
			if(!tmpDic.endsWith(File.separator))
				tmpDic = tmpDic + File.separator;
		return tmpDic;
	}
}
