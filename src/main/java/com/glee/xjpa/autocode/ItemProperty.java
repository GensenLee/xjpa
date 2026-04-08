package com.glee.xjpa.autocode;


import com.glee.xjpa.util.NameUtil;

/**
 */
public class ItemProperty {
	
	private String name;
	
	private String firstBigName;
	
	private String type;
	
	private String origType;
	
	private String jtype;
	
	private String comment;
	
	private String isNull;
	
	private String def;
	
	private boolean priKey;
	
	private boolean autoIncrement;
	
	private String extra;

	public String getName() {
		return name;
	}
	
	public String getUName()
	{
		return name.toUpperCase();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		if(comment != null) {
			return comment.replaceAll("\"", "'");
		} else {
			return comment;
		}
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isPriKey() {
		return priKey;
	}

	public void setPriKey(String priKey) {
		if(priKey == null) {
			this.priKey = false;
		}else {
			this.priKey = priKey.equalsIgnoreCase("pri");
		}
		
	}

	public String getJtype() {
		return jtype;
	}

	public void setJtype(String jtype) {
		this.jtype = jtype;
	}
	
	public String getJavaName(){
		if(name != null && name.trim().length()!=0){
			if(name.contains("_")) {
				return NameUtil.toLUCase(name.toLowerCase()).replaceAll("_", "");
			} else {
				return NameUtil.toLUCase(name).replaceAll("_", "");
			}
		}
		return "";
	}
	
	public String getBigJavaName(){
		if(name != null && name.trim().length()!=0){
			return NameUtil.toUUCase(name).replace("_$", "").toUpperCase();
		}
		return "";
	}
	
	public String getFirstNameBigJavaName(){
		if (name == null) {
			return "";
		}
		String tmpName = name;
		if (tmpName.endsWith("-")) {
			tmpName = tmpName.substring(0, tmpName.length()-1);
		}
		if (tmpName.contains("-")) {
			tmpName = tmpName.replaceAll("-", "_");
		}
		if(tmpName.trim().length()!=0){
			if(tmpName.contains("_")) {
				return NameUtil.toUUCase(tmpName.toLowerCase()).replaceAll("_", "");
			} else {
				return NameUtil.toUUCase(tmpName).replaceAll("_", "");
			}
		}
		return "";
	}

	public String getFirstBigName() {
		if(name != null && name.trim().length()!=0){
			if(name.contains("_")) {
				firstBigName = NameUtil.toUfirst(name.toLowerCase()) ;
			} else {
				firstBigName = NameUtil.toUfirst(name) ;
			}
			
		}
		return firstBigName;
	}

	public void setFirstBigName(String firstBigName) {
		this.firstBigName = firstBigName;
	}

	public String getOrigType() {
		return origType;
	}

	public void setOrigType(String origType) {
		this.origType = origType;
	}

	public String getIsNull() {
		return isNull;
	}
	
	public String getIsNotNull() {
		if(isNull == null){
			return "false";
		}
		return isNull.equalsIgnoreCase("true")?"false":"true";
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull.toLowerCase().replace("yes", "true").replace("no", "false");
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		if(def == null){
			def = "";
		}else if("".equals(def)) {
			def = "''";
		}else if(!def.equalsIgnoreCase("null") && !def.matches("[0-9]+(\\.[0-9+])?") && !def.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
			def = "'"+def.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"")+"'";
		}
		this.def = def;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(String extra) {
		if(extra == null) {
			this.autoIncrement = false;
		}else {
			this.autoIncrement = extra.equalsIgnoreCase("auto_increment");
		}
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.setAutoIncrement(extra);
		this.extra = extra;
	}
}
