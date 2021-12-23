package edu.zsc.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import edu.zsc.statistics.MapLineTotal;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.util.ToolRunner;

import edu.zsc.statistics.UseTimePerDay;
import edu.zsc.util.hbase.HBaseUtil;

@WebServlet(urlPatterns = "/EnterMap")
public class EnterMapServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HBaseUtil.deleteTable("t_bicycle_linetotal");
            HBaseUtil.createTable("t_bicycle_linetotal", "info");
            ToolRunner.run(new MapLineTotal(),
                    new String[]{
                            "t_shared_bicycle",
                            "t_bicycle_linetotal"
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        /**
         * 先跳转map.jsp测试
         */
        request.getRequestDispatcher("/map.jsp").forward(request, response);

    }
}
