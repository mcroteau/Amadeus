package social.amadeus.repository;

import social.amadeus.model.Role;

public interface RoleRepo {

	public int count();

	public Role get(int id);
	
	public Role find(String name);
	
	public void save(Role role);

	// public Role save(Role role);
	
}