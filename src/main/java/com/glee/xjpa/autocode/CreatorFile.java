package com.glee.xjpa.autocode;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class CreatorFile {

	private String targetFile;
	
	private String packages;
	
	private Set<String> importList = new HashSet<String>();
	
	private boolean importExt = false; //是否包含扩展字段include 

	public CreatorFile(String targetFile,String packages,Set<String> importList) {
		super();
		this.targetFile = targetFile;
		this.packages = packages;
		if(importList != null){
			this.importList = importList;
		}
	}
	
	public CreatorFile(String targetFile,String packages,Set<String> importList,boolean importExt) {
		super();
		this.targetFile = targetFile;
		this.packages = packages;
		if(importList != null){
			this.importList = importList;
		}
		this.importExt = importExt;
	}

	public String getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	
	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}
	
	public void setImport(String importStr) {
		importList.add(importStr);
	}
	
	public void setImport(Set<String> importList) {
		importList.addAll(importList);
	}

	public Set<String> getImportList() {
		return importList;
	}
	
	public void clearImportList() {
		importList.clear();
	}

	public boolean isImportExt() {
		return importExt;
	}

	public void setImportExt(boolean importExt) {
		this.importExt = importExt;
	}
	
	
	
	
}
