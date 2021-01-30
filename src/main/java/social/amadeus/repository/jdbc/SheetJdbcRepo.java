package social.amadeus.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import social.amadeus.model.Account;
import social.amadeus.model.Flyer;
import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;

import java.util.List;

@Repository
public class SheetJdbcRepo implements SheetRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
        String sql = "insert into sheets (title, description, image_uri, endpoint, account_id, date_created) values (?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, new Object[]{
                    sheet.getTitle(), sheet.getDescription(), sheet.getImageUri(), sheet.getEndpoint(), sheet.getAccountId(), sheet.getDateCreated()
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        Sheet savedSheet = getLast();
        return savedSheet;
    }

    @Override
    public boolean update(Sheet sheet) {
        String sql = "update sheets set title = ?, description = ?, image_uri = ?, endpoint = ?, sheet_views = ? where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{
                sheet.getTitle(), sheet.getDescription(), sheet.getImageUri(), sheet.getEndpoint(), sheet.getSheetViews(), sheet.getId()
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Sheet getByEndpoint(String endpoint) {
        Sheet sheet = null;
        try {
            String sql = "select * from sheets where endpoint = ?";

            sheet = jdbcTemplate.queryForObject(sql, new Object[]{endpoint},
                    new BeanPropertyRowMapper<>(Sheet.class));

        }catch (Exception e){
            return null;
        }
        return sheet;
    }

    @Override
    public List<Sheet> query(String unclean) {
        String q = unclean.replaceAll("([-+.^:,])","");
        String sql = "select distinct * from sheets where upper(title) like upper(:query) order by title asc";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("query", "%" + q + "%");

        List<Sheet> sheets = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Sheet.class));
        return sheets;

    }

    @Override
    public List<Sheet> getSheets(long accountId) {
        String sql = "select * from sheets where account_id = ?";

        List<Sheet> sheets = jdbcTemplate.query(sql, new Object[] { accountId },
                new BeanPropertyRowMapper<>(Sheet.class));

        return sheets;
    }

    @Override
    public List<Sheet> getSheets() {
        String sql = "select * from sheets";

        List<Sheet> sheets = jdbcTemplate.query(sql, new Object[] { },
                new BeanPropertyRowMapper<>(Sheet.class));

        return sheets;
    }

    @Override
    public boolean updateViews(long views, long id) {
        String sql = "update sheets set sheet_views = ? where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{ views, id });
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(long id) {
        String sql = "delete from sheets where id = ?";
        jdbcTemplate.update(sql, new Object[] { id });
        return true;
    }
}
