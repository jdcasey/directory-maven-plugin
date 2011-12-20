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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * @goal highest-basedir
 * @requiresProject true 
 * @phase initialize
 * 
 * Find the topmost directory in this Maven execution, and set it as a property.
 */
public class HighestBasedirGoal
    extends AbstractDirectoryGoal
    implements Mojo, ContextEnabled
{

    public static final class PathComparator
        implements Comparator<File>
    {
        public int compare( final File first, final File second )
        {
            return first.getAbsolutePath().compareTo( second.getAbsolutePath() );
        }
    }

    protected static final String HIGHEST_DIR_CONTEXT_KEY = "directories.highestDir";

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
        final Stack<MavenProject> toCheck = new Stack<MavenProject>();
        toCheck.addAll( projects );

        final List<File> files = new ArrayList<File>();
        while ( !toCheck.isEmpty() )
        {
            final MavenProject p = toCheck.pop();
            if ( p.getBasedir() == null )
            {
                // we've hit a parent that was resolved. Don't bother going higher up the hierarchy.
                continue;
            }

            if ( !files.contains( p.getBasedir() ) )
            {
                // add to zero to maybe help pre-sort the paths...the shortest (parent) paths should end up near the
                // top.
                files.add( 0, p.getBasedir() );
            }

            if ( p.getParent() != null )
            {
                toCheck.add( p.getParent() );
            }
        }

        if ( files.isEmpty() )
        {
            throw new MojoExecutionException( "No project base directories found! Are you sure you're "
                + "executing this on a valid Maven project?" );
        }

        Collections.sort( files, new PathComparator() );
        final File dir = files.get( 0 );

        if ( files.size() > 1 )
        {
            final File next = files.get( 1 );
            if ( !next.getAbsolutePath().startsWith( dir.getAbsolutePath() ) )
            {
                throw new MojoExecutionException( "Cannot find a single highest directory for this project set. "
                    + "First two candidates directories don't share a common root." );
            }
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
        return HIGHEST_DIR_CONTEXT_KEY;
    }

    /**
     * {@inheritDoc}
     * @see org.commonjava.maven.plugins.execroot.AbstractDirectoryGoal#getLogLabel()
     */
    @Override
    protected String getLogLabel()
    {
        return "Highest basedir";
    }

}
