package social.amadeus.jobs;

import org.apache.log4j.Logger;
import org.quartz.*;
import social.amadeus.repository.FlyerRepo;
import social.amadeus.repository.jdbc.FlyerJdbcRepo;
import social.amadeus.model.Flyer;
import social.amadeus.common.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AdJob implements Job {

    private static final Logger log = Logger.getLogger(AdJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info("************ executing ad job ************");
            JobKey jobKey = new JobKey(Constants.AD_JOB, Constants.AMADEUS_GROUP);
            JobDetail jobDetail = context.getScheduler().getJobDetail(jobKey);

            FlyerRepo flyerRepo = (FlyerJdbcRepo) jobDetail.getJobDataMap().get(Constants.FLYER_DAO_KEY);

            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            String dateStr = df.format(cal.getTime());
            long now = Long.parseLong(dateStr);
            
            List<Flyer> activeFlyers = flyerRepo.getActiveFlyers();

            for(Flyer flyer : activeFlyers){
                long difference = now - flyer.getStartDate();
                if(difference > Constants.AD_DURATION_DIFFERENCE){
                    flyerRepo.crumpleUp(flyer.getId());
                }
            }


            log.info("************ ad job complete ************");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
