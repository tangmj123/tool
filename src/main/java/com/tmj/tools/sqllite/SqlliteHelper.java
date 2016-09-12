package com.tmj.tools.sqllite;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;

/**
 * Sqllite tool to read or write
 * @author Tangmj
 *
 */
//@Component
public class SqlliteHelper<T> {

//	@Value("${sqllite.db.dir}")
	private String DBPATH;

	private static transient Logger LOGGER = LoggerFactory.getLogger(SqlliteHelper.class);

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		// load sqllite driver
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			LOGGER.error("load sqllite driver error.", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * get connection of sqllite
	 * 
	 * @return
	 */
	public Connection getConnection() {
		Connection conn = null;
		try {
			if(DBPATH != null){
				File dir = new File(DBPATH);
				if(!dir.exists()) dir.mkdirs();
			}else DBPATH = "";
			LOGGER.info("sqlite's dir is :{}",DBPATH);
			conn = DriverManager.getConnection("jdbc:sqlite:" + DBPATH+File.separator+"data.db");
		} catch (SQLException e) {
			LOGGER.error("get connection error.", e.getMessage(), e);
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * close connection if not null
	 * 
	 * @param conn
	 */
	public void colseConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			LOGGER.error("close connection error.", e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * create table based on domain
	 * 
	 * @param clazz
	 */
	public boolean createTable(Class<T> clazz) {
		if (clazz == null)
			return false;
		String sql = createSql(clazz);
		try(Connection conn = getConnection();
				Statement statement = conn.createStatement();){
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.error("create table failed.",e.getMessage(),e);
			return false;
		}
		return true;
	}

	private String createSql(Class<T> domain) {
		// create table if not exists person (id integer, name string)
		StringBuffer sql = new StringBuffer("create table if not exists ");
		Class<?> clazz = domain;
		String domainName = clazz.getSimpleName();
		// table name
		String tableName = domainName.substring(0, 1).toLowerCase()+ domainName.substring(1);
		sql.append(tableName);
		sql.append("(id INTEGER PRIMARY KEY AUTOINCREMENT,");
		Field[] fields = clazz.getDeclaredFields();
		//generate columns
		for (Field f : fields) {
			Class<?> ftype = f.getType();
			String fname = f.getName();
			if("id".equalsIgnoreCase(fname)) continue;
			String dbType = null;
			if (ftype == java.lang.String.class) {
				dbType = "string";
			} else if (ftype == java.util.Date.class) {
				dbType = "text";
			} else if (ftype == boolean.class || ftype == Boolean.class){
				dbType = "integer";
			} else if(ftype == int.class || ftype == Integer.class){
				dbType = "integer";
			}else {
				dbType = "string";
			}
			sql.append(fname).append(" ").append(dbType).append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		LOGGER.info("create table'sql is :{}",sql.toString());
		return sql.toString();
	}

	/**
	 * insert
	 * @param domain
	 */
	public void insert(T domain){
		if(domain == null) return;
		createTable((Class)domain.getClass());// 确保表存在
		Class<?> clazz = domain.getClass();
		String domainName = clazz.getSimpleName();
		// table name
		String tableName = domainName.substring(0, 1).toLowerCase()+ domainName.substring(1);
		
		StringBuffer sql = new StringBuffer("insert into "+tableName );
		StringBuffer colums = new StringBuffer("(");
		StringBuffer values = new StringBuffer("(");
		
		Field[] fields = clazz.getDeclaredFields();
		try(Connection connection = getConnection();
				Statement statement = connection.createStatement();) {
			connection.setAutoCommit(false);
			for(Field f : fields){
				f.setAccessible(true);
				Class<?> ftype = f.getType();
				String fname = f.getName();
				Object value = f.get(domain);
				if("id".equalsIgnoreCase(fname)) continue;
				colums.append(fname).append(",");
				if (ftype == java.util.Date.class) {
					values.append("'"+format.format(value)+"'").append(",");
				} else if (ftype == boolean.class || ftype == Boolean.class){
					values.append((Boolean)value? 1:0).append(",");
				}else if(ftype == int.class || ftype == Integer.class){
					values.append(value).append(",");
				}else{
					values.append("'"+value+"'").append(",");
				}
			}
			colums.deleteCharAt(colums.length()-1);
			values.deleteCharAt(values.length()-1);
			sql = sql.append(colums).append(")").append("values").append(values).append(")");
			statement.executeUpdate(sql.toString());
			
			connection.commit();
			LOGGER.info("insert sql is :{}",sql.toString());
		} catch (Exception e) {
			LOGGER.error("insert into table failed.",e.getMessage(),e);
			e.printStackTrace();
		}
	}
	/**
	 * update 
	 * @param domain
	 */
	public void update(T domain){
		if(domain == null) return;
		createTable((Class)domain.getClass());// 确保表存在
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Class<?> clazz = domain.getClass();
		String domainName = clazz.getSimpleName();
		// table name
		String tableName = domainName.substring(0, 1).toLowerCase()+ domainName.substring(1);
		
		StringBuffer sql = new StringBuffer("update "+tableName + " set " );
		
		Field[] fields = clazz.getDeclaredFields();
		try( Connection connection = getConnection();
				Statement statement = connection.createStatement();) {
			for(Field f : fields){
				f.setAccessible(true);
				Class<?> ftype = f.getType();
				String fname = f.getName();
				Object value = f.get(domain);
				if(value == null) continue;
				if("id".equalsIgnoreCase(fname)) continue;
				if (ftype == java.util.Date.class) {
					sql.append(fname).append("=").append("'"+format.format(value)+"'").append(",");
				} else if (ftype == boolean.class || ftype == Boolean.class){
					sql.append(fname).append("=").append((boolean)value?1:0).append(",");
				}else if(ftype == int.class || ftype == Integer.class){
					sql.append(fname).append("=").append(value).append(",");
				}else{
					sql.append(fname).append("=").append("'"+value+"'").append(",");
				}
			}
			Field idField = clazz.getDeclaredField("id");
			idField.setAccessible(true);
			
			sql.deleteCharAt(sql.length()-1).append(" where id="+idField.get(domain));
			
			statement.executeUpdate(sql.toString());
			LOGGER.info("update sql is :{}",sql.toString());
		} catch (Exception e) {
			LOGGER.error("update table failed.",e.getMessage(),e);
			e.printStackTrace();
		}
	}
	/**
	 * query by sql
	 * @param sql
	 * @return
	 */
	public List<T> queryBySql(String sql,Class<T> t){
		if(sql == null || sql.trim()=="") return null;
		LOGGER.info("query sql is :{}",sql);
		try (Connection connection = getConnection();
				Statement statement = connection.createStatement();){
			ResultSet rs = statement.executeQuery(sql);
			return mapRersultSetToList(rs, t);
		} catch (Exception e) {
			LOGGER.error("query failed.",e.getMessage(),e);
		}
		return null;
	}
	private List<T> mapRersultSetToList(ResultSet rs, Class clazz) {
		List<T> outputList = null;
		try {
			// make sure resultset is not null
			if (rs != null) {
				// get the resultset metadata
				ResultSetMetaData rsmd = rs.getMetaData();
				// get all the attributes of outputClass
				Field[] fields = clazz.getDeclaredFields();
				while (rs.next()) {
					T bean = (T) clazz.newInstance();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						// getting the SQL column name
						String columnName = rsmd.getColumnName(i);
						// reading the value of the SQL column
						Object columnValue = rs.getObject(i);
						for (Field field : fields) {
							if(columnName.equals(field.getName()))
								BeanUtils.setProperty(bean, field.getName(),columnValue);
						}
					}
					if(outputList == null){
						outputList = new ArrayList();
					}
					outputList.add(bean);
				}
			} else {
				return null;
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return outputList;
	}
}
