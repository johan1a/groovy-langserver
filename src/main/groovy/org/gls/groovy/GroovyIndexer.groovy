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
        long start = System.currentTimeMillis()
        File basedir = new File(new URL(rootUri).toURI())
        log.info "baseDir: ${basedir}"
        basedir.eachFileRecurse {
          if (it.name =~ /.*\.groovy/) {
            log.info it.name
          }
        }
        long elapsed = System.currentTimeMillis() - start
        log.info("Elapsed: ${elapsed / 1000}s")
      } catch(Exception e) {
        log.error("error", e)
      }
    }

}
