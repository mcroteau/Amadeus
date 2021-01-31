package social.amadeus.model;

import java.util.List;

public class ProfileOutput {
    String status;
    Account profile;
    List<Observed> observing;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getProfile() {
        return profile;
    }

    public void setProfile(Account profile) {
        this.profile = profile;
    }

    public List<Observed> getObserving() {
        return observing;
    }

    public void setObserving(List<Observed> observing) {
        this.observing = observing;
    }
}
