package org.metalisx.monitor.file.parser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;


import org.metalisx.monitor.domain.model.MonitorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class FileParser {

    private static final Logger logger = LoggerFactory.getLogger(FileParser.class);

    private static final String SQL = "insert into monitorlog "
            + "(logDate, logLevel, logClass, thread, sessionId, requestId, parentRequestId, "
            + "organization, username, depth, message, duration) "
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Inject
    private LineParser lineParser;

    @Resource(mappedName = "jdbc/monitorDS")
    private DataSource dataSource;

    public void parse(String filename) throws SQLException, IOException {
        if (filename != null && !"".equals(filename)) {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = getPreparedStatement(connection);
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(filename, "r");
                parse(randomAccessFile, preparedStatement);
            } finally {
                close(randomAccessFile, connection, preparedStatement);
            }
        }
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(SQL);
    }
    
    private void close(RandomAccessFile randomAccessFile, Connection connection, PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            // silent.
        }
        try {
            connection.close();
        } catch (SQLException e1) {
            // silent.
        }
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            // silent.
        }
    }

    private void addMonitorLogToPreparedStatement(PreparedStatement preparedStatement, MonitorLog monitorLog)
            throws SQLException {
        preparedStatement.setTimestamp(1, new java.sql.Timestamp(monitorLog.getLogDate().getTime()));
        preparedStatement.setString(2, monitorLog.getLogLevel());
        preparedStatement.setString(3, monitorLog.getLogClass());
        preparedStatement.setString(4, monitorLog.getThread());
        preparedStatement.setString(5, monitorLog.getSessionId());
        preparedStatement.setString(6, monitorLog.getRequestId());
        preparedStatement.setString(7, monitorLog.getParentRequestId());
        preparedStatement.setString(8, monitorLog.getOrganization());
        preparedStatement.setString(9, monitorLog.getUsername());
        preparedStatement.setLong(10, monitorLog.getDepth());
        preparedStatement.setString(11, monitorLog.getMessage());
        preparedStatement.setLong(12, monitorLog.getDuration());
        preparedStatement.addBatch();
    }

    private void parse(RandomAccessFile randomAccessFile, PreparedStatement preparedStatement) throws IOException,
            SQLException {
        int batchSize = 1000;
        int i = -1;
        int found = 0;
        Date startDate = new Date();
        String line = randomAccessFile.readLine();
        while (line != null) {
            i++;
            if (i % 10000 == 0) {
                logger.info("load() -> Processed records: " + i);
            }
            MonitorLog monitorLog = lineParser.parse(line);
            if (monitorLog != null) {
                found++;
                addMonitorLogToPreparedStatement(preparedStatement, monitorLog);
                if (found % batchSize == 0) {
                    preparedStatement.executeBatch();
                }
            }
            line = randomAccessFile.readLine();
        }
        preparedStatement.executeBatch();
        logger.info("load() -> Found " + found + " matching lines in " + ((new Date()).getTime() - startDate.getTime())
                + " milliseconds. ");
    }

}
