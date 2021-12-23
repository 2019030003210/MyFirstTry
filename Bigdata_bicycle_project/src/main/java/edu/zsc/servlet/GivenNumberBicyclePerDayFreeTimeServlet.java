package edu.zsc.servlet;

import edu.zsc.statistics.GivenNumberBicyclePerDayFreeTime;
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

@WebServlet(urlPatterns = "/GivenNumberBicyclePerDayFreeTime")
public class GivenNumberBicyclePerDayFreeTimeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ToolRunner.run(new GivenNumberBicyclePerDayFreeTime(), new String[]{
                    "t_shared_bicycle",
                    "t_bicycle_freetime"

            });
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        List<Double> free_time = new ArrayList<>();
        List<String> given_number_bicycle = new ArrayList<>();
        given_number_bicycle.add("5996");
        ResultScanner scanner = HBaseUtil.scanByColumn("t_bicycle_freetime", "info", "freeTime");
        for (Result result : scanner) {
            for (Cell cell : result.listCells()) {
                double free_time_value = Double.parseDouble(Bytes.toString(CellUtil.cloneValue(cell)));
                free_time.add(free_time_value);
            }
        }
        scanner.close();
        request.setAttribute("free_time", free_time);
        request.setAttribute("given_number_bicycle", given_number_bicycle);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");

        request.getRequestDispatcher("/given_number_bicycle_perday_freetime_result.jsp").forward(request, response);

    }
}
