package social.amadeus.repository;

import social.amadeus.model.Observe;

import java.util.List;

public interface ObserverRepo {

    public long getCount();

    public Observe getLast();

    public Observe get(long id);

    public Observe get(long observerId, long observedId);

    public Observe observe(Observe observe);

    public boolean unobserve(long observerId, long observedId);

    public List<Observe> getObserved(long observedId);

}
