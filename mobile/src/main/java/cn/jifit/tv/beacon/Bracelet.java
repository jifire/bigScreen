package cn.jifit.tv.beacon;

import java.util.ArrayList;

/**
 * Created by addler on 2017/6/8.
 */

public class Bracelet {
    private static ArrayList<MIBracelet> cache = new ArrayList<MIBracelet>();

    // Singleton
    private Bracelet() {
//        cache.add(new MIBracelet("CAC1CBD3EF40", "宗亭亭"));
//        cache.add(new MIBracelet("FFA854807A4C", "黄立波"));
//        cache.add(new MIBracelet("E1F9A849FD55", "王亚红"));
//        cache.add(new MIBracelet("D93B51CDB35E", "陈展跃"));
//        cache.add(new MIBracelet("C6839D221ECE", "刘佳奇"));
//        cache.add(new MIBracelet("E6F90E8FBFF1", "杨亮"));
//        cache.add(new MIBracelet("CF9020366A64", "张宇"));
//        cache.add(new MIBracelet("D853407901A7", "齐子毅"));
//        cache.add(new MIBracelet("D8BF4F2D993A", "李阳"));
//        cache.add(new MIBracelet("D7C198A75F86", "白云波"));
//        cache.add(new MIBracelet("ECF04501A340", "李素华"));
//        cache.add(new MIBracelet("C1D29352EAD6", "Larry"));
    }
    private static class SingletonHelper{
        private static final Bracelet INSTANCE = new Bracelet();
    }
    public static Bracelet getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public static String formatMAC(String mac){
        //
        String pattern = "^([0-9A-F]{12})|([0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2})$";
        if (mac.toUpperCase().matches(pattern)){
            return mac.toUpperCase().replaceAll(":", "");
        }
        return null;
    }

    public void update(String mac, String cname){
        mac = formatMAC(mac);
        MIBracelet bracelet = null;
        for (int i = 0; i < cache.size(); i++){
            if (cache.get(i).mac.equals(mac)) {
                bracelet = cache.get(i);
                break;
            }

        }
        if (bracelet == null){
            cache.add(new MIBracelet(mac, cname));
        }else{
            if (!bracelet.cname.equals(cname)){
                bracelet.cname = cname;
            }
        }
    }
    public String getName(String mac){
        String cname = "未知用户:" + mac;
        for (int i = 0; i< cache.size(); i++){
            if (cache.get(i).mac.equals(mac)){
                return cache.get(i).cname;
            }
        }
        return "";
    }


    private class MIBracelet{
        private String mac;
        private String cname;
        public MIBracelet(String mac, String cname){
            this.mac = mac;
            this.cname = cname;
        }
    }
}
