package com.example.alykoti.models;

import com.example.alykoti.models.devices.DeviceStatus;
import com.example.alykoti.models.devices.DeviceType;
import com.example.alykoti.services.DatabaseService;

import java.sql.*;
import java.util.*;

public class Device implements IResource<Device> {

	private Integer id;

	private String name;
	private DeviceType type;
	private Integer room;
	private java.sql.Date updated;
	public final AbstractMap<DeviceStatus.Type, DeviceStatus> statuses = new HashMap<>();
	public final List<User> users = new ArrayList<>();

	public Device(Integer id){
		setId(id);
	}
	public Device(){
		this(null);
	}


	@Override
	public List<Device> query() throws SQLException {
		return DatabaseService.getInstance().useConnection(conn -> {

			//Rakennetaan query sen mukaan mitä dataa tässä oliossa on
			StringJoiner sql = new StringJoiner(" AND ");
			if(name != null) sql.add("name = ?");
			if(type != null) sql.add("type = ?");
			if(room != null) sql.add("room = ?");
			if(updated != null) sql.add("updated > ?");

			StringJoiner userIn = new StringJoiner(",");
			for(Object o : users) userIn.add("?");

			if(userIn.length() != 0) sql.add("userId IN (" + userIn.toString() + ")");

			//Nyt StringJoiner sql on esimerkiksi "room = ? AND userId IN (?,?,?)"
			String query = "SELECT * FROM device_full_info";
			if(sql.length() != 0) query += " WHERE " + sql.toString();
			query += ";";
			PreparedStatement statement = conn.prepareStatement(query);

			int index = 0;
			//Täytetään arvot (HUOM! Tässä on ylläpidettävä sama järjestys kuin yllä)
			if(name != null) statement.setString(++index, name);
			if(type != null) statement.setString(++index, type.toString());
			if(room != null) statement.setInt(++index, room);
			if(updated != null) statement.setDate(++index, updated);
			for(User u : users) statement.setInt(++index, u.getId());

			System.out.println("Running query " + statement.toString());

			return fromResultSet(statement.executeQuery());
		});
	}

	@Override
	public void pull() throws SQLException {
		DatabaseService.getInstance().useConnection(conn -> {
			PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM device_full_info WHERE id = ? ORDER BY id, statusType, userId"
			);
			statement.setInt(1, getId());
			List<Device> result = fromResultSet(statement.executeQuery());
			if(result.size() == 1){
				Device d = result.get(0);
				statuses.clear();
				statuses.putAll(d.statuses);
				users.clear();
				users.addAll(d.users);
				name = d.name;
				room = d.room;
				updated = d.updated;
			}
			return null;
		});
	}

	private static List<Device> fromResultSet(ResultSet res) throws SQLException {
		List<Device> devices = new ArrayList<>();
		Integer currentId = null;
		Device d = null;

		List<Integer> userIds = new ArrayList<>();//Lista josta on nopea tarkistaa onko käyttäjä lisätty jo käyttäjälistaan

		while(res.next()){
			Integer nextId = res.getInt("id");
			if(currentId == null || !currentId.equals(nextId)){//Nämä tiedot eivät ole listoja, eli ei tarvitse käsitellä joka riville uudestaan
				d = new Device(nextId);
				devices.add(d);
				currentId = nextId;
				d.name = res.getString("name");
				d.room = res.getInt("room");
				d.updated = res.getDate("updated");
				d.type = DeviceType.fromString(res.getString("type"));
			}
			Integer userId = res.getInt("userId");
			if(!userIds.contains(userId)){
				userIds.add(userId);
				User u = new User();
				u.setId(userId);
				u.setUsername(res.getString("userName"));
				d.users.add(u);
			}
			DeviceStatus.Type statusType = DeviceStatus.Type.fromString(res.getString("statusType"));
			if(!d.statuses.containsKey(statusType)){
				String valueStr = res.getString("statusValueStr");
				Integer valueNumber = res.getInt("statusValueNumber");
				DeviceStatus status;
				if(valueStr == null){
					status = new DeviceStatus(statusType, valueNumber);
				} else {
					status = new DeviceStatus(statusType, valueStr);
				}
				d.statuses.put(statusType, status);
			}
		}
		return devices;
	}

	@Override
	public void create() throws SQLException {

	}

	@Override
	public void update() throws SQLException {

	}

	@Override
	public String toString(){
		return "Device { id: " + this.id +
				", type: " + this.type +
				", name: " + this.name +
				", status: " + Arrays.toString(this.statuses.values().toArray()) +
				", users: " + users.toString() +
				" }";
	}


	public void setRoom(Integer room){
		this.room = room;
	}

	public Integer getRoom(){
		return this.room;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.sql.Date getUpdated() {
		return updated;
	}

	public void setUpdated(java.sql.Date updated) {
		this.updated = updated;
	}

}
