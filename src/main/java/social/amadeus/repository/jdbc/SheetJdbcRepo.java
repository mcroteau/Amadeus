package social.amadeus.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import social.amadeus.model.Flyer;
import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;

import java.util.List;

public class SheetJdbcRepo implements SheetRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public long getCount() {
        String sql = "select count(*) from sheets";
        long count = jdbcTemplate.queryForObject(sql, new Object[] { }, Long.class);
        return count;
    }

    @Override
    public Sheet getLast() {
        String sql = "select max(id) from sheets";
        long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);

        String selectSql = "select * from sheets where id = ?";
        Sheet savedSheet = jdbcTemplate.queryForObject(selectSql, new Object[]{id},
                new BeanPropertyRowMapper<>(Sheet.class));

        return savedSheet;
    }

    @Override
    public Sheet get(long id) {
        String sql = "select * from sheets where id = ?";

        Sheet sheet = jdbcTemplate.queryForObject(sql, new Object[] { id },
                new BeanPropertyRowMapper<>(Sheet.class));

        return sheet;
    }

    @Override
    public Sheet save(Sheet sheet) {
        String sql = "insert into sheets (title, description, image_uri, endpoint, date_created) values (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, new Object[]{
                    sheet.getTitle(), sheet.getDescription(), sheet.getImageUri(), sheet.getEndpoint(), sheet.getDateCreated()
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        Sheet savedSheet = getLast();
        return savedSheet;
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
