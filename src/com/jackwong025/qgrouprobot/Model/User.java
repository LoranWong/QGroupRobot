package com.jackwong025.qgrouprobot.Model;

public class User {
	
	private String uin="";
	private String name="";
	private String rp="";
	
	
	
	@Override
	public String toString() {
		return "uin="+uin+"  name="+name+"  rp="+rp +"\n";
	}
	public String get(String key) {
		switch (key) {
		case "uin":
			return uin;
		case "name":
			return name;
		case "rp":
			return rp;
		}
		return null;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRp() {
		return rp;
	}
	public void setRp(String rp) {
		this.rp = rp;
	}
	
}
