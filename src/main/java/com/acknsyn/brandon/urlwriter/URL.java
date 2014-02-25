package com.acknsyn.brandon.urlwriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Delegating URL class to allow for mocking
 */
public class URL {
    private java.net.URL delegate;

    public URL(String url) throws MalformedURLException {
        delegate = new java.net.URL(url);
    }

    public String getQuery() {
        return delegate.getQuery();
    }

    public boolean sameFile(java.net.URL other) {
        return delegate.sameFile(other);
    }

    /**
     * Constructs a string representation of this <code>URL</code>. The
     * string is created by calling the <code>toExternalForm</code>
     * method of the stream protocol handler for this object.
     *
     * @return a string representation of this object.
     * @see     java.net.URL#URL(String, String, int,
     *                  String)
     * @see     java.net.URLStreamHandler#toExternalForm(java.net.URL)
     */
    @Override
    public String toString() {
        return delegate.toString();
    }

    public static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        java.net.URL.setURLStreamHandlerFactory(fac);
    }

    public Object getContent(Class[] classes) throws IOException {
        return delegate.getContent(classes);
    }

    public String getRef() {
        return delegate.getRef();
    }

    /**
     * Compares this URL for equality with another object.<p>
     *
     * If the given object is not a URL then this method immediately returns
     * <code>false</code>.<p>
     *
     * Two URL objects are equal if they have the same protocol, reference
     * equivalent hosts, have the same port number on the host, and the same
     * file and fragment of the file.<p>
     *
     * Two hosts are considered equivalent if both host names can be resolved
     * into the same IP addresses; else if either host name can't be
     * resolved, the host names must be equal without regard to case; or both
     * host names equal to null.<p>
     *
     * Since hosts comparison requires name resolution, this operation is a
     * blocking operation. <p>
     *
     * Note: The defined behavior for <code>equals</code> is known to
     * be inconsistent with virtual hosting in HTTP.
     *
     *
     * @param   obj   the URL to compare against.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    /**
     * Creates an integer suitable for hash table indexing.<p>
     *
     * The hash code is based upon all the URL components relevant for URL
     * comparison. As such, this operation is a blocking operation.<p>
     *
     * @return a hash code for this <code>URL</code>.
     */
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public Object getContent() throws IOException {
        return delegate.getContent();
    }

    public String getPath() {
        return delegate.getPath();
    }

    public String getAuthority() {
        return delegate.getAuthority();
    }

    public String getUserInfo() {
        return delegate.getUserInfo();
    }

    public int getDefaultPort() {
        return delegate.getDefaultPort();
    }

    public InputStream openStream() throws IOException {
        return delegate.openStream();
    }

    public int getPort() {
        return delegate.getPort();
    }

    public URLConnection openConnection(Proxy proxy) throws IOException {
        return delegate.openConnection(proxy);
    }

    public String getProtocol() {
        return delegate.getProtocol();
    }

    public String getFile() {
        return delegate.getFile();
    }

    public URLConnection openConnection() throws IOException {
        return delegate.openConnection();
    }

    public String getHost() {
        return delegate.getHost();
    }

    public URI toURI() throws URISyntaxException {
        return delegate.toURI();
    }

    public String toExternalForm() {
        return delegate.toExternalForm();
    }
}
