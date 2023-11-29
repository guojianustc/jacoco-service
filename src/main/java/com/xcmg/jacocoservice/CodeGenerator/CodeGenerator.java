package com.xcmg.jacocoservice.CodeGenerator;
/**
 * Created on 2022/8/10.
 *
 * @author GuoJian
 * -version 1.0.0
 */

/**
 * @ClassName：CodeGenerator
 * @Author: GuoJian
 * @Date: 2022/8/10 11:24
 * @Description: mybatis plus代码生成器
 */

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {


    public static void main(String[] args) {
        //全局配置
        GlobalConfig config = new GlobalConfig.Builder()
                //作者
                .author("guojian")
                // 生成路径，最好使用绝对路径，window路径是不一样的
                .outputDir("/home/guojian/IdeaProjects/JacocoService/src/main/java")
                // 文件覆盖
                .fileOverride()
                //设置时间对应类型
                .dateType(DateType.ONLY_DATE)
                .build();

        //包名策略配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent("com.xcmg.jacocoservice")
                .mapper("mapper")
                .service("service")
                .controller("controller")
                .entity("model")
                .xml("mapper")
                .build();

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                //设置需要映射的表名
                .addInclude("xcmg_coverage_overall_data")
                //策略开启⼤写命名
                .enableCapitalMode()
                .entityBuilder()
                //添加后缀
                .formatFileName("%sDO")
                //添加lombock的getter、setter注解
                .enableLombok()
                // 数据库表映射到实体的命名策略
                .columnNaming(NamingStrategy.underline_to_camel)
                .naming(NamingStrategy.underline_to_camel)
                .mapperBuilder()//mapper类添加@Mapper
                //生成基本的SQL片段
                .enableBaseColumnList()
                //生成基本的resultMap
                .enableBaseResultMap()
                .serviceBuilder()
                //添加后缀
                .formatServiceFileName("%sService")
                //使用restcontroller注解
                .controllerBuilder().enableRestStyle()
                .build();

        // 数据源配置
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig
                .Builder(
                "jdbc:mysql://localhost:3306/jacoco_test?characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true",
                "root",
                "123456");

        // 创建代码生成器对象，加载配置
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfigBuilder.build());
        autoGenerator.global(config);
        autoGenerator.packageInfo(packageConfig);
        autoGenerator.strategy(strategyConfig);

        //执行操作
        autoGenerator.execute();
        System.out.println("=======  Done 相关代码生成完毕  ========");
    }
}