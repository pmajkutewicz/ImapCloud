package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class GetHomeDirResponse extends AbstractResponse {
	private String homeDir;

	public GetHomeDirResponse(String homeDir) {
		this.homeDir = homeDir;
	}

	public GetHomeDirResponse() {
	}

	public String getHomeDir() {
		return this.homeDir;
	}

	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}
}
