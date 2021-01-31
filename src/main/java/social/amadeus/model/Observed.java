package social.amadeus.model;

public class Observed {

    long id;
    long observerId;
    long observedId;
    long dateCreated;

    String name;
    String imageUri;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
