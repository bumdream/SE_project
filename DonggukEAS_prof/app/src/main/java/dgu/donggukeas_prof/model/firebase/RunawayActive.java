package dgu.donggukeas_prof.model.firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qowhd on 2017-11-23.
 */

public class RunawayActive {
    private int isActive;

    public RunawayActive()
    {}

    public RunawayActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("isActive", isActive);

        return result;
    }
}
