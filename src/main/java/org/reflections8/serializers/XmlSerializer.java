package org.reflections8.serializers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.reflections8.Reflections;
import org.reflections8.ReflectionsException;
import org.reflections8.Store;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.Utils;

/** serialization of Reflections to xml
 *
 * <p>an example of produced xml:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 *
 * &lt;Reflections&gt;
 *  &lt;SubTypesScanner&gt;
 *      &lt;entry&gt;
 *          &lt;key&gt;com.google.inject.Module&lt;/key&gt;
 *          &lt;values&gt;
 *              &lt;value&gt;fully.qualified.name.1&lt;/value&gt;
 *              &lt;value&gt;fully.qualified.name.2&lt;/value&gt;
 * ...
 * </pre>
 * */
public class XmlSerializer implements Serializer {

    public Reflections read(InputStream inputStream) {
        Reflections reflections8;
        try {
            Constructor<Reflections> constructor = Reflections.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            reflections8 = constructor.newInstance();
        } catch (Exception e) {
            reflections8 = new Reflections(new ConfigurationBuilder());
        }

        try {
            Document document = new SAXReader().read(inputStream);
            for (Object e1 : document.getRootElement().elements()) {
                Element index = (Element) e1;
                for (Object e2 : index.elements()) {
                    Element entry = (Element) e2;
                    Element key = entry.element("key");
                    Element values = entry.element("values");
                    for (Object o3 : values.elements()) {
                        Element value = (Element) o3;
                        reflections8.getStore().getOrCreate(index.getName()).putSingle(key.getText(), value.getText());
                    }
                }
            }
        } catch (DocumentException e) {
            throw new ReflectionsException("could not read.", e);
        } catch (Throwable e) {
            throw new RuntimeException("Could not read. Make sure relevant dependencies exist on classpath.", e);
        }

        return reflections8;
    }

    public File save(final Reflections reflections, final String filename) {
        File file = Utils.prepareFile(filename);


        try {
            Document document = createDocument(reflections);
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            throw new ReflectionsException("could not save to file " + filename, e);
        } catch (Throwable e) {
            throw new RuntimeException("Could not save to file " + filename + ". Make sure relevant dependencies exist on classpath.", e);
        }

        return file;
    }

    public String toString(final Reflections reflections) {
        Document document = createDocument(reflections);

        try {
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private Document createDocument(final Reflections reflections8) {
        Store map = reflections8.getStore();

        Document document = DocumentFactory.getInstance().createDocument();
        Element root = document.addElement("Reflections");
        for (String indexName : map.keySet()) {
            Element indexElement = root.addElement(indexName);
            for (String key : map.get(indexName).keySet()) {
                Element entryElement = indexElement.addElement("entry");
                entryElement.addElement("key").setText(key);
                Element valuesElement = entryElement.addElement("values");
                for (String value : map.get(indexName).get(key)) {
                    valuesElement.addElement("value").setText(value);
                }
            }
        }
        return document;
    }
}
