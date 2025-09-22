package ljc.movie_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import jakarta.annotation.Resource;
import ljc.movie_manage.entity.User;
import ljc.movie_manage.mapper.UserMapper;
import ljc.movie_manage.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author ljc
 * @since 2025-05-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Override
    public IPage<User> pageC(IPage<User> page) {
        return userMapper.pageC(page);
    }
    @Override
    public IPage<User> pageCC(IPage<User> page, Wrapper<User> wrapper) {
        return userMapper.pageCC(page,wrapper);
    }

}
