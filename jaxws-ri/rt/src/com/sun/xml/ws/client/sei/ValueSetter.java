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

package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.api.model.Parameter;
import com.sun.xml.ws.model.ParameterImpl;

import javax.xml.ws.Holder;

/**
 * Moves a Java value unmarshalled from a response message
 * to the right place.
 *
 * <p>
 * Sometimes values are returned as a return value, and
 * others are returned in the {@link Holder} value. Instances
 * of this interface abstracts this detail.
 *
 * <p>
 * {@link ValueSetter} is a stateless behavior encapsulation.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class ValueSetter {
    private ValueSetter() {}

    /**
     * Moves the value to the expected place.
     *
     * @param obj
     *      The unmarshalled object.
     * @param args
     *      The arguments given to the Java method invocation. If <tt>obj</tt>
     *      is supposed to be returned as a {@link Holder} value, a suitable
     *      {@link Holder} is obtained from this argument list and <tt>obj</tt>
     *      is set.
     *
     * @return
     *      if <tt>obj</tt> is supposed to be returned as a return value
     *      from the method, this method returns <tt>obj</tt>. Otherwise null.
     */
    abstract Object put(Object obj, Object[] args);

    /**
     * Singleton instance.
     */
    private static final ValueSetter RETURN_VALUE = new ReturnValue();
    /**
     * {@link Param}s with small index numbers are used often,
     * so we pool them to reduce the footprint.
     */
    private static final ValueSetter[] POOL = new ValueSetter[16];

    static {
        for( int i=0; i<POOL.length; i++ )
            POOL[i] = new Param(i);
    }

    /**
     * Returns a {@link ValueSetter} suitable for the given {@link Parameter}.
     */
    public static ValueSetter get(ParameterImpl p) {
        int idx = p.getIndex();

        if(idx==-1)
            return RETURN_VALUE;
        if(idx<POOL.length)
            return POOL[idx];
        else
            return new Param(idx);
    }

    private static final class ReturnValue extends ValueSetter {
        Object put(Object obj, Object[] args) {
            return obj;
        }
    }

    static final class Param extends ValueSetter {
        /**
         * Index of the argument to put the value to.
         */
        private final int idx;

        public Param(int idx) {
            this.idx = idx;
        }

        Object put(Object obj, Object[] args) {
            Object arg = args[idx];
            if(arg!=null) {
                // we build model in such a way that this is guaranteed
                assert arg instanceof Holder;
                ((Holder)arg).value = obj;
            }
            // else {
            //   if null is given as a Holder, there's no place to return
            //   the value, so just ignore.
            // }

            // no value to return
            return null;
        }
    }
}
