package cn.jifit.tv.beacon;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by addler on 2017/6/14.
 */

public class Snapshot {
    public long minute;
    public Set<String> members = new HashSet<String>();
    public String beacon = null;
    public boolean done = false;
    public String toString(){
        return "" + ((minute + 480) % 1440 / 60) + "H" + (minute % 60) + "M [" + beacon + "] " + members.toString();
    }
}
