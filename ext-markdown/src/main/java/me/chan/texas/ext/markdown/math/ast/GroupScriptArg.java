package me.chan.texas.ext.markdown.math.ast;

public class GroupScriptArg implements ScriptArg {
	public GroupAtom content;

	public GroupScriptArg(GroupAtom content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "{" + content.toString() + "}";
	}
}
