package com.akmans.trade.core;

public class Constants {
	public static final String PRODUCTION_PROPERITES_FILE_PATH = "classpath:/META-INF/config/production.properties";

	public static final String DEVELOPMENT_PROPERITES_FILE_PATH = "classpath:/META-INF/config/development.properties";

	public static final String SMTP_PROPERITES_FILE_PATH = "classpath:/META-INF/config/smtp.properties";

	public static final String JDBC_PROPERITES_FILE_PATH = "classpath:/META-INF/config/jdbc.properties";

	public static final String HIBERNATE_PROPERITES_FILE_PATH = "classpath:/META-INF/jpa/hibernate.properties";

	public static final String NAMED_QUERIES_PROPERITES_FILE_PATH = "classpath*:/META-INF/jpa/*-jpa-named-queries.properties";

	public static final String[] MESSAGE_SOURCE_BASE_NAMES = { "classpath*:/META-INF/i18n/**-messages",
			"classpath*:/META-INF/i18n/**-labels" };

	public static final String UI_DATE_FORMAT = "yyyy-MM-dd";

	public static final String SKIPPED_ROWS = "skippedRows";

	public static final String PROCESSED_ROWS = "processedRows";

	public static final String INSERTED_ROWS = "insertedRows";

	public static final String UPDATED_ROWS = "updatedRows";
}
