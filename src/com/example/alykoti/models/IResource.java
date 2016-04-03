package com.example.alykoti.models;

import java.sql.SQLException;
import java.util.List;

public interface IResource<T extends IResource> {
	List<T> query() throws SQLException;
	void pull() throws SQLException;
	void create() throws SQLException;
	Integer getId();
}
