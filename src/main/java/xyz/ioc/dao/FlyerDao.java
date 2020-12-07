package xyz.ioc.dao;

import xyz.ioc.model.Flyer;

import java.util.List;

public interface FlyerDao {

    public Flyer getLast();

    public Flyer get(long id);

    public Flyer save(Flyer flyer);

    public boolean update(Flyer flyer);

    public boolean delete(long id);

    public List<Flyer> getFlyers();

    public List<Flyer> getFlyers(long accountId);

    public List<Flyer> getActiveFlyers();

    public boolean updateViews(int views, long id);

    public boolean crumpleUp(long id);

}
