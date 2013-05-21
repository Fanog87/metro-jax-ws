/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.util.xml;

import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import junit.framework.TestCase;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

/**
 * @author Jitendra Kotamraju
 */
public class XMLStreamReaderToXMLStreamWriterTest extends TestCase {

    public void testCharacters() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("<a>");
        for(int i=0; i < 10000; i++) {
            builder.append(i);
        }
        builder.append("</a>");
        String str = builder.toString();
        readAndCompare(str);
    }

    public void testCharacters1() throws Exception {
         StringBuilder builder = new StringBuilder();
         builder.append("<a>");
         for(int i=0; i < 4096*3; i++) {
             builder.append('b');
         }
         builder.append("</a>");
         String str = builder.toString();
         readAndCompare(str);
     }

    private void readAndCompare(String str) throws Exception {
        XMLStreamReader rdr = XMLStreamReaderFactory.create(null, new StringReader(str), true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLStreamWriterFactory.create(baos);
        new XMLStreamReaderToXMLStreamWriter().bridge(rdr, writer);
        writer.close();
        assertEquals(str, baos.toString());
    }


}