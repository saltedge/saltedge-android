/*
Copyright © 2018 Salt Edge. https://saltedge.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.saltedge.sdk.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

public class SEErrorTools {

    public final static String ERROR_MSG_INVALID_HPKP_DATA = "Invalid HPKP data";
    public final static String ERROR_MSG_CANT_GET_HPKP_DATA = "Can not get HPKP data";
    public final static String ERROR_MSG_SSL_CERT_FAIL = "SSL Handshake Error!";
    public final static String ERROR_MSG_HOST_UNREACHABLE = "No Internet connection.Please try again later.";

    /**
     * Returns localized message from exception or custom message
     *
     * @param t Throwable exception
     * @return string value of error message
     */
    public static String processConnectionError(Throwable t) {
        if (isNoConnectionException(t)) return ERROR_MSG_HOST_UNREACHABLE;
        else if (isSSLException(t)) return ERROR_MSG_SSL_CERT_FAIL;
        else return t.getLocalizedMessage();
    }

    private static Boolean isNoConnectionException(Throwable t) {
        return (t instanceof ConnectException)
                || (t instanceof NoRouteToHostException)
                || (t instanceof SocketTimeoutException)
                || (t instanceof SocketException)
                || (t instanceof InterruptedIOException)
                || (t instanceof UnknownHostException)
                || (t instanceof IOException);
    }

    private static Boolean isSSLException(Throwable t) {
        return (t instanceof SSLPeerUnverifiedException)
                || (t instanceof SSLHandshakeException)
                || (t instanceof SSLException);
    }
}
