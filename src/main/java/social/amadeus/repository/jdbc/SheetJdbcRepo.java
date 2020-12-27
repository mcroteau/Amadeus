package social.amadeus.repository.jdbc;

import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;

import java.util.List;

public class SheetJdbcRepo implements SheetRepo {

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public long getCount() {
        return 0;
    }

    @Override
    public Sheet save(Sheet sheet) {
        return null;
    }

    @Override
    public boolean update(Sheet sheet) {
        return false;
    }

    @Override
    public List<Sheet> getSheets(long id) {
        return null;
    }

    @Override
    public List<Sheet> getSheets() {
        return null;
    }

    @Override
    public boolean updateViews(long views, long id) {
        return false;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
}
