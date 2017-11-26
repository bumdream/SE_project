package dgu.donggukeas_admin.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 23..
 */

public class RunawayActive {
    private boolean isActive;

    public RunawayActive() {
    }

    public RunawayActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
