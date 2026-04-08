package com.glee.xjpa.autocode;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.util.NameUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableProperty {
	
	/** 表名 */
	private String name;

	/**
	 * 表名
	 */
	private String tableName;
	
	/** 类名 */
	private String className;

	private String project;
	
	private String module;
	
	private String packages;
	
	private String comment;
	
	private List<ItemProperty> items;
	
	private List<ItemProperty> search;
	
	private ItemProperty group;
	
	private ItemProperty prikey;

	private boolean sort;
	
	/**类名小写开头**/
	private String firstLetter;
	
	/**类名Service小写开头**/
	private String firstServiceLetterAddPoint;

	/**类名Repository小写开头**/
	private String firstRepositoryLetterAddPoint;
	
	private Set<String> importList = new HashSet<String>();
	
	
	private List<TableIndex> tableIndexs;
	
	private List<TableKey> tableKeys;

	/**
	 * 建表语句
	 */
	private String ddl;
	
	
	
	public static class TableIndex {
		private String type;
		private String column;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		
		
		
	}
	
	
	public static class TableKey {
		private String type;
		private String column;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
	}


	public String getDdl() {
		if (StrUtil.isEmpty(ddl)) {
			return ddl;
		}

		String[] split = ddl.split("\n");
		return String.join("\" \n\t + \"", split);

	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}

	public List<TableIndex> getTableIndexs() {
		return tableIndexs;
	}

	public void setTableIndexs(List<TableIndex> tableIndexs) {
		this.tableIndexs = tableIndexs;
	}

	public List<TableKey> getTableKeys() {
		return tableKeys;
	}

	public void setTableKeys(List<TableKey> tableKeys) {
		this.tableKeys = tableKeys;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public boolean getSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
	}

	public ItemProperty getGroup() {
		return group;
	}

	public void setGroup(ItemProperty group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}
	
	public String getUName(){
		return name.toUpperCase();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<ItemProperty> getItems() {
		return items;
	}

	public void setItems(List<ItemProperty> items) {
		this.items = items;
	}

	public List<ItemProperty> getSearch() {
		return search;
	}

	public void setSearch(List<ItemProperty> search) {
		this.search = search;
	}

	public ItemProperty getPrikey() {
		return prikey;
	}

	public void setPrikey(ItemProperty prikey) {
		this.prikey = prikey;
	}

	public String getClassName() {
		if(name != null && name.trim().length()!=0){
			className = NameUtil.toHHCase(name.toLowerCase());
		}
		return className;
	}
	public String getClassNameFirstLower(){
		if(name != null && name.trim().length()!=0){
			className = NameUtil.toUUCase(name.toLowerCase());
		}
		return NameUtil.toLCase(className);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public void setImport(String importStr) {
		importList.add(importStr);
	}
	
	public void setImport(Set<String> importList) {
		this.importList  = importList;
	}
	
	public void addImport(Set<String> importList) {
		this.importList.addAll(importList);
	}
	
	public void addImport(String importPackage) {
		this.importList.add(importPackage);
	}

	public Set<String> getImportList() {
		return importList;
	}
	
	public void resetImportList() {
		importList = new HashSet<String>();
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	public String getFirstServiceLetterAddPoint() {
		return firstServiceLetterAddPoint;
	}

	public void setFirstServiceLetterAddPoint(String firstServiceLetterAddPoint) {
		this.firstServiceLetterAddPoint = firstServiceLetterAddPoint;
	}

	public String getFirstRepositoryLetterAddPoint() {
		return firstRepositoryLetterAddPoint;
	}

	public void setFirstRepositoryLetterAddPoint(String firstRepositoryLetterAddPoint) {
		this.firstRepositoryLetterAddPoint = firstRepositoryLetterAddPoint;
	}
	
	// 是否拥有字典
	public boolean hasDict() {
		
		if(items == null || items.isEmpty()) {
			return false;
		}
		
		
		for(ItemProperty item : items) {
			if(StrUtil.isEmpty(item.getComment())) {
				continue;
			}
			if(item.getComment().matches(".*\\([0-9a-zA-Z_]*:.*?,.*\\).*")) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<Map<String,Object>> listDict() {
		if(!hasDict()) {
			return new ArrayList<>();
		}
		List<Map<String,Object>> result = new ArrayList<>();
		for(ItemProperty item : items) {
			if(StrUtil.isEmpty(item.getComment())) {
				continue;
			}
			Pattern p = Pattern.compile(".*\\(([0-9a-zA-Z_]*:.*?,.*?)\\).*");
			Matcher m = p.matcher(item.getComment());
			if(m.find()) {
				Map<String,Object> itemDict = new HashMap<>();
				result.add(itemDict);
				List<Map<String,Object>> itemEnumDict = new ArrayList<>();
				itemDict.put("name",item.getFirstNameBigJavaName());
				itemDict.put("comment", item.getComment());
				itemDict.put("fields", itemEnumDict);
				String[] tmp = m.group(1).split(",");
				boolean isYesNo = false;
				for(String t : tmp) {
					String[] kv = t.split(":");
					Map<String,Object> itemEnum = new HashMap<>();
					if(kv[0].matches("^\\d+$")) {
						if(kv[0].equals("0")) {
							isYesNo = true;
						}
						if(isYesNo) {
							String field = "YES"; 
							if(kv[0].equals("0")) {
								field = "NO";
							}
							itemEnum.put("field", field);
						} else {
							itemEnum.put("field", "" + item.getBigJavaName() + "_" + kv[0]);
						}
						itemEnum.put("type", "byte");
					} else {
						itemEnum.put("field", kv[0].toUpperCase().replaceAll("-", "_"));
						itemEnum.put("type", "String");
					}
					itemEnum.put("value", kv[0]);
					itemEnum.put("comment", kv[1]);
					itemEnumDict.add(itemEnum);

				}
			}
		}
		return result;
	}
	
}
