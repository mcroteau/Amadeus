package social.amadeus.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import social.amadeus.model.Observe;
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
    public Observe getLast() {
        String sql = "select max(id) from observers";
        long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);

        String selectSql = "select * from observers where id = ?";
        Observe persistedObserve = jdbcTemplate.queryForObject(selectSql, new Object[]{id},
                new BeanPropertyRowMapper<>(Observe.class));

        return persistedObserve;
    }

    public Observe get(long id) {
        String sql = "select * from observers where id = ?";

        Observe observer = jdbcTemplate.queryForObject(sql, new Object[] { id },
                new BeanPropertyRowMapper<>(Observe.class));

        return observer;
    }


    public Observe get(long observerId, long observedId) {
        String sql = "select * from observers where observer_id = ? and observed_id = ?";

        Observe observer = jdbcTemplate.queryForObject(sql, new Object[] { observerId, observedId },
                new BeanPropertyRowMapper<>(Observe.class));

        return observer;
    }

    public Observe observe(Observe observer) {

        String sql = "insert into observers (observer_id, observed_id) values (?, ?)";
        try {
            jdbcTemplate.update(sql, new Object[]{
                observer.getObserverId(), observer.getObserverId()
            });
        }catch(Exception e){
            e.printStackTrace();
        }

        Observe persistedObserve = getLast();

        return persistedObserve;
    }


    @Override
    public boolean unobserve(long observerId, long observedId) {
        String sql = "delete from observers where id = ?";
        try {
            jdbcTemplate.update(sql, new Object[]{ observedId, observedId });
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public List<Observe> getObserved(long observedId) {
        String sql = "select * from observers where observed_id = ?";

        List<Observe> observers = jdbcTemplate.query(sql, new Object[] { observedId },
                new BeanPropertyRowMapper<>(Observe.class));

        return observers;
    }
}
