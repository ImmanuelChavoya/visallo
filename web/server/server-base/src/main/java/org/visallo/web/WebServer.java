package org.visallo.web;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import org.visallo.core.cmdline.CommandLineTool;

import java.io.File;

public abstract class WebServer extends CommandLineTool {
    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final int DEFAULT_HTTPS_SERVER_PORT = 8443;
    public static final String DEFAULT_CONTEXT_PATH = "/";
    public static final int DEFAULT_SESSION_TIMEOUT = 30;

    @Parameter(names = {"--port"}, arity = 1, description = "The port to run the HTTP connector on")
    private int httpPort = DEFAULT_SERVER_PORT;

    @Parameter(names = {"--httpsPort"}, arity = 1, description = "The port to run the HTTPS connector on")
    private int httpsPort = DEFAULT_HTTPS_SERVER_PORT;

    @Parameter(names = {"--keyStorePath"}, required = true, arity = 1, converter = FileConverter.class, description = "Path to the JKS keystore used for SSL")
    private File keyStorePath;

    @Parameter(names = {"--keyStorePassword"}, required = true, arity = 1, description = "JKS keystore password")
    private String keyStorePassword;

    @Parameter(names = {"--trustStorePath"}, arity = 1, converter = FileConverter.class, description = "Path to the JKS truststore used for SSL")
    private File trustStorePath;

    @Parameter(names = {"--trustStorePassword"}, arity = 1, description = "JKS truststore password")
    private String trustStorePassword;

    @Parameter(names = {"--requireClientCert"}, description = "require client certificate")
    private boolean requireClientCert = false;

    @Parameter(names = {"--webAppDir"}, required = true, arity = 1, converter = FileConverter.class, description = "Path to the webapp directory")
    private File webAppDir;

    @Parameter(names = {"--contextPath"}, arity = 1, description = "Context path for the webapp")
    private String contextPath = DEFAULT_CONTEXT_PATH;

    @Parameter(names = {"--sessionTimeout"}, arity = 1, description = "number of minutes before idle sessions expire")
    private int sessionTimeout = DEFAULT_SESSION_TIMEOUT;

    public int getHttpPort() {
        return httpPort;
    }

    public int getHttpsPort() {
        return httpsPort;
    }

    public File getKeyStorePath() {
        return keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public File getTrustStorePath() {
        if (trustStorePath != null) {
            return trustStorePath;
        } else {
            return keyStorePath;
        }
    }

    public String getTrustStorePassword() {
        if (trustStorePassword != null) {
            return trustStorePassword;
        } else {
            return keyStorePassword;
        }
    }

    public boolean getRequireClientCert() {
        return requireClientCert;
    }

    public String getContextPath() {
        return contextPath;
    }

    public File getWebAppDir() {
        return webAppDir;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }
}
