package me.chan.texas.issue;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class IssueSystem {

	private static Texas.IssueCallback sIssueCallback = null;

	public static void submit(String moduleName, Throwable throwable) {
		if (sIssueCallback != null) {
			sIssueCallback.onIssueCaught(moduleName, throwable);
		}
	}

	public static void setIssueCallback(Texas.IssueCallback issueCallback) {
		sIssueCallback = issueCallback;
	}
}
