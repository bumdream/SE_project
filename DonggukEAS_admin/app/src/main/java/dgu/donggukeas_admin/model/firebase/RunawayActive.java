package dgu.donggukeas_admin.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 23..
 */

public class RunawayActive {
    private int isActive;

    public RunawayActive(int isActive) {
        this.isActive = isActive;
    }

    public RunawayActive() {
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
