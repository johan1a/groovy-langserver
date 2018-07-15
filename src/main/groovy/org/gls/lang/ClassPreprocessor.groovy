package org.gls.lang

import groovy.util.logging.Slf4j

@Slf4j
class ClassPreprocessor {

    // Add a log field to the class and return as String
    static String addLogField(String path, String content) {
        List<String> lines = content.split(System.lineSeparator())
        String className = getClassName(path)
        int classDefBracketLine = LocationFinder.findClassStart(lines, className)
        lines[classDefBracketLine] += ";java.util.logging.Logger log = org.slf4j.LoggerFactory.getLogger(this.class)"

        lines.join(System.lineSeparator())
    }

    static String getClassName(String path) {
        path.split('/').last().split('.groovy').first()
    }
}
