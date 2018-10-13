import ch.qos.logback.classic.filter.ThresholdFilter
import org.gls.ConfigService

import java.nio.file.Paths


URI currentDir = new URI(System.getProperty("user.dir"))
String logDirectory = Paths.get(new ConfigService().getConfigDir(currentDir))
def appenders = []

// STDOUT
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{yyyy-MM-dd HH:mm:ss}] [%-5level] [%-48logger{48}] - %msg%n"
    }
}

appenders.add('STDOUT')


def filePattern = "[%d{yyyy-MM-dd HH:mm:ss}] [%-5level] [%-22.-22thread] [%-48logger{48}] - %msg%n"

appender('FILE', RollingFileAppender) {
    file = "${logDirectory}/groovy-langserver.log"

    filter(ThresholdFilter) {
        level = INFO
    }

    encoder(PatternLayoutEncoder) {
        pattern = filePattern
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logDirectory}/groovy-langserver.%d.log"
    }
}
appenders.add("FILE")

appender('FILE-DEBUG', RollingFileAppender) {
    file = "${logDirectory}/groovy-langserver-debug.log"

    encoder(PatternLayoutEncoder) {
        pattern = filePattern
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logDirectory}/groovy-langserver-debug.%d.log"
    }
}
appenders.add("FILE-DEBUG")

root(INFO, appenders)

logger('org', DEBUG)
logger("StackTrace", ERROR)