package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {
	
	private static final String PREFERENCES_PATH = "preferences";
	
	public static final LinkedHashMap<String, Properties> PROPERTIES_MAP;
	
	static{
		PROPERTIES_MAP = new LinkedHashMap<String, Properties>();
		PROPERTIES_MAP.put("None", new Properties());
		try {
			for (String path: getResourcePaths(PREFERENCES_PATH)){
				Properties props;
				try {
					props = getPropsFromResource(path);
					PROPERTIES_MAP.put(props.getProperty("name"), props);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static Properties getPropsFromFile(String path) throws FileNotFoundException, IOException{
		Properties props = new Properties();
		props.load(new FileInputStream(path));
		return props;
	}
	
	public static Properties getPropsFromResource(String resourcePath) throws IOException{
		Properties props = new Properties();
		props.load(ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath));
		return props;
	}
	
	public static String getResourcePath(String resourcePath){
		return ClassLoader.getSystemClassLoader().getResource(resourcePath).getPath();
	}
	
	public static ArrayList<File> getFiles(String dir){
		return (ArrayList<File>) Arrays.asList((new File(dir)).listFiles());
	}
	
	public static ArrayList<String> getResourcePaths(String resourceDir) throws IOException, URISyntaxException{
		ArrayList<String> res = new ArrayList<String>();
		final File jarFile = new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	    final JarFile jar = new JarFile(jarFile);
	    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
	    while(entries.hasMoreElements()) {
	        final String name = entries.nextElement().getName();
	        if (name.startsWith(resourceDir + "/") && !name.equals(resourceDir + "/")) { //filter according to the path
	            res.add(name);
	        	System.out.println(name);
	        }
	    }
	    jar.close();
	    return res;
	}

}
