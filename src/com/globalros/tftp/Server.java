/**
 * (c) Melexis Telecom and or Remote Operating Services B.V.
 * <p>
 * Distributable under LGPL license
 * See terms of license at gnu.org
 */
package com.globalros.tftp;

import java.net.InetAddress;

import com.globalros.tftp.server.EventListener;
import com.globalros.tftp.server.TFTPServer;
import com.globalros.tftp.common.VirtualFileSystem;

import org.apache.log4j.Logger;

/**
 * @author marco
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Server implements EventListener {
    /**
     * logger
     */
    private Logger log = Logger.getLogger(Server.class.getName());


    private TFTPServer tftpServer;

    /**
     * Constructor for Server.
     */
    public Server(String directory, InetAddress inetAddress) {
        VirtualFileSystem vfs = new FileSystem(directory);
        tftpServer = new TFTPServer(vfs, this, inetAddress);
        tftpServer.setPoolSize(2);
        tftpServer.setPort(1069);
    }

    public void connect() throws Exception {
        if (tftpServer == null) return;
        tftpServer.start();
    }

    public void disconnect() {
        if (tftpServer == null) return;
        tftpServer.stop();
    }

    public void onAfterDownload(InetAddress a, int p, String fileName, boolean ok) {
        if (ok) log.debug("Send " + fileName + " sucessfully to client: " + a.getHostAddress() + " port: " + p);
        else log.debug("Send " + fileName + " file not sucessfully to client: " + a.getHostAddress() + " port: " + p);
    }

    public void onAfterUpload(InetAddress a, int p, String fileName, boolean ok) {
        if (ok) log.debug("received " + fileName + " sucessfully from client: " + a.getHostAddress() + " port: " + p);
        else
            log.debug("received " + fileName + " file not sucessfully from client: " + a.getHostAddress() + " port: " + p);
    }
}
