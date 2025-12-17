package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	public final String name;
	public FunctionCallAtom(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return  "\\" + name;
	}
}
