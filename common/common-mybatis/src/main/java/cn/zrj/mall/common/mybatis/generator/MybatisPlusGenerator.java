package cn.zrj.mall.common.mybatis.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhaorujie
 * @version 1.0
 * @date 2022/7/1 14:55
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {

        String projectPath = System.getProperty("user.dir");
        AtomicReference<String> modulePath = new AtomicReference<>("");
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/my-im?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
                        "root", "root")
                // 全局配置
                .globalConfig((scanner, builder) -> {
                            builder
                                    .author(scanner.apply("请输入作者名称？"));
                            //.enableSwagger() //开始swagger注解
                            modulePath.set(scanner.apply("请输入模块名？"));
                            builder.outputDir(projectPath + "/" + modulePath.get() + "/src/main/java");
                })
                // 包配置
                .packageConfig((scanner, builder) -> builder
                        .parent(scanner.apply("请输入包名？"))
                        .mapper("mapper")
                        .xml("mapper")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/" + modulePath.get() + "\\src\\main\\resources\\mapper"))
                )
                // 策略配置
                .strategyConfig((scanner, builder) ->
                        builder
                                .addInclude(
                                    getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all"))
                                )

                                //Controller策略配置
                                .controllerBuilder()
                                .enableRestStyle()
                                .enableHyphenStyle()

                                //实体类策略配置
                                .entityBuilder()
                                //数据库表映射到实体的命名策略：下划线转驼峰命
                                .naming(NamingStrategy.underline_to_camel)
                                //数据库表字段映射到实体的命名策略：下划线转驼峰命
                                .columnNaming(NamingStrategy.underline_to_camel)
                                .enableLombok()
                                .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("update_time", FieldFill.INSERT_UPDATE))

                                //service 策略配置
                                .serviceBuilder()
                                .convertServiceFileName(entityName -> entityName + "Service")

                                //Mapper策略配置
                                .mapperBuilder()
                                //开启 @Mapper 注解
                                .enableMapperAnnotation()
                                //生成基本的resultMap
                                .enableBaseResultMap()
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
