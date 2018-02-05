/*
 * XmlConfig.java
 *
 * Created on 2006年5月30日, 上午11:09
 *
 * 提供读xml格式的配置文件的方法
 */

package com.aspire.etl.tool;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/**
 *
 * @author luoqi
 */
public class XmlConfig {
    SAXReader reader = new SAXReader();
    
    Document document=null;
    String configFileName = null;
    public static String DefaultConfigPath = "./cfg/cfg.xml";
    //20080415 修改:解决非线程同步问题
    public synchronized void reloadConfigFile() throws FileNotFoundException, DocumentException{
        document = reader.read(new FileReader(configFileName));        
        return ;
    }
    /** Creates a new instance of XmlConfig */
    public XmlConfig(String configFileName) {
        this.configFileName = configFileName;        
    }
    public Node getSingleNode(String xpath) throws FileNotFoundException, DocumentException{
        reloadConfigFile();
        XPath xpathSelector = DocumentHelper
                .createXPath(xpath);
        Node node =  xpathSelector.selectSingleNode(document);
        return node;
    }
    private String readSingleAttributeValue(Node node,String attribute) throws FileNotFoundException, DocumentException{
        reloadConfigFile();
        return node.valueOf(attribute);
    }
    
    public List<String> readMultiNodeValue(String xpath) throws FileNotFoundException, DocumentException{
        List nodes = getNodes(xpath);
        
        List<String> ret = new ArrayList<String>();
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element node = (Element) it.next();
            ret.add(node.getStringValue());
        }
        return ret;
    }

	@SuppressWarnings("unchecked")
	public List<String> readMultiAttributeValue(String xpath,String attribute) throws FileNotFoundException, DocumentException{
        return readMultiAttributeValue(getNodes(xpath),attribute);
    }
    private List<String> readMultiAttributeValue(List<Node> nodeList,String attribute){
        List<String> ret = new ArrayList<String>();
        for (Iterator it = nodeList.iterator(); it.hasNext();) {
            Element node = (Element) it.next();
            ret.add(node.valueOf(attribute));
        }
        return ret;
    }
    public String readSingleNodeValue(String xpath) throws FileNotFoundException, DocumentException{
        reloadConfigFile();
        return document.valueOf(xpath);
    }
    public String readSingleAttributeValue(String xpath,String attribute) throws FileNotFoundException, DocumentException{
        return readSingleAttributeValue(getSingleNode(xpath),attribute);
    }
    public List getNodes(String xpath) throws FileNotFoundException, DocumentException{
        reloadConfigFile();
        XPath xpathSelector = DocumentHelper
                .createXPath(xpath);
        return xpathSelector.selectNodes(document);
    }
    
    public static void main(String[] args)  {
        XmlConfig config;
        config = new XmlConfig("./cfg/cfg.xml");
        
        try {
            
            String a ;
            
            //取单个节点的值
            a = config.readSingleNodeValue("//province[@id='GD']/DSTP/database/url");
            System.out.println("=="+a);
            
            //取多个节点的值
            List<String> a1 = config.readMultiNodeValue("//province[@id='GD']/DSTP/database");
            
            for (String element : a1) {
                System.out.println(element);
            }
            
            //取单个属性的值,这种配置文件写法
        /*
         *<tnsname name="misc"/>
          <tnsname name="report"/>
         */
            a = config.readSingleAttributeValue("//province[@id='GD']/DSTP/database/password","@value");
            System.out.println(a);
            
            //取多个属性的值
            a1 = config.readMultiAttributeValue("//province[@id='GD']/DSTP/database/tnsname","@name");
            
            for (String element : a1) {
                System.out.println(element);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getDefaultPath()  {
        String strPath;
        strPath = System.getProperty("user.dir");
        strPath = strPath + "\\cfg\\db\\cfg.xml";
        return strPath;
    }
}
