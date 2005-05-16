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

package org.apache.tapestry.services;

import org.apache.bsf.BSFManager;

/**
 * A factory for {@link org.apache.bsf.BSFManager}instances.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public interface BSFManagerFactory
{
    /**
     * Creates and initializes a BSFManager instance that can be used by this thread.
     * The BSFManager should be discarded after use.
     */

    public BSFManager createBSFManager();
}