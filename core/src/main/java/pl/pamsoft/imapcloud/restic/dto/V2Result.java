package pl.pamsoft.imapcloud.restic.dto;

public class V2Result extends V1Result{

	private long size;

	public V2Result(String name, long size) {
		super(name);
		this.size = size;
	}

	public long getSize() {
		return size;
	}
}
