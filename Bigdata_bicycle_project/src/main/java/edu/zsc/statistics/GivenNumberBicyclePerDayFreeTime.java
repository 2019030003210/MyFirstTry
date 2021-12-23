package edu.zsc.statistics;

import java.io.IOException;

import java.math.BigDecimal;

import java.math.RoundingMode;

import edu.zsc.util.hbase.FilterUtil;
import edu.zsc.util.hbase.HBaseUtil;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.hbase.CompareOperator;

import org.apache.hadoop.hbase.client.Put;

import org.apache.hadoop.hbase.client.Result;

import org.apache.hadoop.hbase.client.Scan;

import org.apache.hadoop.hbase.filter.Filter;

import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;

import org.apache.hadoop.hbase.mapreduce.TableMapper;

import org.apache.hadoop.hbase.mapreduce.TableReducer;

import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.io.BytesWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.util.Tool;

/**
 * 统计5996共享单车每次使用的空闲平均时间
 */

public class GivenNumberBicyclePerDayFreeTime extends Configured implements Tool {
    public static final byte[] family = "info".getBytes();

    public static class MyMapper extends TableMapper<Text, BytesWritable> {

        protected void map(ImmutableBytesWritable rowKey, Result result, Context context)

                throws IOException, InterruptedException {

            /********** Begin *********/

            long beginTime = Long.parseLong(Bytes.toString(result.getValue(family, "startTime".getBytes())));

            long endTime = Long.parseLong(Bytes.toString(result.getValue(family, "endTime".getBytes())));

            BytesWritable bytesWritable = new BytesWritable(Bytes.toBytes(beginTime + "_" + endTime));

            context.write(new Text("5996"), bytesWritable);

            /********** End *********/

        }

    }

    public static class MyTableReducer extends TableReducer<Text, BytesWritable, ImmutableBytesWritable> {

        @Override

        public void reduce(Text key, Iterable<BytesWritable> values, Context context)

                throws IOException, InterruptedException {

            /********** Begin *********/

            long freeTime = 0;

            long beginTime = 0;

            int length = 0;

            for (BytesWritable time : values) {

                byte[] copyBytes = time.copyBytes();

                String timeLong = Bytes.toString(copyBytes);

                String[] split = timeLong.split("_");

                if (beginTime == 0) {

                    beginTime = Long.parseLong(split[0]);

                    continue;

                } else {

                    freeTime = freeTime + beginTime - Long.parseLong(split[1]);

                    beginTime = Long.parseLong(split[0]);

                    length++;

                }

            }

            Put put = new Put(Bytes.toBytes(key.toString()));

            BigDecimal decimal = new BigDecimal(freeTime / length / 1000 / 60 / 60);

            BigDecimal setScale = decimal.setScale(2, RoundingMode.HALF_DOWN);

            put.addColumn(family, "freeTime".getBytes(), Bytes.toBytes(setScale.toString()));

            context.write(null, put);

            /********** End *********/

        }

    }

    public int run(String[] args) throws Exception {

        // 配置Job

        Configuration conf = HBaseUtil.conf;

        String arg1 = "t_shared_bicycle";

        String arg2 = "t_bicycle_freetime";

        try {

            HBaseUtil.createTable(arg2, new String[]{"info"});

        } catch (Exception e) {

            // 创建表失败

            e.printStackTrace();

        }

        Job job = configureJob(conf, new String[]{arg1, arg2});

        return job.waitForCompletion(true) ? 0 : 1;

    }

    private Job configureJob(Configuration conf, String[] args) throws IOException {

        String tablename = args[0];

        String targetTable = args[1];

        Job job = new Job(conf, tablename);

        Scan scan = new Scan();

        scan.setCaching(300);

        scan.setCacheBlocks(false);// 在mapreduce程序中千万不要设置允许缓存

        /********** Begin *********/

        //设置过滤条件 经过我的改写
//
//        Filter filter = new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("bicycleId"), CompareOperator.EQUAL, Bytes.toBytes("5996"));
//
//        scan.setFilter(filter);

        /**
         * 其实我认为有两种可以得到答案的方式，第一设置过滤器，这是一个可以过滤5个值的过滤器。
         * 其二 生成一张完整的有所有编号的平均日使用时间，在前段selvet中双重循环处，用if语句筛选出target行。
         */
        FilterUtil.createFilterList();
        try{
            FilterUtil.addSingleColumnValueFilter("info","bicycleId",CompareOperator.EQUAL,"5771");
        }catch (Exception e){
            e.printStackTrace();
        }
        Filter filters = new FilterList(FilterUtil.getList());
        scan.setFilter(filters);


        /********** End *********/

        // 初始化Mapreduce程序

        TableMapReduceUtil.initTableMapperJob(tablename, scan, MyMapper.class, Text.class, BytesWritable.class, job);

        // 初始化Reduce

        TableMapReduceUtil.initTableReducerJob(targetTable, // output table

                MyTableReducer.class, // reducer class

                job);

        job.setNumReduceTasks(1);

        return job;

    }

}