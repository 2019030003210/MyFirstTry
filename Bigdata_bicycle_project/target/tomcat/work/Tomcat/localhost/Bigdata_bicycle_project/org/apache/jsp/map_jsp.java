package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class map_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html; charset=utf-8");
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
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
      out.write("    <style type=\"text/css\">\n");
      out.write("        body, html,#allmap {\n");
      out.write("            width: 100%;\n");
      out.write("            height: 100%;\n");
      out.write("            overflow: hidden;\n");
      out.write("            margin:0;\n");
      out.write("        }\n");
      out.write("    </style>\n");
      out.write("    <script type=\"text/javascript\" src=\"http://api.map.baidu.com/api?v=1.4\"></script>\n");
      out.write("    <title>step1</title>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("<div id=\"allmap\"></div>\n");
      out.write("</body>\n");
      out.write("</html>\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("    var map = new BMap.Map(\"allmap\");// ??????????????????\n");
      out.write("    var point = new BMap.Point(116.10 ,38.98);// ???????????????\n");
      out.write("    map.centerAndZoom(point, 13);//????????????????????? ??????center?????????Point??????zoom?????????????????????3-19????????????????????????????????????????????????????????????zoom??????????????????3-18????????????center?????????????????????????????????????????????zoom????????????????????????????????????center????????????zoom??????\n");
      out.write("    map.addControl(new BMap.NavigationControl());//??????????????????\n");
      out.write("    map.enableScrollWheelZoom();//??????????????????????????????\n");
      out.write("    var start_longitude=116.233093;//????????????\n");
      out.write("    var start_latitude=39.04607;//????????????\n");
      out.write("    var stop_longitude=116.235352;//????????????\n");
      out.write("    var stop_latitude=39.041691;//????????????\n");
      out.write("    var address=[\"????????????????????????228???\",\"?????????????????????????????????609???\"];\n");
      out.write("    /**********  Begin  **********/\n");
      out.write("    //1.??????????????????\n");
      out.write("    var polyline = new BMap.Polyline([\n");
      out.write("        new BMap.Point(start_longitude, start_latitude),\n");
      out.write("        new BMap.Point(stop_longitude, stop_latitude)\n");
      out.write("    ], {strokeColor:\"red\", strokeWeight:3, strokeOpacity:0.5});\n");
      out.write("    //2.????????????????????????\n");
      out.write("    map.addOverlay(polyline);\n");
      out.write("    //3.?????????????????????\n");
      out.write("    var marker = new BMap.Marker(new BMap.Point(start_longitude,start_latitude));\n");
      out.write("    var label = new BMap.Label(address[0],{offset: new BMap.Size(20, 0)});\n");
      out.write("    marker.setLabel(label);\n");
      out.write("    map.addOverlay(marker);\n");
      out.write("    var marker = new BMap.Marker(new BMap.Point(stop_longitude,stop_latitude));\n");
      out.write("    var label = new BMap.Label(address[1], {offset: new BMap.Size(20, 0)});\n");
      out.write("    marker.setLabel(label);\n");
      out.write("    map.addOverlay(marker);\n");
      out.write("    /**********  End  **********/\n");
      out.write("</script>");
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
