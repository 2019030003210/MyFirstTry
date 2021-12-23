package edu.zsc.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.util.ToolRunner;
import edu.zsc.savedata.SaveData;
//import edu.zsc.SaveData.SaveData;

@WebServlet(urlPatterns = "/saveData")
public class SaveDataServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 功能：导入数据
        try {
            // 此段代码执行的前提：
            // 1. 已在hdfs://localhost:9000/bicycle 存放数据文件
            // 2. HBase中已建立表t_shared_bicycle
            // 可在此处插入java代码完成上述两个功能。
            // 参考知识点：
            // 1. https://www.educoder.net/shixuns/wemv85q2/challenges
            // 2. https://www.educoder.net/shixuns/zpwx7c69/challenges
            ToolRunner.run(new SaveData(),
                    new String[]{
                            "hdfs://localhost:9000/bicycle",
                            "t_shared_bicycle"
                    });
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.getRequestDispatcher("/succeed.jsp").forward(request, response);
    }
}
//package edu.zsc.servlet;
//
//import java.io.IOException;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.apache.hadoop.util.ToolRunner;
//import edu.zsc.savedata.SaveData;
////import edu.zsc.SaveData.SaveData;
//
//@WebServlet(urlPatterns = "/saveData")
//public class SaveDataServlet extends HttpServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        // 功能：导入数据
//        try {
//            // 此段代码执行的前提：
//            // 1. 已在hdfs://localhost:9000/bicycle 存放数据文件
//            // 2. HBase中已建立表t_shared_bicycle
//            // 可在此处插入java代码完成上述两个功能。
//            // 参考知识点：
//            // 1. https://www.educoder.net/shixuns/wemv85q2/challenges
//            // 2. https://www.educoder.net/shixuns/zpwx7c69/challenges
//            ToolRunner.run(new SaveData(),
//                    new String[] {
//                        "hdfs://localhost:9000/bicycle",
//                        "t_shared_bicycle"
//                });
//        } catch (Exception e) {
//            System.out.println(e);
//            e.printStackTrace();
//        }
//        request.setCharacterEncoding("utf-8");
//        response.setContentType("text/html;charset=utf-8");
//        request.getRequestDispatcher("/succeed.jsp").forward(request, response);
//    }
//}
//
