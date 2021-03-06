/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gflogger.perftest;

import org.gflogger.GFLog;
import org.gflogger.GFLogFactory;
import org.gflogger.LogLevel;
import org.gflogger.LoggerService;
import org.gflogger.PatternLayout;
import org.gflogger.appender.AppenderFactory;
import org.gflogger.appender.FileAppenderFactory;


/**
 * @author Vladimir Dolzhenko, vladimir.dolzhenko@gmail.com
 */
public abstract class AbstractLoggerExample extends AbstractExample {

	protected GFLog log;
	protected LoggerService service;
	protected final StringBuilder builder = new StringBuilder(1 << 10);

	@Override
	protected void initLogger() throws Throwable {
		service = createLoggerImpl();

		GFLogFactory.init(service);

		this.log = GFLogFactory.getLog("com.db.fxpricing.Logger");
	}

	protected AppenderFactory[] createAppenderFactories(){
		final FileAppenderFactory fileAppender = new FileAppenderFactory();
		fileAppender.setLogLevel(LogLevel.INFO);
		fileAppender.setFileName(fileAppenderFileName());
		fileAppender.setAppend(false);
		fileAppender.setImmediateFlush(false);
		fileAppender.setMultibyte(true);
		fileAppender.setLayout(new PatternLayout("%d{HH:mm:ss,SSS zzz} %p %m [%c{2}] [%t]%n"));

		return new AppenderFactory[]{fileAppender};
	}

	protected abstract String fileAppenderFileName();

	protected abstract LoggerService createLoggerImpl() throws Throwable;

	@Override
	protected void stop() throws Throwable {
		GFLogFactory.stop();
	}

	@Override
	protected void logDebugTestMessage(int i) {
		log.debug().append("test").append(i).commit();
	}

	@Override
	protected void logMessage(String msg, int j) {
		log.info().append(msg).append(j).commit();
	}

	@Override
	protected void logFinalMessage(final int count, final long t, final long e, final long gcTime) {
		log.info().append("final count: ").
			append(count).append(" time: ").append((e - t) / 1e6, 3).append(" ms, gc: ").append(gcTime).append(" ms").
			commit();
		builder.setLength(0);
		builder.append("final count: ").append(count).append(" time: ").append(((int)((e-t)/1000)) / 1e3);
		System.out.println(builder.toString());
	}

	@Override
	protected void logTotalMessage(final long start) {
		log.info().append("total time:").append(System.currentTimeMillis() - start).append(" ms.").commit();
	}
}
