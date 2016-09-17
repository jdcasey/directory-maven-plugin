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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;

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

    /**
     * @parameter default-value="false"
     */
    protected boolean systemProperty;

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

        resolveProperties();

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

    /**
     * Resolve all properties of current project placeholder against the new property. 
     */
	private void resolveProperties() {
		Properties projectProperties = currentProject.getProperties();

		for (Enumeration<?> n = projectProperties.propertyNames(); n.hasMoreElements();) {
			String k = (String) n.nextElement();
			if (!k.equals(property)) {
				projectProperties.setProperty(k, getPropertyValue(k, projectProperties));
			}
		}
	}

	private String getPropertyValue(String key, Properties properties) {
		String value = properties.getProperty(key);
		String placeholder = "${" + property + "}";

		if ( value.contains(placeholder) ) {
			value = value.replace(placeholder, properties.getProperty(property));
		}
		return value;
	}

}
