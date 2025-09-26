package com.ywhc.admin.modules.generator.service.impl;


import com.ywhc.admin.modules.generator.dto.GeneratorConfigDTO;
import com.ywhc.admin.modules.generator.entity.ColumnInfo;
import com.ywhc.admin.modules.generator.entity.TableInfo;
import com.ywhc.admin.modules.generator.service.GeneratorService;
import com.ywhc.admin.modules.generator.util.GeneratorUtils;
import com.ywhc.admin.modules.generator.vo.GeneratedCodeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Override
    public List<TableInfo> getTableList() {
        String sql = """
            SELECT 
                table_name,
                table_comment,
                engine,
                create_time
            FROM information_schema.tables 
            WHERE table_schema = (SELECT DATABASE())
            AND table_type = 'BASE TABLE'
            ORDER BY table_name
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(rs.getString("table_name"));
            tableInfo.setTableComment(rs.getString("table_comment"));
            tableInfo.setEngine(rs.getString("engine"));
            tableInfo.setCreateTime(rs.getString("create_time"));
            return tableInfo;
        });
    }

    @Override
    public TableInfo getTableInfo(String tableName) {
        // 获取表基本信息
        String tableSql = """
            SELECT 
                table_name,
                table_comment,
                engine,
                create_time
            FROM information_schema.tables 
            WHERE table_schema = (SELECT DATABASE())
            AND table_name = ?
            """;

        TableInfo tableInfo = jdbcTemplate.queryForObject(tableSql, (rs, rowNum) -> {
            TableInfo info = new TableInfo();
            info.setTableName(rs.getString("table_name"));
            info.setTableComment(rs.getString("table_comment"));
            info.setEngine(rs.getString("engine"));
            info.setCreateTime(rs.getString("create_time"));
            return info;
        }, tableName);

        // 获取字段信息
        String columnSql = """
            SELECT 
                column_name,
                data_type,
                column_comment,
                is_nullable,
                column_default,
                character_maximum_length,
                column_key,
                extra
            FROM information_schema.columns 
            WHERE table_schema = (SELECT DATABASE())
            AND table_name = ?
            ORDER BY ordinal_position
            """;

        List<ColumnInfo> columns = jdbcTemplate.query(columnSql, (rs, rowNum) -> {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnName(rs.getString("column_name"));
            columnInfo.setDataType(rs.getString("data_type"));
            columnInfo.setColumnComment(rs.getString("column_comment"));
            columnInfo.setIsNullable("YES".equals(rs.getString("is_nullable")));
            columnInfo.setColumnDefault(rs.getString("column_default"));
            columnInfo.setColumnLength(rs.getLong("character_maximum_length"));
            columnInfo.setIsPrimaryKey("PRI".equals(rs.getString("column_key")));
            columnInfo.setIsAutoIncrement("auto_increment".equals(rs.getString("extra")));

            // 设置Java类型和字段名
            columnInfo.setJavaType(GeneratorUtils.getJavaType(rs.getString("data_type")));
            columnInfo.setJavaField(GeneratorUtils.toCamelCase(rs.getString("column_name")));

            return columnInfo;
        }, tableName);

        if (tableInfo != null) {
            tableInfo.setColumns(columns);
        }
        return tableInfo;
    }

    @Override
    public GeneratedCodeVO previewCode(GeneratorConfigDTO config) {
        try {
            // 验证配置
            if (config == null || config.getTableName() == null || config.getTableName().trim().isEmpty()) {
                throw new RuntimeException("表名不能为空");
            }

            // 创建临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/generator/" + System.currentTimeMillis();
            log.info("创建临时目录: {}", tempDir);

            // 确保临时目录存在
            File tempDirFile = new File(tempDir);
            if (!tempDirFile.exists()) {
                boolean created = tempDirFile.mkdirs();
                if (!created) {
                    throw new RuntimeException("无法创建临时目录: " + tempDir);
                }
            }

            // 生成代码到临时目录
            generateCodeToDirectory(config, tempDir);

            // 读取生成的文件内容
            Map<String, String> files = readGeneratedFiles(tempDir);

            GeneratedCodeVO result = new GeneratedCodeVO();
            result.setFiles(files);
            result.setMenuSql(generateMenuSql(config));
            result.setPermissionSql(generatePermissionSql(config));
            result.setGenerateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 清理临时目录
            deleteDirectory(tempDirFile);

            return result;
        } catch (Exception e) {
            log.error("预览代码失败", e);
            throw new RuntimeException("预览代码失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] generateCode(GeneratorConfigDTO config) {
        try {
            // 创建临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/generator/" + System.currentTimeMillis();

            // 生成代码到临时目录
            generateCodeToDirectory(config, tempDir);

            // 打包成ZIP
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                zipDirectory(new File(tempDir), "", zos);

                // 添加SQL文件
                addSqlToZip(zos, config);
            }

            // 清理临时目录
            deleteDirectory(new File(tempDir));

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("生成代码失败", e);
            throw new RuntimeException("生成代码失败: " + e.getMessage());
        }
    }

    private void generateCodeToDirectory(GeneratorConfigDTO config, String outputDir) {
        try {
            // 获取表信息用于模板渲染
            TableInfo tableInfo = getTableInfo(config.getTableName());

            // 准备模板数据
            Map<String, Object> templateData = prepareTemplateData(config, tableInfo);

            // 创建后端目录结构
            String basePackage = config.getPackageName().replace(".", "/") + "/modules/" + config.getModuleName();
            String javaDir = outputDir + "/backend/src/main/java/" + basePackage + "/" + config.getBusinessName();
            String resourcesDir = outputDir + "/backend/src/main/resources" + "/mapper/" + config.getModuleName() + "/" +config.getBusinessName();

            // 生成后端代码
            generateBackendCode(config, javaDir, resourcesDir, templateData);

            // 生成DTO和VO
            if (config.getGenerateOptions().getGenerateDto() || config.getGenerateOptions().getGenerateVo()) {
                generateDtoAndVo(config, outputDir + "/backend/src/main/java");
            }

            // 生成前端代码
            if (config.getGenerateOptions().getGenerateVuePage() || config.getGenerateOptions().getGenerateVueApi()) {
                generateFrontendCode(config, outputDir + "/frontend");
            }

        } catch (Exception e) {
            log.error("生成代码到目录失败", e);
            throw new RuntimeException("生成代码到目录失败: " + e.getMessage());
        }
    }

    private void generateBackendCode(GeneratorConfigDTO config, String javaDir, String resourcesDir, Map<String, Object> templateData) {
        try {
            String entityName = GeneratorUtils.toPascalCase(config.getBusinessName());

            // 创建目录
            new File(javaDir + "/entity").mkdirs();
            new File(javaDir + "/controller").mkdirs();
            new File(javaDir + "/service").mkdirs();
            new File(javaDir + "/service/impl").mkdirs();
            new File(javaDir + "/mapper").mkdirs();
            new File(resourcesDir).mkdirs();

            // 生成Entity
            if (config.getGenerateOptions().getGenerateEntity()) {
                generateFileFromTemplate("entity.java.ftl",
                    javaDir + "/entity/" + entityName + ".java",
                    templateData);
            }

            // 生成Controller
            if (config.getGenerateOptions().getGenerateController()) {
                generateFileFromTemplate("controller.java.ftl",
                    javaDir + "/controller/" + entityName + "Controller.java",
                    templateData);
            }

            // 生成Service
            if (config.getGenerateOptions().getGenerateService()) {
                generateFileFromTemplate("service.java.ftl",
                    javaDir + "/service/" + entityName + "Service.java",
                    templateData);
            }

            // 生成ServiceImpl
            if (config.getGenerateOptions().getGenerateServiceImpl()) {
                generateFileFromTemplate("serviceImpl.java.ftl",
                    javaDir + "/service/impl/" + entityName + "ServiceImpl.java",
                    templateData);
            }

            // 生成Mapper
            if (config.getGenerateOptions().getGenerateMapper()) {
                generateFileFromTemplate("mapper.java.ftl",
                    javaDir + "/mapper/" + entityName + "Mapper.java",
                    templateData);
            }

            // 生成MapperXML
            if (config.getGenerateOptions().getGenerateMapperXml()) {
                generateFileFromTemplate("mapper.xml.ftl",
                    resourcesDir + "/" + entityName + "Mapper.xml",
                    templateData);
            }

        } catch (Exception e) {
            log.error("生成后端代码失败", e);
            throw new RuntimeException("生成后端代码失败: " + e.getMessage());
        }
    }

    private void generateDtoAndVo(GeneratorConfigDTO config, String outputDir) {
        try {
            // 获取表信息用于模板渲染
            TableInfo tableInfo = getTableInfo(config.getTableName());

            // 创建DTO和VO目录
            String basePackage = config.getPackageName().replace(".", "/") + "/modules/" + config.getModuleName()+ "/" + config.getBusinessName();
            String dtoDir = outputDir + "/" + basePackage + "/dto";
            String voDir = outputDir + "/" + basePackage + "/vo";

            new File(dtoDir).mkdirs();
            new File(voDir).mkdirs();

            // 准备模板数据
            Map<String, Object> templateData = prepareTemplateData(config, tableInfo);

            // 生成DTO
            if (config.getGenerateOptions().getGenerateDto()) {
                // 生成CreateDTO
                generateFileFromTemplate("createDto.java.ftl",
                    dtoDir + "/" + GeneratorUtils.toPascalCase(config.getBusinessName()) + "CreateDTO.java",
                    templateData);

                // 生成UpdateDTO
                generateFileFromTemplate("updateDto.java.ftl",
                    dtoDir + "/" + GeneratorUtils.toPascalCase(config.getBusinessName()) + "UpdateDTO.java",
                    templateData);

                // 生成QueryDTO
                generateFileFromTemplate("queryDto.java.ftl",
                    dtoDir + "/" + GeneratorUtils.toPascalCase(config.getBusinessName()) + "QueryDTO.java",
                    templateData);
            }

            // 生成VO
            if (config.getGenerateOptions().getGenerateVo()) {
                generateFileFromTemplate("vo.java.ftl",
                    voDir + "/" + GeneratorUtils.toPascalCase(config.getBusinessName()) + "VO.java",
                    templateData);
            }

        } catch (Exception e) {
            log.error("生成DTO和VO失败", e);
            throw new RuntimeException("生成DTO和VO失败: " + e.getMessage());
        }
    }

    private void generateFrontendCode(GeneratorConfigDTO config, String outputDir) {
        try {
            // 获取表信息用于模板渲染
            TableInfo tableInfo = getTableInfo(config.getTableName());

            // 创建前端目录结构
            String pagesDir = outputDir + "/src/pages/" + config.getModuleName() + "/" + config.getBusinessName();
            String apiDir = outputDir + "/src/api/" + config.getModuleName()+ "/" + config.getBusinessName();

            new File(pagesDir).mkdirs();
            new File(apiDir).mkdirs();

            // 准备模板数据
            Map<String, Object> templateData = prepareTemplateData(config, tableInfo);

            // 生成Vue页面
            if (config.getGenerateOptions().getGenerateVuePage()) {
                generateFileFromTemplate("vue-page.vue.ftl",
                    pagesDir + "/" + GeneratorUtils.getVueComponentName(config.getBusinessName()) + ".vue",
                    templateData);

                generateFileFromTemplate("vue-edit-dialog.vue.ftl",
                    pagesDir + "/" + GeneratorUtils.toPascalCase(config.getBusinessName()) + "EditDialog.vue",
                    templateData);
            }

            // 生成API文件
            if (config.getGenerateOptions().getGenerateVueApi()) {
                generateFileFromTemplate("vue-api.js.ftl",
                    apiDir + "/" + GeneratorUtils.getApiFileName(config.getBusinessName()),
                    templateData);
            }

        } catch (Exception e) {
            log.error("生成前端代码失败", e);
            throw new RuntimeException("生成前端代码失败: " + e.getMessage());
        }
    }

    private Map<String, Object> prepareTemplateData(GeneratorConfigDTO config, TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();

        // 基本配置
        data.put("cfg", Map.of(
            "moduleName", config.getModuleName(),
            "businessName", config.getBusinessName(),
            "functionName", config.getFunctionName(),
            "author", config.getAuthor(),
            "packageName", config.getPackageName()
        ));

        // 表信息
        Map<String, Object> table = new HashMap<>();
        table.put("name", tableInfo.getTableName());
        table.put("comment", tableInfo.getTableComment());
        table.put("serviceName", GeneratorUtils.toPascalCase(config.getBusinessName()) + "Service");
        table.put("controllerName", GeneratorUtils.toPascalCase(config.getBusinessName()) + "Controller");
        table.put("mapperName", GeneratorUtils.toPascalCase(config.getBusinessName()) + "Mapper");
        table.put("serviceImplName", GeneratorUtils.toPascalCase(config.getBusinessName()) + "ServiceImpl");

        // 字段信息
        List<Map<String, Object>> fields = new ArrayList<>();
        Set<String> importPackages = new HashSet<>();
        importPackages.add("java.io.Serializable");
        importPackages.add("com.baomidou.mybatisplus.annotation.TableName");
        importPackages.add("com.baomidou.mybatisplus.annotation.TableId");
        importPackages.add("com.baomidou.mybatisplus.annotation.TableField");
        importPackages.add("com.baomidou.mybatisplus.annotation.IdType");

        for (ColumnInfo column : tableInfo.getColumns()) {
            Map<String, Object> field = new HashMap<>();
            field.put("columnName", column.getColumnName());
            field.put("propertyName", column.getJavaField());
            field.put("propertyType", column.getJavaType());
            field.put("comment", column.getColumnComment());
            field.put("keyFlag", column.getIsPrimaryKey());
            field.put("nullable", column.getIsNullable());
            field.put("columnLength", column.getColumnLength());
            field.put("columnType", column.getDataType());
            field.put("annotationColumnName", column.getColumnName());
            field.put("capitalName", GeneratorUtils.toPascalCase(column.getJavaField()));
            field.put("keyIdentityFlag", column.getIsAutoIncrement());
            field.put("convert", true);
            field.put("versionField", false);
            field.put("logicDeleteField", false);
            field.put("fill", null);

            // 添加时间类型的导入
            if ("LocalDateTime".equals(column.getJavaType())) {
                importPackages.add("java.time.LocalDateTime");
            } else if ("LocalDate".equals(column.getJavaType())) {
                importPackages.add("java.time.LocalDate");
            } else if ("LocalTime".equals(column.getJavaType())) {
                importPackages.add("java.time.LocalTime");
            } else if ("BigDecimal".equals(column.getJavaType())) {
                importPackages.add("java.math.BigDecimal");
            }

            fields.add(field);
        }
        table.put("fields", fields);
        table.put("importPackages", new ArrayList<>(importPackages));
        table.put("convert", true);

        data.put("table", table);
        data.put("entity", GeneratorUtils.toPascalCase(config.getBusinessName()));
        data.put("author", config.getAuthor());
        data.put("date", java.time.LocalDate.now().toString());

        // 包信息
        Map<String, String> packageInfo = new HashMap<>();
        String basePackage = config.getPackageName() + ".modules." + config.getModuleName() + "." + config.getBusinessName();
        packageInfo.put("Controller", basePackage + ".controller");
        packageInfo.put("Service", basePackage + ".service");
        packageInfo.put("ServiceImpl", basePackage + ".service.impl");
        packageInfo.put("Mapper", basePackage + ".mapper");
        packageInfo.put("Entity", basePackage + ".entity");
        packageInfo.put("Parent", basePackage);
        data.put("package", packageInfo);

        // 添加其他必要的配置
        data.put("swagger", true);
        data.put("entityLombokModel", true);
        data.put("entitySerialVersionUID", true);
        data.put("superEntityClass", null);
        data.put("activeRecord", false);
        data.put("entityColumnConstant", false);
        data.put("chainModel", false);
        data.put("idType", null);
        data.put("schemaName", "");

        return data;
    }

    private void generateFileFromTemplate(String templateName, String outputPath, Map<String, Object> data) {
        try {
            log.info("生成文件: {} -> {}", templateName, outputPath);

            freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
            cfg.setClassForTemplateLoading(this.getClass(), "/templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);

            freemarker.template.Template template = cfg.getTemplate(templateName);

            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    throw new RuntimeException("无法创建目录: " + parentDir.getAbsolutePath());
                }
            }

            try (java.io.FileWriter writer = new java.io.FileWriter(outputFile)) {
                template.process(data, writer);
                writer.flush();
            }

            log.info("文件生成成功: {}", outputPath);
        } catch (Exception e) {
            log.error("生成文件失败: {} -> {}", templateName, outputPath, e);
            throw new RuntimeException("生成文件失败[" + templateName + "]: " + e.getMessage());
        }
    }

    private Map<String, String> readGeneratedFiles(String directory) throws IOException {
        Map<String, String> files = new HashMap<>();
        Path dirPath = Paths.get(directory);

        if (Files.exists(dirPath)) {
            Files.walk(dirPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String relativePath = dirPath.relativize(path).toString().replace("\\", "/");
                        String content = Files.readString(path);
                        files.put(relativePath, content);
                    } catch (IOException e) {
                        log.error("读取文件失败: " + path, e);
                    }
                });
        }

        return files;
    }

    private String generateMenuSql(GeneratorConfigDTO config) {
        return String.format("""
            -- %s管理菜单SQL
            -- 1. 添加主菜单（根据实际需要调整parent_id，1表示系统管理目录）
            INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `is_external`, `is_cache`, `is_visible`, `status`, `remark`, `deleted`, `create_time`, `update_time`, `create_by`, `update_by`) 
            VALUES (1, '%s管理', 1, '/%s/%s', '%s/%s', '%s:%s:list', 'table', 10, 0, 1, 1, 1, '%s管理菜单', 0, NOW(), NOW(), 1, 1);
            
            -- 获取刚插入的菜单ID
            SET @menu_id = LAST_INSERT_ID();
            
            -- 2. 添加功能按钮
            INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `is_external`, `is_cache`, `is_visible`, `status`, `remark`, `deleted`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES
            (@menu_id, '%s查询', 2, '', '', '%s:%s:query', null, 1, 0, 1, 1, 1, '%s查询按钮', 0, NOW(), NOW(), 1, 1),
            (@menu_id, '%s新增', 2, '', '', '%s:%s:add', null, 2, 0, 1, 1, 1, '%s新增按钮', 0, NOW(), NOW(), 1, 1),
            (@menu_id, '%s修改', 2, '', '', '%s:%s:edit', null, 3, 0, 1, 1, 1, '%s修改按钮', 0, NOW(), NOW(), 1, 1),
            (@menu_id, '%s删除', 2, '', '', '%s:%s:remove', null, 4, 0, 1, 1, 1, '%s删除按钮', 0, NOW(), NOW(), 1, 1),
            (@menu_id, '%s导出', 2, '', '', '%s:%s:export', null, 5, 0, 1, 1, 1, '%s导出按钮', 0, NOW(), NOW(), 1, 1);
            
            -- 3. 查看生成的菜单
            SELECT 
                m.id,
                m.parent_id,
                m.menu_name,
                m.menu_type,
                m.path,
                m.component,
                m.permission,
                m.icon,
                m.sort_order,
                CASE m.menu_type 
                    WHEN 0 THEN '目录'
                    WHEN 1 THEN '菜单' 
                    WHEN 2 THEN '按钮'
                    ELSE '未知'
                END as type_name
            FROM `sys_menu` m 
            WHERE m.permission LIKE '%s:%s:%%' 
               OR m.id = @menu_id
            ORDER BY m.parent_id, m.sort_order;
            """,
            config.getFunctionName(),
            config.getFunctionName(), config.getModuleName(), config.getBusinessName(), config.getModuleName(), config.getBusinessName(), config.getModuleName(), config.getBusinessName(), config.getFunctionName(),
            config.getFunctionName(), config.getModuleName(), config.getBusinessName(), config.getFunctionName(),
            config.getFunctionName(), config.getModuleName(), config.getBusinessName(), config.getFunctionName(),
            config.getFunctionName(), config.getModuleName(), config.getBusinessName(), config.getFunctionName(),
            config.getFunctionName(), config.getModuleName(), config.getBusinessName(), config.getFunctionName(),
            config.getFunctionName(), config.getModuleName(), config.getBusinessName(), config.getFunctionName(),
            config.getModuleName(), config.getBusinessName()
        );
    }

    private String generatePermissionSql(GeneratorConfigDTO config) {
        return String.format("""
            -- %s管理权限SQL
            -- 为管理员角色(ID=1)添加权限，如需为其他角色添加权限请修改role_id
            INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
            SELECT 1, id FROM `sys_menu` WHERE `permission` LIKE '%s:%s:%%' AND `deleted` = 0;
            
            -- 查看角色权限分配结果
            SELECT 
                r.role_name,
                m.menu_name,
                m.permission,
                CASE m.menu_type 
                    WHEN 0 THEN '目录'
                    WHEN 1 THEN '菜单' 
                    WHEN 2 THEN '按钮'
                    ELSE '未知'
                END as menu_type_name
            FROM `sys_role_menu` rm
            JOIN `sys_role` r ON rm.role_id = r.id
            JOIN `sys_menu` m ON rm.menu_id = m.id
            WHERE m.permission LIKE '%s:%s:%%' 
              AND r.deleted = 0 
              AND m.deleted = 0
            ORDER BY r.id, m.sort_order;
            """,
            config.getFunctionName(),
            config.getModuleName(), config.getBusinessName(),
            config.getModuleName(), config.getBusinessName()
        );
    }

    private void addSqlToZip(ZipOutputStream zos, GeneratorConfigDTO config) throws IOException {
        // 添加菜单SQL
        ZipEntry menuEntry = new ZipEntry("sql/menu.sql");
        zos.putNextEntry(menuEntry);
        zos.write(generateMenuSql(config).getBytes());
        zos.closeEntry();

        // 添加权限SQL
        ZipEntry permissionEntry = new ZipEntry("sql/permission.sql");
        zos.putNextEntry(permissionEntry);
        zos.write(generatePermissionSql(config).getBytes());
        zos.closeEntry();
    }

    private void zipDirectory(File directory, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String entryName = basePath.isEmpty() ? file.getName() : basePath + "/" + file.getName();

                if (file.isDirectory()) {
                    zipDirectory(file, entryName, zos);
                } else {
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    Files.copy(file.toPath(), zos);
                    zos.closeEntry();
                }
            }
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
