package com.example.alykoti.models;

import java.sql.Types;

public class HomeTest extends Resource<HomeTest> {

	@Resource.Column(sqlType = Types.VARCHAR)
	String name;

	private Integer id;


	public HomeTest(){
		this(null);
	}
	public HomeTest(Integer id){
		super(HomeTest.class, "homes");
		setId(id);
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public Integer getId(){
		return id;
	}

	@Override
	public void setId(Integer id){
		this.id = id;
	}

	@Override
	public String toString(){
		return "HomeTest { id: " + getId() + ", name: " + name + " }";
	}

}
