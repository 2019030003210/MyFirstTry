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
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import Entity.Good;
import Jobs.CountJob;
import Jobs.SortJob;
public class Task {
    public static void main(String[] args) throws Exception{
        System.out.println("abc");
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "compute visit count");
        job1.setMapperClass(CountJob.GoodCountMapper.class);
        job1.setCombinerClass(CountJob.SumReducer.class);
        job1.setReducerClass(CountJob.SumReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        Path job1InputPath = new Path("/user/input");
        Path job1OutputPath = new Path("/user/job1");
        FileInputFormat.addInputPath(job1, job1InputPath);
        FileOutputFormat.setOutputPath(job1, job1OutputPath);
//        System.exit(job1.waitForCompletion(true) ? 0 : 1);

        Job job2 = Job.getInstance(conf, "sort");
        job2.setMapperClass(SortJob.SortMapper.class);
        job2.setReducerClass(SortJob.SortReducer.class);
        job2.setOutputKeyClass(Good.class);
        job2.setOutputValueClass(NullWritable.class);
        Path job2InputPath = job1OutputPath;
        Path job2OutputPath = new Path("/user/output");
        FileInputFormat.addInputPath(job2, job2InputPath);
        FileOutputFormat.setOutputPath(job2, job2OutputPath);
        //System.exit(job2.waitForCompletion(true) ? 0 : 1);


        JobControl jobControl = new JobControl("Good Count job control");
        // 把job1和job2添加到jobControl
        // job1转换成受控作业ControlledJob
        ControlledJob ctrljob1 = new ControlledJob(conf);
        ctrljob1.setJob(job1);
        jobControl.addJob(ctrljob1);

        ControlledJob ctrljob2 = new ControlledJob(conf);
        ctrljob2.setJob(job2);
        ctrljob2.addDependingJob(ctrljob1); // job2依赖于job1， 表示job1执行完了，才执行job2
        jobControl.addJob(ctrljob2);

        Thread jobControlThread = new Thread(jobControl);
        jobControlThread.setDaemon(true);
        jobControlThread.start();
        //获取JobControl线程的运行状态
        while(true) {
            //判断整个jobControl是否全部运行结束
            if (jobControl.allFinished()) {
                System.out.println(jobControl.getSuccessfulJobList());
                break; // 结束，退出循环。
            }
        }    
    }
}