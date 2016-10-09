package com.akmans.trade.core;

public class Constants {
	public static final String SYSTEM_PROPERITES_FILE_PATH = "classpath:/META-INF/config/system.properties";

	public static final String NAMED_QUERIES_PROPERITES_FILE_PATH = "classpath*:/META-INF/jpa/*-jpa-named-queries.properties";

	public static final String[] MESSAGE_SOURCE_BASE_NAMES = { "classpath*:/META-INF/i18n/*-messages",
			"classpath*:/META-INF/i18n/*-labels" };

	public static final String UI_DATE_FORMAT = "yyyy-MM-dd";

	public static final String SKIPPED_ROWS = "skippedRows";

	public static final String PROCESSED_ROWS = "processedRows";

	public static final String INSERTED_ROWS = "insertedRows";

	public static final String UPDATED_ROWS = "updatedRows";
}
