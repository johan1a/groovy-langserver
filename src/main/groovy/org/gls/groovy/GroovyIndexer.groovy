package org.gls.groovy

import groovy.util.logging.Slf4j
import groovy.transform.TypeChecked

@Slf4j
@TypeChecked
class GroovyIndexer {

  String rootUri

    def startIndexing() {
      try {
        log.info "Indexing..."
          File basedir = new File(new URL(rootUri).toURI())
          log.info "baseDir: ${basedir}"
          basedir.eachFileRecurse {
            if (it.name =~ /.*\.groovy/) {
              log.info it.name
            }
          }
      } catch(Exception e) {
        log.error("error", e)
      }
    }

}
