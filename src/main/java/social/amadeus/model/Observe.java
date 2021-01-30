package social.amadeus.model;

public class Observe {

    long id;
    long observerId;
    long observedId;

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
}
