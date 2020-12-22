package social.amadeus.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import social.amadeus.repository.ActionRepo;
import social.amadeus.model.Resource;
import social.amadeus.model.ActionLike;
import social.amadeus.model.ActionShare;

public class ActionJdbcRepo implements ActionRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Resource getWebsite(String uri) {
        Resource resource = null;
        try{
            String sql = "select * from resources where uri = ?";
            resource = jdbcTemplate.queryForObject(sql, new Object[] { uri },
                    new BeanPropertyRowMapper<>(Resource.class));

        }catch(EmptyResultDataAccessException e){}
        return resource;
    }

    @Override
    public boolean saveWebsite(Resource resource) {
        String sql = "insert into resources ( uri, account_id, date_added ) values ( ?, ?, ? )";
        jdbcTemplate.update(sql, new Object[] {
            resource.getUri(), resource.getAccountId(), resource.getDateAdded()
        });
        return true;
    }

    @Override
    public boolean likeWebsite(ActionLike actionLike){
        String sql = "insert into action_likes (resource_id, account_id, date_liked) values ( ?, ?, ? )";
        jdbcTemplate.update(sql, new Object[] {
                actionLike.getResourceId(), actionLike.getAccountId(), actionLike.getDateLiked()
        });
        return true;
    }


    @Override
    public boolean isLiked(ActionLike actionLike){
        String sql = "select * from action_likes where resource_id = ? and account_id = ?";
        ActionLike existingLike = null;
        try {
            existingLike = jdbcTemplate.queryForObject(sql, new Object[]{ actionLike.getResourceId(), actionLike.getAccountId() }, new BeanPropertyRowMapper<ActionLike>(ActionLike.class));
        }catch(Exception e){}

        if(existingLike != null) return true;
        return false;
    }

    @Override
    public boolean shareWebsite(ActionShare actionShare){
        String sql = "insert into action_shares (resource_id, account_id, post_id, date_shared, comment) values ( ?, ?, ?, ?, ? )";
        jdbcTemplate.update(sql, new Object[] {
                actionShare.getResourceId(), actionShare.getAccountId(), actionShare.getPostId(), actionShare.getDateShared(), actionShare.getComment()
        });
        return true;
    }

    @Override
    public long getLikesCount(long resourceId) {
        String sql = "select count(*) from action_likes where resource_id = ?";
        long count = jdbcTemplate.queryForObject(sql, new Object[]{ resourceId }, Long.class);
        return count;
    }

    @Override
    public long getSharesCount(long resourceId) {
        String sql = "select count(*) from action_shares where resource_id = ?";
        long count = jdbcTemplate.queryForObject(sql, new Object[]{ resourceId }, Long.class);
        return count;
    }

}
