package social.amadeus.repository;

import java.util.List;

import social.amadeus.model.MusicFile;
import social.amadeus.model.AccountMusic;

public interface MusicRepo {
		
	public long id();

	public long count();

	public long getCountAccountMusics(long accountId);

	public MusicFile get(long id);

	public MusicFile get(String artist, String title);

	public boolean exists(String artist, String title);

	public List<MusicFile> search(String query);

	public List<AccountMusic> getAccountMusics(long id);

	public MusicFile save(MusicFile musicFile);

	public boolean addPlaylist(AccountMusic accountMusic);

	public boolean remove(AccountMusic accountMusic);

    public long countByQuery(String query);
}