package com.prosc.asfgrp;

import com.prosc.servlet.ServletUtil;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: val Date: 7/2/12 Time: 3:55 PM
 */
public class DbConnectionManager {
	public static Connection getConnection(ServletContext context) throws ClassNotFoundException, SQLException {
		Class.forName("com.prosc.fmpjdbc.Driver");
		String jdbcUsername = ServletUtil.configValueForKey(context, "username");
		String jdbcPassword = ServletUtil.configValueForKey(context, "password");
		String jdbcHost = ServletUtil.configValueForKey(context, "host");
		String db = ServletUtil.configValueForKey(context, "database");
		return DriverManager.getConnection("jdbc:fmp360://" + jdbcHost + "/" + db, jdbcUsername, jdbcPassword);
	}
}
