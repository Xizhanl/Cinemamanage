package ljc.movie_manage.mapper;

import ljc.movie_manage.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 电影票订单表 Mapper 接口
 * </p>
 *
 * @author ljc
 * @since 2025-07-01
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
