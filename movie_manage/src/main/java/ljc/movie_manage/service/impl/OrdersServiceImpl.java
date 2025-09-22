package ljc.movie_manage.service.impl;

import ljc.movie_manage.entity.Orders;
import ljc.movie_manage.mapper.OrdersMapper;
import ljc.movie_manage.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电影票订单表 服务实现类
 * </p>
 *
 * @author ljc
 * @since 2025-07-01
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
