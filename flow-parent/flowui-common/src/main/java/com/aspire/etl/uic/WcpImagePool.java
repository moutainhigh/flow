package com.aspire.etl.uic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 
 * @author wangcaiping
 * @since 2008-1-16
 */
public class WcpImagePool {
    private static HashMap<URL, BufferedImage> images = new HashMap<URL, BufferedImage>();

    private static HashMap<String, BufferedImage> pathImages = new HashMap<String, BufferedImage>();

    private static HashMap<URL, ImageIcon> icons = new HashMap<URL, ImageIcon>();

    private static HashMap<String, ImageIcon> pathIcons = new HashMap<String, ImageIcon>();

    public static BufferedImage getImage(URL url) {
	if (url == null) {
	    return null;
	}
	BufferedImage bi = null;
	if (images.containsKey(url) == false) {
	    try {
		bi = ImageIO.read(url);
		images.put(url, bi);
	    } catch (IOException e) {
	    }
	} else {
	    bi = images.get(url);
	}
	return bi;
    }

    public static BufferedImage getImage(Class cls, String path) {
	if ((path == null) || (path.trim().equals(""))) {
	    return null;
	}
	java.net.URL url = cls.getResource(path);
	return getImage(url);
    }

    public static BufferedImage getImage(String path) {
	return getImage(WcpImagePool.class, path);
    }

    public static BufferedImage getImageOfPath(String path) {
	if ((path == null) || (path.trim().equals(""))) {
	    return null;
	}
	BufferedImage bi = null;
	if (pathImages.containsKey(path) == false) {
	    try {
		bi = ImageIO.read(new File(path));
		pathImages.put(path, bi);
	    } catch (IOException e) {
	    }
	} else {
	    bi = pathImages.get(path);
	}
	return bi;
    }

    public static ImageIcon getIcon(URL url) {
	if (url == null) {
	    return null;
	}
	ImageIcon ii = null;
	if (icons.containsKey(url) == false) {
	    ii = new ImageIcon(url);
	    icons.put(url, ii);
	} else {
	    ii = icons.get(url);
	}
	return ii;
    }

    public static ImageIcon getIconOfPath(String path) {

    	//如果文件不存直接返回null
    	if ((path == null) || (path.trim().equals("")) 
    			|| !new File(path).exists()) {
    		return null;
    	}
    	ImageIcon ii = null;
    	if (pathIcons.containsKey(path) == false) {
    		ii = new ImageIcon(path);
    		pathIcons.put(path, ii);
    	} else {
    		ii = pathIcons.get(path);
    	}
    	return ii;
    }

    public static ImageIcon getIcon(Class cls, String path) {
	if ((path == null) || (path.trim().equals(""))) {
	    return null;
	}

	java.net.URL url = cls.getResource(path);
	return getIcon(url);
    }

    public static ImageIcon getIcon(String path) {
	return getIcon(WcpImagePool.class, path);
    }
}
