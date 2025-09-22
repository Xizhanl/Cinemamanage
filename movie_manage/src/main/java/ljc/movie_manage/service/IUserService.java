package ljc.movie_manage.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ljc.movie_manage.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author ljc
 * @since 2025-05-29
 */
public interface IUserService extends IService<User> {

    IPage<User> pageC(IPage<User> page);
    IPage<User> pageCC(IPage<User> page, Wrapper<User> Wrapper);
}
