package edu.zsc.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.util.ToolRunner;

import edu.zsc.statistics.UseTimePerDay;
import edu.zsc.util.hbase.HBaseUtil;


@WebServlet(urlPatterns = "/UseTimePerDay")
public class UseTimePerDayServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 从HBase的t_shared_bicycle表中统计数据，写入t_avg表。
        String input = request.getParameter("startDate").toString();
        try {
            HBaseUtil.deleteTable("t_avg");
            HBaseUtil.createTable("t_avg","info");
            ToolRunner.run(new UseTimePerDay(),
                    new String[]{
                            "t_shared_bicycle",
                            "t_avg",
                            input
                    });
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        // 2. 从HBase的t_avg表中读出数据，传递给jsp页面

        List<String> date = new ArrayList<String>();
        List<Long> time = new ArrayList<Long>();
        ResultScanner scanner = HBaseUtil.scanByColumn("t_avg", "info", "time");
        for (Result result : scanner) {
            date.add(new String(result.getRow(), "UTF-8"));
            for (Cell cell : result.listCells()) {
                long timevalue = Long.parseLong(Bytes.toString(CellUtil.cloneValue(cell)));
                time.add(timevalue / 1000 / 3600);
            }
        }

        scanner.close();
        request.setAttribute("date", date);
        request.setAttribute("time", time);
        response.setContentType("text/html; charset=utf-8");
        request.getRequestDispatcher("/result.jsp").
                forward(request, response);


        // 注意读取完表数据后要关闭HBase连接
        // 参考知识点 https://www.educoder.net/shixuns/7i2yje6k/challenges
    }
}
