/*
 * Copyright 2011 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.commonjava.maven.plugins.execroot;

import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;

import java.io.File;

/**
 * @goal execution-root
 * @requiresProject true 
 * @phase initialize
 * @threadSafe true
 */
public class ExecutionRootGoal
    extends AbstractDirectoryGoal
    implements Mojo, ContextEnabled
{

    protected static final String EXEC_ROOT_CONTEXT_KEY = "directories.execRoot";

    /**
     * {@inheritDoc}
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#findDirectory()
     */
    @Override
    protected File findDirectory()
    {
        return new File( session.getExecutionRootDirectory() );
    }

    /**
     * {@inheritDoc}
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#getContextKey()
     */
    @Override
    protected String getContextKey()
    {
        return EXEC_ROOT_CONTEXT_KEY;
    }

    /**
     * {@inheritDoc}
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#getLogLabel()
     */
    @Override
    protected String getLogLabel()
    {
        return "Execution-Root";
    }

}
