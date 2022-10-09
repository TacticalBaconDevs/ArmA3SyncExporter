package de.tacticalbacon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Repo {
	
	private List<Addon> addons = new ArrayList<>();
	
	public List<Addon> getAddons() {
		return addons;
	}
	
	public void setAddons(List<Addon> addons) {
		this.addons = addons;
	}
	
	public void addAddon(Addon addon) {
		if (addon == null)
			return;
		
		addons.add(addon);
	}
	
	@Override
	public String toString() {
		return String.format("Repo [addons=%s]", addons);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(addons);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		final Repo other = (Repo) obj;
		return Objects.equals(addons, other.addons);
	}
	
}
