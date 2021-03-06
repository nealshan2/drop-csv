package com.noosh.csvapi.service;

import com.noosh.csvapi.dao.CsvHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Neal Shan
 * @since 0.0.1
 */
@Service
public class CsvDataService {

    private static final Logger log = LoggerFactory.getLogger(CsvDataService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createCsvDataTable(String csvName, String[] headers) {
        log.info("Creating tables csv_data");
        // prepare columns
        StringBuilder colNameSb = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            String colName = "attr_" + i;

            colNameSb.append(", ");
            colNameSb.append(colName);
            colNameSb.append(" VARCHAR(255) ");
        }

        // create csv_data table
        String csvDataTableName = "csv_data_" + csvName;
        jdbcTemplate.execute("DROP TABLE " + csvDataTableName + " IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE " + csvDataTableName + "(" +
                "id IDENTITY "
                + colNameSb.toString()
                + ")");
    }

    public void insertIntoDataTableWithId(String csvName, int columnSize, List<Object[]> batchLine) {
        String csvDataTableName = "csv_data_" + csvName;
        StringBuilder columnNameSb = new StringBuilder();
        columnNameSb.append("id,");
        StringBuilder columnParamSb = new StringBuilder();
        columnParamSb.append("?,");
        for (int i = 0; i < columnSize - 1; i++) {
            columnNameSb.append("attr_" + i + ",");

            columnParamSb.append("?,");
        }

        String insertColumns = columnNameSb.toString().substring(0, columnNameSb.toString().lastIndexOf(","));
        String insertParams = columnParamSb.toString().substring(0, columnParamSb.toString().lastIndexOf(","));

        jdbcTemplate.batchUpdate("INSERT INTO " + csvDataTableName + "(" +
                insertColumns +
                ") VALUES (" +
                insertParams +
                ")", batchLine);
    }

    public List<Map<String, String>> findCsvData(String searchName, int page, int size, List<CsvHeader> csvHeaders) {
        String csvDataTableName = "csv_data_" + searchName;
        List<Map<String, String>> csvData = new ArrayList<>();
        jdbcTemplate.query(
                "SELECT * FROM " + csvDataTableName +
                        " limit " + size + " offset " + ((page - 1) * size),
                (rs, rowNum) -> {
                    Map<String, String> csvLineData = new HashMap<>();
                    csvLineData.put("key", rs.getLong("id") + "");
                    for (CsvHeader csvHeader : csvHeaders) {
                        csvLineData.put(csvHeader.getColumnName(), rs.getString(csvHeader.getColumnName()));
                    }
                    csvData.add(csvLineData);
                    return null;
                }

        );

        return csvData;
    }
}
