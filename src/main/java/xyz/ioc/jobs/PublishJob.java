package xyz.ioc.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.ioc.common.Utilities;
import xyz.ioc.dao.jdbc.PostJdbcDao;
import xyz.ioc.model.Post;

import java.util.List;

public class PublishJob implements Job {

    private static final Logger log = Logger.getLogger(PublishJob.class);

    @Autowired
    private Utilities utilities;

    @Autowired
    private PostJdbcDao postJdbcDao;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long date = utilities.getCurrentDate();
        List<Post> unpublished = postJdbcDao.getUnpublished(date);
        for(Post post : unpublished){
            log.info("publish post : " + post.getId());
            postJdbcDao.publish(post.getId());
        }
    }
}
