package ljc.movie_manage.service.impl;

import ljc.movie_manage.entity.Sessions;
import ljc.movie_manage.mapper.SessionsMapper;
import ljc.movie_manage.service.ISessionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 场次信息表 服务实现类
 * </p>
 *
 * @author ljc
 * @since 2025-06-28
 */
@Service
public class SessionsServiceImpl extends ServiceImpl<SessionsMapper, Sessions> implements ISessionsService {

}
