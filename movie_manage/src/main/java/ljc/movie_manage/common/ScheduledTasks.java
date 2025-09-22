package ljc.movie_manage.common;

import ljc.movie_manage.controller.SessionsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
//定时任务
public class ScheduledTasks {
    
    @Autowired
    private SessionsController sessionsController;
    
    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoRenewSessionsDaily() {
        sessionsController.autoRenewSessions();
    }
}