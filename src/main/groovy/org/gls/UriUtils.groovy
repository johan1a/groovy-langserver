package org.gls

class UriUtils {

    private static final String SLASH = "/"

    static URI appendURI(URI first, URI second) {
        return appendURI(first, second.toString())
    }

    static URI appendURI(URI uri, String path) {
        String uriString = uri
        if (uriString.endsWith(SLASH) && path.startsWith(SLASH)) {
            return new URI(uriString + path.substring(1))
        } else if (!uriString.endsWith(SLASH) && !path.startsWith(SLASH)) {
            return new URI(uriString + SLASH + path)
        }
        return new URI(uriString + path)
    }
}
