package xyz.ioc.dao.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import xyz.ioc.dao.FlyerDao;
import xyz.ioc.model.Flyer;
import java.util.List;

public class FlyerJdbcDao implements FlyerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Flyer getLast() {
        Flyer persistedFlyer = null;
        try {
            String sql = "select max(id) from flyers";
            long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);

            String selectSql = "select * from flyers where id = ?";
            persistedFlyer = jdbcTemplate.queryForObject(selectSql, new Object[]{id},
                    new BeanPropertyRowMapper<Flyer>(Flyer.class));
        }catch(Exception e){ }
        return persistedFlyer;
    }

    public Flyer get(long id) {
        String sql = "select * from flyers where id = ?";

        Flyer flyer = jdbcTemplate.queryForObject(sql, new Object[] { id },
                new BeanPropertyRowMapper<Flyer>(Flyer.class));

        return flyer;
    }

    public Flyer save(Flyer flyer) {

        String sql = "insert into flyers (account_Id, image_uri, page_uri, description, start_date, active) values (?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, new Object[]{
                flyer.getAccountId(), flyer.getImageUri(), flyer.getPageUri(), flyer.getDescription(), flyer.getStartDate(), flyer.isActive()
            });
        }catch(Exception e){
            e.printStackTrace();
        }

        Flyer persistedFlyer = getLast();

        return persistedFlyer;
    }

    @Override
    public boolean update(Flyer flyer) {
        String sql = "update flyers set image_uri = ?, page_uri = ?, description = ?, start_date = ?, active = ? where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{ flyer.getImageUri(), flyer.getPageUri(), flyer.getDescription(), flyer.getStartDate(), flyer.isActive(), flyer.getId()});
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean delete(long id) {
        String sql = "delete from flyers where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{ id });
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public List<Flyer> getFlyers() {
        String sql = "select * from flyers";

        List<Flyer> flyers = jdbcTemplate.query(sql, new Object[] { },
                new BeanPropertyRowMapper<Flyer>(Flyer.class));

        return flyers;
    }


    @Override
    public List<Flyer> getFlyers(long accountId) {
        String sql = "select * from flyers where account_id = ?";

        List<Flyer> flyers = jdbcTemplate.query(sql, new Object[] { accountId },
                new BeanPropertyRowMapper<Flyer>(Flyer.class));

        return flyers;
    }

    @Override
    public List<Flyer> getActiveFlyers() {
        String sql = "select * from flyers where active = true";

        List<Flyer> flyers = jdbcTemplate.query(sql, new Object[] { },
                new BeanPropertyRowMapper<Flyer>(Flyer.class));

        return flyers;
    }

    @Override
    public boolean updateViews(int views, long id) {
        String sql = "update flyers set views = ? where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{ views, id });
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean crumpleUp(long id) {
        String sql = "update flyers set active = false where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{ id });
        }catch(Exception e){
            return false;
        }
        return true;
    }
}
