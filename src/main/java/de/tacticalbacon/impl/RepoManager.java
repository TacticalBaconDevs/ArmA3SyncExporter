package de.tacticalbacon.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tacticalbacon.model.Addon;
import de.tacticalbacon.model.File;
import de.tacticalbacon.model.Repo;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.repository.Event;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;

public class RepoManager {
	
	public static Repo getRepo(List<SyncTreeNode> treeNodeList) {
		final Repo repo = new Repo();
		
		treeNodeList.stream()
				.filter(x -> x instanceof SyncTreeDirectory)
				.map(x -> (SyncTreeDirectory) x)
				.filter(SyncTreeDirectory::isMarkAsAddon)
				.forEach(x -> repo.addAddon(getAddon(x)));
		
		return repo;
	}
	
	public static Addon getAddon(SyncTreeDirectory treeDirectory) {
		final Addon addon = new Addon(treeDirectory);
		
		final SyncTreeDirectory addonDir = (SyncTreeDirectory) treeDirectory.getList().stream().filter(x -> "addons".equalsIgnoreCase(x.getName())).findFirst().orElse(null);
		if (addonDir == null)
			return null;
		
		addonDir.getList().stream()
				.filter(x -> x instanceof SyncTreeLeaf)
				.map(x -> (SyncTreeLeaf) x)
				.filter(x -> x.getName().endsWith(DataAccessConstants.PBO_EXTENSION))
				.forEach(x -> addon.addFile(new File(x)));
		
		return addon;
	}
	
	/**
	 * Output: ADDONNAME#FILE1|FILE2|FILE3
	 */
	public static List<String> csvRepo(Repo repo) {
		final List<String> result = new ArrayList<>();
		
		for (final Addon addon : repo.getAddons())
			result.add(String.format("%s#%s", addon.getAddonName(), addon.getFiles().stream().map(File::getFileName).collect(Collectors.joining("|"))));
		
		return result;
	}
	
	/**
	 * Output: EVENT#ADDON1|ADDON2|ADDON3
	 */
	public static List<String> csvEvents(List<Event> events) {
		final List<String> result = new ArrayList<>();
		
		for (final Event event : events)
			result.add(String.format("%s#%s", event.getName(), String.join("|", event.getAddonNames().keySet())));
		
		return result;
	}
	
}
