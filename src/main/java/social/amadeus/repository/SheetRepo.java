package social.amadeus.repository;

import social.amadeus.model.Sheet;

import java.util.List;

public interface SheetRepo {

    public long getId();

    public long getCount();

    public Sheet getLast();

    public Sheet get(long id);

    public Sheet save(Sheet sheet);

    public boolean update(Sheet sheet);

    public Sheet getByEndpoint(String endpoint);

    public List<Sheet> query(String query);

    public List<Sheet> getSheets(long id);

    public List<Sheet> getSheets();

    public boolean updateViews(long views, long id);

    public boolean delete(long id);

}
