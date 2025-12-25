package com.glee.xjpa.autocode;

import java.util.HashSet;
import java.util.Set;

public class ProjectInfo {

	private String projectDir;

	private String packageDir;

	private String exclude;

	private String nameSuffix;

	private String vm;

	public Set<String> importList = new HashSet<String>();

	public Boolean importExt = false;

	public ProjectInfo() {

	}

	public ProjectInfo(String projectDir, String packageDir, String vm, String nameSuffix) {
		super();
		this.projectDir = projectDir;
		this.packageDir = packageDir;
		this.nameSuffix = nameSuffix;
		this.vm = vm;
	}

	public ProjectInfo(String projectDir, String packageDir, String vm, String nameSuffix, Set<String> importList) {
		super();
		this.projectDir = projectDir;
		this.packageDir = packageDir;
		this.nameSuffix = nameSuffix;
		this.importList = importList;
		this.vm = vm;
	}

	public ProjectInfo(String projectDir, String packageDir, String vm, String nameSuffix, Set<String> importList, Boolean importExt) {
		super();
		this.projectDir = projectDir;
		this.packageDir = packageDir;
		this.nameSuffix = nameSuffix;
		this.importList = importList;
		this.importExt = importExt;
		this.vm = vm;
	}

	public ProjectInfo(String projectDir, String packageDir, String vm, String nameSuffix, String exclude) {
		super();
		this.projectDir = projectDir;
		this.packageDir = packageDir;
		this.vm = vm;
		this.nameSuffix = nameSuffix;
		this.exclude = exclude;
	}

	public String getProjectDir() {
		return projectDir;
	}

	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}

	public String getPackageDir() {
		return packageDir;
	}

	public void setPackageDir(String packageDir) {
		this.packageDir = packageDir;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public String getVm() {
		return vm;
	}

	public void setVm(String vm) {
		this.vm = vm;
	}

	public String getNameSuffix() {
		return nameSuffix;
	}

	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}

	public Set<String> getImportList() {
		return importList;
	}

	public void setImportList(Set<String> importList) {
		this.importList = importList;
	}

	public Boolean getImportExt() {
		return importExt;
	}

	public void setImportExt(Boolean importExt) {
		this.importExt = importExt;
	}
	
	
}
