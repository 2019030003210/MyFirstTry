package edu.zsc.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;

public class HdfsUtil {
    private static Configuration conf;
    private static FileSystem fs;

    static {
        try {
            if (conf == null) {
                Configuration conf = new Configuration();
                conf.addResource(new Path("$HADOOP_HOME/etc/hadoop/core-site.xml"));
                conf.set("fs.defaultFS", "hdfs://localhost:9000");
                System.setProperty("HADOOP_USER_NAME", "user"); // 替换成你的登陆账号
                System.setProperty("HADOOP_USER_PASSWORD", "qwer1234"); // 替换成密码
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 上传本地文件strLocalPath到Hdfs指定路径strRemotePath
     * 如果目标文件已存在，overWrite指示是否覆盖
     * */
    public static void UploadFile(String strLocalPath, String strRemotePath, boolean overWrite)
            throws Exception {
        Path localPath = new Path(strLocalPath);
        Path remotePath = new Path(strRemotePath);
        fs = FileSystem.get(conf);
        if (fs.exists(remotePath)) {
            if (!overWrite) {
                throw new Exception("目标文件已存在");
            }// 目标文件已存在，不覆盖
        }
        fs.copyFromLocalFile(localPath, remotePath);
        fs.close();
    }

    // 重载UploadFile函数，默认overWrite为true
    public static void UploadFile(String strLocalPath, String strRemotePath) throws Exception {
        UploadFile(strLocalPath, strRemotePath, true);
    }


    // 下载文件
    public static void DownloadFile(String strRemotePath, String strLocalPath)
            throws Exception {
        Path localFilePath = new Path(strLocalPath);
        Path remoteFilePath = new Path(strRemotePath);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);//创建文件系统对象
        try {
            fs.copyToLocalFile(remoteFilePath, localFilePath);
            System.out.println("下载完成");
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除指定文件(夹)，默认递归删除文件夹下的所有内容
    public static boolean Delete(String strRemotePath) throws Exception {
        return Delete(strRemotePath, true);
    }

    // 删除指定文件(夹)，如果strRemotePath是文件夹的话，resursive指定是否默认递归删除文件夹下所有内容
    public static boolean Delete(String strRemotePath, boolean recursive) throws Exception {
        // 补充完成代码
        Configuration conf = new Configuration();
        Path remoteFilePath = new Path(strRemotePath);
        try {
            FileSystem fs = FileSystem.get(conf);
            if (fs.exists(remoteFilePath)) {
                System.out.println("文件删除:" + strRemotePath);
                fs.delete(remoteFilePath, false);
            } else {
                System.out.println("操作失败文件不存在或已损坏");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 补充其它函数，例如创建文件夹、显示文件内容、写文件等等。
    public static boolean createFilePath(String filePath) throws IOException {
        FileSystem fs = null;
        try {
            Configuration conf = new Configuration();
            fs = FileSystem.get(conf);
            Path dfs = new Path(filePath);
            return fs.mkdirs(dfs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fs.close();
        return false;
    }

    public static void main(String[] args) throws IOException {
//        createFilePath("hdfs:///localhost:9000/dataa");
        System.out.println(createFilePath("hdfs:///localhost:9000/dataa"));
    }//hdfs://localhost:9000

}
