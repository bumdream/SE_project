package dgu.donggukeas_client.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hanseungbeom on 2017. 11. 24..
 */

public class Utils {
    public static int getWeek()
    {
        Calendar semesterStart = Calendar.getInstance();
        semesterStart.set(Calendar.YEAR,  2017);
        semesterStart.set(Calendar.MONTH,  Calendar.AUGUST);
        semesterStart.set(Calendar.DATE,  28);

        int startDate = semesterStart.get(Calendar.DAY_OF_YEAR);
        //Log.d("###","startDate : "+startDate);

        Calendar currentDate = Calendar.getInstance();
        int todayDate = currentDate.get(Calendar.DAY_OF_YEAR);
        //Log.d("###","todayDate : "+todayDate);

        Calendar semesterEnd = Calendar.getInstance();
        //  semesterEnd = Calendar.getInstance();
        semesterEnd.set(Calendar.YEAR,  2017);
        semesterEnd.set(Calendar.MONTH,  Calendar.DECEMBER);
        semesterEnd.set(Calendar.DATE,  18);

//        int endDate = semesterEnd.get(Calendar.DAY_OF_YEAR);

        int weekNo = (todayDate-startDate)/7 + 1;

        if(weekNo>16)
            return 16;
        else
            return weekNo;
    }
    public static String getDateFromMilli(Long milli) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return new String(sdf.format(calendar.getTime()));
    }
}
