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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractDirectoryGoal
    extends AbstractMojo
{

    /**
     * @parameter default-value="dirProperty"
     * @required
     */
    protected String property;

    /**
     * @parameter default-value="${project}"
     * @readonly
     */
    protected MavenProject currentProject;

    /**
     * @parameter default-value="${session}"
     * @readonly
     */
    protected MavenSession session;

    /**
     * @parameter default-value="false"
     */
    protected boolean quiet;

    /**
     * @parameter default-value="false"
     */
    protected boolean systemProperty;

    protected AbstractDirectoryGoal()
    {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public final void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File execRoot;
        synchronized ( session )
        {
            final String key = getContextKey();
            execRoot = (File) getPluginContext().get( key );
            if ( execRoot == null )
            {
                execRoot = findDirectory();
                getPluginContext().put( key, execRoot );
            }
        }

        if ( !quiet )
        {
            getLog().info( getLogLabel() + " set to: " + execRoot );
        }

        currentProject.getProperties().setProperty( property, execRoot.getAbsolutePath() );

        if (systemProperty) {
            String existingValue = System.getProperty(property);
            if (existingValue == null) {
                System.setProperty(property, execRoot.getAbsolutePath());
            }
        }

        if ( getLog().isDebugEnabled() )
        {
            final StringWriter str = new StringWriter();
            currentProject.getProperties().list( new PrintWriter( str ) );

            getLog().debug( "After setting property '" + property + "', project properties are:\n\n" + str );
        }
    }

    protected abstract String getLogLabel();

    protected abstract File findDirectory()
        throws MojoExecutionException;

    protected abstract String getContextKey();

}
