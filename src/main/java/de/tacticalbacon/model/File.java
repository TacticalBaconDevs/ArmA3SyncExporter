package de.tacticalbacon.model;

import java.util.Objects;

import fr.soe.a3s.domain.repository.SyncTreeLeaf;

public class File {
	
	private String fileName;
	
	public File(SyncTreeLeaf treeLeaf) {
		fileName = treeLeaf.getName();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return String.format("File [fileName=%s]", fileName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fileName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		final File other = (File) obj;
		return Objects.equals(fileName, other.fileName);
	}
	
}
