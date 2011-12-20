Directory Plugin for Maven
==========================

Disclaimer
----------

This plugin is for use in very specific situations!

If you're not comfortable guaranteeing that a whole multimodule project structure will always be present on disk every time a build is run, then be warned that using this plugin may produce inconsistent results. Inconsistencies like this can cause false positives or false negatives - builds that succeed when they should have failed, or fail when they should have succeeded.

You have been warned.


Overview
--------

The Directory Plugin for Maven is used to discover various project-related paths, such as the execution root directory, the directory for a specific project in the current build session, or the highest project basedir (closed to the filesystem root directory) available in the projects loaded from disk (not resolved from a remote repository). The plugin will then reflect this value to the console, and also inject it into each project's properties using the value of the `property` plugin parameter.

This plugin is meant to be run as part of the standard build lifecycle, to help establish locations for files in multimodule builds, where the directory structure referenced is stable and will always be intact.


Using
-----

The basic plugin declaration looks like the following:

    <build>
      <plugins>
        <plugin>
          <groupId>org.commonjava.maven.plugins</groupId>
          <artifactId>directory-maven-plugin</artifactId>
          <version>0.1</version>
          <executions>
            <execution>
              <id>directories</id>
              <goals>
                <goal>[GOAL NAME]</goal>
              </goals>
              <phase>initialize</phase>
              <configuration>
                <property>myDirectory</property>
              </configuration>
            </execution>
          </executions>
        </plugin>
        <plugin>
          <artifactId>maven-antrun-plugin</artifactId>
          <version>1.7</version>
          <executions>
            <execution>
              <id>echo</id>
              <phase>initialize</phase>
              <goals>
                <goal>run</goal>
              </goals>
              <configuration>
                <target>
                  <echo>Test Configuration Directory: ${myDirectory}/src/test/configs</echo>
                </target>
              </configuration>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>

Note: We're using the `antrun` plugin here to illustrate the proper way to make use of the discovered directories. They will be useful **ONLY IN PLUGIN CONFIGURATIONS, NOT DURING POM INTERPOLATION**.

`execution-root` Goal
---------------------

This goal's output is roughly equivalent to using the `${session.executionRootDirectory}` expression in a plugin parameter value. Using the `execution-root` goal in place of `[GOAL NAME]` above, we see the following:

    [INFO] --- directory-maven-plugin:0.1-SNAPSHOT:execution-root (directories) @ routem-web-admin ---
    [INFO] Execution-Root set to: /Users/jdcasey/workspace/couch-java-looseLeaf/routem
    [INFO] 
    [INFO] --- maven-antrun-plugin:1.7:run (echo) @ routem-web-admin ---
    [INFO] Executing tasks

    main:
         [echo] Test Configuration Directory: /Users/jdcasey/workspace/couch-java-looseLeaf/routem/src/test/configs


**NOTE:** This goal will inject a property that contains the absolute path of the directory in which Maven was invoked. Each project will have the property, and any plugins that execute after the `directory:execution-root` runs will have access to it.

`directory-of` Goal
-------------------

If, instead of the execution root, you need to reference a directory in a specific module within the reactor, but don't want to do endless relative-path calculus, you can use the `directory-of` goal. For this goal to function properly, you need to specify a `project` parameter containing `groupId` and `artifactId`, like this:

    <configuration>
      <property>myDirectory</property>
      <project>
        <groupId>org.commonjava.routem</groupId>
        <artifactId>routem-api</artifactId>
      </project>
    </configuration>


Now, when we substitute `directory-of` for `[GOAL NAME]` in the usage template above, we get output like this:


    [INFO] --- directory-maven-plugin:0.1-SNAPSHOT:directory-of (directories) @ routem-web-admin ---
    [INFO] Directory of org.commonjava.routem:routem-api set to: /Users/jdcasey/workspace/couch-java-looseLeaf/routem/api
    [INFO] 
    [INFO] --- maven-antrun-plugin:1.7:run (echo) @ routem-web-admin ---
    [INFO] Executing tasks

    main:
         [echo] Test Configuration Directory: /Users/jdcasey/workspace/couch-java-looseLeaf/routem/api/src/test/configs


**NOTE:** This goal will function similarly to `execution-root` in terms of injecting a project property for later plugins to use. **HOWEVER**, if the reference project isn't found in the current build session, this goal will fail the build.

`highest-basedir` Goal
----------------------

If you have a multimodule project structure on disk, and you want the flexibility to reference directories in a parent project's directory structure, even while executing only the child build, you can use the `highest-basedir` goal. This goal will traverse all projects and their parents, going up the inheritance hierarchy until it runs into a POM that was resolved from the repository instead of being built from disk. At this point, it will sort the paths given by `${project.basedir}` for each project, and attempt to select the shortest path from the list.

If the multimodule hierarchy uses multiple sibling parents from the disk that are on the same path-depth in the filesystem, the goal will fail the build. If this is the case, you may prefer to use `directory-of` instead.

If we modify the `execution-root` example to use the `highest-basedir` goal, then execute from the web/admin subdirectory of our sample project, we will see the following:

    [INFO] --- directory-maven-plugin:0.1-SNAPSHOT:highest-basedir (directories) @ routem-web-admin ---
    [INFO] Highest basedir set to: /Users/jdcasey/workspace/couch-java-looseLeaf/routem
    [INFO] 
    [INFO] --- maven-antrun-plugin:1.7:run (echo) @ routem-web-admin ---
    [INFO] Executing tasks

    main:
         [echo] Test Configuration Directory: /Users/jdcasey/workspace/couch-java-looseLeaf/routem/src/test/configs

That is, the highest parent basedir attainable using the `relativePath` element of the projects' `parent` specifications.