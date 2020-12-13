package social.amadeus.jobs;

import org.apache.log4j.Logger;
import org.quartz.*;
import social.amadeus.common.Constants;
import social.amadeus.repository.PostRepo;
import social.amadeus.repository.jdbc.PostJdbcRepo;
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

            log.info("************ executing publish job ************");

            JobKey jobKey = new JobKey(Constants.PUBLISHING_JOB, Constants.AMADEUS_GROUP);
            JobDetail jobDetail = context.getScheduler().getJobDetail(jobKey);

            PostRepo postRepo = (PostJdbcRepo) jobDetail.getJobDataMap().get(Constants.POSTS_DAO_KEY);

            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            String dateStr = df.format(cal.getTime());
            long now = Long.parseLong(dateStr);

            List<Post> unpublished = postRepo.getUnpublished();
            for(Post post : unpublished){
                long difference = now - post.getUpdateDate();
                if(difference > Constants.PUBLISH_DURATION_DIFFERENCE){
                    postRepo.publish(post.getId());
                }
            }

            log.info("************ publish job complete ************");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
