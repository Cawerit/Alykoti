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
		//Rakennetaan query sen mukaan mitä dataa tässä oliossa on
		StringJoiner sql = new StringJoiner(" AND ");
		if(name != null) sql.add("name = ?");
		if(type != null) sql.add("type = ?");
		if(room != null) sql.add("room = ?");
		if(updated != null) sql.add("updated > ?");

		StringJoiner userIn = new StringJoiner(",");
		for(Object o : users) userIn.add("?");

		if(userIn.length() != 0) {
			sql.add("id IN (SELECT device FROM device_users WHERE user IN (" + userIn.toString() + "))");
		}

		//Nyt StringJoiner sql on esimerkiksi "room = ? AND userId IN (?,?,?)"
		String query = "SELECT * FROM device_full_info";
		if(sql.length() != 0) query += " WHERE " + sql.toString();
		query += ";";

		try(
			Connection conn = DatabaseService.getInstance().getConnection();
			PreparedStatement statement = conn.prepareStatement(query)
		){
			int index = 0;
			//Täytetään arvot (HUOM! Tässä on ylläpidettävä sama järjestys kuin yllä)
			if(name != null) statement.setString(++index, name);
			if(type != null) statement.setString(++index, type.toString());
			if(room != null) statement.setInt(++index, room);
			if(updated != null) statement.setDate(++index, updated);
			for(User u : users) statement.setInt(++index, u.getId());

			System.out.println("Running query " + statement.toString());

			return fromResultSet(statement.executeQuery());
		}
	}

	@Override
	public void pull() throws SQLException {
		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM device_full_info WHERE id = ? ORDER BY id, statusType, userId"
				)
		){
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
		}
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
				userIds.clear();
				devices.add(d);
				currentId = nextId;
				d.name = res.getString("name");
				d.room = res.getInt("room");
				d.updated = res.getDate("updated");
				d.type = DeviceType.fromString(res.getString("type"));
			}
			Integer userId = res.getInt("userId");
			if(!userId.equals(0) && !userIds.contains(userId)){
				userIds.add(userId);
				User u = new User();
				u.setId(userId);
				u.setUsername(res.getString("userName"));
				d.users.add(u);
			}
			DeviceStatus.Type statusType = DeviceStatus.Type.fromString(res.getString("statusType"));
			if(statusType != null && !d.statuses.containsKey(statusType)){
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
		try(Connection conn = DatabaseService.getInstance().getConnection()){
			String createDeviceSql = "INSERT INTO devices (room, name, type) VALUES (?,?,?)";

			PreparedStatement createStatement = conn.prepareStatement(createDeviceSql, Statement.RETURN_GENERATED_KEYS);
			createStatement.setInt(1, room);
			createStatement.setString(2, name);
			createStatement.setString(3, type.toString());
			createStatement.executeUpdate();

			try(ResultSet idRes = createStatement.getGeneratedKeys()) {
				if (idRes.first()) {
					this.id = idRes.getInt(1);
				}
			}

			//Seuraavat 2 updatea voidaan tehdä samalla transactionilla
			boolean prevAutoCommitValue = conn.getAutoCommit();
			conn.setAutoCommit(false);

			try {

				if (id != null && users.size() != 0) {
					StringJoiner addUsers = new StringJoiner(",");
					for (Object ignored : users)
						addUsers.add("(" + id + ", ?)");

					PreparedStatement addUsersStatement =
							conn.prepareStatement("INSERT INTO device_users (device, user) VALUES " + addUsers.toString() + ";");

					for (int i = 0, n = users.size(); i < n; i++)
						addUsersStatement.setInt(i + 1, users.get(i).getId());

					addUsersStatement.executeUpdate();
				}

				if (id != null && statuses.size() != 0) {
					StringJoiner addStatuses = new StringJoiner(",");
					Collection<DeviceStatus> statusValues = statuses.values();

					for (Object ignored : statusValues)
						addStatuses.add("(" + id + ", ?, ?, ?, NOW())");

					PreparedStatement addStatusesStatement = conn.prepareStatement(
							"INSERT INTO device_status (device, status_type, value_str, value_number, updated) " +
									"VALUES " + addStatuses.toString() + ";");

					int index = 0;
					for (DeviceStatus s : statusValues) {
						addStatusesStatement.setString(++index, s.statusType.toString());
						addStatusesStatement.setString(++index, s.valueStr);
						addStatusesStatement.setInt(++index, s.valueNumber);
					}
					System.out.println("Setting status for :"+id + "\n" + addStatusesStatement.toString());
					addStatusesStatement.executeUpdate();
				}


				conn.commit();
				conn.setAutoCommit(prevAutoCommitValue);

			} finally {
				try {
					conn.setAutoCommit(prevAutoCommitValue);
				} catch (Exception ignored){}
			}
		}
	}

	public void setStatus(DeviceStatus stat) throws SQLException {
		statuses.put(stat.statusType, stat);
		final String sql = "INSERT INTO device_status (device, status_type, value_str, value_number, updated) " +
				"VALUES(?, ?, ?, ?, NOW()) " +
				"ON DUPLICATE KEY UPDATE " +
				"value_str = ?," +
				"value_number = ?," +
				"updated = NOW();";
		try(
				Connection conn = DatabaseService.getInstance().getConnection();
				PreparedStatement statement = conn.prepareStatement(sql)
		){
			statement.setInt(1, getId());
			statement.setString(2, stat.statusType.toString());
			statement.setString(3, stat.valueStr);
			statement.setInt(4, stat.valueNumber);
			statement.setString(5, stat.valueStr);
			statement.setInt(6, stat.valueNumber);
			statement.executeUpdate();
		}
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

	public void setType(DeviceType type){
		this.type = type;
	}

	public DeviceType getType(){
		return type;
	}

}
