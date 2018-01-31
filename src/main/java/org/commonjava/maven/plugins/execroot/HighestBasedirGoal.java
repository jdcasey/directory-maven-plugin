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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * @goal highest-basedir
 * @requiresProject true
 * @phase initialize
 * @threadSafe true
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
            if (System.getProperty("os.name").startsWith("Windows")) {
                return first.getAbsolutePath().compareToIgnoreCase( second.getAbsolutePath() );
            } else {
                return first.getAbsolutePath().compareTo( second.getAbsolutePath() );
            }
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

            File file = new File(Paths.get(p.getBasedir().toURI()).normalize().toString());

            if ( !files.contains( file ) )
            {
                // add to zero to maybe help pre-sort the paths...the shortest (parent) paths should end up near the
                // top.
                files.add( 0, file );
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
            String dirPath = dir.getAbsolutePath();
            String nextPath = next.getAbsolutePath();
            if (System.getProperty("os.name").startsWith("Windows")) {
                dirPath = dirPath.toLowerCase();
                nextPath = nextPath.toLowerCase();
            }
            if ( !nextPath.startsWith( dirPath ) )
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
