package pl.pamsoft.imapcloud.dto;

import java.util.Map;

public class GitRepositoryState {
	private String tags;                    // =${git.tags} // comma separated tag names
	private String branch;                  // =${git.branch}
	private String dirty;                   // =${git.dirty}

	private String commitId;                // =${git.commit.id.full} OR ${git.commit.id}
	private String commitIdAbbrev;          // =${git.commit.id.abbrev}
	private String describe;                // =${git.commit.id.describe}
	private String describeShort;           // =${git.commit.id.describe-short}
	private String commitUserName;          // =${git.commit.user.name}
	private String commitUserEmail;         // =${git.commit.user.email}
	private String commitMessageFull;       // =${git.commit.message.full}
	private String commitMessageShort;      // =${git.commit.message.short}
	private String commitTime;              // =${git.commit.time}
	private String closestTagName;          // =${git.closest.tag.name}
	private String closestTagCommitCount;   // =${git.closest.tag.commit.count}

	private String buildUserName;           // =${git.build.user.name}
	private String buildUserEmail;          // =${git.build.user.email}
	private String buildTime;               // =${git.build.time}
	private String buildHost;               // =${git.build.host}
	private String buildVersion;             // =${git.build.version}

	public GitRepositoryState(Map<Object, Object> properties) {
		this.tags = String.valueOf(properties.get("git.tags"));
		this.branch = String.valueOf(properties.get("git.branch"));
		this.dirty = String.valueOf(properties.get("git.dirty"));

		this.commitId = String.valueOf(properties.get("git.commit.id"));
		this.commitIdAbbrev = String.valueOf(properties.get("git.commit.id.abbrev"));
		this.describe = String.valueOf(properties.get("git.commit.id.describe"));
		this.describeShort = String.valueOf(properties.get("git.commit.id.describe-short"));
		this.commitUserName = String.valueOf(properties.get("git.commit.user.name"));
		this.commitUserEmail = String.valueOf(properties.get("git.commit.user.email"));
		this.commitMessageFull = String.valueOf(properties.get("git.commit.message.full"));
		this.commitMessageShort = String.valueOf(properties.get("git.commit.message.short"));
		this.commitTime = String.valueOf(properties.get("git.commit.time"));
		this.closestTagName = String.valueOf(properties.get("git.closest.tag.name"));
		this.closestTagCommitCount = String.valueOf(properties.get("git.closest.tag.commit.count"));

		this.buildUserName = String.valueOf(properties.get("git.build.user.name"));
		this.buildUserEmail = String.valueOf(properties.get("git.build.user.email"));
		this.buildTime = String.valueOf(properties.get("git.build.time"));
		this.buildHost = String.valueOf(properties.get("git.build.host"));
		this.buildVersion = String.valueOf(properties.get("git.build.version"));
	}

	public GitRepositoryState() {
	}

	public String prettyPrint() {
		String newLine = System.getProperty("line.separator");
		final StringBuilder sb = new StringBuilder();
		sb.append("tags=").append(tags).append(newLine);
		sb.append("branch=").append(branch).append(newLine);
		sb.append("dirty=").append(dirty).append(newLine);
		sb.append("commitId=").append(commitId).append(newLine);
		sb.append("commitIdAbbrev=").append(commitIdAbbrev).append(newLine);
		sb.append("describe=").append(describe).append(newLine);
		sb.append("describeShort=").append(describeShort).append(newLine);
		sb.append("commitUserName=").append(commitUserName).append(newLine);
		sb.append("commitUserEmail=").append(commitUserEmail).append(newLine);
		sb.append("commitMessageFull=").append(commitMessageFull).append(newLine);
		sb.append("commitMessageShort=").append(commitMessageShort).append(newLine);
		sb.append("commitTime=").append(commitTime).append(newLine);
		sb.append("closestTagName=").append(closestTagName).append(newLine);
		sb.append("closestTagCommitCount=").append(closestTagCommitCount).append(newLine);
		sb.append("buildUserName=").append(buildUserName).append(newLine);
		sb.append("buildUserEmail=").append(buildUserEmail).append(newLine);
		sb.append("buildTime=").append(buildTime).append(newLine);
		sb.append("buildHost=").append(buildHost).append(newLine);
		sb.append("buildVersion=").append(buildVersion).append(newLine);
		return sb.toString();
	}

	public String getTags() {
		return this.tags;
	}

	public String getBranch() {
		return this.branch;
	}

	public String getDirty() {
		return this.dirty;
	}

	public String getCommitId() {
		return this.commitId;
	}

	public String getCommitIdAbbrev() {
		return this.commitIdAbbrev;
	}

	public String getDescribe() {
		return this.describe;
	}

	public String getDescribeShort() {
		return this.describeShort;
	}

	public String getCommitUserName() {
		return this.commitUserName;
	}

	public String getCommitUserEmail() {
		return this.commitUserEmail;
	}

	public String getCommitMessageFull() {
		return this.commitMessageFull;
	}

	public String getCommitMessageShort() {
		return this.commitMessageShort;
	}

	public String getCommitTime() {
		return this.commitTime;
	}

	public String getClosestTagName() {
		return this.closestTagName;
	}

	public String getClosestTagCommitCount() {
		return this.closestTagCommitCount;
	}

	public String getBuildUserName() {
		return this.buildUserName;
	}

	public String getBuildUserEmail() {
		return this.buildUserEmail;
	}

	public String getBuildTime() {
		return this.buildTime;
	}

	public String getBuildHost() {
		return this.buildHost;
	}

	public String getBuildVersion() {
		return this.buildVersion;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setDirty(String dirty) {
		this.dirty = dirty;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public void setCommitIdAbbrev(String commitIdAbbrev) {
		this.commitIdAbbrev = commitIdAbbrev;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public void setDescribeShort(String describeShort) {
		this.describeShort = describeShort;
	}

	public void setCommitUserName(String commitUserName) {
		this.commitUserName = commitUserName;
	}

	public void setCommitUserEmail(String commitUserEmail) {
		this.commitUserEmail = commitUserEmail;
	}

	public void setCommitMessageFull(String commitMessageFull) {
		this.commitMessageFull = commitMessageFull;
	}

	public void setCommitMessageShort(String commitMessageShort) {
		this.commitMessageShort = commitMessageShort;
	}

	public void setCommitTime(String commitTime) {
		this.commitTime = commitTime;
	}

	public void setClosestTagName(String closestTagName) {
		this.closestTagName = closestTagName;
	}

	public void setClosestTagCommitCount(String closestTagCommitCount) {
		this.closestTagCommitCount = closestTagCommitCount;
	}

	public void setBuildUserName(String buildUserName) {
		this.buildUserName = buildUserName;
	}

	public void setBuildUserEmail(String buildUserEmail) {
		this.buildUserEmail = buildUserEmail;
	}

	public void setBuildTime(String buildTime) {
		this.buildTime = buildTime;
	}

	public void setBuildHost(String buildHost) {
		this.buildHost = buildHost;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
}
