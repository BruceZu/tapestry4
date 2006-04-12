//
// Tapestry Web Application Framework
// Copyright (c) 2000-2002 by Howard Lewis Ship
//
// Howard Lewis Ship
// http://sf.net/projects/tapestry
// mailto:hship@users.sf.net
//
// This library is free software.
//
// You may redistribute it and/or modify it under the terms of the GNU
// Lesser General Public License as published by the Free Software Foundation.
//
// Version 2.1 of the license should be included with this distribution in
// the file LICENSE, as well as License.html. If the license is not
// included with this distribution, you may find a copy at the FSF web
// site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
// Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied waranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//

package net.sf.tapestry.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.tapestry.Tapestry;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *  A wrapper around {@link DocumentBuilder} (itself a wrapper around
 *  some XML parser), this class provides error handling and entity
 *  resolving.
 *
 *  @version $Id$
 *  @author Howard Lewis Ship
 *  @since 0.2.10
 * 
 **/

public abstract class AbstractDocumentParser
    implements ErrorHandler, EntityResolver
{
    private static final Category CAT =
        Category.getInstance(AbstractDocumentParser.class);

    private DocumentBuilder builder;
    private String resourcePath;
    private static final int MAP_SIZE = 7;

    /**
     *  Map used to resolve public identifiers to corresponding InputSource.
     *
     **/

    private Map entities;

    /**
     *  Invoked by subclasses (usually inside thier constructor) to register
     *  a public id and corresponding input source.  Generally, the source
     *  is a wrapper around an input stream to a package resource.
     *
     *  @param publicId the public identifier to be registerred, generally
     *  the publicId of a DTD related to the document being parsed
     *  @param entityPath the resource path of the entity, typically a DTD
     *  file.  Relative files names are expected to be stored in the same package
     *  as the class file, otherwise a leading slash is an absolute pathname
     *  within the classpath.
     *
     **/

    protected void register(String publicId, String entityPath)
    {
        if (CAT.isDebugEnabled())
            CAT.debug("Registerring " + publicId + " as " + entityPath);

        if (entities == null)
            entities = new HashMap(MAP_SIZE);

        entities.put(publicId, entityPath);
    }

    public String getResourcePath()
    {
        return resourcePath;
    }

    public void setResourcePath(String value)
    {
        resourcePath = null;
    }

    /** 
     * Invoked by subclasses to parse a document.  Obtains (or re-uses) a
     *  {@link DocumentBuilder} and parses the document from the {@link InputSource}.
     *
     *  @param source source from which to read the document
     *  @param resourcePath a description of the source, used in errors
     *  @param rootElementName the expected root element of the {@link Document}
     *
     *  @throws DocumentParseException wrapped around {@link SAXParseException} or
     *  {@link IOException}, or if the root element is wrong.
     *`
     **/

    protected Document parse(
        InputSource source,
        String resourcePath,
        String rootElementName)
        throws DocumentParseException
    {
        Document document;
        Element root;
        boolean error = true;

        if (CAT.isDebugEnabled())
            CAT.debug(
                "Parsing " + source + " (" + resourcePath + ") for element " + rootElementName);

        try
        {
            if (builder == null)
                builder = constructBuilder();

            document = builder.parse(source);

            error = false;

            root = document.getDocumentElement();
            if (!root.getTagName().equals(rootElementName))
            {
                throw new DocumentParseException(
                    Tapestry.getString(
                        "AbstractDocumentParser.incorrect-document-type",
                        rootElementName,
                        root.getTagName()),
                    resourcePath,
                    null);
            }

            return document;
        }
        catch (SAXParseException ex)
        {
            // This constructor captures the line number and column number

            throw new DocumentParseException(
                Tapestry.getString(
                    "AbstractDocumentParser.unable-to-parse",
                    resourcePath,
                    ex.getMessage()),
                resourcePath,
                ex);
        }
        catch (SAXException ex)
        {
            throw new DocumentParseException(
                Tapestry.getString(
                    "AbstractDocumentParser.unable-to-parse",
                    resourcePath,
                    ex.getMessage()),
                resourcePath,
                ex);
        }
        catch (IOException ex)
        {
            throw new DocumentParseException(
                Tapestry.getString(
                    "AbstractDocumentParser.unable-to-read",
                    resourcePath,
                    ex.getMessage()),
                resourcePath,
                ex);
        }
        catch (ParserConfigurationException ex)
        {
            throw new DocumentParseException(
                Tapestry.getString(
                    "AbstractDocumentParser.unable-to-construct-builder",
                    ex.getMessage()),
                ex);
        }
        finally
        {
            // If there was an error, discard the builder --- it may be in
            // an unknown and unusable state.

            if (error && builder != null)
            {
                CAT.debug("Discarding builder due to parse error.");
                builder = null;
            }
        }
    }

    /**
     *  Throws the exception, which is caught and wrapped
     *  in a {@link DocumentParseException} by {@link #parse(InputSource,String,String)}.
     *
     **/

    public void warning(SAXParseException exception) throws SAXException
    {
        throw exception;
    }

    /**
     *  Throws the exception, which is caught and wrapped
     *  in a {@link DocumentParseException} by {@link #parse(InputSource,String,String)}.
     *
     **/

    public void error(SAXParseException exception) throws SAXException
    {
        throw exception;
    }

    /**
     *  Throws the exception, which is caught and wrapped
     *  in a {@link DocumentParseException} by {@link #parse(InputSource,String,String)}.
     *
     **/

    public void fatalError(SAXParseException exception) throws SAXException
    {
        throw exception;
    }

    /**
     *  Checks for a previously registered public ID and returns the corresponding
     *  input source.
     *
     **/

    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
        String entityPath = null;

        if (CAT.isDebugEnabled())
            CAT.debug(
                "Attempting to resolve entity; publicId = "
                    + publicId
                    + " systemId = "
                    + systemId);

        if (entities != null)
            entityPath = (String) entities.get(publicId);

        if (entityPath == null)
        {
            if (CAT.isDebugEnabled())
                CAT.debug("Entity not found, using " + systemId);

            return null;
        }

        InputStream stream = getClass().getResourceAsStream(entityPath);

        InputSource result = new InputSource(stream);

        if (result != null && CAT.isDebugEnabled())
            CAT.debug(
                "Resolved " + publicId + " as " + result + " (for " + entityPath + ")");

        return result;
    }

    /**
     *  Returns true if the node is an element with the specified
     *  name.
     *
     **/

    protected boolean isElement(Node node, String elementName)
        throws DocumentParseException
    {
        if (node.getNodeType() != Node.ELEMENT_NODE)
            return false;

        // Cast it to Element

        Element element = (Element) node;

        // Note:  Using Xerces 1.0.3 and deferred DOM loading
        // (which is explicitly turned off), this sometimes
        // throws a NullPointerException.

        return element.getTagName().equals(elementName);

    }

    /**
     *  Returns the value of an {@link Element} node.  That is, all the {@link Text}
     *  nodes appended together.  Invokes trim() to remove leading and trailing spaces.
     *
     **/

    protected String getValue(Node node)
    {
        String result;
        Node child;
        Text text;
        StringBuffer buffer;

        buffer = new StringBuffer();

        for (child = node.getFirstChild();
            child != null;
            child = child.getNextSibling())
        {
            text = (Text) child;

            buffer.append(text.getData());
        }

        result = buffer.toString().trim();

        return result;
    }

    /**
     *  Returns the value of an {@link Element} node (via {@link #getValue(Node)}),
     *  but then validates that the result is a good identifier (starts with a
     *  letter, contains letters, numbers, dashes, underscore).
     *
     **/

    protected String getId(Node node) throws DocumentParseException
    {
        String result = getValue(node);
        char[] array = result.toCharArray();
        char ch;
        boolean fail = false;

        for (int i = 0; i < array.length; i++)
        {
            ch = array[i];

            if (i == 0)
                fail = !Character.isLetter(ch);
            else
            {
                fail =
                    !(Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '_');
            }

            if (fail)
                throw new DocumentParseException(
                    Tapestry.getString(
                        "AbstractDocumentParser.invalid-identifier",
                        result,
                        getNodePath(node.getParentNode())),
                    resourcePath,
                    null);
        }

        return result;
    }

    /**
     *  Returns a 'path' to the given node, which is a list of enclosing
     *  element names seperated by periods.  The root element name is first,
     *  and the node's element is last.  This is used when reporting some
     *  parse errors.
     *
     **/

    protected String getNodePath(Node node)
    {
        int count = 0;
        int length = 0;

        String[] path = new String[10];

        while (node != null)
        {
            // Dynamically expand the list before it overflows.

            if (count == path.length)
            {
                String newPath[] = new String[count * 2];
                System.arraycopy(path, 0, newPath, 0, count);

                path = newPath;
            }

            String nodeName = node.getNodeName();

            path[count++] = nodeName;
            node = node.getParentNode();

            length += nodeName.length() + 1;

        }

        StringBuffer buffer = new StringBuffer(length);
        boolean addDot = false;

        for (int i = count - 1; i >= 0; i--)
        {

            if (addDot)
                buffer.append('.');

            buffer.append(path[i]);

            addDot = true;
        }

        return buffer.toString();
    }

    /**
     *  Constructs a new {@link DocumentBuilder} to be used for parsing.
     *  The builder is used and reused, at least until there is an error
     *  parsing a document (at which point, it is discarded).
     *
     *  <p>This implementation obtains a builder with the following
     *  characteristics:
     *  <ul>
     *  <li>validating (if {@link #getRequireValidatingParser()} returns true}
     *  <li>ignoringElementContentWhitespace
     *  <li>ignoringComments
     *  <li>coalescing
     *  </ul>
     *
     *  <p>These characteristics are appropriate to parsing things such
     *  as Tapestry specifications; subclasses with unusual demands
     *  may need to override this method.
     *
     *  <p>The builder uses this {@link AbstractDocumentParser}
     *  as the entity resolver and error handler.
     *
     **/

    protected DocumentBuilder constructBuilder()
        throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setValidating(getRequireValidatingParser());
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        factory.setCoalescing(true);

        DocumentBuilder result = factory.newDocumentBuilder();

        result.setErrorHandler(this);
        result.setEntityResolver(this);

        if (CAT.isDebugEnabled())
            CAT.debug("Constructed new builder " + result);

        return result;
    }

    /**
     *  Used by {@link #constructBuilder()} to determine if the a validating
     *  {@link DocumentBuilder} is required.  This implementation returns true,
     *  subclasses that don't require a validating builder (such as documents
     *  without a known DTD), may override to return false.
     *
     *  @since 1.0.1
     **/

    protected boolean getRequireValidatingParser()
    {
        return true;
    }

    /**
     *  Returns the value of the named attribute of the node.  Returns null
     *  if the node doesn't contain an attribute with the given name.
     *
     *  @since 1.0.1
     * 
     **/

    protected String getAttribute(Node node, String attributeName)
    {
        NamedNodeMap map = node.getAttributes();

        if (map == null)
            return null;

        Node attributeNode = map.getNamedItem(attributeName);

        if (attributeNode == null)
            return null;

        return attributeNode.getNodeValue();
    }

}