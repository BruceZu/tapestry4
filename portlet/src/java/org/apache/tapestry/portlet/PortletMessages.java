// Copyright 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.portlet;

import java.net.MalformedURLException;

import org.apache.hivemind.impl.MessageFormatter;

/**
 * @author Howard M. Lewis Ship
 * @since 3.1
 */
class PortletMessages
{
    private static final MessageFormatter _formatter = new MessageFormatter(PortletMessages.class,
            "PortletStrings");

    public static String unsupportedMethod(String methodName)
    {
        return _formatter.format("unsupported-method", methodName);
    }

    public static String errorGettingResource(String path, Throwable cause)
    {
        return _formatter.format("error-getting-resource", path, cause);
    }

    public static String errorProcessingAction(Throwable cause)
    {
        return _formatter.format("error-processing-action", cause);
    }

    public static String errorProcessingRender(Throwable cause)
    {
        return _formatter.format("error-processing-render", cause);
    }
}