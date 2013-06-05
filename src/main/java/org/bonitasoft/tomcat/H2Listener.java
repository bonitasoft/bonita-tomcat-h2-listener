/**                                                                                                                   
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.tomcat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.h2.tools.Server;


public class H2Listener implements LifecycleListener {

    
    private final static Logger log = Logger.getLogger(H2Listener.class.getName());

    private Server h2Server;

    private boolean start = true;
    
    private boolean trace;
    
    private Integer tcpPort;

    private boolean tcpAllowOthers;
    
    private String baseDir;

    private boolean ifExists;

    private boolean web;

    private Integer webPort;
    
    public void setStart(boolean start) {
        this.start = start;
    }
    
    public void setTrace(boolean trace) {
        this.trace = trace;
    }
    
    public void setTcpPort(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }
    
    public void setTcpAllowOthers(boolean tcpAllowOthers) {
        this.tcpAllowOthers = tcpAllowOthers;
    }
    
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }
    
    public void setWeb(boolean web) {
        this.web = web;
    }
    
    public void setWebPort(Integer webPort) {
        this.webPort = webPort;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        
        if (Lifecycle.BEFORE_START_EVENT.equals(event.getType()) && start) {
            List<String> h2Args = buildH2Arguments();
            log.info("Starting H2 server : " + h2Args);
            try {
                h2Server = Server.createTcpServer(h2Args.toArray(new String[] {}));

                h2Server.setOut(System.err);
                h2Server.start();
            } catch (SQLException sqe) {
                throw new RuntimeException(sqe);
            }
        } 

        if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
            if (h2Server != null) {
                log.info("Stopping H2 server.");
                h2Server.stop();
            }
        }
        
    }

    private List<String> buildH2Arguments() {
        List<String> h2Args = new ArrayList<String>();
        h2Args.add("-tcp");
        
        if (trace) {
            h2Args.add("-trace");
        }
        
        if (tcpPort != null) {
            h2Args.add("-tcpPort");
            h2Args.add(tcpPort.toString());
        }
        
        if (tcpAllowOthers) {
            h2Args.add("-tcpAllowOthers");
        }

        if (baseDir != null && !baseDir.isEmpty()) {
            h2Args.add("-baseDir");
            h2Args.add(baseDir);
        }

        if (ifExists) {
            h2Args.add("-ifExists");
        }

        if (web) {
            h2Args.add("-web");
            if (webPort != null) {
                h2Args.add("-webPort");
                h2Args.add(webPort.toString());
            }
        }
        return h2Args;
    }

}
