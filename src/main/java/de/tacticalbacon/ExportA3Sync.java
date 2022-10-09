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
import java.util.ArrayList;
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
	
	public static void main(String[] args) throws IOException {
		File a3sFile = null;
		
		try {
			System.out.println("v1.0.1 (09.10.22)");
			
			// parse arguments
			final Map<String, String> arguments = parseArguments(args);
			
			// read input
			final String url = arguments.get("url");
			
			// validate url
			if (url == null || (!url.endsWith("/" + DataAccessConstants.SYNC_FILE_NAME) && !url.endsWith("/" + DataAccessConstants.EVENTS_FILE_NAME)))
				exit("Need a parameter: url to sync or events file", true);
			
			// download file
			a3sFile = downloadFile(url);
			
			// choose modi
			final List<String> ausgabe = new ArrayList<>();
			if (url.endsWith(DataAccessConstants.SYNC_FILE_NAME)) {
				final SyncTreeDirectory sync = (SyncTreeDirectory) A3SFilesAccessor.read(a3sFile);
				final Repo repo = RepoManager.getRepo(sync.getList());
				ausgabe.addAll(RepoManager.csvRepo(repo));
			} else if (url.endsWith(DataAccessConstants.EVENTS_FILE_NAME)) {
				final Events events = (Events) A3SFilesAccessor.read(a3sFile);
				ausgabe.addAll(RepoManager.csvEvents(events.getList()));
			}
			
			// output modi
			if (arguments.containsKey("console")) {
				consoleOutput(ausgabe);
			} else {
				// write file output
				final String fileName = a3sFile.getName() + ".csv";
				Files.write(Paths.get(fileName), ausgabe, StandardOpenOption.CREATE);
				System.out.println(String.format("Output to file: %s", fileName));
			}
		} catch (final Exception e) {
			exit("Fehler: %s\r\n%s", true, e.getLocalizedMessage(), getStacktrace(e));
		} finally {
			// remove temp downloaded files
			if (a3sFile != null)
				Files.deleteIfExists(a3sFile.toPath());
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
		System.out.println(String.format("-----------------START-----------------\r\n%s\r\n-----------------END-----------------", String.join("\r\n", ausgabe)));
	}
	
	private static String getStacktrace(Exception e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
