package org.gtri.jaxb

import org.apache.maven.plugin.logging.Log

/**
 * Created by brad on 8/10/16.
 */
class LogHolder {

    static Log log;

    public static void setLog(Log someLog){
        log = someLog;
    }

    public static Log getLog(){
        return log;
    }

}
