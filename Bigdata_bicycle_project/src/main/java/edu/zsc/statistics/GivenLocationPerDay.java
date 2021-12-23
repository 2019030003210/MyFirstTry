package edu.zsc.statistics;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import edu.zsc.util.hbase.HBaseUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;


/**
 * 共享单车每天在韩庄村的平均空闲时间
 */
public class GivenLocationPerDay extends Configured implements Tool {

    public static final byte[] family = "info".getBytes();
    public int run(String[] args) throws Exception {
        // 配置Job
        Configuration conf = HBaseUtil.conf;
        //Scanner sc = new Scanner(System.in);
        //String arg1 = sc.next();
        //String arg2 = sc.next();
        String arg1 = "t_shared_bicycle";
        String arg2 = "t_bicycle_avgnum";
        try {
            HBaseUtil.createTable(arg2, new String[] { "info" });
        } catch (Exception e) {
            // 创建表失败
            e.printStackTrace();
        }
        Job job = configureJob(conf, new String[] { arg1, arg2 });
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
        //设置过滤
        ArrayList<Filter> listForFilters = new ArrayList<Filter>();
        Filter destinationFilter = new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("destination"),
                CompareOperator.EQUAL, new SubstringComparator("Hanzhuangcun"));
        Filter departure = new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("departure"),
                CompareOperator.EQUAL, Bytes.toBytes("Hebei Baoding Xiongxian"));
        listForFilters.add(departure);
        listForFilters.add(destinationFilter);
        scan.setCaching(300);
        scan.setCacheBlocks(false);
        Filter filters = new FilterList(listForFilters);
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
    public static class MyMapper extends TableMapper<Text, BytesWritable> {
        protected void map(ImmutableBytesWritable rowKey, Result result, Context context)
                throws IOException, InterruptedException {
            /********** Begin *********/
            String beginTime = Bytes.toString(result.getValue(family, "startTime".getBytes()));
            String format = DateFormatUtils.format(Long.parseLong(beginTime), "yyyy-MM-dd", Locale.CHINA);
            BytesWritable bytesWritable = new BytesWritable(Bytes.toBytes(format));
            context.write(new Text("Hebei Baoding Xiongxian-Hanzhuangcun"), bytesWritable);
            /********** End *********/
        }
    }

    public static class MyTableReducer extends TableReducer<Text, BytesWritable, ImmutableBytesWritable> {
        @Override
        public void reduce(Text key, Iterable<BytesWritable> values, Context context)
                throws IOException, InterruptedException {
            /********** Begin *********/
            double sum = 0;
            int length = 0;
            Map<String, Integer> map = new HashMap<String, Integer>();
            for (BytesWritable price : values) {
                byte[] copyBytes = price.copyBytes();
                String string = Bytes.toString(copyBytes);
                if (map.containsKey(string)) {
                    Integer integer = map.get(string) + 1;
                    map.put(string, integer);

                } else {
                    map.put(string, new Integer(1));
                }
            }
            Collection<Integer> values2 = map.values();
            for (Integer i : values2) {
                length++;
                sum += i;

            }
            BigDecimal decimal = new BigDecimal(sum / length);
            BigDecimal setScale = decimal.setScale(2, RoundingMode.HALF_DOWN);
            Put put = new Put(Bytes.toBytes(key.toString()));
            put.addColumn(family, "avgNum".getBytes(), Bytes.toBytes(setScale.toString()));
            context.write(null, put);
            /********** End *********/
        }

    }


}
