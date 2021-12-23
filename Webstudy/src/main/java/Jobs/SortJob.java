package Jobs;
import Entity.Good;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SortJob {
    public static class SortMapper extends Mapper <Object, Text, Good, NullWritable> {
        public void map(Object key, Text value, Context context) 
            throws IOException, InterruptedException {
                String[] strs = value.toString().split("\t");
                if (strs.length < 2) return;
                Good good = new Good(strs[0], Integer.parseInt(strs[1]));
                context.write(good, NullWritable.get());
            }
    }

    public static class SortReducer extends Reducer<Good, NullWritable, Good, NullWritable> {
        public void reduce(Good key, Iterable<NullWritable> values, Context context)
            throws IOException, InterruptedException {
                context.write(key, NullWritable.get());
            }
    }
}