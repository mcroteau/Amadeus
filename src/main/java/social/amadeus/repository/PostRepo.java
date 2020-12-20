package social.amadeus.repository;

import java.util.List;

import social.amadeus.model.*;

public interface PostRepo {

	public long id();

	public long getCount();
	
	public Post get(long id);

	public List<Post> getPosts(Account authdAccount);

	public List<Post> getActivity(long start, long end, Account authdAccount);

	public long getNewestCount(long start, long end, long accountId);

	public List<Post> getUserPosts(long accountId);
	
	public Post save(Post post);

	public boolean update(Post post);

	public boolean publish(long id);

	public List<Post> getUnpublished();

	public boolean hide(long id);

	public boolean delete(long id);

	public long likes(long id);

	public boolean like(PostLike postLike);

	public boolean liked(PostLike postLike);

	public PostLike getPostLike(long postId, long accountId);

	public boolean unlike(PostLike postLike);

	public boolean deletePostLikes(long postId);

	public long shares(long id);

	public PostShare sharePost(PostShare postShare);

	public List<PostShare> getPostShares(long start, long end, long accountId);

	public List<PostShare> getPostShares(long postId);

	public List<PostShare> getUserPostShares(long accountId);

	public PostShare getPostShare(long postShareId);

	public boolean hasPostShares(long postId);

	public boolean deletePostShare(long id);

	public boolean removePosts(long id);

	public boolean removePostShares(long id);

	public List<PostComment> getPostComments(long id);

	public List<PostShareComment> getPostShareComments(long id);

	public PostComment savePostComment(PostComment postComment);

	public PostShareComment savePostShareComment(PostShareComment postShareCommentComment);

	public boolean deletePostComment(long id);

	public boolean deletePostComments(long postId);

	public boolean deletePostShareComment(long id);

	public boolean deletePostShareComments(long postShareId);//TODO:

	public PostImage getImage(long id, String uri);

	public List<PostImage> getImages(long id);

	public boolean saveImage(PostImage postImage);

	public boolean deletePostImage(long id);

	public boolean deletePostImage(long id, String imageUri);

	public List<PostMusic> getMusic(long id);

	public boolean saveMusic(PostMusic postMusic);

	public boolean deletePostMusic(long id);

	public boolean flagPost(PostFlag postFlag);

	public List<Post> getFlaggedPosts();

	public Post getFlaggedPost(long id);

	public boolean updateFlagged(Post post);

	public boolean removePostFlags(long postId);

	public boolean makeInvisible(HiddenPost hiddenPost);

	public List<HiddenPost> getHiddenPosts(long postId, long accountId);

}