package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class given_005ftime_005fusecount_005fresult_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("    <title>共享单车使用数据统计</title>\n");
      out.write("    <!-- 引入 echarts.js -->\n");
      out.write("    <script type=\"text/javascript\" src=\"echarts/echarts.js\"></script>\n");
      out.write("</head>\n");
      out.write("<body style=\"margin: 5% 26% 0% 29%;\">\n");
      out.write("<div id=\"main\" style=\"width: 600px; height: 400px;\"></div>\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("    var myChart = echarts.init(document.getElementById('main'));\n");
      out.write("\n");
      out.write("    function change(obj) {\n");
      out.write("        obj = obj.substring(1, obj.length - 1).split(\",\");\n");
      out.write("        return obj;\n");
      out.write("    }\n");
      out.write("\n");
      out.write("    var use_count = change('");
      out.print(request.getAttribute("use_count") );
      out.write("');\n");
      out.write("    var given_time = change('");
      out.print(request.getAttribute("given_time") );
      out.write("');\n");
      out.write("    //初始化，默认显示标题，图例和xy空坐标轴\n");
      out.write("    myChart.setOption({\n");
      out.write("        title: {text: '统计指定地点的单车的每天平均使用次数'},\n");
      out.write("        tooltip: {},\n");
      out.write("        legend: {data: ['每天使用总时间(小时)'], show: true},\n");
      out.write("        xAxis: {data: given_time},\n");
      out.write("        yAxis: {},\n");
      out.write("        series: [{name: '使用次数', type: 'bar', data: use_count, barWidth: 30}]\n");
      out.write("    });\n");
      out.write("</script>\n");
      out.write("<div align=\"center\">\n");
      out.write("    <button onclick=\"window.location.href = 'index.jsp'\">\n");
      out.write("        返回\n");
      out.write("    </button>\n");
      out.write("</div>\n");
      out.write("</body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
