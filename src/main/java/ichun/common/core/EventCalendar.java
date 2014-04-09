package ichun.common.core;

import java.util.Calendar;

public class EventCalendar
{
    public static boolean isNewYear; //1/1
    public static boolean isPgBirthday; //9/3
    public static boolean isAFDay; //1/4
    public static boolean isHalloween; //31/10
    public static boolean isChristmas; //25/12

    public static void checkDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        switch(calendar.get(2)) //month
        {
            case 1:
            {
                if(calendar.get(5) == 1)
                {
                    isNewYear = true;
                }
                break;
            }
            case 3:
            {
                if(calendar.get(5) == 9) //day
                {
                    isPgBirthday = true;
                }
                break;
            }
            case 4:
            {
                if(calendar.get(5) == 1)
                {
                    isAFDay = true;
                }
                break;
            }
            case 10:
            {
                if(calendar.get(5) == 31)
                {
                    isHalloween = true;
                }
                break;
            }
            case 12:
            {
                if(calendar.get(5) == 25)
                {
                    isChristmas = true;
                }
                break;
            }
        }
    }
}
