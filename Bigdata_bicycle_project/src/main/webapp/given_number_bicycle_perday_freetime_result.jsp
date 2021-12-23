<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>共享单车使用数据统计</title>
    <!-- 引入 echarts.js -->
    <script type="text/javascript" src="echarts/echarts.js"></script>
</head>
<body style="margin: 5% 26% 0% 29%;">
<div id="main" style="width: 600px; height: 400px;"></div>
<script type="text/javascript">
    var myChart = echarts.init(document.getElementById('main'));

    function change(obj) {
        obj = obj.substring(1, obj.length - 1).split(",");
        return obj;
    }

    var free_time = change('<%=request.getAttribute("free_time") %>');
    var given_number_bicycle = change('<%=request.getAttribute("given_number_bicycle") %>');
    //初始化，默认显示标题，图例和xy空坐标轴
    myChart.setOption({
        title: {text: '统计共享单车指定车辆每次使用的空闲平均时间'},
        tooltip: {},
        legend: {data: ['每天使用总时间(小时)'], show: true},
        xAxis: {data: given_number_bicycle},
        yAxis: {},
        series: [{name: '空闲平均时间', type: 'bar', data: free_time, barWidth: 30}]
    });
</script>
<div align="center">
    <button onclick="window.location.href = 'index.jsp'">
        返回
    </button>
</div>
</body>
</html>
