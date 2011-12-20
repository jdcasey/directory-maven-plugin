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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

public class ProjectRef
{

    private String groupId;

    private String artifactId;

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( final String groupId )
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId( final String artifactId )
    {
        this.artifactId = artifactId;
    }

    public void validate()
        throws MojoExecutionException
    {
        if ( empty( groupId ) )
        {
            throw new MojoExecutionException( "Project references must contain groupId AND artifactId." );
        }
        else if ( empty( artifactId ) )
        {
            throw new MojoExecutionException( "Project references must contain groupId AND artifactId." );
        }
    }

    private boolean empty( final String str )
    {
        return str == null || str.trim().length() == 0;
    }

    public boolean matches( final MavenProject project )
    {
        return project.getGroupId().equals( groupId ) && project.getArtifactId().equals( artifactId );
    }

    @Override
    public String toString()
    {
        return groupId + ":" + artifactId;
    }

}
