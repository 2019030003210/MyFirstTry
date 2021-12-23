package edu.zsc.statistics;

import java.io.IOException;

import edu.zsc.util.hbase.HBaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapred.TableMap;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;

public class MapLineTotal extends Configured implements Tool {
    public static final byte[] family = Bytes.toBytes("info");

    public int run(String[] args) throws Exception {
        Configuration conf = HBaseUtil.conf;
        Job job = ConfigureJob(conf, new String[]{args[0], args[1]});
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public Job ConfigureJob(Configuration conf, String[] args) throws IOException {
        String tableName = args[0];
        String targetTable = args[1];
        Job job = new Job(conf, tableName);
        // 简单来说，过滤器就是用于在建表的时候，把过滤器放在scan中
        Scan scan = new Scan();
        scan.setCaching(300);
        scan.setCacheBlocks(false);

        TableMapReduceUtil.initTableMapperJob(tableName, scan, MyMapper.class, Text.class, IntWritable.class, job);

        TableMapReduceUtil.initTableReducerJob(targetTable, MyTableReducer.class, job);

        job.setNumReduceTasks(1);

        return job;

    }

    public static class MyMapper extends TableMapper<Text, IntWritable> {
        protected void map(ImmutableBytesWritable rowKey, Result result, Context context) throws IOException, InterruptedException {
            String start_latitude = Bytes.toString(result.getValue(family,Bytes.toBytes("start_latitude")));
            String start_longitude = Bytes.toString(result.getValue(family,Bytes.toBytes("start_longitude")));
            String stop_latitude = Bytes.toString(result.getValue(family,Bytes.toBytes("stop_latitude")));
            String stop_longitude = Bytes.toString(result.getValue(family, Bytes.toBytes("stop_longitude")));
            String departure = Bytes.toString(result.getValue(family, Bytes.toBytes("departure")));
            String destination = Bytes.toString(result.getValue(family, Bytes.toBytes("destination")));
            IntWritable doubleWritable =new IntWritable(1);
            context.write(new Text(start_latitude + "-" + start_longitude + "_" + stop_latitude + "-" + stop_longitude + "_" +
                    departure + "-" + destination), doubleWritable);

            //组合经纬度作为键，1作为值

        }
    }


    public static class MyTableReducer extends TableReducer<Text, IntWritable, ImmutableBytesWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int totalNum = 0;
            for (IntWritable num : values){
                int d = num.get();
                totalNum+=d;

            }
            Put put =new Put(Bytes.toBytes(key.toString()+totalNum));//键Key
            put.addColumn(family,Bytes.toBytes("lineTotal"),Bytes.toBytes(String.valueOf(totalNum)));//值Value
            context.write(null,put);//?
        }
    }
}
