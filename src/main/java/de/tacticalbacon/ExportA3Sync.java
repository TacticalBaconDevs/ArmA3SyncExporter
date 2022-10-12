package de.tacticalbacon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tacticalbacon.impl.RepoManager;
import de.tacticalbacon.model.Repo;
import fr.soe.a3s.dao.A3SFilesAccessor;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;

public class ExportA3Sync {
	
	private static boolean DEBUG = false;
	private static SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		File a3sFile = null;
		
		try {
			System.out.println("v1.0.3 (12.10.22)");
			
			// parse arguments
			final Map<String, String> arguments = parseArguments(args);
			DEBUG = arguments.containsKey("debug");
			debug("CWD: %s", System.getProperty("user.dir"));
			
			// read input
			final String url = arguments.get("url");
			
			// validate url
			if (url == null || (!url.endsWith("/" + DataAccessConstants.SYNC_FILE_NAME) && !url.endsWith("/" + DataAccessConstants.EVENTS_FILE_NAME)))
				exit("Need a parameter: url to sync or events file", true);
			
			// download file
			debug("Starting download of: %s", url);
			a3sFile = downloadFile(url);
			debug("Download of: '%s' finished!", a3sFile.getName());
			
			// choose modi
			debug("Starting processing...");
			final List<String> ausgabe = new ArrayList<>();
			if (url.endsWith(DataAccessConstants.SYNC_FILE_NAME)) {
				debug("Processing: sync");
				final SyncTreeDirectory sync = (SyncTreeDirectory) A3SFilesAccessor.read(a3sFile);
				debug("Processing: sync -> read done");
				final Repo repo = RepoManager.getRepo(sync.getList());
				debug("Processing: sync -> getRepo done");
				ausgabe.addAll(RepoManager.csvRepo(repo));
				debug("Processing: sync -> csvRepo done");
			} else if (url.endsWith(DataAccessConstants.EVENTS_FILE_NAME)) {
				debug("Processing: events");
				final Events events = (Events) A3SFilesAccessor.read(a3sFile);
				debug("Processing: events -> read done");
				ausgabe.addAll(RepoManager.csvEvents(events.getList()));
				debug("Processing: events -> csvEvents done");
			}
			debug("Process finished: count: %s | length: %s", ausgabe.size(), ausgabe.stream().mapToInt(String::length).sum());
			
			// output modi
			debug("Starting output...");
			if (arguments.containsKey("console")) {
				debug("Output: console");
				consoleOutput(ausgabe);
				debug("Output: console ENDE");
			} else {
				// write file output
				debug("Output: file");
				final String fileName = a3sFile.getName() + ".csv";
				Files.write(Paths.get(fileName), ausgabe, StandardOpenOption.CREATE);
				System.out.println(String.format("Output to file: %s", fileName));
			}
			debug("Output finished!");
		} catch (final Exception e) {
			exit("Fehler: %s\r\n%s", true, e.getLocalizedMessage(), getStacktrace(e));
		} finally {
			// remove temp downloaded files
			if (a3sFile != null)
				try {
					Files.deleteIfExists(a3sFile.toPath());
				} catch (final IOException e) {
					exit("Fehler2: %s\r\n%s", true, e.getLocalizedMessage(), getStacktrace(e));
				}
			
			debug("Execution time: %sms", System.currentTimeMillis() - startTime);
		}
	}
	
	private static void exit(String msg, String... args) {
		System.out.println(String.format(msg, (Object[]) args));
		System.exit(-1);
	}
	
	private static void exit(String msg, boolean showHelp, String... args) {
		if (showHelp)
			help();
		
		exit(msg, args);
	}
	
	private static void debug(String msg, Object... args) {
		if (DEBUG)
			System.out.println(String.format("[DEBUG][%s] %s", SDF.format(new Date()), String.format(msg, args)));
	}
	
	private static Map<String, String> parseArguments(String[] args) {
		final Map<String, String> results = new HashMap<>();
		
		for (final String arg : args) {
			if (arg.contains("?") || arg.contains("help") || "-h".equalsIgnoreCase(arg) || "/h".equalsIgnoreCase(arg)) {
				help();
				System.exit(-1);
			} else if (arg.startsWith("-")) {
				results.put(arg.substring(1).toLowerCase(), "true");
			} else if (arg.startsWith("http")) {
				results.put("url", arg);
			}
		}
		
		return results;
	}
	
	private static void help() {
		System.out.println(String.format("java -jar ArmA3SyncExporter.jar [options] <URL>\r\n"
				+ "Options:\r\n"
				+ "\t-h / -? / -help: shows help\r\n"
				+ "\t-console: output option as console output\r\n"
				+ "\t-debug: more informations\r\n"
				+ "URL:\r\n"
				+ "\thttp://***/sync => ADDON|FILE1|FILE2|...\r\n"
				+ "\thttp://***/events => EVENT|ADDON1|ADDON2|..."));
	}
	
	private static File downloadFile(String url) {
		final String fileName = getEndName(url);
		final Path pathName = Paths.get(fileName);
		try (InputStream in = new URL(url).openStream()) {
			Files.copy(in, pathName, StandardCopyOption.REPLACE_EXISTING);
		} catch (final Exception e) {
			exit("Error on download: %s", url);
		}
		
		final File syncFile = pathName.toFile();
		if (syncFile == null || !syncFile.exists())
			exit("Download file is not there: %s", url);
		
		return syncFile;
	}
	
	private static String getEndName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	private static void consoleOutput(List<String> ausgabe) {
		System.out.println("-----------------START-----------------");
		for (final String line : ausgabe)
			System.out.println(line);
		System.out.println("-----------------END-----------------");
	}
	
	private static String getStacktrace(Exception e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
