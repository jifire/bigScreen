package cn.jifit.tv.beacon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created by addler on 2017/6/6.
 */

public class WorkFactory {
    public enum TYPE{
        MI_BEACON_JSON_WORK (0),
        MI_BEACON_DATA_REMOVE_WORK (1),
        MI_BEACON_DATA_AFTER_POST_WORK (2);

        TYPE(int nativeInt) {
            this.nativeInt = nativeInt;
        }
        final int nativeInt;
    }
    private WorkFactory(){}
    private static class SingletonHelper{
        private static final WorkFactory INSTANCE = new WorkFactory();
    }
    public static WorkFactory getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public Callable getCallable(WorkFactory.TYPE type, Object obj){
        if (type == TYPE.MI_BEACON_JSON_WORK){
            if (obj instanceof String){
                return new MIBeaconJsonWork((String)obj);
            }
        }
        if (type == TYPE.MI_BEACON_DATA_REMOVE_WORK){
            if (obj instanceof Snapshot){
                return new MIBeaconDataRemoveWork((Snapshot)obj);
            }
        }
        if (type == TYPE.MI_BEACON_DATA_AFTER_POST_WORK){
            if (obj instanceof Snapshot){
                return new MIBeaconDataTagDoneWork((Snapshot)obj);
            }
        }
        return null;
    }
    private class MIBeaconJsonWork implements Callable{
        private String json = null;
        private long currentTime = 0;
        private MIBeaconJsonWork(String json){
            this.json = json;
            this.currentTime = System.currentTimeMillis();
        }

        @Override
        public Object call() throws Exception {
            JSONObject obj;
            try {
                obj = new JSONObject(this.json);
                String oa = obj.getString("oa");
                JSONArray ds = obj.getJSONArray("ds");
                String index = obj.getString("index");
                for (int i = 0; i < ds.length(); i++){
                    String da = ds.getJSONObject(i).getString("da");
                    // {"oa":"fe01fa998811","index":"1","ds":[{"da":"001122434455"}]}
//                    System.out.println("Index:[" + index + "], OA:[" + oa + "], DA:[" + da + "]");
                    KeyValueCache.getInstance().saveBeaconData(currentTime, oa, da);
                }
            } catch (JSONException e) {
//                System.out.println("channel output, size: " + size
//                        + ", input: \r\n" + Channel.bytesToHexString(buf));
//                e.printStackTrace();
            }

            return null;
        }
    }
    private class MIBeaconDataRemoveWork implements Callable{
        private Snapshot snapshot = null;
        private long currentTime = 0;
        private MIBeaconDataRemoveWork(Snapshot snapshot){
            this.snapshot = snapshot;
        }

        @Override
        public Object call() throws Exception {
            KeyValueCache.getInstance().removeData(snapshot.minute, snapshot.beacon);
            return null;
        }
    }
    private class MIBeaconDataTagDoneWork implements Callable{
        private Snapshot snapshot = null;
        private long currentTime = 0;
        private MIBeaconDataTagDoneWork(Snapshot snapshot){
            this.snapshot = snapshot;
        }

        @Override
        public Object call() throws Exception {
            KeyValueCache.getInstance().tagDoneData(snapshot.minute, snapshot.beacon);
            return null;
        }
    }
}
