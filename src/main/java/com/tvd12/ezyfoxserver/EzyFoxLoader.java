/**
 * 
 */
package com.tvd12.ezyfoxserver;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tvd12.ezyfoxserver.ccl.EzyAppClassLoader;
import com.tvd12.ezyfoxserver.config.EzyFoxConfig;
import com.tvd12.ezyfoxserver.config.EzyFoxSettings;
import com.tvd12.ezyfoxserver.service.EzyFoxXmlReading;

/**
 * @author tavandung12
 *
 */
public class EzyFoxLoader {
    
    private Logger logger;
    private EzyFoxConfig config;
    private EzyFoxXmlReading xmlReading;
    private ClassLoader classLoader;
    
    private EzyFoxLoader() {
        this.logger = LoggerFactory.getLogger(getClass());
    }
    
    public static EzyFoxLoader newInstance() {
    	return new EzyFoxLoader();
    }
    
    public EzyFox load() {
    	EzyFox answer = new EzyFox();
    	answer.setConfig(config);
    	answer.setXmlReading(xmlReading);
    	answer.setClassLoader(classLoader);
    	answer.setSettings(readSettings());
    	answer.setAppClassLoaders(newAppClassLoaders());
    	return answer;
    }
    
    private EzyFoxSettings readSettings() {
    	logger.info("read setting file: " + getSettingsFilePath());
    	return xmlReading.read(getSettingsFilePath(), EzyFoxSettings.class);
    }
    
    private Map<String, EzyAppClassLoader> newAppClassLoaders() {
        Map<String, EzyAppClassLoader> answer = new HashMap<>();
        for(File dir : getEntryFolders())
        	answer.put(dir.getName(), newAppClassLoader(dir));
        return answer;
    }
    
    private EzyAppClassLoader newAppClassLoader(File dir) {
    	logger.info("load " + dir);
        return new EzyAppClassLoader(dir, classLoader);
    }
    
    private File[] getEntryFolders() {
        File entries = getEntriesFolder();
        return entries.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
    }
    
    private File getEntriesFolder() {
        String entriesPath = getEntriesPath();
        File entries = new File(entriesPath);
        if(!entries.exists() || entries.isFile())
            throw new IllegalStateException("entries path " + 
                    entriesPath + " is not exists or is not a directory");
        return entries;
    }
    
    private String getEntriesPath() {
        return getPath(getAppsPath(), "entries");
    }
    
    private String getAppsPath() {
    	return getPath(getHomePath(), "apps");
    }
    
    private String getSettingsPath() {
    	return getPath(getHomePath(), "settings");
    }
    
    private String getSettingsFilePath() {
    	return getPath(getSettingsPath(), "ezy-settings.xml");
    }
    
    private String getPath(String first, String... more) {
        return Paths.get(first, more).toString();
    }
    
    private String getHomePath() {
    	return config.getEzyfoxHome();
    }
    
    @SuppressWarnings("unused")
    private String getVersion() {
    	return config.getEzyfoxVersion();
    }
    
    public EzyFoxLoader classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }
    
    public EzyFoxLoader config(EzyFoxConfig config) {
    	this.config = config;
    	return this;
    }
    
    public EzyFoxLoader xmlReading(EzyFoxXmlReading xmlReading) {
    	this.xmlReading = xmlReading;
    	return this;
    }
    
}
