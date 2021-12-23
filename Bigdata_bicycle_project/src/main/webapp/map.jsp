<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <style type="text/css">
        body, html,#allmap {
            width: 100%;
            height: 100%;
            overflow: hidden;
            margin:0;
        }
    </style>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=1.4"></script>
    <title>step1</title>
</head>
<body>
<div id="allmap"></div>
</body>
</html>
<script type="text/javascript">
    var map = new BMap.Map("allmap");// 创建地图实例
    var point = new BMap.Point(116.10 ,38.98);// 创建点坐标
    map.centerAndZoom(point, 13);//设初始化地图。 如果center类型为Point时，zoom必须赋值，范围3-19级，若调用高清底图（针对移动端开发）时，zoom可赋值范围为3-18级。如果center类型为字符串时，比如“北京”，zoom可以忽略，地图将自动根据center适配最佳zoom级别
    map.addControl(new BMap.NavigationControl());//缩放平移控件
    map.enableScrollWheelZoom();//利用鼠标滚轮控制大小
    var start_longitude=116.233093;//开始经度
    var start_latitude=39.04607;//开始纬度
    var stop_longitude=116.235352;//结束经度
    var stop_latitude=39.041691;//结束纬度
    var address=["乡里乡情铁锅炖南228米","擎天矿用材料有限公司北609米"];
    /**********  Begin  **********/
    //1.初始化路程线
    var polyline = new BMap.Polyline([
        new BMap.Point(start_longitude, start_latitude),
        new BMap.Point(stop_longitude, stop_latitude)
    ], {strokeColor:"red", strokeWeight:3, strokeOpacity:0.5});
    //2.将线添加到地图上
    map.addOverlay(polyline);
    //3.设置起始点标注
    var marker = new BMap.Marker(new BMap.Point(start_longitude,start_latitude));
    var label = new BMap.Label(address[0],{offset: new BMap.Size(20, 0)});
    marker.setLabel(label);
    map.addOverlay(marker);
    var marker = new BMap.Marker(new BMap.Point(stop_longitude,stop_latitude));
    var label = new BMap.Label(address[1], {offset: new BMap.Size(20, 0)});
    marker.setLabel(label);
    map.addOverlay(marker);
    /**********  End  **********/
</script>