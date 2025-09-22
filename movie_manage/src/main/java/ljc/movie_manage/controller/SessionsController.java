package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.common.SeatWebSocketHandler;
import ljc.movie_manage.entity.Hall;
import ljc.movie_manage.entity.Sessions;
import ljc.movie_manage.service.IHallService;
import ljc.movie_manage.service.ISessionsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/sessions")
public class SessionsController {
    @Autowired
    private ISessionsService sessionsService;
    @Autowired
    private IHallService hallService;
    @Autowired
    private SeatWebSocketHandler seatWebSocketHandler;
    @GetMapping("/list")
    public List<Sessions> list(){return sessionsService.list();}
    @PostMapping("/mod")
    public Result mod(@RequestBody Sessions sessions){
        return sessionsService.updateById(sessions)?Result.suc():Result.fail();
    }
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return sessionsService.removeById(id)?Result.suc():Result.fail();
    }
    @PostMapping("/save")
    public Result save(@RequestBody Sessions sessions) {
        return sessionsService.save(sessions)?Result.suc():Result.fail();
    }
    //新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Sessions sessions){
        return sessionsService.saveOrUpdate(sessions)?Result.suc():Result.fail();
    }
    @GetMapping("/autoRenewSessions")
    public Result autoRenewSessions() {
        // 1. 定义日期格式（仅用于字符串转换）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 2. 处理昨天的场次（删除）
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.format(formatter); // 用于数据库查询
        LambdaQueryWrapper<Sessions> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Sessions::getScreeningtime, yesterdayStr);

        // 3. 处理后天的场次（新增）
        LocalDate dayAfterTomorrow = yesterday.plusDays(2); // 从昨天+2天=后天

        // 获取昨天的场次作为模板
        LambdaQueryWrapper<Sessions> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Sessions::getScreeningtime, yesterdayStr);
        List<Sessions> yesterdaySessions = sessionsService.list(queryWrapper);

        // 复制场次并修改日期为后天
        List<Sessions> newSessions = new ArrayList<>();
        for (Sessions session : yesterdaySessions) {
            // 通过影厅名称获取影厅信息
            LambdaQueryWrapper<Hall> hallQuery = new LambdaQueryWrapper<>();
            hallQuery.eq(Hall::getHallname, session.getHallname()) // 使用hallname字段匹配
                    .last("LIMIT 1"); // 确保只获取一条记录

            Hall hall = hallService.getOne(hallQuery);

            Sessions newSession = new Sessions();
            BeanUtils.copyProperties(session, newSession, "id","seatssold", "seatstatus"); // 排除id和seatStatus的复制
            newSession.setScreeningtime(dayAfterTomorrow);
            newSession.setSeatstatus(hall.getSeatstatus()); // 使用影厅的最新座位状态
            newSession.setTotalseats(hall.getTotalseats());
            newSession.setSeatssold(0);

            newSessions.add(newSession);
        }

        // 批量保存后天的场次
        sessionsService.saveBatch(newSessions);
        sessionsService.remove(deleteWrapper);
        return Result.suc("已删除昨天场次并新增后天场次");
    }

    @PostMapping("/updateseatstatus")
    public Result updateSeatStatus(@RequestBody Map<String, Object> request) {
        try {
            Integer sessionId = (Integer) request.get("id");
            Integer seatsold = (Integer) request.get("seatsold");
            // 处理seatstatus - 接受对象或字符串
            String seatStatusStr;
            Object seatStatusObj = request.get("seatstatus");
            if (seatStatusObj instanceof String) {
                seatStatusStr = (String) seatStatusObj;
            } else {
                // 如果是对象，转换为JSON字符串
                try {
                    seatStatusStr = new ObjectMapper().writeValueAsString(seatStatusObj);
                } catch (JsonProcessingException e) {
                    return Result.fail("seatstatus格式不正确");
                }
            }
            // 根据sessionId查找场次
            Sessions session = sessionsService.getById(sessionId);
            if (session == null) {
                return Result.fail("场次不存在");
            }
            // 更新座位状态
            session.setSeatstatus(seatStatusStr);
            session.setSeatssold(session.getSeatssold()+seatsold);
            sessionsService.updateById(session);
            return Result.suc("座位状态更新成功");
        } catch (Exception e) {
            return Result.fail("座位状态更新失败: " + e.getMessage());
        }
    }
    @PostMapping("/cancelseatstatus")
    public Result cancelseatstatus(@RequestBody Map<String, Object> request) {
        try {
            Integer sessionId = (Integer) request.get("id");
            Integer seatsold = (Integer) request.get("seatsold");
            // 处理seatstatus - 接受对象或字符串
            String seatStatusStr;
            Object seatStatusObj = request.get("seatstatus");
            if (seatStatusObj instanceof String) {
                seatStatusStr = (String) seatStatusObj;
            } else {
                // 如果是对象，转换为JSON字符串
                try {
                    seatStatusStr = new ObjectMapper().writeValueAsString(seatStatusObj);
                } catch (JsonProcessingException e) {
                    return Result.fail("seatstatus格式不正确");
                }
            }
            // 根据sessionId查找场次
            Sessions session = sessionsService.getById(sessionId);
            if (session == null) {
                return Result.fail("场次不存在");
            }
            // 更新座位状态
            session.setSeatstatus(seatStatusStr);
            session.setSeatssold(session.getSeatssold()-seatsold);
            boolean success = sessionsService.updateById(session);
            return success ? Result.suc("座位状态更新成功") : Result.fail("座位状态更新失败");
        } catch (Exception e) {
            return Result.fail("座位状态更新失败: " + e.getMessage());
        }
    }
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String hallname =(String)param.get("hallname");
        String moviename = (String)param.get("moviename");

        Page<Sessions> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Sessions> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(hallname)){
            queryWrapper.like(Sessions::getHallname,hallname);
        }
        if (StringUtils.isNotBlank(moviename)){
            queryWrapper.like(Sessions::getMoviename,moviename);
        }
        // 添加排序规则：按hallname中的数字排序
        queryWrapper.last("ORDER BY CAST(SUBSTRING_INDEX(hallname, '号', 1) AS UNSIGNED), hallname, id");

        IPage<Sessions> result = sessionsService.page(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }
}
