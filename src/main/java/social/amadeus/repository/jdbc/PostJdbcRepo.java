package social.amadeus.repository.jdbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.stereotype.Repository;
import social.amadeus.repository.ObserverRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.model.*;

@Repository
public class PostJdbcRepo implements PostRepo {

	private static final Logger log = Logger.getLogger(PostJdbcRepo.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private ObserverRepo observerRepo;


	public Long getPostId() {
		try {
			String sql = "select max(id) from posts";
			long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);
			return id;
		}catch(Exception ex){}
		return null;
	}

	public Long getPostShareId() {
		try{
			String sql = "select max(id) from post_shares";
			return jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);
		}catch (Exception ex){
			//ex.printStackTrace();
		}
		return null;
	}

	public Long getPostCommentId() {
		try {
			String sql = "select max(id) from post_comments";
			long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);
			return id;
		}catch (Exception ex){}
		return null;
	}


	public Long getPostShareCommentId() {
		try {
			String sql = "select max(id) from post_share_comments";
			long id = jdbcTemplate.queryForObject(sql, new Object[]{}, Long.class);
			return id;
		}catch(Exception ex){}
		return null;
	}


	public long getCount() {
		String sql = "select count(*) from posts";
		long count = jdbcTemplate.queryForObject(sql, new Object[] { }, Long.class);
	 	return count; 
	}

	
	public Post get(long id) {
		String sql = "select * from posts where id = ?";
		
		Post post = jdbcTemplate.queryForObject(sql, new Object[] { id }, 
				new BeanPropertyRowMapper<>(Post.class));

		return post;
	}


	public PostComment getPostComment(long id) {
		String sql = "select * from post_comments where id = ?";

		PostComment comment = jdbcTemplate.queryForObject(sql, new Object[] { id },
				new BeanPropertyRowMapper<>(PostComment.class));

		return comment;
	}


	public PostShareComment getPostShareComment(long id) {
		String sql = "select * from post_share_comments where id = ?";

		PostShareComment comment = jdbcTemplate.queryForObject(sql, new Object[] { id },
				new BeanPropertyRowMapper<PostShareComment>(PostShareComment.class));

		if(comment == null) comment = new PostShareComment();

		return comment;
	}


	public List<Post> getPosts(Account authdAccount){

		String sql = "select * from posts " +
				"where account_id = " + authdAccount.getId();

		List<Post> posts = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Post>(Post.class));

		return posts;
	}


	public List<Post> getActivity(long start, long end, Account authdAccount){
		
		List<Observed> observed = observerRepo.getObserving(authdAccount.getId());
		Set<Long> ids = new HashSet<>();
		ids.add(authdAccount.getId());
		for(Observed observing : observed) {
			ids.add(observing.getObservedId());
		}

		String idsString = StringUtils.join(ids, ",");

		String sql = "select p.id, p.account_id, p.content, p.date_posted, p.music_file_uri, p.video_file_uri, p.hidden, p.flagged," +
					"a.image_uri, a.name, a.username from " +
						"posts p inner join account a on p.account_id = a.id " +
							"where p.flagged = false and p.hidden = false and account_id in (" + idsString + ") " +
								"and p.date_posted between " + start + " and " + end + " order by p.date_posted desc";

		List<Post> activity = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Post>(Post.class));

		return activity;
	}



	public long getNewestCount(long start, long end, long authdAccountId){
		List<Observed> observed = observerRepo.getObserving(authdAccountId);
		Set<Long> ids = new HashSet<>();
		ids.add(authdAccountId);
		for(Observed observing : observed) {
			ids.add(observing.getObservedId());
		}

		String idsString = StringUtils.join(ids, ",");

		String sql = "select count(*) as c from " +
				"posts p left join account a on p.account_id = a.id " +
				"where p.account_id in (" + idsString + ") " +
				"and p.date_posted between " + start + " and " + end;

		return jdbcTemplate.queryForObject(sql, new Object[] { }, Long.class);
	}


	@Override
	public List<Post> getUserPosts(long accountId) {
		String sql = "select p.id, p.account_id, p.content, p.date_posted, p.image_file_uri, p.music_file_uri, p.video_file_uri, p.hidden, p.flagged, " +
				"a.image_uri, a.name, a.username from " +
				"posts p inner join account a on p.account_id = a.id " +
				"where p.flagged = false and p.hidden = false and account_id = ? order by p.date_posted desc";

		List<Post> posts = jdbcTemplate.query(sql, new Object[]{ accountId }, new BeanPropertyRowMapper<Post>(Post.class));

		return posts;
	}


	public Post save(Post post){
		String sql = "insert into posts (account_id, content, video_file_uri, video_file_name, date_posted, update_date, hidden, flagged ) values ( ?, ?, ?, ?, ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] { 
			post.getAccountId(), post.getContent(), post.getVideoFileUri(), post.getVideoFileName(), post.getDatePosted(), post.getUpdateDate(), false, false
		});
		Long id = getPostId();
		Post savedPost = get(id);
		
		return savedPost;
	}

	public boolean update(Post post) {
		String sql = "update posts set content = ?, update_date = ? where id = ?";
		jdbcTemplate.update(sql, new Object[] {
			post.getContent(), post.getUpdateDate(), post.getId()
		});
		return true;
	}

	public boolean publish(long id) {
		String sql = "update posts set published = true where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}

	public List<Post> getUnpublished() {
		String sql = "select * from posts where published = false";
		List<Post> posts = jdbcTemplate.query(sql, new Object[]{ }, new BeanPropertyRowMapper<Post>(Post.class));
		return posts;
	}

	public boolean hide(long id){
		String sql = "update posts set hidden = true where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}

	public boolean deleteHiddenPost(long postId, long accountId){
		String sql = "delete from hidden_posts where post_id = ? and account_id = ?";
		jdbcTemplate.update(sql, new Object[] { postId, accountId });
		return true;
	}

	public boolean delete(long id){
		String sql = "delete from posts where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}

	public long likes(long id){
		String sql = "select count(*) from post_likes where post_id = ?";
		long count = jdbcTemplate.queryForObject(sql, new Object[]{ id }, Long.class);
		return count;
	}


	public boolean like(PostLike postLike){
		String sql = "insert into post_likes (post_id, account_id, date_liked) values ( ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
				postLike.getPostId(), postLike.getAccountId(), postLike.getDateLiked()
		});
		return true;
	}


	public boolean liked(PostLike postLike){
		String sql = "select * from post_likes where post_id = ? and account_id = ?";
		PostLike existinPostLike = null;

		try {
			existinPostLike = jdbcTemplate.queryForObject(sql,
					new Object[]{
							postLike.getPostId(), postLike.getAccountId()
					}, new BeanPropertyRowMapper<PostLike>(PostLike.class));
		}catch(Exception e){}

		if(existinPostLike != null) return true;
		return false;
	}


	public PostLike getPostLike(long postId, long accountId){
		String sql = "select * from post_likes where post_id = ? and account_id = ?";
		PostLike postLike = null;

		try {
			postLike = jdbcTemplate.queryForObject(sql, new Object[]{ postId, accountId }, new BeanPropertyRowMapper<PostLike>(PostLike.class));
		}catch(Exception e){}

		return postLike;
	}


	public boolean unlike(PostLike postLike){
		String sql = "delete from post_likes where post_id = ? and account_id = ?";
		jdbcTemplate.update(sql, new Object[] {
				postLike.getPostId(), postLike.getAccountId()
		});
		return true;
	}


	public boolean deletePostLikes(long postId){
		String sql = "delete from post_likes where post_id = ?";
		jdbcTemplate.update(sql, new Object[] { postId });
		return true;
	}

	public List<PostShare> getPostShares(long postId){
		String sql = "select * from post_shares where post_id = ?";
		List<PostShare> postShares = jdbcTemplate.query(sql, new Object[]{ postId }, new BeanPropertyRowMapper<PostShare>(PostShare.class));
		return postShares;
	}

	public List<PostShare> getPostShares(long start, long end, long accountId){

		String sql = "select * from post_shares where account_id in (:ids) and date_shared between " + start + " and " + end + " order by date_shared desc";

		List<Observed> observed = observerRepo.getObserving(accountId);
		Set<Long> ids = new HashSet<>();
		ids.add(accountId);
		for(Observed observing : observed) {
			ids.add(observing.getObservedId());
		}

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", ids);

		List<PostShare> shares = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<PostShare>(PostShare.class));
		return shares;
	}

	@Override
	public List<PostShare> getUserPostShares(long accountId) {
		String sql = "select * from post_shares where account_id = ? order by date_shared desc";
		List<PostShare> postShares = jdbcTemplate.query(sql, new Object[]{ accountId }, new BeanPropertyRowMapper<PostShare>(PostShare.class));

		return postShares;
	}


	public long shares(long id){
		String sql = "select count(*) from post_shares where post_id = ?";
		long count = jdbcTemplate.queryForObject(sql, new Object[]{ id }, Long.class);
		return count;
	}


	public PostShare sharePost(PostShare postShare){
		String sql = "insert into post_shares (post_id, account_id, comment, date_shared) values ( ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
				postShare.getPostId(), postShare.getAccountId(), postShare.getComment(), postShare.getDateShared()
		});

		Long id = getPostShareId();
		PostShare savePostShare = getPostShare(id);

		return savePostShare;
	}


	public PostShare getPostShare(long postShareId){
		String sql = "select * from post_shares where id = ?";
		PostShare postShare = jdbcTemplate.queryForObject(sql, new Object[]{ postShareId }, new BeanPropertyRowMapper<PostShare>(PostShare.class));
		return postShare;
	}



	public boolean hasPostShares(long postId){
		List<PostShare> postShares = new ArrayList<PostShare>();

		try {
			String sql = "select * from post_shares where post_id = ?";
			postShares = jdbcTemplate.query(sql, new Object[]{ postId }, new BeanPropertyRowMapper<PostShare>(PostShare.class));
		}catch(Exception e){
			return false;
		}

		if(postShares.size() > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean deletePostShare(long id) {
		String sql = "delete from post_shares where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}


	public boolean removePosts(long id) {
		String sql = "delete from posts where account_id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}

	public boolean removePostShares(long id) {
		String sql = "delete from post_shares where post_id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}


	public List<PostComment> getPostComments(long postId){
		List<PostComment> postComments = null;
		try {
			String sql = "select distinct pc.id, pc.account_id, pc.date_created, pc.comment, " +
					"a.name as account_name, a.image_uri as account_image_uri from post_comments " +
					"inner join post_comments pc inner join account a on pc.account_id = a.id " +
					"where pc.post_id = ? order by pc.date_created asc";
			postComments = jdbcTemplate.query(sql, new Object[]{ postId }, new BeanPropertyRowMapper<>(PostComment.class));
			if (postComments == null) postComments = new ArrayList<PostComment>();
		}catch(Exception e){
			e.printStackTrace();
		}
		return postComments;
	}


	public List<PostShareComment> getPostShareComments(long postShareId){
		List<PostShareComment> postShareComments = null;
		try {
			String sql = "select distinct psc.id, psc.account_id, psc.date_created, psc.comment, " +
					"a.name as account_name, a.image_uri as account_image_uri from post_share_comments " +
					"inner join post_share_comments psc inner join account a on psc.account_id = a.id " +
					"where psc.post_share_id = ? order by psc.date_created asc";
			postShareComments = jdbcTemplate.query(sql, new Object[]{ postShareId }, new BeanPropertyRowMapper<>(PostShareComment.class));
			if (postShareComments == null) postShareComments = new ArrayList<>();
		}catch(Exception e){
			e.printStackTrace();
		}
		return postShareComments;
	}


	public PostComment savePostComment(PostComment postComment){
		log.info(postComment.getPostId());
		String sql = "insert into post_comments (post_id, account_id, comment, date_created) values ( ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
			postComment.getPostId(), postComment.getAccountId(), postComment.getComment(), postComment.getDateCreated()
		});

		Long id = getPostCommentId();
		PostComment savedComment = getPostComment(id);
		return savedComment;
	}


	public PostShareComment savePostShareComment(PostShareComment postShareComment){
		String sql = "insert into post_share_comments (post_share_id, account_id, comment, date_created) values ( ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
				postShareComment.getPostShareId(), postShareComment.getAccountId(), postShareComment.getComment(), postShareComment.getDateCreated()
		});

		Long id = getPostShareCommentId();
		PostShareComment savedComment = getPostShareComment(id);
		return savedComment;
	}


	public boolean deletePostComment(long id) {
		String sql = "delete from post_comments where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}


	public boolean deletePostComments(long postId){
		String sql = "delete from post_comments where post_id = ?";
		jdbcTemplate.update(sql, new Object[] { postId });
		return true;
	}


	public boolean deletePostShareComment(long id) {
		String sql = "delete from post_share_comments where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}


	public boolean deletePostShareComments(long postShareId){
		String sql = "delete from post_share_comments where post_share_id = ?";
		jdbcTemplate.update(sql, new Object[] { postShareId });
		return true;
	}

	public PostImage getImage(long id, String uri) {
		String sql = "select * from post_images where post_id = ? and uri = ?";
		PostImage postImage = jdbcTemplate.queryForObject(sql, new Object[]{ id, uri }, new BeanPropertyRowMapper<PostImage>(PostImage.class));
		return postImage;
	}

	public List<PostImage> getImages(long id) {
		List<PostImage> postImages = null;

		try {
			String sql = "select * from post_images where post_id = ?";
			postImages = jdbcTemplate.query(sql, new Object[]{ id }, new BeanPropertyRowMapper<PostImage>(PostImage.class));

			if (postImages == null) postImages = new ArrayList<>();
		}catch(Exception e){
			e.printStackTrace();
		}
		return postImages;
	}

	public boolean saveImage(PostImage postImage) {
		String sql = "insert into post_images (post_id, uri, file_name, date_uploaded) values ( ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
			postImage.getPostId(), postImage.getUri(), postImage.getFileName(), postImage.getDate()
		});
		return true;
	}

	public boolean deletePostImage(long id) {
		String sql = "delete from post_images where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}

	public boolean deletePostImage(long id, String imageUri) {
		String sql = "delete from post_images where post_id = ? and uri = ?";
		jdbcTemplate.update(sql, new Object[] { id, imageUri });
		return true;
	}


	@Override
	public List<PostMusic> getMusic(long id) {
		List<PostMusic> postMusics = null;

		try {
			String sql = "select * from post_music where post_id = ?";
			postMusics = jdbcTemplate.query(sql, new Object[]{ id }, new BeanPropertyRowMapper<PostMusic>(PostMusic.class));

			if (postMusics == null) postMusics = new ArrayList<PostMusic>();
		}catch(Exception e){
			e.printStackTrace();
		}
		return postMusics;
	}

	@Override
	public boolean saveMusic(PostMusic postMusic) {
		String sql = "insert into post_music (post_id, account_id, music_file_id, date_uploaded) values ( ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
				postMusic.getPostId(), postMusic.getAccountId(), postMusic.getMusicFileId(), postMusic.getDate()
		});
		return true;
	}

	@Override
	public boolean deletePostMusic(long id) {
		String sql = "delete from post_music where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
		return true;
	}

	public boolean flagPost(PostFlag postFlag) {
		String sql = "insert into post_flags (post_id, account_id, date_flagged, shared) values ( ?, ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
				postFlag.getPostId(), postFlag.getAccountId(), postFlag.getDateFlagged(), postFlag.isShared()
		});
		return true;
	}


	public List<Post> getFlaggedPosts(){
		String sql = "select * from posts where flagged = true and hidden = false";
		List<Post> posts = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Post>(Post.class));
		return posts;
	}

	public Post getFlaggedPost(long id){
		String sql = "select * from posts where id = ? and flagged = true and hidden = false";
		Post post = jdbcTemplate.queryForObject(sql, new Object[]{ id }, new BeanPropertyRowMapper<Post>(Post.class));
		return post;
	}


	public boolean updateFlagged(Post post) {
		String sql = "update posts set flagged = ? where id = ?";
		jdbcTemplate.update(sql, new Object[] {
				post.isFlagged(), post.getId()
		});
		return true;
	}

	public boolean removePostFlags(long postId){
		String sql = "delete from post_flags where post_id = ?";
		jdbcTemplate.update(sql, new Object[] { postId });
		return true;
	}

	public boolean deletePostFlag(long postId, long accountId){
		String sql = "delete from post_flags where post_id = ? and account_id = ?";
		jdbcTemplate.update(sql, new Object[] { postId, accountId });
		return true;
	}

	public boolean makeInvisible(HiddenPost hiddenPost){
		String sql = "insert into hidden_posts (post_id, account_id, date_hidden) values ( ?, ?, ? )";
		jdbcTemplate.update(sql, new Object[] {
			hiddenPost.getPostId(), hiddenPost.getAccountId(), hiddenPost.getDateHidden()
		});
		return true;
	}

	public List<HiddenPost> getHiddenPosts(long postId, long accountId) {
		String sql = "select * from hidden_posts where post_id = ? and account_id = ?";
		List<HiddenPost> hiddenPosts = jdbcTemplate.query(sql, new Object[]{ postId, accountId }, new BeanPropertyRowMapper<HiddenPost>(HiddenPost.class));
		return hiddenPosts;
	}
}