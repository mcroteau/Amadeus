package xyz.ioc.jobs;

import org.apache.log4j.Logger;
import org.quartz.*;
import xyz.ioc.common.Constants;
import xyz.ioc.dao.FlyerDao;
import xyz.ioc.dao.jdbc.FlyerJdbcDao;
import xyz.ioc.model.Flyer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AdJob implements Job {

    private static final Logger log = Logger.getLogger(AdJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobKey jobKey = new JobKey(Constants.AD_JOB, Constants.AMADEUS_GROUP);
            JobDetail jobDetail = context.getScheduler().getJobDetail(jobKey);

            FlyerDao flyerDao = (FlyerJdbcDao) jobDetail.getJobDataMap().get(Constants.FLYER_DAO_KEY);

            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            String dateStr = df.format(cal.getTime());
            long now = Long.parseLong(dateStr);
            
            List<Flyer> activeFlyers = flyerDao.getActiveFlyers();

            for(Flyer flyer : activeFlyers){
                long difference = now - flyer.getStartDate();
                if(difference > Constants.AD_DURATION_DIFFERENCE){
                    flyerDao.crumpleUp(flyer.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
