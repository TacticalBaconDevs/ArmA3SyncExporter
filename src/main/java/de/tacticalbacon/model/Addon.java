package de.tacticalbacon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.soe.a3s.domain.repository.SyncTreeDirectory;

public class Addon {
	
	private String addonName;
	private List<File> files = new ArrayList<>();
	
	public Addon(SyncTreeDirectory treeDirectory) {
		this.addonName = treeDirectory.getName();
	}
	
	public String getAddonName() {
		return addonName;
	}
	
	public void setAddonName(String addonName) {
		this.addonName = addonName;
	}
	
	public List<File> getFiles() {
		return files;
	}
	
	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	public void addFile(File file) {
		if (file == null)
			return;
		
		files.add(file);
	}
	
	@Override
	public String toString() {
		return String.format("Addon [addonName=%s, files=%s]", addonName, files);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(addonName, files);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		final Addon other = (Addon) obj;
		return Objects.equals(addonName, other.addonName) && Objects.equals(files, other.files);
	}
	
}
