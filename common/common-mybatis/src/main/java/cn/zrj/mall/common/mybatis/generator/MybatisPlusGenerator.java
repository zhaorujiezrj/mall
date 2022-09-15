package cn.zrj.mall.common.mybatis.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;;
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
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/mall_test?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
                        "root", "root")
                // 全局配置
                .globalConfig((scanner, builder) ->
                                builder.author(scanner.apply("请输入作者名称？"))
                                //.enableSwagger() //开始swagger注解
                                .outputDir(scanner.apply("请输入文件保存目录？"))
                        )
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名？")))
                // 策略配置
                .strategyConfig((scanner, builder) ->
                        builder
                                .addInclude(
                                    getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all"))
                                )
                            .controllerBuilder()
                        .enableRestStyle()
                        .enableHyphenStyle()
                        .entityBuilder()
                        .enableLombok()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time", FieldFill.INSERT_UPDATE)
                        )
                                .serviceBuilder().convertServiceFileName(entityName -> entityName + "Service")
                        .build()
                )
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }


    /**
     * 处理 all 情况
     * @param tables
     * @return
     */
    public static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }


}
