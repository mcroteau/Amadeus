package social.amadeus.repository;

import social.amadeus.model.Resource;
import social.amadeus.model.ResourceLike;
import social.amadeus.model.ResourceShare;

public interface ResourceRepo {

    public Resource get(String uri);

    public boolean save(Resource resource);

    public boolean like(ResourceLike resourceLike);

    public boolean liked(ResourceLike resourceLike);

    public boolean share(ResourceShare resourceShare);

    public long likesCount(long resourceId);

    public long sharesCount(long resourceId);

}
