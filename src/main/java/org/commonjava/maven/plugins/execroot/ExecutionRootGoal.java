/*
 *  Copyright (C) 2011 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.commonjava.maven.plugins.execroot;

import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;

import java.io.File;

/**
 * @goal execution-root
 * @requiresProject true 
 * @phase initialize
 * 
 * Find the topmost directory in this Maven execution, and set it as a property.
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
