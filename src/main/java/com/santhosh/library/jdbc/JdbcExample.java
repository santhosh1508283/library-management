package com.santhosh.library.jdbc;

import com.santhosh.library.entity.User;

import java.sql.*;

//Example without Hibernate and JPA database connection
public class JdbcExample {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/library_db";
        String username = "root";
        String password = "Santhosh@733";

        try {

            Connection connection =
                    DriverManager.getConnection(url, username, password);

            String sql = """
                    SELECT * FROM USERS where email = ?;
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, "santhosh.kumar@gmail.com");

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setEmail(rs.getString("email"));
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                System.out.println("name: "+ user.getName() + " id: " + user.getId() + " email: "+user.getEmail());
            }
            rs.close();
            System.out.println("Santhosh row inserted Successfully!");
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}