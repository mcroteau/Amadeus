package social.amadeus.repository;

import social.amadeus.model.Resource;
import social.amadeus.model.ActionLike;
import social.amadeus.model.ActionShare;

public interface ActionRepo {

    public Resource getWebsite(String uri);

    public boolean saveWebsite(Resource resource);

    public boolean likeWebsite(ActionLike actionLike);

    public boolean isLiked(ActionLike actionLike);

    public boolean shareWebsite(ActionShare actionShare);

    public long getLikesCount(long resourceId);

    public long getSharesCount(long resourceId);

}
