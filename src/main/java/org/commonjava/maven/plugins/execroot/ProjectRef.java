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
