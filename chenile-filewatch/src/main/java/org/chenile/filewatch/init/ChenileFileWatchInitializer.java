package org.chenile.filewatch.init;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.chenile.core.init.BaseInitializer;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.filewatch.model.FileWatchDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

/**
 * Picks up all the file watch resources and registers all of them in
 * {@link ChenileConfiguration} ChenileConfiguration provides for extension
 * points for registering new type of resources. This class uses the extension
 * point to register the file watches
 * 
 * @author Raja Shankar Kolluru
 *
 */

public class ChenileFileWatchInitializer
		extends
			BaseInitializer<FileWatchDefinition> {

	private static final Logger LOG = LoggerFactory
			.getLogger(ChenileFileWatchInitializer.class);

	@Value("${chenile.file.watch.source.folder}")
	private String srcFolder;

	@Value("${chenile.file.watch.dest.folder}")
	private String destFolder;

	public ChenileFileWatchInitializer(Resource[] resources) {
		super(resources);
	}

	protected void registerModelInChenile(FileWatchDefinition fwd) {
		Map<String, FileWatchDefinition> map = getExtensionMap(
				FileWatchDefinition.EXTENSION);
		map.put(fwd.getFileWatchId(), fwd);

		Path srcPath = Paths.get(System.getProperty("java.io.tmpdir") + ""
				+ File.separator + "src");
		Path destPath = Paths.get(System.getProperty("java.io.tmpdir") + ""
				+ File.separator + "dest");

		if (Files.exists(srcPath)) {
			if (srcPath.toFile().mkdir()) {
				LOG.info("src dir created..");
			}
		}
		if (Files.exists(destPath)) {
			if (destPath.toFile().mkdir()) {
				LOG.info("dest dir created..");
			}
		}

		Path spath = Paths.get(
				srcFolder + "" + File.separator + "" + fwd.getDirToWatch());
		Path dpath = Paths.get(
				destFolder + "" + File.separator + "" + fwd.getDirToWatch());

		if (!Files.exists(spath)) {
			try {
				Files.createDirectories(spath);
			} catch (IOException e) {
				LOG.error("failed to create dr " + spath);
			}
		}

		if (!Files.exists(dpath)) {
			try {
				Files.createDirectories(dpath);
			} catch (IOException e) {
				LOG.error("failed to create dr " + dpath);
			}
		}
	}

	@Override
	protected Class<FileWatchDefinition> getModelType() {
		return FileWatchDefinition.class;
	}
}
