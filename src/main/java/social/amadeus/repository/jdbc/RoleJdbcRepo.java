package social.amadeus.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import org.springframework.stereotype.Repository;
import social.amadeus.repository.RoleRepo;
import social.amadeus.model.Role;

@Repository
public class RoleJdbcRepo implements RoleRepo {
	
	
	@Autowired
	public JdbcTemplate jdbcTemplate;
	

	public int count() {
		String sql = "select count(*) from role";
		int count = jdbcTemplate.queryForObject(sql, new Object[] { }, Integer.class);
	 	return count; 
	}


	public Role get(int id) {
		String sql = "select * from role where id = ?";
		Role role = jdbcTemplate.queryForObject(sql, new Object[] { id }, 
				new BeanPropertyRowMapper<Role>(Role.class));
		return role;
	}

	
	public Role find(String name) {
		Role role = null; 
		try{
			String sql = "select * from role where name = '" + name + "'";
			role = jdbcTemplate.queryForObject(sql, new Object[] {}, 
				new BeanPropertyRowMapper<Role>(Role.class));
		}catch(EmptyResultDataAccessException e){
			
		}
		return role;
	}
	
	
	public List<Role> findAll() {
		String sql = "select * from role";
		List<Role> roles = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Role>(Role.class));
		return roles;
	}
	
	
	public void save(Role role) {
		String sql = "insert into role (name) values(?)";
		jdbcTemplate.update(sql, new Object[] { 
			role.getName()  
		});
	}
	
}