package ljc.movie_manage.common;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;


public class CodeGenerator {

    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/film_manage?serverTimezone=GMT%2B8",
                        "root", "134066")
                //全局配置
                .globalConfig(builder -> {
                    builder.outputDir(Paths.get(System.getProperty("user.dir")+"/movie_manage") + "/src/main/java") // 设置输出目录
                            .author("ljc") // 设置作者名
                            .dateType(DateType.ONLY_DATE) // 设置时间类型策略
                            .commentDate("yyyy-MM-dd"); // 设置注释日期格式
                })
                //包配置
                .packageConfig(builder -> {
                    builder.parent("ljc.movie_manage") // 设置父包名
                            .entity("entity") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mapper")// 设置 Mapper XML 文件包名
                            .controller("controller");// 设置 Controller 包名
                })
                //策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("order") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }

}
