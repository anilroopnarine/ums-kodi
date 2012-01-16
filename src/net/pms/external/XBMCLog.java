package net.pms.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XBMCLog {

	private final static Logger log = LoggerFactory.getLogger(XBMCLog.class);
	private static long startTime;

	public final static void info(Object msg) {
		log.info("<XBMCLog> - " + msg);
	}

	public final static void error(Object msg) {
		log.error("<XBMCLog> - " + msg);
	}

	public final static void logTimeStart() {
		info("time taken: ");
		startTime = System.currentTimeMillis();
	}

	public final static void logTimeStart(Object msg) {
		info("timer start: " + msg);
		startTime = System.currentTimeMillis();
	}

	public final static void logTimeStop() {
		info("time taken: " + (System.currentTimeMillis() - startTime) + "ms");
	}

}
