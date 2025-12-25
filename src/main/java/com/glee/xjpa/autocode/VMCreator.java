package com.glee.xjpa.autocode;


import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.JdkUtil;
import cn.hutool.core.io.FileUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VMCreator {

    public static void create(TableProperty table, Map<String, CreatorFile> modelToFile) throws Exception {

        Set<String> importList = new HashSet<String>(table.getImportList());

        for (String k : modelToFile.keySet()) {
            CreatorFile creatorFile = modelToFile.get(k);

            Map<String, String> mapTemplate = new HashMap<String, String>();

            mapTemplate.put("${table}", table.getName());
            mapTemplate.put("${className}", table.getClassName());
            mapTemplate.put("${classNameFirstLower}", table.getClassNameFirstLower());
            mapTemplate.put("${project}", table.getProject());
            mapTemplate.put("${packages}", table.getPackages());
            mapTemplate.put("${firstServiceLetterAddPoint}", table.getFirstServiceLetterAddPoint());
            mapTemplate.put("${firstLetter}", table.getFirstLetter());
            mapTemplate.put("${firstRepositoryLetterAddPoint}", table.getFirstRepositoryLetterAddPoint());
            table.resetImportList();
            if (creatorFile.isImportExt() && !CollectionUtils.isEmpty(importList)) {
                table.addImport(importList);
            }

            if (!CollectionUtils.isEmpty(creatorFile.getImportList())) {
                for (String importStr : creatorFile.getImportList()) {
                    if (!importStr.matches("^import\\s.*")) {
                        importStr = "import " + importStr;
                    }
                    if (!importStr.matches(".*;$")) {
                        importStr = importStr + ";";
                    }
                    for (String key : mapTemplate.keySet()) {
                        importStr = importStr.replaceAll(key.replace("$", "\\$").replace("{", "\\{").replace("}", "\\}"), mapTemplate.get(key));
                    }
                    table.addImport(importStr);
                }
            }

            table.setPackages(creatorFile.getPackages());
            String target = creatorFile.getTargetFile();

            int i = target.lastIndexOf("/");
            String filename = target.substring(i + 1);
            String targetDir = target.substring(0, i + 1);

            Map<String, Object> map = new HashMap<>();
            map.put("table", table);

            Map<String, Object> jvm = new HashMap<>();
            jvm.put("version", JdkUtil.JVM_VERSION);
            map.put("jvm", jvm);

            Map<String, Object> ext = new HashMap<>();
            ext.put("swaggerEnabled", ClassLoaderUtil.isPresent("io.swagger.annotations.ApiModel"));
            map.put("ext", ext);

            String content = generateContent(k, map);

            //写到目标目录
            FileUtil.writeString(content, targetDir + File.separator + filename, StandardCharsets.UTF_8);

        }

    }


    public static String generateContent(String tpl, Map<String, Object> dataInfo) {
        System.out.println("template file :" + tpl + " build data : " + dataInfo);

        Properties properties = new Properties();
        //设置文件编码
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
        properties.setProperty(Velocity.RUNTIME_LOG_REFERENCE_LOG_INVALID, "false");

        Velocity.init(properties);

        VelocityContext context = new VelocityContext(dataInfo);

        return convert(tpl, context);
    }

    /*
     * 采用Velocity，将传入的vm串，结合params对象，转换为字符串。
     */
    static public String convert(String vmFileName, VelocityContext context) {
        StringWriter w = new StringWriter();
        String vmContent = FileUtil.readString(vmFileName, StandardCharsets.UTF_8);
        Velocity.evaluate(context, w, "util.velocity", vmContent);
        System.out.println(w);
        return w.toString();
    }

}
