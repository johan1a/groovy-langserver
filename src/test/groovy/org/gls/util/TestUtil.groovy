package org.gls.util

import java.nio.file.Paths

class TestUtil {
    static URI uri(String path) {
        return Paths.get(path).toUri()
    }
}
