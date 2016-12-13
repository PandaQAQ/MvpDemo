package com.pandaq.mvpdemo.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by PandaQ on 2016/12/12.
 * email : 767807368@qq.com
 */

public class Utils {
    /**
     * 根据号码获取联系人名字
     *
     * @param context
     * @param phoneNum
     * @return
     */
    public static String getContactName(Context context, String phoneNum) {
        String contactName = phoneNum;
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER};
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNum));
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor
                    .getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            cursor.close();
        }
        return contactName;
    }

    /**
     * 获取短信中的验证码
     */
    public static String getSmsCode(String smsBody) {
        String code = "";
        //这里就简单的以连续6位数字作为验证码的匹配
        Pattern pattern = Pattern.compile("(\\d{6})");
        return code;
    }

    /**
     * 根据手机分辨率把dp转换成px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机分辨率把px转换成dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 格式化手环返回的数据
     *
     * @param data
     * @param characteristic
     * @return
     */
    public static String[] formatData(byte[] data,
                                      BluetoothGattCharacteristic characteristic) {
        if (data != null && data.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            return stringBuilder.toString().split(" ");
        } else {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            return null;
        }
    }

    /**
     * 16进制数组转10进制数组
     *
     * @param data
     * @return
     */
    public static String[] decode(String data) {
        String[] datas = data.split(" ");
        String[] stringDatas = new String[datas.length];
        for (int i = 0; i < datas.length; i++) {
            stringDatas[i] = Integer.toString(Integer.parseInt(datas[i], 16));
        }
        return stringDatas;
    }

    /**
     * 10进制转16进制
     *
     * @param data
     * @return
     */
    public static String decodeToHex(String data) {
        String string = Integer.toHexString(Integer.parseInt(data));
        return string;
    }

    /**
     * 16进制转10进制
     *
     * @param data
     * @return
     */
    public static String decodeToString(String data) {
        String string = Integer.toString(Integer.parseInt(data, 16));
        return string;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.trim().equals("")
                || s.trim().equals("null");
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() != 0 && !s.trim().equals("")
                && !s.trim().equals("null");
    }

    /**
     * 计算两个日期的时间间隔
     *
     * @param sDate 开始时间
     * @param eDate 结束时间
     * @return interval时间间隔
     */
    public static int calInterval(Date sDate, Date eDate) {
        // 时间间隔，初始为0
        int interval = 0;

		/* 比较两个日期的大小，如果开始日期更大，则交换两个日期 */
        // 标志两个日期是否交换过
        boolean reversed = false;
        if (compareDate(sDate, eDate) > 0) {
            Date dTest = sDate;
            sDate = eDate;
            eDate = dTest;
            // 修改交换标志
            reversed = true;
        }

		/* 将两个日期赋给日历实例，并获取年、月、日相关字段值 */
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sDate);
        int sYears = sCalendar.get(Calendar.YEAR);
        int sMonths = sCalendar.get(Calendar.MONTH);
        int sDays = sCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar eCalendar = Calendar.getInstance();
        eCalendar.setTime(eDate);
        int eYears = eCalendar.get(Calendar.YEAR);
        int eMonths = eCalendar.get(Calendar.MONTH);
        int eDays = eCalendar.get(Calendar.DAY_OF_YEAR);

        // 年
        interval = eYears - sYears;
        if (eMonths < sMonths) {
            --interval;
        }
        // 月
        interval = 12 * (eYears - sYears);
        interval += (eMonths - sMonths);
        // 日
        interval = 365 * (eYears - sYears);
        interval += (eDays - sDays);
        // 除去闰年天数
        while (sYears < eYears) {
            if (isLeapYear(sYears)) {
                --interval;
            }
            ++sYears;
        }
        // 如果开始日期更大，则返回负值
        if (reversed) {
            interval = -interval;
        }
        // 返回计算结果
        return interval;
    }

    /**
     * 比较两个Date类型的日期大小
     *
     * @param sDate 开始时间
     * @param eDate 结束时间
     * @return result返回结果(0--相同 1--前者大 2--后者大)
     */
    public static int compareDate(Date sDate, Date eDate) {
        int result = 0;
        // 将开始时间赋给日历实例
        Calendar sC = Calendar.getInstance();
        sC.setTime(sDate);
        // 将结束时间赋给日历实例
        Calendar eC = Calendar.getInstance();
        eC.setTime(eDate);
        // 比较
        result = sC.compareTo(eC);
        // 返回结果
        return result;
    }

    /**
     * 字符串去除两头空格，如果为空，则返回""，如果不空，则返回该字符串去掉前后空格
     *
     * @param tStr 输入字符串
     * @return 如果为空，则返回""，如果不空，则返回该字符串去掉前后空格
     */
    public static String cTrim(String tStr) {
        String ttStr = "";
        if (tStr == null) {
        } else {
            ttStr = tStr.trim();
        }
        return ttStr;
    }

    /**
     * 判定某个年份是否是闰年
     *
     * @param year 待判定的年份
     * @return 判定结果
     */
    public static boolean isLeapYear(int year) {
        return (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0));
    }

    /**
     * 字符串时间转换成calendar
     *
     * @param strDate
     * @param pattern
     * @return
     */
    public static Calendar strDate2Calendar(String strDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(strDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取间隔时间（分钟）
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param pattern   时间格式
     * @return intervalMin 间隔分钟
     */
    public static int getIntervalMin(String startDate, String endDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        int intervalMin = 0;
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            long interval = end.getTime() - start.getTime();
            intervalMin = (int) (interval / 1000 / 60);
            return intervalMin;
        } catch (Exception e) {
            e.printStackTrace();
            return intervalMin;
        }
    }

    /**
     * calendar转换成字符串时间
     *
     * @param calendar
     * @param pattern
     * @return
     */
    public static String calendar2strDate(Calendar calendar, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(calendar.getTime());
    }

    /**
     * 注意问题:
     * 在Calendar中每周是从我们中国人的周日(星期七)开始计算的.
     * 所以Calendar的周一实际为我们中国人的上周的星期七.
     * 在此需要特殊处理一下.
     */
    public static int getWeekInChina(Calendar calendar) {
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            week = week - 1;
        }
        return week;
    }

    public static int getWeekDayInChina(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 7;
        } else {
            day = day - 1;
        }
        return day;
    }

    /**
     * 将二进制字符转换成16进制字符
     *
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 将16进制字符串转换成二进制字符串
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(
                    hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }
}
