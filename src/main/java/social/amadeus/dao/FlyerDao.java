package social.amadeus.dao;

import social.amadeus.model.Flyer;

import java.util.List;

public interface FlyerDao {

    public long getCount();

    public Flyer getLast();

    public Flyer get(long id);

    public Flyer save(Flyer flyer);

    public boolean update(Flyer flyer);

    public boolean delete(long id);

    public List<Flyer> getFlyers();

    public List<Flyer> getFlyers(long accountId);

    public List<Flyer> getActiveFlyers();

    public boolean updateViews(long views, long id);

    public boolean crumpleUp(long id);

}
