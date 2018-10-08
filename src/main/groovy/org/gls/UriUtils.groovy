package org.gls

class UriUtils {

    static URI appendURI(URI first, URI second) {
        return appendURI(first, second.toString())
    }

    static URI appendURI(URI uri, String path) {
        String uriString = uri.toString()
        if (uriString.endsWith("/") && path.startsWith("/")) {
            return new URI(uriString + path.substring(1))
        } else if (!uriString.endsWith("/") && !path.startsWith("/")) {
            return new URI(uriString + "/" + path)
        }
        return new URI(uriString + path)
    }
}
