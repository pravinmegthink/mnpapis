package com.megthink.gateway.dao.common;

import java.security.GeneralSecurityException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.megthink.gateway.utils.EncryptDecrypt;
import com.megthink.gateway.utils.ReadConfigFile;

@Configuration
public class DaoConfig {

	//@Value("${spring.datasource.driver-class-name}")
		private String firstDataSourceDriver = ReadConfigFile.getProperties().getProperty("spring.datasource.driver-class-name");

		//@Value("${spring.datasource.username}")
		private String firstDataSourceUsername = ReadConfigFile.getProperties().getProperty("spring.datasource.username");

		//@Value("${spring.datasource.password}")
		private String firstDataSourcePassword = ReadConfigFile.getProperties().getProperty("spring.datasource.password");

		//@Value("${spring.datasource.url}")
		private String firstDataSourceURL = ReadConfigFile.getProperties().getProperty("spring.datasource.url");

		@Bean
		@Primary
		public DataSource firstDataSource() throws GeneralSecurityException {
//			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
			dataSource.setSuppressClose(true);
			dataSource.setDriverClassName(firstDataSourceDriver);
			dataSource.setUrl(firstDataSourceURL);
			dataSource.setUsername(EncryptDecrypt.decrypt(firstDataSourceUsername));
			dataSource.setPassword(EncryptDecrypt.decrypt(firstDataSourcePassword));
			return dataSource;

		}

		@Bean(name = "namedParameterJdbcOperations")
		public NamedParameterJdbcOperations createNamedParameterJdbcOperations(
				@Qualifier("firstDataSource") DataSource ds) {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
			jdbcTemplate.setFetchSize(100);
			return new NamedParameterJdbcTemplate(jdbcTemplate);
		}

}