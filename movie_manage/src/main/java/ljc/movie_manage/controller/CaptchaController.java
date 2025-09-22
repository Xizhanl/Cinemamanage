package ljc.movie_manage.controller;



import com.wf.captcha.SpecCaptcha;
import jakarta.servlet.http.HttpServletResponse;
import ljc.movie_manage.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//验证码控制类
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @GetMapping("/image")
    public void getCode(HttpServletResponse response) throws IOException {
        // 生成验证码（英文+数字）
        SpecCaptcha captcha = new SpecCaptcha(120, 40);
        String code = captcha.text(); // 获取验证码文本
        System.out.println(code);
        // 生成唯一 key（用于前端提交时验证）
        String captchaKey = UUID.randomUUID().toString();
        System.out.println(captchaKey);
        // 存入 Redis，有效期 1 分钟
        redisTemplate.opsForValue().set(
                "captcha:" + captchaKey,
                code,
                30,
                TimeUnit.SECONDS
        );
        // 将 key 返回给前端（通过 Header）
        response.setHeader("captcha-key", captchaKey);
        // 返回图片流
        response.setContentType("image/png");
        captcha.out(response.getOutputStream());
    }

    // 验证验证码
    @PostMapping("/verify")
    public Result verifyCaptcha(@RequestBody Map<String, String> params) {
        String captchaKey = params.get("captchaKey");
        String captchaText = params.get("captchaText");

        String redisKey = "captcha:" + captchaKey;
        String storedCaptcha = redisTemplate.opsForValue().get(redisKey);

        if (storedCaptcha == null) {
            return Result.fail("验证码已过期");
        }
        if (!storedCaptcha.equalsIgnoreCase(captchaText)) {
            return Result.fail("验证码错误");
        }

        redisTemplate.delete(redisKey);
        return Result.suc();
    }
}