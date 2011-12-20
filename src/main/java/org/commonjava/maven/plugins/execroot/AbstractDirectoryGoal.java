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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Map;

public abstract class AbstractDirectoryGoal
    implements Mojo, ContextEnabled
{

    private Log log;

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

    protected Map<String, Object> context;

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
            execRoot = (File) context.get( key );
            if ( execRoot == null )
            {
                execRoot = findDirectory();
                context.put( key, execRoot );
            }
        }

        if ( !quiet )
        {
            getLog().info( getLogLabel() + " set to: " + execRoot );
        }

        currentProject.getProperties().setProperty( property, execRoot.getAbsolutePath() );
    }

    protected abstract String getLogLabel();

    protected abstract File findDirectory()
        throws MojoExecutionException;

    protected abstract String getContextKey();

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.plugin.Mojo#getLog()
     */
    public synchronized Log getLog()
    {
        if ( log == null )
        {
            log = new SystemStreamLog();
        }

        return log;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.plugin.Mojo#setLog(org.apache.maven.plugin.logging.Log)
     */
    public void setLog( final Log log )
    {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.maven.plugin.ContextEnabled#getPluginContext()
     */
    @SuppressWarnings( "rawtypes" )
    public Map getPluginContext()
    {
        return context;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.maven.plugin.ContextEnabled#setPluginContext(java.util.Map)
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void setPluginContext( final Map context )
    {
        this.context = context;
    }

}