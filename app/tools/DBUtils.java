package tools;

import models.ProcedureResult;
import play.Logger;
import play.db.DB;

import java.sql.*;

/**
 * Created by mac on 14/10/2020.
 */
public class DBUtils {

    public static void closeQuietly(Connection connection){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Connection connection, Statement statement){
        try {
            connection.close();
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Connection connection, Statement statement, ResultSet resultSet){
        try {
            connection.close();
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Connection connection, PreparedStatement preparedStatement ){
        try {
            connection.close();
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        try {
            connection.close();
            if (preparedStatement != null) preparedStatement.close();
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Connection connection, CallableStatement callableStatement){
        try {
            connection.close();
            if (callableStatement != null) callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Connection connection, CallableStatement callableStatement, ResultSet resultSet){
        try {
            connection.close();
            if (callableStatement != null) callableStatement.close();
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getTotalRows(String reqForTotalRows) {
        Logger.debug(reqForTotalRows);
        Connection connection = DB.getConnection();
        Statement statement = null;
        int i;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(reqForTotalRows);
            if (resultSet.next()) {
                i = resultSet.getInt("total");
            } else {
                i = 0;
            }
        } catch (SQLException e) {
            return 0;
        } finally {
            DBUtils.closeQuietly(connection, statement, null);
        }
        Logger.debug("Total: " + i);
        return i;
    }

    public static String getPartnerName(int partner) {
        Connection connection = DB.getConnection();
        CallableStatement cStmt = null;
        String req = "{? = call f_get_partner_by_id(?)}";
        Logger.info("Req: " + req);
        try {

            cStmt = connection.prepareCall(req);
            cStmt.registerOutParameter(1, Types.VARCHAR);
            cStmt.setInt(2, partner);
            cStmt.execute();
            return cStmt.getString(1);

        } catch (SQLException e) {
            return null;
        } finally {
            DBUtils.closeQuietly(connection, cStmt, null);
        }
    }

    public static ProcedureResult saveNotification(String adresse, String type, String titre, String notification) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_add_notification(?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(3, adresse);
            callableStatement.setString(4, type);
            callableStatement.setString(5, titre);
            callableStatement.setString(6, notification);
            callableStatement.execute();
            int value = callableStatement.getInt(1);
            String message = callableStatement.getString(2);
            return new ProcedureResult(value, message);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        } finally {
            DBUtils.closeQuietly(connection, callableStatement);
        }
    }

}
