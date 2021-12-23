package edu.zsc.util.hbase;

import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.util.Bytes;
import java.util.List;
import java.util.ArrayList;

public class FilterUtil {
    public static List<Filter> list;

    public static List<Filter> getList() { return list;}

    public static void createFilterList() {
        if (list == null) {
            list = new ArrayList<Filter>();
        }
        else {
            list.clear();
        }
    }
    public static  void addFamilyFilter(String family, CompareOperator op) {
        Filter filter = new FamilyFilter(
            op,
            new BinaryComparator(Bytes.toBytes(family))
        );
        list.add(filter);
    }

    public static  void addQualifier(String qualifier, CompareOperator op) {
        Filter filter = new QualifierFilter(
            op,
            new BinaryComparator(Bytes.toBytes(qualifier))
        );
        list.add(filter);
    }

    public static  void addSingleColumnValueFilter(String family,
        String qualifier,
        CompareOperator op,
        String value) {
        Filter fiter = new SingleColumnValueFilter(
            Bytes.toBytes(family), 
            Bytes.toBytes(qualifier),
            op, 
            Bytes.toBytes(value));
        list.add(fiter);
    }    
}
