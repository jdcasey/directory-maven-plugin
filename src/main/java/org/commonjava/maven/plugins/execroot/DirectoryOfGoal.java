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
import java.util.List;
import java.util.Stack;

/**
 * @goal directory-of
 * @requiresProject true 
 * @phase initialize
 * @threadSafe true
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
