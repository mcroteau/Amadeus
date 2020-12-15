package social.amadeus.repository;

import social.amadeus.model.Resource;
import social.amadeus.model.ActionLike;
import social.amadeus.model.ActionShare;

public interface ActionRepo {

    public Resource get(String uri);

    public boolean save(Resource resource);

    public boolean like(ActionLike actionLike);

    public boolean liked(ActionLike actionLike);

    public boolean share(ActionShare actionShare);

    public long likesCount(long resourceId);

    public long sharesCount(long resourceId);

}
