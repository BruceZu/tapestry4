/*
 *  ====================================================================
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 2002 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Apache" and "Apache Software Foundation" and
 *  "Apache Tapestry" must not be used to endorse or promote products
 *  derived from this software without prior written permission. For
 *  written permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache",
 *  "Apache Tapestry", nor may "Apache" appear in their name, without
 *  prior written permission of the Apache Software Foundation.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
 */
package net.sf.tapestry.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import net.sf.tapestry.ApplicationRuntimeException;
import net.sf.tapestry.multipart.UploadPart;
import net.sf.tapestry.multipart.ValuePart;

/**
 *  A few tests to fill in the code coverage of 
 * {@link net.sf.tapestry.multipart.ValuePart} and
 * {@link net.sf.tapestry.multipart.UploadPart}.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 2.4
 *
 **/
public class TestMultipart extends TapestryTestCase
{

    public TestMultipart(String name)
    {
        super(name);
    }

    public void testSingle()
    {
        ValuePart p = new ValuePart("first");

        assertEquals(1, p.getCount());
        assertEquals("first", p.getValue());

        checkList("values", new String[] { "first" }, p.getValues());
    }

    public void testTwo()
    {
        ValuePart p = new ValuePart("alpha");

        p.add("beta");

        assertEquals(2, p.getCount());
        assertEquals("alpha", p.getValue());
        checkList("values", new String[] { "alpha", "beta" }, p.getValues());
    }

    public void testThree()
    {
        ValuePart p = new ValuePart("moe");
        p.add("larry");
        p.add("curly");

        checkList("values", new String[] { "moe", "larry", "curly" }, p.getValues());
    }

    public void testGetStreamFailure()
    {
        UploadPart p = new UploadPart("filePath", "contentType", new File("DOES-NOT-EXIST"));

        try
        {
            p.getStream();

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            checkException(ex, "Unable to open uploaded file");
        }
    }

    public void testUnableToCleanup() throws Exception
    {
        File file = File.createTempFile("unable-to-delete-", ".data");

        FileOutputStream out = new FileOutputStream(file);
        PrintWriter w = new PrintWriter(out);

        w.println("Test Data");

        w.close();

        UploadPart p = new UploadPart("filePath", "contentType", file);

        // Open the stream, and leave it open, so the
        // file can't be deleted.

        p.getStream();

        p.cleanup();
    }
}
