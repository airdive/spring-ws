/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.soap.saaj;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.util.ObjectUtils;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.support.SaajUtils;
import org.springframework.ws.transport.TransportConstants;
import org.springframework.ws.transport.TransportOutputStream;

/**
 * SAAJ 1.2 specific implementation of the <code>SaajImplementation</code> interface.
 *
 * @author Arjen Poutsma
 * @since 1.0.0
 */
class Saaj12Implementation extends SaajImplementation {

    private static final Saaj12Implementation INSTANCE = new Saaj12Implementation();

    private Saaj12Implementation() {
    }

    public static Saaj12Implementation getInstance() {
        return INSTANCE;
    }

    public QName getName(SOAPElement element) {
        return SaajUtils.toQName(element.getElementName());
    }

    public QName getFaultCode(SOAPFault fault) {
        return SaajUtils.toQName(fault.getFaultCodeAsName());
    }

    public boolean isSoap11(SOAPElement element) {
        return true;
    }

    public DetailEntry addDetailEntry(Detail detail, QName name) throws SOAPException {
        Name detailEntryName = SaajUtils.toName(name, detail);
        return detail.addDetailEntry(detailEntryName);
    }

    public SOAPHeaderElement addHeaderElement(SOAPHeader header, QName name) throws SOAPException {
        Name saajName = SaajUtils.toName(name, header);
        return header.addHeaderElement(saajName);
    }

    public SOAPFault addFault(SOAPBody body, QName faultCode, String faultString, Locale locale) throws SOAPException {
        Name name = SaajUtils.toName(faultCode, body);
        if (locale == null) {
            return body.addFault(name, faultString);
        }
        else {
            return body.addFault(name, faultString, locale);
        }
    }

    public Source getSource(SOAPElement element) {
        return new DOMSource(element);
    }

    public Result getResult(SOAPElement element) {
        return new DOMResult(element);
    }

    public void addAttribute(SOAPElement element, QName name, String value) throws SOAPException {
        Name attributeName = SaajUtils.toName(name, element);
        element.addAttribute(attributeName, value);
    }

    public void removeAttribute(SOAPElement element, QName name) throws SOAPException {
        Name attributeName = SaajUtils.toName(name, element);
        element.removeAttribute(attributeName);
    }

    public String getAttributeValue(SOAPElement element, QName name) throws SOAPException {
        Name attributeName = SaajUtils.toName(name, element);
        return element.getAttributeValue(attributeName);
    }

    public Iterator getAllAttibutes(SOAPElement element) {
        List results = new ArrayList();
        for (Iterator iterator = element.getAllAttributes(); iterator.hasNext();) {
            Name attributeName = (Name) iterator.next();
            results.add(SaajUtils.toQName(attributeName));
        }
        return results.iterator();
    }

    public String getText(SOAPElement element) {
        return element.getValue();
    }

    public void setText(SOAPElement element, String content) {
        element.setValue(content);
    }

    public SOAPEnvelope getEnvelope(SOAPMessage message) throws SOAPException {
        return message.getSOAPPart().getEnvelope();
    }

    public SOAPHeader getHeader(SOAPEnvelope envelope) throws SOAPException {
        return envelope.getHeader();
    }

    public SOAPBody getBody(SOAPEnvelope envelope) throws SOAPException {
        return envelope.getBody();
    }

    public Iterator examineAllHeaderElements(SOAPHeader header) {
        return header.examineAllHeaderElements();
    }

    public Iterator examineMustUnderstandHeaderElements(SOAPHeader header, String actorOrRole) {
        return header.examineMustUnderstandHeaderElements(actorOrRole);
    }

    public String getActorOrRole(SOAPHeaderElement headerElement) {
        return headerElement.getActor();
    }

    public void setActorOrRole(SOAPHeaderElement headerElement, String actorOrRole) {
        headerElement.setActor(actorOrRole);
    }

    public boolean getMustUnderstand(SOAPHeaderElement headerElement) {
        return headerElement.getMustUnderstand();
    }

    public void setMustUnderstand(SOAPHeaderElement headerElement, boolean mustUnderstand) {
        headerElement.setMustUnderstand(mustUnderstand);
    }

    public boolean hasFault(SOAPBody body) {
        return body.hasFault();
    }

