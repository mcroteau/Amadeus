package social.amadeus.jobs;

import org.apache.log4j.Logger;
import org.quartz.*;
import social.amadeus.common.Constants;
import social.amadeus.dao.PostDao;
import social.amadeus.dao.jdbc.PostJdbcDao;
import social.amadeus.model.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PublishJob implements Job {

    private static final Logger log = Logger.getLogger(PublishJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobKey jobKey = new JobKey(Constants.PUBLISHING_JOB, Constants.AMADEUS_GROUP);
            JobDetail jobDetail = context.getScheduler().getJobDetail(jobKey);

            PostDao postDao = (PostJdbcDao) jobDetail.getJobDataMap().get(Constants.POSTS_DAO_KEY);

            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            String dateStr = df.format(cal.getTime());
            long now = Long.parseLong(dateStr);

            List<Post> unpublished = postDao.getUnpublished();
            for(Post post : unpublished){
                long difference = now - post.getUpdateDate();
                if(difference > Constants.PUBLISH_DURATION_DIFFERENCE){
                    postDao.publish(post.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
