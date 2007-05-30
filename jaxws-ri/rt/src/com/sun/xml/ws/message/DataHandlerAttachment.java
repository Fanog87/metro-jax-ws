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

package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.util.ByteArrayBuffer;

import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Jitendra Kotamraju
 */
public final class DataHandlerAttachment implements Attachment {
    // TODO fix the hack
    // So that SAAJ registers DCHs for MIME types
    static {
        new com.sun.xml.messaging.saaj.soap.AttachmentPartImpl();
    }

    private final DataHandler dh;
    private final String contentId;

    /**
     * This will be constructed by {@link AttachmentMarshallerImpl}
     */
    public DataHandlerAttachment(@NotNull String contentId, @NotNull DataHandler dh) {
        this.dh = dh;
        this.contentId = contentId;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentType() {
        return dh.getContentType();
    }

    public byte[] asByteArray() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            dh.writeTo(os);
            return os.toByteArray();
        } catch (IOException e) {
            throw new WebServiceException(e);
        }
    }

    public DataHandler asDataHandler() {
        return dh;
    }

    public Source asSource() {
        try {
            return new StreamSource(dh.getInputStream());
        } catch (IOException e) {
            throw new WebServiceException(e);
        }
    }

    public InputStream asInputStream() {
        try {
            return dh.getInputStream();
        } catch (IOException e) {
            throw new WebServiceException(e);
        }
    }

    public void writeTo(OutputStream os) throws IOException {
        dh.writeTo(os);
    }

    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(dh);
        part.setContentId(contentId);
        saaj.addAttachmentPart(part);
    }
}