    public SOAPFault getFault(SOAPBody body) {
        return body.getFault();
    }

    public String getFaultActor(SOAPFault fault) {
        return fault.getFaultActor();
    }

    public void setFaultActor(SOAPFault fault, String actorOrRole) throws SOAPException {
        fault.setFaultActor(actorOrRole);
    }

    public String getFaultString(SOAPFault fault) {
        return fault.getFaultString();
    }

    public Locale getFaultStringLocale(SOAPFault fault) {
        return fault.getFaultStringLocale();
    }

    public Detail getFaultDetail(SOAPFault fault) {
        return fault.getDetail();
    }

    public Detail addFaultDetail(SOAPFault fault) throws SOAPException {
        return fault.addDetail();
    }

    public void addTextNode(DetailEntry detailEntry, String text) throws SOAPException {
        detailEntry.addTextNode(text);
    }

    public Iterator getDetailEntries(Detail detail) {
        return detail.getDetailEntries();
    }

    public SOAPElement getFirstBodyElement(SOAPBody body) {
        for (Iterator iterator = body.getChildElements(); iterator.hasNext();) {
            Object child = iterator.next();
            if (child instanceof SOAPElement) {
                return (SOAPElement) child;
            }
        }
        return null;
    }

    public void removeContents(SOAPElement element) {
        element.removeContents();
    }

    Iterator getChildElements(SOAPElement element, QName name) throws SOAPException {
        Name elementName = SaajUtils.toName(name, element);
        return element.getChildElements(elementName);
    }

    void addNamespaceDeclaration(SOAPElement element, String prefix, String namespaceUri) throws SOAPException {
        element.addNamespaceDeclaration(prefix, namespaceUri);
    }

    public void writeTo(SOAPMessage message, OutputStream outputStream) throws SOAPException, IOException {
        if (message.saveRequired()) {
            message.saveChanges();
        }
        if (outputStream instanceof TransportOutputStream) {
            TransportOutputStream transportOutputStream = (TransportOutputStream) outputStream;
            // some SAAJ implementations (Axis 1) do not have a Content-Type header by default
            MimeHeaders headers = message.getMimeHeaders();
            if (ObjectUtils.isEmpty(headers.getHeader(TransportConstants.HEADER_CONTENT_TYPE))) {
                headers.addHeader(TransportConstants.HEADER_CONTENT_TYPE, SoapVersion.SOAP_11.getContentType());
                if (message.saveRequired()) {
                    message.saveChanges();
                }
            }
            for (Iterator iterator = headers.getAllHeaders(); iterator.hasNext();) {
                MimeHeader mimeHeader = (MimeHeader) iterator.next();
                transportOutputStream.addHeader(mimeHeader.getName(), mimeHeader.getValue());
            }
        }
        message.writeTo(outputStream);

    }

    public MimeHeaders getMimeHeaders(SOAPMessage message) {
        return message.getMimeHeaders();
    }

    public Iterator getAttachments(SOAPMessage message) {
        return message.getAttachments();
    }

    public Iterator getAttachment(SOAPMessage message, MimeHeaders mimeHeaders) {
        return message.getAttachments(mimeHeaders);
    }

    public AttachmentPart addAttachmentPart(SOAPMessage message, DataHandler dataHandler) {
        AttachmentPart attachmentPart = message.createAttachmentPart(dataHandler);
        message.addAttachmentPart(attachmentPart);
        return attachmentPart;
    }

    //
    // Unsupported
    //

    public String getFaultRole(SOAPFault fault) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public void setFaultRole(SOAPFault fault, String role) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public SOAPHeaderElement addNotUnderstoodHeaderElement(SOAPHeader header, QName name) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public SOAPHeaderElement addUpgradeHeaderElement(SOAPHeader header, String[] supportedSoapUris) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public Iterator getFaultSubcodes(SOAPFault fault) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public void appendFaultSubcode(SOAPFault fault, QName subcode) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public String getFaultNode(SOAPFault fault) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public void setFaultNode(SOAPFault fault, String uri) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public String getFaultReasonText(SOAPFault fault, Locale locale) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

    public void setFaultReasonText(SOAPFault fault, Locale locale, String text) {
        throw new UnsupportedOperationException("SAAJ 1.2 does not support SOAP 1.2");
    }

}
