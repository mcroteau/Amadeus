package social.amadeus.repository;

import social.amadeus.model.Observed;

import java.util.List;

public interface ObserverRepo {

    public long getCount();

    public Observed getLast();

    public Observed get(long id);

    public Observed get(long observerId, long observedId);

    public boolean observe(Observed observed);

    public boolean unobserve(Observed observed);

    public List<Observed> getObserving(long observedId);

    boolean isObserved(Observed observed);
}
