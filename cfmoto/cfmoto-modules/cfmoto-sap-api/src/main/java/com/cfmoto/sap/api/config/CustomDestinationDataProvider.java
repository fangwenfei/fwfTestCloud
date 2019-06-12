package com.cfmoto.sap.api.config;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CustomDestinationDataProvider implements DestinationDataProvider {

    private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();

    public Properties getDestinationProperties(String destinationName){
        return secureDBStorage.get(destinationName);
    }
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener){
        this.eL = eventListener;
    }

    public boolean supportsEvents(){
        return true;
    }

    public void addDestinationProperties(String destName, Properties properties){
        synchronized(secureDBStorage){
            secureDBStorage.put( destName, properties );
            eL.updated( destName ); // create or updated
        }
    }
}
