package cn.jifit.tv.beacon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by addler on 2017/6/7.
 */

public class KeyValueCache {

    private static ArrayList<Snapshot> cache = new ArrayList<Snapshot>();

    // Singleton
    private KeyValueCache() {}
    private static class SingletonHelper{
        private static final KeyValueCache INSTANCE = new KeyValueCache();
    }
    public static KeyValueCache getInstance(){
        return SingletonHelper.INSTANCE;
    }


    public void saveBeaconData(long time, String oa, String da){
        oa = Bracelet.formatMAC(oa);
        da = Bracelet.formatMAC(da);
        if (da.equals("880F10FFFFFF")) return;
        long second = time / 60000;
        if ((oa != null) && (da != null)){
//            String key = "" + second + "|" + oa;
            int size = cache.size();
            boolean saved = false;
            for (int i = 0; i < size; i++){
                if ((cache.get(i).minute == second) && (oa.equals(cache.get(i).beacon))){
                    cache.get(i).members.add(da);
                    saved = true;
                    break;
                }
            }
            if (!saved) {
                Snapshot ss = new Snapshot();
                ss.beacon = oa;
                ss.minute = second;
                ss.members.add(da);
                cache.add(ss);
            }
        }
    }
    public String[] toStrings(){
        int size = cache.size();
        String[] output = new String[size];
        for (int i = 0; i < size; i++){
            output[i] = cache.get(i).toString();
        }
        return output;
    }

//    private String format(String mac){
//        //
//        String pattern = "^([0-9A-F]{12})|([0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2}:[0-9A-F]{2})$";
//        if (mac.toUpperCase().matches(pattern)){
//            return mac.toUpperCase().replaceAll(":", "");
//        }
//        return null;
//    }

    /**
     * get all members mac near beaconIDSet last minutes
     * ignore beacons param;
     */

    public String[] getActiveMembers(int minutes, Set<String> beacons){
        Set<String> result = new HashSet<String>();
        long now = System.currentTimeMillis() / 60000;
        for (int i = 0; i < cache.size(); i++){
            Snapshot ss = cache.get(i);
            if (now - ss.minute < minutes){
                result.addAll(ss.members);
            }
        }
        String[] strings = new String[result.size()];
        strings = result.toArray(strings);
        return strings;
    }

    public Snapshot getData(int minutes){
        if (cache.size() > 0){
            long now = System.currentTimeMillis() / 60000;
            for (int i = 0; i < cache.size(); i++){
                Snapshot ss = cache.get(i);
                long time = ss.minute;
                if ((now - time > minutes) && (!ss.done)) {
                    return ss;
                }
            }
        }
        return null;
    }

    public Snapshot getData(int minutes, boolean done){
        if (cache.size() > 0){
            long now = System.currentTimeMillis() / 60000;
            for (int i = 0; i < cache.size(); i++){
                Snapshot ss = cache.get(i);
                long time = ss.minute;
                if ((now - time > minutes) && (ss.done == done)) {
                    return ss;
                }
            }
        }
        return null;
    }

    public void removeData(long minutes, String beacon){
        if (cache.size() > 0){
            int index = -1;
            for (int i = 0; i < cache.size(); i++){
                Snapshot ss = cache.get(i);
                if ((minutes == ss.minute) && (beacon.equals(ss.beacon))) {
                    index = i;
                }
            }
            if (index >= 0){
                cache.remove(index);
            }
        }
    }
    public void tagDoneData(long minutes, String beacon){
        if (cache.size() > 0){
            for (int i = 0; i < cache.size(); i++){
                Snapshot ss = cache.get(i);
                if ((minutes == ss.minute) && (beacon.equals(ss.beacon))) {
                    ss.done = true;
                }
            }
        }
    }


}
