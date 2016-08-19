package com.akmans.trade.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.exception.TradeException;
import com.akmans.trade.core.service.MessageService;

@Component
public class FileUtil {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FileUtil.class);

	@Autowired
	private MessageService messageService;

	public String download(String sourceUrl, String targetFile) throws TradeException {
		Path targetPath = null;
		try {
			URL url = new URL(sourceUrl);
//			String fileName = url.getFile();
//			if (fileName != null && fileName.lastIndexOf('/') > 0) {
//				fileName = fileName.substring(fileName.lastIndexOf('/'));
//			}
			targetPath = new File(targetFile).toPath();
			Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (MalformedURLException mfe) {
			logger.error("The URL is not correct!", mfe);
			throw new TradeException(messageService.getMessage("core.util.file.download.url.invalid", sourceUrl));
		} catch (IOException ie) {
			logger.error("Error copying file.", ie);
			throw new TradeException(
					messageService.getMessage("core.util.file.download.copy.error", ie.getMessage()));
		}
		return targetPath.toString();
	}
}
