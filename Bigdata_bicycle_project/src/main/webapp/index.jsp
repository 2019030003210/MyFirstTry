<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>登录</title>
    <meta name="author" content="首页">
</head>

<body>
<div align="center">请点击按钮
    <form name="form11" method="post" action="saveData">
        <table>
            <tr>
                <td>
                    <input type="submit" value="导入数据" />
                </td>
            </tr>
        </table>
    </form>

    <form name="form1" method="post" action="UseTimePerDay">
        <table>
            <tr>
                <td>统计一周内共享单车使用次数</td>
                <td><input type="text" name="startDate"></td>
                <td>
                    <input type="submit" value="统计" />
                </td>
            </tr>
        </table>
    </form>
    <form name="form2" method="post" action="givenlocation">
        <table>
            <tr>
                <td>统计指定地点所有单车的每天平均使用次数</td>
                <td>
                    <input type="submit" value="统计" />
                </td>
            </tr>
        </table>
    </form>
    <form name="form3" method="post" action="GivenNumberBicyclePerDayFreeTime">
        <table>
            <tr>
                <td>统计指定共享单车每次使用的空闲平均时间 </td>
                <td>
                    <input type="submit" value="统计" />
                </td>
            </tr>
        </table>
    </form>
    <form name="form4" method="post" action="GivenTimeUseCountPerDay">
        <table>
            <tr>
                <td>统计指定时间段内的单车使用次数</td>
                <td>
                    <input type="submit" value="统计" />
                </td>
            </tr>
        </table>
    </form>
    <form name="form5" method="post" action="EnterMap">
        <table>
            <tr>
                <td>进入地图查看各路线流量</td>
                <td>
                    <input type="submit" value="进入" />
                </td>
            </tr>
        </table>
    </form>

</div>
</body>

</html>