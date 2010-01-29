/*
 * Copyright 2006 the original author or authors.
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

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;

/**
 * SAAJ-specific abstract base class of the <code>SoapFault</code> interface. Wraps a {@link javax.xml.soap.SOAPFault}.
 *
 * @author Arjen Poutsma
 * @since 1.0.0
 */
abstract class SaajSoapFault extends SaajSoapElement implements SoapFault {

    protected SaajSoapFault(SOAPFault fault) {
        super(fault);
    }

    public QName getFaultCode() {
        return getImplementation().getFaultCode(getSaajFault());
    }

    protected SOAPFault getSaajFault() {
        return (SOAPFault) getSaajElement();
    }

    public SoapFaultDetail getFaultDetail() {
        Detail saajDetail = getImplementation().getFaultDetail(getSaajFault());
        return saajDetail == null ? null : new SaajSoapFaultDetail(saajDetail);
    }

    public SoapFaultDetail addFaultDetail() {
        try {
            Detail saajDetail = getImplementation().addFaultDetail(getSaajFault());
            return new SaajSoapFaultDetail(saajDetail);
        }
        catch (SOAPException ex) {
            throw new SaajSoapFaultException(ex);
        }
    }
}
