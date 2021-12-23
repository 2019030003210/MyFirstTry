package edu.zsc.statistics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.IntWritable;
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

public class GivenTimeUseCountPerDay extends Configured implements Tool {
    // args[0] ... 输入数据表名
    // args[1] ... 输出数据表名
    public static byte[] family = Bytes.toBytes("info");

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(HBaseUtil.conf);
        job.setJarByClass(GivenTimeUseCountPerDay.class);
        Scan scan = new Scan();
        scan.setCaching(300);
        scan.setCacheBlocks(false);

        FastDateFormat instance = FastDateFormat.getInstance("yyyy-MM-dd");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); # 用于将长段的文字日期转为yyyy-MM-dd
        //Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse();
        FilterUtil.createFilterList();
        try {
            FilterUtil.addSingleColumnValueFilter("info", "startTime", CompareOperator.GREATER_OR_EQUAL,
                    String.valueOf(instance.parse("2017-08-15").getTime()));
            FilterUtil.addSingleColumnValueFilter("info", "endTime", CompareOperator.LESS_OR_EQUAL,
                    String.valueOf(instance.parse("2017-08-22").getTime()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(String.valueOf(instance.parse("2017-08-22").getTime()));
        Filter filters = new FilterList(FilterUtil.getList());
        scan.setFilter(filters);
        TableMapReduceUtil.initTableMapperJob(args[0], scan, ReadTimePerDayMapper.class, Text.class,
                IntWritable.class, job);

        TableMapReduceUtil.initTableReducerJob(args[1], MyTableReducer.class, job);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class ReadTimePerDayMapper extends TableMapper<Text, IntWritable> {


        @Override
        protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
                throws IOException, InterruptedException {
            /********** Begin *********/
            IntWritable a = new IntWritable(1);
            context.write(new Text("2017-08-15----2017-08-22"), a);
            /********** End *********/
        }
    }

    public static class MyTableReducer extends TableReducer<Text, IntWritable, ImmutableBytesWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            /********** Begin *********/
            int totalNum = 0;

            for (IntWritable num : values) {
                int d = num.get();
                totalNum += d;
            }
            Put put = new Put(Bytes.toBytes(key.toString()));
            put.addColumn(family, Bytes.toBytes("usageRate"), Bytes.toBytes(String.valueOf(totalNum)));
            context.write(null, put);
            /********** End *********/
        }
    }
}


