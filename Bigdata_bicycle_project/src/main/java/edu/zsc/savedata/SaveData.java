//package edu.zsc.savedata;
//
//import java.io.IOException;
//import java.util.Date;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.conf.Configured;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
//import org.apache.hadoop.hbase.mapreduce.TableReducer;
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
//import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.NullWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.util.Tool;
//import org.apache.hadoop.util.ToolRunner;
//
//import edu.zsc.util.hbase.HBaseUtil;
//
//import org.apache.hadoop.hbase.util.Bytes;
//import org.apache.commons.lang3.time.FastDateFormat;
//
//// 从存放在HDFS上的csv文件导数据到HBase的指定表格中
//// 参考编程知识点 https://www.educoder.net/shixuns/h9jzevkb/challenges
//public class SaveData extends Configured implements Tool {
//
//    @Override
//    // args[0]表示数据文件在hdfs存放路径
//    // args[1]表示数据导进哪个HBase表
//    // 例如调用:
//    // run(String[] {"hdfs://localhost:9000/bicycle", "t_shared_bicycle"});
//    public int run(String[] args) throws Exception {
//        /**** Begin ****/
//        Configuration conf = HBaseConfiguration.create();
//        Job job = Job.getInstance(conf);
//        job.setJarByClass(SaveData.class);
//        job.setMapperClass(HDFSToHbaseMapper.class);
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(NullWritable.class);
//        Path inputPath = new Path(args[0]);
//        FileInputFormat.addInputPath(job, inputPath);
//        TableMapReduceUtil.initTableReducerJob(args[1], HDFSToHbaseReducer.class, job);
//        job.setOutputKeyClass(NullWritable.class);
//        job.setOutputValueClass(Put.class);
//        boolean isDone = job.waitForCompletion(true);
//        return isDone ? 0 : 1;
//
//        /**** End ****/
//    }
//
//    public static class HDFSToHbaseMapper
//            extends Mapper<LongWritable, Text, Text, NullWritable> {
//        @Override
//        protected void map(LongWritable key, Text value, Context context)
//                throws IOException, InterruptedException {
//            /**** Begin ****/
//            context.write(value, NullWritable.get());
//
//            /***** End *****/
//        }
//    }
//
//    public static class HDFSToHbaseReducer
//            extends TableReducer<Text, NullWritable, ImmutableBytesWritable> {
//        public static byte[] info_bytes = Bytes.toBytes("info");
//
//        @Override
//        protected void reduce(Text key,
//                              Iterable<NullWritable> values, Context context)
//                throws IOException, InterruptedException {
//            String[] split = key.toString().split(",");
//            if (split.length == 11) {
//                Put put = new Put(split[0].getBytes());
//                FastDateFormat instance = FastDateFormat.getInstance("MM/dd/yyyy HH:mm");
//                try {
//                    // 将时间转化为毫秒
//                    Date parse = instance.parse(split[1]);
//                    put.addColumn(info_bytes,
//                            Bytes.toBytes("startTime"),
//                            Bytes.toBytes(String.valueOf(parse.getTime())));
//                    // 补充插入结束时间
//                    /**** Begin ****/
//                    parse = instance.parse(split[2]);
//                    put.addColumn(info_bytes,
//                            Bytes.toBytes("endTime"),
//                            Bytes.toBytes(String.valueOf(parse.getTime())));
//
//
//                    /****  End ****/
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//                /****Begin *****/
//                // 补充插入其它列数据
//                put.addColumn("info".getBytes(), "bicycleId".getBytes(), split[3].getBytes());
//                put.addColumn("info".getBytes(), "departure".getBytes(), split[4].getBytes());
//                put.addColumn("info".getBytes(), "destination".getBytes(), split[5].getBytes());
//                put.addColumn("info".getBytes(), "city".getBytes(), split[6].getBytes());
//                put.addColumn("info".getBytes(), "start_longitude".getBytes(), split[7].getBytes());
//                put.addColumn("info".getBytes(), "start_latitude".getBytes(), split[8].getBytes());
//                put.addColumn("info".getBytes(), "stop_longitude".getBytes(), split[9].getBytes());
//                put.addColumn("info".getBytes(), "stop_latitude".getBytes(), split[10].getBytes());
//                context.write(null, put);
//
//                /**** End *****/
//            }
//        }
//    }
//}
package edu.zsc.savedata;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.commons.lang3.time.FastDateFormat;

// 从存放在HDFS上的csv文件导数据到HBase的指定表格中
// 参考编程知识点 https://www.educoder.net/shixuns/h9jzevkb/challenges
public class SaveData extends Configured implements Tool {

    @Override
    // args[0]表示数据文件在hdfs存放路径
    // args[1]表示数据导进哪个HBase表
    // 例如调用:
    // run(String[] {"hdfs://localhost:9000/bicycle", "t_shared_bicycle"});
    public int run(String[] args) throws Exception {
        /**** Begin ****/
        Configuration conf = HBaseConfiguration.create();
        Job job = Job.getInstance(conf);
        job.setJarByClass(SaveData.class);
        job.setMapperClass(HDFSToHbaseMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        Path inputPath = new Path(args[0]);
        FileInputFormat.addInputPath(job, inputPath);
        TableMapReduceUtil.initTableReducerJob(args[1], HDFSToHbaseReducer.class, job);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Put.class);
        boolean isDone = job.waitForCompletion(true);
        return isDone ? 0 : 1;

        /**** End ****/
    }

    public static class HDFSToHbaseMapper
            extends Mapper<LongWritable, Text, Text, NullWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            /**** Begin ****/
            context.write(value, NullWritable.get());
            /***** End *****/
        }
    }

    public static class HDFSToHbaseReducer
            extends TableReducer<Text, NullWritable, ImmutableBytesWritable> {
        public static byte[] info_bytes = Bytes.toBytes("info");

        @Override
        protected void reduce(Text key,
                              Iterable<NullWritable> values, Context context)
                throws IOException, InterruptedException {
            String[] split = key.toString().split(",");
            if (split.length == 11) {
                Put put = new Put(split[0].getBytes());
                FastDateFormat instance = FastDateFormat.getInstance("MM/dd/yyyy HH:mm");
                try {
                    // 将时间转化为毫秒
                    Date parse = instance.parse(split[1]);
                    put.addColumn(info_bytes,
                            Bytes.toBytes("startTime"),
                            Bytes.toBytes(String.valueOf(parse.getTime())));
                    // 补充插入结束时间
                    /**** Begin ****/
                    parse = instance.parse(split[2]);
                    put.addColumn(info_bytes,
                            Bytes.toBytes("endTime"),
                            Bytes.toBytes(String.valueOf(parse.getTime())));


                    /****  End ****/
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                /****Begin *****/
                // 补充插入其它列数据
                put.addColumn("info".getBytes(), "bicycleId".getBytes(), split[3].getBytes());
                put.addColumn("info".getBytes(), "departure".getBytes(), split[4].getBytes());
                put.addColumn("info".getBytes(), "destination".getBytes(), split[5].getBytes());
                put.addColumn("info".getBytes(), "city".getBytes(), split[6].getBytes());
                put.addColumn("info".getBytes(), "start_longitude".getBytes(), split[7].getBytes());
                put.addColumn("info".getBytes(), "start_latitude".getBytes(), split[8].getBytes());
                put.addColumn("info".getBytes(), "stop_longitude".getBytes(), split[9].getBytes());
                put.addColumn("info".getBytes(), "stop_latitude".getBytes(), split[10].getBytes());
                context.write(null, put);
                /**** End *****/
            }
        }
    }
}