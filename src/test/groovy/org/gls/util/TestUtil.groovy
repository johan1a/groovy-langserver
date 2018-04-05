package org.gls.util
import java.nio.file.Paths

class TestUtil {
    static List<URI> uriList(String path) {
        try {
            return [Paths.get(path).toUri()]
        } catch (Exception e) {
            return []
        }
    }
}
