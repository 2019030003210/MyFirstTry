package Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

// 定义商品类用于排序
public class Good implements WritableComparable<Good> {
    private String id; // 商品id
    private int visitCount; // 访问量    

    // 定义构造函数
    // 反序列化时，需要反射调用空参构造函数，所以必须定义空构造函数
    public Good() {
    }

    public Good(String id, int visitCount) {
        this.id = id;
        this.visitCount = visitCount;
    }

    @Override
    public String toString() {
        return id + "\t" + visitCount;
    }

    // 获取id和vistCount的getter
    public String getId() {
        return id;
    }

    public int getVisitCount() {
        return visitCount;
    }

    /**
     * 序列化方法
     *
     * @param out
     * @throws IOException
     */
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id); // 序列化字符串用writeUTF方法
        out.writeInt(visitCount);
    }

    /**
     * 反序列化方法 注意反序列化的顺序和序列化的顺序完全一致
     *
     * @param in
     * @throws IOException
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        id = in.readUTF();
        visitCount = in.readInt();
    }

    @Override
    /*
     * 自定义排序逻辑
     * 用自身对象(this)与another比较。
     * 若希望排序后this放在another前面，函数需返回-1
     * 若希望排序后this放在another后面，函数需返回1
     * 顺序无要求（this与another相同)，函数需返回0
     */
    public int compareTo(Good another) {
        // 按访问量倒序排序
        if (this.visitCount > another.visitCount) return -1;
        if (this.visitCount < another.visitCount) return 1;
        // this.visitCount == another.visitCount
        return this.id.compareTo(another.id);
    }
}