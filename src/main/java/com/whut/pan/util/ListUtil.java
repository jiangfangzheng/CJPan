package com.whut.pan.util;

import com.whut.pan.domain.FileMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by zc on 2018/11/10.
 */
public class ListUtil {
    /**
     * 对文件列表按照（一）文件夹在前，文件在后，（二）更新时间最近的在前
     * @param fileMsgs
     */
    public static void listSort(List<FileMsg> fileMsgs){
        Collections.sort(fileMsgs,new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                FileMsg fileMsg1=(FileMsg)o1;
                FileMsg fileMsg2=(FileMsg)o2;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time1=fileMsg1.getTime();
                Date date1=null;
                Date date2=null;
                try {
                    date1 = formatter.parse(time1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String time2=fileMsg2.getTime();
                String isDir1=fileMsg1.getSize();
                String isDir2=fileMsg2.getSize();
                try {
                    date2 = formatter.parse(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(isDir1.equals("Directory") && !isDir2.equals("Directory")){
                    return -1;

                }else if(!isDir1.equals("Directory") && isDir2.equals("Directory")){
                    return 1;
                }else{
                    if(date1.getTime()>date2.getTime()){
                        return -1;
                    }else if(date1.getTime()<date2.getTime()){
                        return 1;
                    }else{
                        return 0;
                    }
                }

            }
        });

    }
}
