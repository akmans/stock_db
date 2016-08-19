package com.akmans.trade.core;

public class Constants {
	public static final String ENVIRONMENT_PROPERITES_FILE_PATH = "classpath:/META-INF/config/environment.properties";

	public static final String SMTP_PROPERITES_FILE_PATH = "classpath:/META-INF/config/smtp.properties";

	public static final String JDBC_PROPERITES_FILE_PATH = "classpath:/META-INF/config/jdbc.properties";

	public static final String HIBERNATE_PROPERITES_FILE_PATH = "classpath:/META-INF/jpa/hibernate.properties";

	public static final String NAMED_QUERIES_PROPERITES_FILE_PATH = "classpath*:/META-INF/jpa/*-jpa-named-queries.properties";

	public static final String[] MESSAGE_SOURCE_BASE_NAMES = { "classpath*:/META-INF/i18n/**-messages",
			"classpath*:/META-INF/i18n/**-labels" };

	// public static final String STOCK_TOKYO_SUFFIX = "T";

	// public static final String ZERO = "0";

	// public static final String DATE_PATTERN = "[0-9]{4}年[0-9]{2}月[0-9]{2}日";

	// public static final String DATE_FORMAT = "YYYY年MM月DD日";

	public static final String UI_DATE_FORMAT = "yyyy-MM-dd";

	// public static final String BEGIN_TIMESTAMP = "beginTimestamp";

	// public static final String END_TIMESTAMP = "endTimestamp";

	public static final String SKIPPED_ROWS = "skippedRows";

	public static final String PROCESSED_ROWS = "processedRows";

	public static final String INSERTED_ROWS = "insertedRows";

	public static final String UPDATED_ROWS = "updatedRows";
}
