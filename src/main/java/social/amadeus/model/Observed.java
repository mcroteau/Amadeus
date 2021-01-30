package social.amadeus.model;

public class Observed {

    long id;
    long observerId;
    long observedId;
    long dateCreated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getObserverId() {
        return observerId;
    }

    public void setObserverId(long observerId) {
        this.observerId = observerId;
    }

    public long getObservedId() {
        return observedId;
    }

    public void setObservedId(long observedId) {
        this.observedId = observedId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

}
