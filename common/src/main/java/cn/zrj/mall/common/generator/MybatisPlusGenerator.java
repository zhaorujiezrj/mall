package cn.zrj.mall.common.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhaorujie
 * @version 1.0
 * @date 2022/7/1 14:55
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        generate();
        //generate2();
    }

    public static void generate() {

        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/auth?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
                        "root", "1234")
                // 全局配置
                .globalConfig((scanner, builder) ->
                                builder.author(scanner.apply("请输入作者名称？"))
                                //.enableSwagger() //开始swagger注解
                                .outputDir(scanner.apply("请输入文件保存目录？"))
                        )
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名？")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(
                                new Column("gmt_create", FieldFill.INSERT),
                                new Column("gmt_modified", FieldFill.INSERT_UPDATE)
                        ).build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }



    // 处理 all 情况
    public static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    public static void generate2() {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/auth?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
                        "root", "1234")
                .globalConfig(builder -> {
                    builder.author("baomidou") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.baomidou.mybatisplus.samples.generator") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D://")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("ums_member") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }

}
