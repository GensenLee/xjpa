package com.glee.xjpa.autocode;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XJpaAutoCoder {

    public static List<TableProperty> initTable(String url, String username, String password, String tableName, String project, String packages) throws Exception {
        List<TableProperty> liTableProperty = new ArrayList<TableProperty>();
        Connection conn = DriverManager.getConnection(url, username, password);
        List<String> tables = new ArrayList<String>();
        ResultSet rs;

        if (!tableName.matches("^[a-zA-Z0-9_-]+$")) {
            rs = conn.prepareStatement("show tables").executeQuery();

            while (rs.next()) {
                if (rs.getString(1).matches(tableName)) {
                    tables.add(rs.getString(1));
                }
            }
        } else {
            tables.add(tableName);
        }

        for (String table : tables) {
            TableProperty tableProperty = new TableProperty();
            String comm = "";
            rs = conn.prepareStatement("show table STATUS").executeQuery();
            while (rs.next()) {
                if (rs.getString("Name").equals(table)) {
                    comm = rs.getString("Comment");
                    break;
                }
            }

            List<ItemProperty> items = new ArrayList<>();

            rs = conn.prepareStatement("show full fields from " + table).executeQuery();
            while (rs.next()) {
                ItemProperty item = new ItemProperty();
                ResultSetMetaData md = rs.getMetaData();
                String name = rs.getString("Field");
                String type = rs.getString("Type");
                String comment = rs.getString("Comment");
                String key = rs.getString("Key");
                item.setName(name);
                item.setDef(rs.getString("Default"));
                item.setPriKey(key);
                item.setExtra(rs.getString("Extra"));
                item.setIsNull(rs.getString("Null"));
                item.setOrigType(type);
                item.setType(type.split("\\(")[0]);
                item.setJtype(JavaType.get(item.getType()));
                if (JavaType.getImport(item.getType()) != null) {
                    tableProperty.setImport(JavaType.getImport(item.getType()));
                }
                item.setComment(comment);
                items.add(item);

                if (item.isPriKey()) {
                    tableProperty.setPrikey(item);
                }

            }
            tableProperty.setName(table);
            tableProperty.setTableName(table);
            tableProperty.setComment(comm.split(";")[0]);
            tableProperty.setItems(items);
            tableProperty.setProject(project);
            tableProperty.setPackages(packages);

            if (tableProperty.getPrikey() == null) {
                ItemProperty item = new ItemProperty();
                item.setJtype("Void");
                tableProperty.setPrikey(item);
            }

            // 索引外键


            rs = conn.prepareStatement("SHOW INDEX FROM " + table).executeQuery();

            Map<String, TableProperty.TableIndex> mapTableIndex = new HashMap<String, TableProperty.TableIndex>();
            Map<String, TableProperty.TableKey> mapTableKey = new HashMap<String, TableProperty.TableKey>();

            while (rs.next()) {
                ResultSetMetaData md = rs.getMetaData();
                String nonUnique = rs.getString("Non_unique");
                String keyName = rs.getString("Key_name");
                String columnName = rs.getString("Column_name");
                String indexType = rs.getString("Index_type");
                if (keyName.equalsIgnoreCase("primary")) {
                    continue;
                }

                if (nonUnique.equalsIgnoreCase("1")) {
                    // 索引
                    TableProperty.TableIndex tableIndex = mapTableIndex.get(keyName);
                    if (tableIndex == null) {
                        tableIndex = new TableProperty.TableIndex();
                        mapTableIndex.put(keyName, tableIndex);
                        tableIndex.setColumn(columnName);
                        tableIndex.setType(indexType);
                    } else {
                        tableIndex.setColumn(tableIndex.getColumn() + "," + columnName);
                    }
                } else if (nonUnique.equalsIgnoreCase("0")) {
                    // 索引
                    TableProperty.TableKey tableKey = mapTableKey.get(keyName);
                    if (tableKey == null) {
                        tableKey = new TableProperty.TableKey();
                        mapTableKey.put(keyName, tableKey);
                        tableKey.setColumn(columnName);
                        tableKey.setType(indexType);
                    } else {
                        tableKey.setColumn(tableKey.getColumn() + "," + columnName);
                    }
                }
            }


            List<TableProperty.TableIndex> liTableIndex = new ArrayList<TableProperty.TableIndex>();

            tableProperty.setTableIndexs(liTableIndex);

            for (String key : mapTableIndex.keySet()) {
                liTableIndex.add(mapTableIndex.get(key));
            }

            List<TableProperty.TableKey> liTableKey = new ArrayList<TableProperty.TableKey>();

            tableProperty.setTableKeys(liTableKey);

            for (String key : mapTableKey.keySet()) {
                liTableKey.add(mapTableKey.get(key));
            }


            // 获取表ddl
            rs = conn.prepareStatement("SHOW CREATE TABLE " + table).executeQuery();

            if (rs.next()) {
                tableProperty.setDdl(rs.getString("Create Table"));
            }


            liTableProperty.add(tableProperty);


        }

        return liTableProperty;
    }

    public static Map<String, CreatorFile> createBaseJavaFile(TableProperty table, List<ProjectInfo> projectList) {
        Map<String, CreatorFile> modelToFile = new HashMap<String, CreatorFile>();

        for (ProjectInfo projectInfo : projectList) {
            modelToFile.put(projectInfo.getVm(),
                    new CreatorFile(projectInfo.getProjectDir().replace("/", File.separatorChar + "") +
                            File.separatorChar + projectInfo.getPackageDir().replace(".", File.separatorChar + "") +
                            File.separatorChar + table.getClassName() + projectInfo.getNameSuffix() + ".java",
                            projectInfo.getPackageDir(),
                            projectInfo.getImportList(),
                            projectInfo.getImportExt())
            );
        }
        return modelToFile;
    }
}
