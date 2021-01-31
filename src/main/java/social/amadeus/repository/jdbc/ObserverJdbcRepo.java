package social.amadeus.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import social.amadeus.model.Observed;
import social.amadeus.repository.ObserverRepo;

import java.util.List;

@Repository
public class ObserverJdbcRepo implements ObserverRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long getCount() {
        String sql = "select count(*) from observers";
        long count = jdbcTemplate.queryForObject(sql, new Object[] { }, Long.class);
        return count;
    }

    @Override
    public Observed getLast() {
        String sql = "select max(id) from observers";
        long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);

        String selectSql = "select * from observers where id = ?";
        Observed persistedObserved = jdbcTemplate.queryForObject(selectSql, new Object[]{id},
                new BeanPropertyRowMapper<>(Observed.class));

        return persistedObserved;
    }

    public Observed get(long id) {
        String sql = "select * from observers where id = ?";

        Observed observed = jdbcTemplate.queryForObject(sql, new Object[] { id },
                new BeanPropertyRowMapper<>(Observed.class));

        return observed;
    }


    public Observed get(long observerId, long observedId) {
        String sql = "select * from observers where observer_id = ? and observed_id = ?";

        Observed observed = jdbcTemplate.queryForObject(sql, new Object[] { observerId, observedId },
                new BeanPropertyRowMapper<>(Observed.class));

        return observed;
    }

    public boolean observe(Observed observed) {
        String sql = "insert into observers (observed_id, observer_id, date_created) values (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, new Object[]{
                observed.getObservedId(), observed.getObserverId(), observed.getDateCreated()
            });
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean unobserve(Observed observed) {
        String sql = "delete from observers where observed_id = ? and observer_id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{
                    observed.getObservedId(), observed.getObserverId()
            });
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public List<Observed> getObserving(long observerId) {
        String sql = "select * from observers where observer_id = ?";

        List<Observed> observed = jdbcTemplate.query(sql, new Object[] { observerId },
                new BeanPropertyRowMapper<>(Observed.class));

        return observed;
    }

    @Override
    public boolean isObserved(Observed observed) {
        String sql = "select * from observers where observed_id = ? and observer_id = ?";
        Observed savedObserved = null;
        try {
            savedObserved = jdbcTemplate.queryForObject(sql, new Object[]{
                        observed.getObservedId(), observed.getObserverId()
                    },new BeanPropertyRowMapper<>(Observed.class));
        }catch(Exception ex){
            return false;
        }
        if(savedObserved == null) return false;
        return true;
    }
}
