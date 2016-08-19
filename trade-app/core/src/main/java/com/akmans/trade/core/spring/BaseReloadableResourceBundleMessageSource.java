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

	// private final Charset encoding = Charset.forName("UTF-8");

	// @Autowired
	// private Environment environment;

	/**
	 * Returns the resource bundle corresponding to the given locale.
	 */
	public Properties getResourceBundle(Locale locale) {
		clearCacheIncludingAncestors();
		return getMergedProperties(locale).getProperties();
	}

	@Override
	public void afterPropertiesSet() {
		// setBasename("classpath*:/" +
		// environment.getProperty("spring.messages.basename", "messages"));
		setBasenames(Constants.MESSAGE_SOURCE_BASE_NAMES);
		// setDefaultEncoding(environment.getProperty("spring.messages.encoding",
		// encoding.name()));
		// setCacheSeconds(environment.getProperty("spring.messages.cache-seconds",
		// int.class, -1));
		setCacheSeconds(3600);
		// setFallbackToSystemLocale(
		// environment.getProperty("spring.messages.fallback-to-system-locale",
		// boolean.class, true));
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