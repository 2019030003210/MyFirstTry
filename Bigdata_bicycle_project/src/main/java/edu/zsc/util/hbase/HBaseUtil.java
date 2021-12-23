package edu.zsc.util.hbase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.CompareOperator;
import java.util.List;
import java.io.IOException;
public class HBaseUtil {
    public static Configuration conf;
	private static Connection conn;
    static {
		// 静态代码块初始化config对象
		try {
			if (conf == null) {
				conf = HBaseConfiguration.create();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = ConnectionFactory.createConnection(conf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
   	/**
	 * 创建表
	 *
	 * @param tableName
	 *            表名
	 * @param columnNames
	 *            列族的动态数组
	 * @throws Exception
	 */
	public static void createTable(String tableName, String... columnNames) throws Exception {
		if (tableName != null && !tableName.isEmpty()) {
			Admin admin = getConnection().getAdmin();
			TableName tableNameObj = TableName.valueOf(Bytes.toBytes(tableName));
			if (!admin.tableExists(tableNameObj)) {
				TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableNameObj);
				for (String columnName : columnNames) {
					ColumnFamilyDescriptorBuilder columnBuild = ColumnFamilyDescriptorBuilder
							.newBuilder(Bytes.toBytes(columnName));
					ColumnFamilyDescriptor family = columnBuild.build();
					builder.setColumnFamily(family);
				}
				admin.createTable(builder.build());
			}			
		}
	}

	/**
	 * 刪除表
	 *
	 * @param tableName
	 *            表名
	 * @throws Exception
	 */
	public static void deleteTable(String tableName) throws Exception {
		if (tableName != null && !tableName.isEmpty()) {
			Admin admin = getConnection().getAdmin();
			TableName tableNameObj = TableName.valueOf(Bytes.toBytes(tableName));
			admin.disableTable(tableNameObj);
			admin.deleteTable(tableNameObj);
		}
	}
	/**
	 * 往指定表添加数据
	 *
	 * @param tablename
	 *            表名
	 * @param puts
	 *            需要添加的数据
	 * @return long 返回执行时间
	 * @throws IOException
	 */
	public static long putByTable(String tablename, List<Put> puts) throws Exception {
		long currentTime = System.currentTimeMillis();
		Connection conn = getConnection();
		Table table = conn.getTable(TableName.valueOf(Bytes.toBytes(tablename)));
		try {
			table.put(puts);
		} finally {
			table.close();
			closeConnect();
		}
		return System.currentTimeMillis() - currentTime;
	}

	public static void closeConnect() {
		// 实现关闭连接对象功能
		if (null != conn) {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

    	/**
	 * 行个数
	 * 
	 * @param tName
	 * @return
	 */
	public static long rowCount(String tName) {
		long rowCount = 0;
		try {
			Connection conn = getConnection();
			TableName tableName = TableName.valueOf(Bytes.toBytes(tName));
			Table table = conn.getTable(tableName);
			Scan scan = new Scan();
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result result : resultScanner) {
				rowCount += result.size();
			}
			System.out.println("rowCount-->" + rowCount);
		} catch (IOException e) {
		}
		return rowCount;
	}
	// 查询tableName指定列的值
	public static ResultScanner scanByColumn(String strTableName, 
		String strFamily,
		String strQualifier) throws IOException {
		ResultScanner scanner = null;
		try {
			Connection conn = getConnection();
			TableName tableName = TableName.valueOf(Bytes.toBytes(strTableName));
			Table table = conn.getTable(tableName);
			FilterUtil.createFilterList();
			FilterUtil.addFamilyFilter(strFamily, CompareOperator.EQUAL);
			FilterUtil.addQualifier(strQualifier, CompareOperator.EQUAL);
		    Filter filters = new FilterList(FilterUtil.getList());     
			Scan scan = new Scan();
			scan.setFilter(filters);
			scanner = table.getScanner(scan);			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return scanner;
	}
}