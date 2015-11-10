/*
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com The software in this package is published
 * under the terms of the CPAL v1.0 license, a copy of which has been included with this distribution in the LICENSE.txt
 * file.
 */

/**
 * TODO <a href="https://www.mulesoft.org/jira/browse/MULE-9049">https://www.mulesoft.org/jira/browse/MULE-9049</a>
 * <p/>
 * Re-re-packaged class from spring's cglib that incorporates PermGen leak fix as in
 * <a href="https://github.com/cglib/cglib/pull/49">https://github.com/cglib/cglib/pull/49</a>. Refer to
 * <a href="https://www.mulesoft.org/jira/browse/MULE-9046">https://www.mulesoft.org/jira/browse/MULE-9046</a> for more
 * details.
 * <p/>
 * This should be removed when a spring version that repackages the cglib version containing this fix is upgraded to.
 * <p/>
 * Abstract class for all code-generating CGLIB utilities. In addition to caching generated classes for performance, it
 * provides hooks for customizing the <code>ClassLoader</code>, name of the generated class, and transformations applied
 * before generation.
 */
package org.springframework.cglib;