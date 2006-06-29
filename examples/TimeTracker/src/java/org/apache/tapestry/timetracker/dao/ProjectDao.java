// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.apache.tapestry.timetracker.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.timetracker.model.Project;

/**
 * Manages db actions related to {@link Project} instances.
 * 
 * @author jkuhnert
 */
public class ProjectDao extends BaseDao
{
    
    /**
     * Default constructor.
     */
    public ProjectDao()
    {
        super();
    }
    
    /**
     * Creates a list of all projects.
     * @return
     */
    public List<Project> listProjects()
    {
        List<Project> ret = new ArrayList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            ps = _conn.prepareStatement("select project_id, name from projects");
            rs = ps.executeQuery();
            
            int x = 0;
            while (rs.next()) {
                x = 0;
                Project pr = new Project();
                
                pr.setId(rs.getLong(++x));
                pr.setName(rs.getString(++x));
                
                ret.add(pr);
            }
            
            return ret;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) { }
            try { if (ps != null) ps.close(); } catch (Exception e) { }
        }
    }
}