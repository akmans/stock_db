package com.akmans.trade.core.spring;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.akmans.trade.core.Constants;

public class BaseReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource
		implements InitializingBean {

	private static final String PROPERTIES_SUFFIX = ".properties";

	private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	/**
	 * Returns the resource bundle corresponding to the given locale.
	 */
	public Properties getResourceBundle(Locale locale) {
		clearCacheIncludingAncestors();
		return getMergedProperties(locale).getProperties();
	}

	@Override
	public void afterPropertiesSet() {
		// Set base names.
		setBasenames(Constants.MESSAGE_SOURCE_BASE_NAMES);
		// Set cache seconds.
		setCacheSeconds(3600);
	}

	@Override
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		final Properties properties = new Properties();
		long lastModified = -1;
		try {
			for (Resource resource : resolver.getResources(filename + PROPERTIES_SUFFIX)) {
				final PropertiesHolder holder = super.refreshProperties(cleanPath(resource), propHolder);
				properties.putAll(holder.getProperties());
				if (lastModified < resource.lastModified())
					lastModified = resource.lastModified();
			}
		} catch (IOException ignored) {
			// nothing to do
		}
		return new PropertiesHolder(properties, lastModified);
	}

	private String cleanPath(Resource resource) throws IOException {
		return resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
	}
}