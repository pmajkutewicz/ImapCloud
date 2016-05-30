package pl.pamsoft.imapcloud.utils;

import lombok.Getter;

import java.util.Map;

@Getter
public class GitRepositoryState {
	private String tags;                    // =${git.tags} // comma separated tag names
	private String branch;                  // =${git.branch}
	private String dirty;                   // =${git.dirty}
	private String remoteOriginUrl;         // =${git.remote.origin.url}

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

	public GitRepositoryState(Map properties) {
		this.tags = String.valueOf(properties.get("git.tags"));
		this.branch = String.valueOf(properties.get("git.branch"));
		this.dirty = String.valueOf(properties.get("git.dirty"));
		this.remoteOriginUrl = String.valueOf(properties.get("git.remote.origin.url"));

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
}
