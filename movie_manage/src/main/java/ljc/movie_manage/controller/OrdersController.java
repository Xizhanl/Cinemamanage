package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.Orders;
import ljc.movie_manage.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 电影票订单表 前端控制器
 * </p>
 *
 * @author ljc
 * @since 2025-07-01
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    @GetMapping("/list")
    public List<Orders> list(){return ordersService.list();}
    //修改
    @PostMapping("/mod")
    public Result mod(@RequestBody Orders orders){
        return ordersService.updateById(orders)?Result.suc():Result.fail();
    }
    @PostMapping("/confirm")
    public Result confirmOrder(@RequestBody HashMap<String, Object> params) {
        try {
            Integer orderId = (Integer) params.get("id");
            Orders order = ordersService.getById(orderId);
            if (order == null) {
                return Result.fail("订单不存在");
            }
            if (!"待支付".equals(order.getOrderstatus())) {
                return Result.fail("当前订单状态不可完成");
            }
            // 更新订单状态
            order.setOrderstatus("待取票");

            boolean updated = ordersService.updateById(order);
            return updated ? Result.suc() : Result.fail("完成订单失败");
        } catch (Exception e) {
            return Result.fail("系统错误: " + e.getMessage());
        }
    }
    @PostMapping("/confirmz")
    public Result confirmzOrder(@RequestBody HashMap<String, Object> params) {
        try {
            Integer orderId = (Integer) params.get("id");
            Orders order = ordersService.getById(orderId);

            // 更新订单状态
            order.setOrderstatus("已完成");
            boolean updated = ordersService.updateById(order);
            return updated ? Result.suc() : Result.fail("完成订单失败");
        } catch (Exception e) {
            return Result.fail("系统错误: " + e.getMessage());
        }
    }
    @PostMapping("/cancel")
    public Result cancelOrder(@RequestBody HashMap<String, Object> params) {
        try {
            Integer orderId = (Integer) params.get("id");
            Orders order = ordersService.getById(orderId);
            // 更新订单状态
            order.setOrderstatus("已取消");
            boolean updated = ordersService.updateById(order);
            return updated ? Result.suc() : Result.fail("取消订单失败");
        } catch (Exception e) {
            return Result.fail("系统错误: " + e.getMessage());
        }
    }
    //提交订单逻辑
    @PostMapping("/create")
    public Result createOrder(@RequestBody Orders order) {
        try {
            // 保存订单
            boolean saved = ordersService.save(order);
            if (saved) {
                return Result.suc(order); // 返回创建的订单信息
            } else {
                return Result.fail("订单创建失败");
            }
        } catch (Exception e) {
            return Result.fail("系统错误: " + e.getMessage());
        }
    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return ordersService.removeById(id)?Result.suc():Result.fail();
    }
    //分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String username =(String)param.get("username");
        String moviename =(String)param.get("moviename");
        String hallname =(String)param.get("hallname");
        String orderstatus =(String)param.get("orderstatus");
        //分页mybatis
        Page<Orders> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Orders::getUsername,username);
        queryWrapper.like(Orders::getMoviename,moviename);
        queryWrapper.like(Orders::getHallname,hallname);
        queryWrapper.like(Orders::getOrderstatus,orderstatus);

        IPage<Orders> result = ordersService.page(page,queryWrapper);

        return Result.suc(result.getRecords(),result.getTotal());
    }
}
