package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class SqlServerOperacionalJdbc {
    protected final SqlServerOperacionalProperties properties;
    protected final JdbcTemplate jdbcTemplate;

    protected SqlServerOperacionalJdbc(SqlServerOperacionalProperties properties) {
        properties.validarConfiguracao();
        this.properties = properties;
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(properties.driverClassName());
        dataSource.setUrl(properties.url());
        dataSource.setUsername(properties.usuario());
        dataSource.setPassword(properties.senha());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setQueryTimeout(properties.queryTimeoutSegundos());
        this.jdbcTemplate.setMaxRows(properties.maxRowsAdmin());
    }

    protected String table(String nome) {
        return "[" + properties.schema().replace("]", "") + "].[" + nome.replace("]", "") + "]";
    }
}
