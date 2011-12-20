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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * @goal directory-of
 * @requiresProject true 
 * @phase initialize
 * 
 * Find the topmost directory in this Maven execution, and set it as a property.
 */
public class DirectoryOfGoal
    extends AbstractDirectoryGoal
    implements Mojo, ContextEnabled
{

    protected static final String DIR_OF_CONTEXT_KEY = "directories.directoryOf-";

    /**
     * @parameter
     */
    private ProjectRef project;

    /**
     * @parameter default-value="${reactorProjects}"
     * @readonly
     */
    protected List<MavenProject> projects;

    /**
     * {@inheritDoc}
     * @throws MojoExecutionException 
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#findDirectory()
     */
    @Override
    protected File findDirectory()
        throws MojoExecutionException
    {
        File dir = null;

        final Stack<MavenProject> toCheck = new Stack<MavenProject>();
        toCheck.addAll( projects );

        while ( !toCheck.isEmpty() )
        {
            final MavenProject p = toCheck.pop();
            if ( project.matches( p ) )
            {
                dir = p.getBasedir();
                break;
            }

            if ( p.getParent() != null )
            {
                toCheck.add( p.getParent() );
            }
        }

        if ( dir == null )
        {
            throw new MojoExecutionException( "Cannot find directory for project: " + project );
        }

        return dir;
    }

    /**
     * {@inheritDoc}
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#getContextKey()
     */
    @Override
    protected String getContextKey()
    {
        return DIR_OF_CONTEXT_KEY + project;
    }

    /**
     * {@inheritDoc}
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#getLogLabel()
     */
    @Override
    protected String getLogLabel()
    {
        return "Directory of " + project;
    }

}
