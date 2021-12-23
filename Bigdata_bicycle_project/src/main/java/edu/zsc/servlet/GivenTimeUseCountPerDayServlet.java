package edu.zsc.servlet;

import edu.zsc.statistics.GivenTimeUseCountPerDay;
import edu.zsc.util.hbase.HBaseUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.ToolRunner;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/GivenTimeUseCountPerDay")
public class GivenTimeUseCountPerDayServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("hello");
        //1 先创建一张指定时间内使用次数的表，名为"t_bicycle_usagerate"
        try{
            HBaseUtil.deleteTable("t_bicycle_usagerate");
            HBaseUtil.createTable("t_bicycle_usagerate","info");
            ToolRunner.run(new GivenTimeUseCountPerDay(),new String[]{
                    "t_shared_bicycle",
                    "t_bicycle_usagerate"
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 用scan从表中读取数据
        List<String> given_time = new ArrayList<>();
        List<Long> use_count = new ArrayList<>();
        ResultScanner scanner = HBaseUtil.scanByColumn("t_bicycle_usagerate","info","usageRate");
        for (Result result : scanner) {
            given_time.add(new String(result.getRow(), "UTF-8"));
            for (Cell cell : result.listCells()) {
                long timevalue = Long.parseLong(Bytes.toString(CellUtil.cloneValue(cell)));
                use_count.add(timevalue);
            }
        }
        System.out.println(use_count);
        scanner.close();
        request.setAttribute("use_count",use_count);
        request.setAttribute("given_time",given_time);
        response.setContentType("text/html; charset=utf-8");
        request.getRequestDispatcher("given_time_usecount_result.jsp").forward(request,response);

    }
}
