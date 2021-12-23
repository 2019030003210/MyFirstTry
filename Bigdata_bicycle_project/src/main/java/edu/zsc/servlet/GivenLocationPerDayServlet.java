package edu.zsc.servlet;

import edu.zsc.statistics.GivenLocationPerDay;
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


@WebServlet(urlPatterns = "/givenlocation")
public class GivenLocationPerDayServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ToolRunner.run(new GivenLocationPerDay(), new String[]{
                    "t_shared_bicycle" ,
                    "t_bicycle_avgnum"

            });
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        List<Double>use_num=new ArrayList<>();
        List<String>given_location = new ArrayList<>();
//        location.add("Hebei Baoding Xiongxian-Hanzhuangcun");
        given_location.add("Hebei Baoding Xiongxian-Hanzhuangcun");
        ResultScanner scanner = HBaseUtil.scanByColumn("t_bicycle_avgnum","info","avgNum");
        for (Result result:scanner){
            for (Cell cell :result.listCells()){
                double use_num_value=Double.parseDouble(Bytes.toString(CellUtil.cloneValue(cell)));
                use_num.add(use_num_value);
            }
        }
        scanner.close();
        request.setAttribute("use_num",use_num);
        request.setAttribute("given_location",given_location);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        /**
         * 已经实现计算问题，剩下页面展现数据问题
         */
        request.getRequestDispatcher("/givenlocation_result.jsp").forward(request,response);

    }
}
