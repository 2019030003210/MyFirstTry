package edu.zsc.statistics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.time.DateFormatUtils;

import edu.zsc.util.hbase.FilterUtil;
import edu.zsc.util.hbase.HBaseUtil;

public class UseTimePerDay extends Configured implements Tool {
    @Override
    // args[0] ... 输入数据表名
    // args[1] ... 输出数据表名
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(HBaseUtil.conf);
        job.setJarByClass(UseTimePerDay.class);
        Scan scan = new Scan();
        scan.setCaching(300);
        scan.setCacheBlocks(false);//在mapreduce程序中千万不要设置允许缓存

        String startdate = args[2];//传入的是一周的某一个时间点，要计算一周的所有日期。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);
        List<Date> days = dateToWeek(currentDate);
        System.out.println(days);
        for (int i=0;i<days.size();i++)
        {
            System.out.println(days.get(i));
        }

        FastDateFormat instance = FastDateFormat.getInstance("yyyy-MM-dd");
        //设置指定时间段
        FilterUtil.createFilterList();
        try {
            FilterUtil.addSingleColumnValueFilter(
                    "info", "startTime",
                    CompareOperator.GREATER_OR_EQUAL,
                    String.valueOf(instance.parse(sdf.format(days.get(0))).getTime())///?2017-7-17
            );
            FilterUtil.addSingleColumnValueFilter(
                    "info", "endTime",
                    CompareOperator.LESS_OR_EQUAL,
                    String.valueOf(instance.parse(sdf.format(days.get(7))).getTime())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        Filter filters = new FilterList(FilterUtil.getList());
        scan.setFilter(filters);
        TableMapReduceUtil.initTableMapperJob(args[0], scan, ReadTimePerDayMapper.class, Text.class,
                LongWritable.class,
                job);
        TableMapReduceUtil.initTableReducerJob(args[1],
                SumTimePerDayReducer.class,
                job);
        // 启动job
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static List<Date> dateToWeek(Date mdate) {
        int b = mdate.getDay();
        Date fdate;
        List<Date> list = new ArrayList<Date>();
        Long fTime = mdate.getTime() - b * 24 * 3600000;
        for (int a = 1; a <= 8; a++) {
            fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(a - 1, fdate);
        }
        return list;
    }

    public static class ReadTimePerDayMapper extends TableMapper<Text, LongWritable> {
        public static byte[] family = Bytes.toBytes("info");

        @Override
        protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
                throws IOException, InterruptedException {
            long startTime = Long.parseLong(Bytes.toString(
                    result.getValue(family, Bytes.toBytes("startTime"))
            ));
            long endTime = Long.parseLong(Bytes.toString(
                    result.getValue(family, Bytes.toBytes("endTime"))
            ));
            String startDate = DateFormatUtils.format
                    (startTime, "yyyy-MM-dd", Locale.CHINA);
            long useTime = endTime - startTime;
            context.write(new Text(startDate), new LongWritable(useTime));
        }
    }

    public static class SumTimePerDayReducer
            extends TableReducer<Text, LongWritable, ImmutableBytesWritable> {
        public static byte[] info_bytes = Bytes.toBytes("info");

        @Override
        protected void reduce(Text key,
                              Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable v : values) {
                sum += v.get();
            }
            Put put = new Put(key.toString().getBytes());
            put.addColumn(Bytes.toBytes("info"),
                    Bytes.toBytes("time"),
                    Bytes.toBytes(String.valueOf(sum)));
            context.write(null, put);
        }
    }


}
