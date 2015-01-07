package us.ichun.mods.ichunutil.common.core.util;

import java.util.Calendar;

public class EventCalendar
{
    private static boolean isNewYear; //1/1
    private static boolean isPgBirthday; //9/3
    private static boolean isAFDay; //1/4
    private static boolean isHalloween; //31/10
    private static boolean isChristmas; //25/12

    public static int day;

    public static void checkDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        day = calendar.get(Calendar.DAY_OF_MONTH);

        switch(calendar.get(Calendar.MONTH)) //month
        {
            case 1:
            {
                if(day == 1)
                {
                    isNewYear = true;
                }
                break;
            }
            case 3:
            {
                if(day == 9)
                {
                    isPgBirthday = true;
                }
                break;
            }
            case 4:
            {
                if(day == 1)
                {
                    isAFDay = true;
                }
                break;
            }
            case 10:
            {
                if(day == 31)
                {
                    isHalloween = true;
                }
                break;
            }
            case 12:
            {
                if(day == 25)
                {
                    isChristmas = true;
                }
                break;
            }
        }
    }

    public static boolean isNewYear()
    {
        return isNewYear;
    }

    public static boolean isPgBirthday()
    {
        return isPgBirthday;
    }

    public static boolean isAFDay()
    {
        return isAFDay;
    }

    public static boolean isHalloween()
    {
        return isHalloween;
    }

    public static boolean isChristmas()
    {
        return isChristmas;
    }
}
