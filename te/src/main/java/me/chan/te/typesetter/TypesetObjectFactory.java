package me.chan.te.typesetter;

import me.chan.te.misc.ObjectFactory;

// TODO opt
public class TypesetObjectFactory {
	private ObjectFactory<Candidate> mCandidateFactory = new ObjectFactory<>(16);
	private ObjectFactory<BreakPoint> mBreakPointFactory = new ObjectFactory<>(1000);
	private ObjectFactory<Node> mNodeFactory = new ObjectFactory<>(1000);
	private ObjectFactory<Sum> mSumFactory = new ObjectFactory<>(1000);
}
