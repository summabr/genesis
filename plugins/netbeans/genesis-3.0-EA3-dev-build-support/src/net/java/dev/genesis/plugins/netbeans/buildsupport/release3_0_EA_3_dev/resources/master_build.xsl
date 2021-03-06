<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:genesis="https://genesis.dev.java.net/ns/netbeans/projecttype/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project genesis">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="3"/>
    <xsl:template match="/">
        <xsl:comment><![CDATA[
*** GENERATED FROM project.xml - DO NOT EDIT  ***
***         EDIT ../build.xml INSTEAD         ***
]]></xsl:comment>
<project name="genesis-based-project" default="all" basedir="." 
         xmlns:genesis="https://genesis.dev.java.net/nonav/ns/master_build.xml">
   <target name="-pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the whole build process ]]></xsl:comment> 
   </target>

   <target name="-do-master-init">
      <property name="genesisBasedApplication.serverName" 
                value="${{genesisBasedApplication.name}}" />

      <property name="sources.dir" value="src" />
      <property name="build.dir" value="target" />
      <property name="classes.dir" value="${{build.dir}}/classes" />
      <property name="annotated.dir" value="${{build.dir}}/annotated" />
      <property name="timestamp.file" value=".timestamp" />
      <property name="verbose" value="true" />
      <property name="genjp" value="true" />

      <property name="local.mode" value="false" />
      <property name="remote.mode" value="true" />

      <property name="force.annotation" value="false" />
      <available property="supports.java.annotation" classname="java.lang.annotation.Annotation"  />

      <property name="keep.debug.information" value="true" />
      <condition property="java.source.level" value="1.4">
         <not>
            <isset property="supports.java.annotation" />
         </not>
      </condition>
      <condition property="java.source.level" value="1.5">
         <isset property="supports.java.annotation" />
      </condition>
   </target>

   <target name="-do-additional-init" />

   <target name="-do-init" depends="-do-master-init,-do-additional-init" />

   <target name="-post-init">
      <xsl:comment><![CDATA[ Override to do custom initialization ]]></xsl:comment> 
   </target>

   <target name="init" depends="-pre-init,-do-init,-post-init" />

   <target name="-pre-init-paths">
      <xsl:comment><![CDATA[ Override to do custom initialization ]]></xsl:comment> 
   </target>

   <target name="-do-master-init-paths">
      <property name="genesis.version" value="3.0-EA3-dev" />
      <property name="genesis.home" location="../../genesis" />
      <property name="genesis.dist" location="${{genesis.home}}/dist" />
      <property name="annotation.properties" 
                location="${{genesis.dist}}/annotation.properties" />

      <property name="aspectwerkz.dist" 
                location="${{genesis.home}}/lib/aspectwerkz" />
      <property name="commons.dist" location="${{genesis.home}}/lib/commons" />
      <property name="hibernate.dist" location="${{genesis.home}}/lib/hibernate" />
      <property name="j2ee.dist" location="${{genesis.home}}/lib/j2ee" />
      <property name="thinlet.dist" location="${{genesis.home}}/lib/thinlet" />
      <property name="xdoclet.dist" location="${{genesis.home}}/../xdoclet/dist" />
      <property name="anttasks.dist" location="${{genesis.home}}/../ant-tasks/dist" />
      <property name="backport175.dist" location="${{genesis.home}}/lib/backport175" />
      <property name="script.dist" location="${{genesis.home}}/lib/script" />

      <property name="jboss.app" 
                location="${{jboss.home}}/server/${{genesisBasedApplication.serverName}}" />
      <property name="jboss.client"
                location="${{jboss.home}}/client" />

      <property name="jdbc.driver.jar.name" value="hsqldb.jar" />
      <property name="jdbc.driver" 
                value="${{jboss.app}}/lib/${{jdbc.driver.jar.name}}" />

      <condition property="needs.annotation">
         <or>
            <istrue value="${{force.annotation}}" />
            <not>
               <isset property="supports.java.annotation" />
            </not>
         </or>
      </condition>

      <condition property="genesis-annotation.jar" value="genesis-annotation-jdk5-${{genesis.version}}.jar">
         <isset property="supports.java.annotation" />
      </condition>

      <condition property="genesis-annotation.jar" value="genesis-annotation-jdk14-${{genesis.version}}.jar">
         <not>
            <isset property="supports.java.annotation" />
         </not>
      </condition>

      <path id="aspectwerkz.path">
         <fileset dir="${{aspectwerkz.dist}}">
            <include name="aspectwerkz-*.jar" />
            <include name="ant*.jar" />
            <exclude name="asm*.jar" />
            <include name="concurrent*.jar" />
            <include name="dom4j*.jar" />
            <include name="javassist*.jar" />
            <include name="jrexx*.jar" />
            <include name="junit*.jar" />
            <include name="managementapi*.jar" />
            <include name="piccolo*.jar" />
            <include name="qdox*.jar" />
            <include name="trove*.jar" />
         </fileset>
      </path>

      <path id="j2ee.path">
         <fileset dir="${{j2ee.dist}}" includes="*.jar" />
      </path>

      <path id="xdoclet.path">
         <fileset dir="${{xdoclet.dist}}" includes="*.jar" />
      </path>
      
      <path id="backport175.path">
         <fileset dir="${{backport175.dist}}" includes="*.jar" />
      </path>

      <path id="anttasks.path">
         <path refid="backport175.path" />
         <path refid="aspectwerkz.path" />
         <fileset dir="${{anttasks.dist}}" includes="*.jar" />
      </path>

      <taskdef name="succeededtargets" 
               classname="net.java.dev.genesis.anttasks.SucceededTasksListener"
               classpathref="anttasks.path" />
       <succeededtargets alreadyexecuted="init,-pre-init-paths" />
   </target>

   <target name="-do-additional-init-paths" />

   <target name="-do-init-paths"
           depends="-do-master-init-paths,-do-additional-init-paths" />

   <target name="-post-init-paths">
      <xsl:comment><![CDATA[ Override to do custom initialization ]]></xsl:comment> 
   </target>

   <target name="init-paths" 
           depends="init,-pre-init-paths,-do-init-paths,-post-init-paths" />

   <target name="define-call-task" depends="init-paths">
      <taskdef name="call" 
               uri="https://genesis.dev.java.net/nonav/ns/master_build.xml"
               classname="net.java.dev.genesis.anttasks.Call"
               classpathref="anttasks.path" />
   </target>

   <target name="define-aw-macros" depends="init-paths">
      <macrodef name="annotationc"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="srcdir" />
         <attribute name="destdir" />
         <attribute name="classpath" />
         <attribute name="touchfile" />
         <element name="customize" optional="true" />

         <sequential>
            <taskdef name="annotationc" 
                     classname="net.java.dev.genesis.anttasks.aspectwerkz.AnnotationCTask" 
                     classpathref="anttasks.path" />

            <mkdir dir="@{{destdir}}" />
            <annotationc verbose="${{verbose}}"
                  srcdir="@{{srcdir}}"
                  destdir="@{{destdir}}"
                  properties="${{annotation.properties}}">
               <classpath>
                  <path location="@{{classpath}}" />
                  <fileset dir="${{genesis.dist}}" 
                           includes="genesis-annotation-jdk14-${{genesis.version}}.jar" />
               </classpath>
               <customize />
            </annotationc>
            <copy todir="@{{destdir}}">
               <fileset dir="@{{classpath}}" />
            </copy>
            <touch file="@{{touchfile}}" />
         </sequential>
      </macrodef>

      <macrodef name="awc"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="verbose" default="false" />
         <attribute name="taskverbose" default="${{verbose}}" />
         <attribute name="genjp" default="${{genjp}}" />
         <attribute name="classpath" />
         <attribute name="sourcepath" />
         <attribute name="destdir" />
         <attribute name="touchfile" />
         <sequential>
            <taskdef name="awc" 
                     classname="net.java.dev.genesis.anttasks.aspectwerkz.SimplifiedAspectWerkzCTask" 
                     classpathref="anttasks.path" />

            <mkdir dir="@{{destdir}}" />
            <awc classpathref="@{{classpath}}" genjp="@{{genjp}}" destdir="@{{destdir}}"
                 taskverbose="@{{taskverbose}}" verbose="@{{verbose}}">
               <sourcepath location="@{{sourcepath}}"/>
            </awc>
            <touch file="@{{touchfile}}"/>
         </sequential>
      </macrodef>
   </target>

   <target name="define-hibernate-tasks" if="shared.hibernate.tasks.needed" 
           depends="init-paths">
      <taskdef name="hibernatedoclet" 
               classname="xdoclet.modules.hibernate.HibernateDocletTask" 
               classpathref="xdoclet.path" />
   </target>

   <target name="shared:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the -shared:init target ]]></xsl:comment> 
   </target>

   <target name="shared:init" depends="init-paths,shared:pre-init">
      <property name="has.shared.sources" value="true" />

      <property name="shared.dir" value="modules/shared" />
      <property name="shared.sources.dir" 
                value="${{shared.dir}}/${{sources.dir}}" />
      <property name="shared.classes.dir" 
                value="${{shared.dir}}/${{classes.dir}}" />
      <property name="shared.annotated.dir" 
                value="${{shared.dir}}/${{annotated.dir}}" />
      <property name="shared.annotated.timestamp.file" 
                location="${{shared.annotated.dir}}/${{timestamp.file}}" />
      <property name="shared.hibernate.dir" 
                value="${{shared.dir}}/${{build.dir}}/hibernate" />
      <property name="shared.hibernate.timestamp.file" 
                location="${{shared.hibernate.dir}}/${{timestamp.file}}" />
      <property name="shared.hibernate.merge.dir"
                value="${{shared.sources.dir}}" />

      <property name="hibernate.generate.files" value="true" />
      <property name="hibernate.session.factory.jndi.name" 
                value="jboss:/hibernate/SessionFactory" />
      <property name="hibernate.jboss.service.name" 
                value="HibernateFactory,name=HibernateFactory" />
      <property name="hibernate.dialect" 
                value="net.sf.hibernate.dialect.HSQLDialect"/>
      <property name="hibernate.show.sql" value="true" />
      <property name="hibernate.version" value="2.1.8" />

      <property name="jdbc.driver.class" value="org.hsqldb.jdbcDriver" />
      <property name="jdbc.username" value="sa" />
      <property name="jdbc.password" value="" />
      <property name="jdbc.connection.url" 
                value="jdbc:hsqldb:genesis/useradmin" />

      <property name="jboss.datasource.jndi.name" value="java:/DefaultDS" />

      <path id="shared.standard.javac.classpath">
         <fileset dir="${{genesis.dist}}">
            <include name="${{genesis-annotation.jar}}" />
            <include name="genesis-shared-${{genesis.version}}.jar" />
         </fileset>
         <fileset dir="${{hibernate.dist}}" includes="hibernate*.jar" />
         <fileset dir="${{commons.dist}}">
            <include name="commons-beanutils*.jar" />
            <include name="commons-logging*.jar" />
            <include name="reusable-components*.jar" />
         </fileset>
      </path>
   </target>

   <target name="shared:define-conditions" depends="shared:init">
      <condition property="shared.compile">
         <istrue value="${{has.shared.sources}}" />
      </condition>
      <condition property="shared.clean">
         <istrue value="${{clean.build}}" />
      </condition>
   </target>

   <target name="shared:clean" if="shared.clean" 
           depends="shared:define-conditions">
      <delete dir="${{shared.classes.dir}}" />
      <delete dir="${{shared.annotated.dir}}" />
      <delete file="${{shared.annotated.timestamp.file}}" />
      <delete dir="${{shared.hibernate.dir}}" />
      <delete file="${{shared.hibernate.timestamp.file}}" />
   </target>

   <target name="shared:pre-define-classpath">
      <xsl:comment><![CDATA[ Override to define shared.javac.overriden.classpath or 
         shared.additional.javac.classpath]]></xsl:comment> 
   </target>

   <target name="shared:check-classpath-conditions" depends="shared:init">
      <condition property="shared.set.custom.javac.classpath">
         <isreference refid="shared.javac.overriden.classpath" type="path" />
      </condition>
      <condition property="shared.set.additional.javac.classpath">
         <and>
            <not>
               <isset property="shared.set.custom.javac.classpath" />
            </not>
            <isreference refid="shared.additional.javac.classpath" />
         </and>
      </condition>
      <condition property="shared.set.standard.javac.classpath">
         <and>
            <not>
               <isset property="shared.set.additional.javac.classpath" />
            </not>
            <not>
               <isset property="shared.set.custom.javac.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="shared:define-overriden-classpath"
           depends="shared:init" if="shared.set.custom.javac.classpath">
      <path id="shared.javac.classpath">
         <path refid="shared.javac.overriden.classpath" />
      </path>
   </target>

   <target name="shared:define-classpath-with-extensions" 
           depends="shared:init" if="shared.set.additional.javac.classpath">
      <path id="shared.javac.classpath">
         <path refid="shared.standard.javac.classpath" />
         <path refid="shared.additional.javac.classpath" />
      </path>
   </target>

   <target name="shared:define-standard-classpath"
           depends="shared:init" if="shared.set.standard.javac.classpath">
      <path id="shared.javac.classpath">
         <path refid="shared.standard.javac.classpath" />
      </path>
   </target>

   <target name="shared:define-classpath" 
           depends="shared:init,shared:pre-define-classpath,
                    shared:check-classpath-conditions,
                    shared:define-overriden-classpath,
                    shared:define-classpath-with-extensions,
                    shared:define-standard-classpath"/>

   <target name="shared:javac" if="shared.compile" 
           depends="shared:clean,shared:define-classpath">
      <mkdir dir="${{shared.classes.dir}}" />
      <javac destdir="${{shared.classes.dir}}"
            debug="${{keep.debug.information}}"
            source="${{java.source.level}}"
            classpathref="shared.javac.classpath">
         <src path="${{shared.sources.dir}}" />
      </javac>
   </target>

   <target name="shared:check-annotations-needed" depends="shared:init">
      <condition property="shared.classes.changed">
         <and>
            <istrue value="${{has.shared.sources}}" />
            <or>
               <not>
                  <available file="${{shared.annotated.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{shared.classes.dir}}" />
                     <mapper type="merge" to="${{shared.annotated.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>

      <condition property="aw.tasks.needed">
         <istrue value="${{shared.classes.changed}}" />
      </condition>

      <condition property="shared.annotationc.tasks.needed">
         <and>
            <istrue value="${{shared.classes.changed}}" />
            <istrue value="${{needs.annotation}}" />
         </and>
      </condition>
   </target>

   <target name="shared:annotations" if="shared.annotationc.tasks.needed"
           depends="shared:check-annotations-needed,define-aw-macros">
      <genesis:annotationc srcdir="${{shared.sources.dir}}" 
                           destdir="${{shared.annotated.dir}}"
                           classpath="${{shared.classes.dir}}"
                           touchfile="${{shared.annotated.timestamp.file}}" />
   </target>

   <target name="shared:master-check-hibernate-conditions" depends="shared:init">
      <condition property="shared.hibernate.needed">
         <and>
            <istrue value="${{has.shared.sources}}" />
            <istrue value="${{hibernate.generate.files}}" />
            <or>
               <istrue value="${{hibernate.force.generation}}" />
               <not>
                  <available file="${{shared.hibernate.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{shared.sources.dir}}" includes="**/*.java" />
                     <mapper type="merge" to="${{shared.hibernate.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>

      <condition property="shared.hibernate.tasks.needed">
         <istrue value="${{shared.hibernate.needed}}" />
      </condition>
   </target>

   <target name="shared:additional-check-hibernate-conditions" />

   <target name="shared:check-hibernate-conditions"
           depends="shared:master-check-hibernate-conditions,
                    shared:additional-check-hibernate-conditions"/>
   
   <target name="master-hibernate-macrodefs" if="shared.hibernate.tasks.needed"
      depends="deploy:check-conditions,server:check-conditions">
      <macrodef name="hibernatedoclet-macro" 
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <element name="subtasks" optional="true" />
         <sequential>
            <delete dir="${{shared.hibernate.dir}}" />
            <mkdir dir="${{shared.hibernate.dir}}" />
            <hibernatedoclet destdir="${{shared.hibernate.dir}}" 
                             verbose="${{verbose}}">
               <fileset dir="${{shared.sources.dir}}">
                  <include name="**/*.java" />
               </fileset>
               <hibernate version="2.0"
                          xmlencoding="ISO-8859-1"
                          validateXML="false" 
                          mergedir="${{shared.hibernate.merge.dir}}" />
               <subtasks />
            </hibernatedoclet>
            <touch file="${{shared.hibernate.timestamp.file}}" />
         </sequential>
      </macrodef>
      <macrodef name="hibernatedoclet-remote"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <element name="customize" optional="true" />
         <sequential>
            <basename property="jboss.ds.name" 
                      file="${{jboss.datasource.jndi.name}}"/>
            <loadproperties resource="org/jboss/version.properties">
               <classpath>
                  <fileset dir="${{jboss.home}}/bin" includes="run.jar"/>
               </classpath>
               <filterchain>
                  <tokenfilter>
                      <replacestring from="version.major" to="jboss.jca.service.name" />
                      <replacestring from="3" to="LocalTxCM"/>
                      <replacestring from="4" to="DataSourceBinding"/>
                  </tokenfilter>
                  <linecontains>
                     <contains value="jboss.jca.service.name"/>
                  </linecontains>
               </filterchain>
            </loadproperties>
            <fail unless="jboss.jca.service.name">
               Unable to determine JBoss version
            </fail>
            <genesis:hibernatedoclet-macro>
               <subtasks>
                  <jbossservice jndiName="${{hibernate.session.factory.jndi.name}}"
                                serviceName="${{hibernate.jboss.service.name}}"
                                dialect="${{hibernate.dialect}}"
                                dataSource="${{jboss.datasource.jndi.name}}"
                                transactionManagerStrategy="net.sf.hibernate.transaction.JBossTransactionManagerLookup"
                                transactionStrategy="net.sf.hibernate.transaction.JTATransactionFactory"
                                userTransactionName="UserTransaction"
                                showSql="${{hibernate.show.sql}}"
                                depends="jboss.jca:service=${{jboss.jca.service.name}},name=${{jboss.ds.name}}"
                                version="${{hibernate.version}}" />
                  <customize />
               </subtasks>
            </genesis:hibernatedoclet-macro>
         </sequential>
      </macrodef>
   </target>

   <target name="additional-hibernate-macrodefs" if="shared.hibernate.tasks.needed" />

   <target name="hibernate-macrodefs" if="shared.hibernate.tasks.needed"
           depends="define-hibernate-tasks,master-hibernate-macrodefs,
                    additional-hibernate-macrodefs" />

   <target name="shared:hibernate-doclet" />

   <target name="shared:compile" 
           depends="shared:javac,shared:annotations,shared:hibernate-doclet" />

   <target name="client:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the client:init target ]]></xsl:comment> 
   </target>

   <target name="client:pre-additional-init" />

   <target name="client:master-init">
      <property name="has.client.sources" value="true" />

      <property name="client.dir" value="modules/client" />
      <property name="client.sources.dir" value="${{client.dir}}/${{sources.dir}}" />
      <property name="client.classes.dir" value="${{client.dir}}/${{classes.dir}}" />
   </target>

   <target name="client:additional-init" />

   <target name="client:init"
           depends="init-paths,
                    shared:init,
                    client:pre-init,
                    client:pre-additional-init,
                    client:master-init,
                    client:additional-init" />

   <target name="client:define-conditions" depends="client:init">
      <condition property="client.compile">
         <istrue value="${{has.client.sources}}" />
      </condition>
      <condition property="client.clean">
         <istrue value="${{clean.build}}" />
      </condition>
   </target>

   <target name="client:master-clean" if="client.clean">
      <delete dir="${{client.classes.dir}}" />
      <delete dir="${{client.annotated.dir}}" />
      <delete file="${{client.annotated.timestamp.file}}" />
      <delete dir="${{client.hibernate.dir}}" />
      <delete file="${{client.hibernate.timestamp.file}}" />
   </target>

   <target name="client:additional-clean" if="client.clean" />

   <target name="client:clean" if="client.clean" 
              depends="client:define-conditions,client:master-clean,
                       client:additional-clean" />

   <target name="client:pre-define-classpath">
      <xsl:comment><![CDATA[ Override to define client.javac.overriden.classpath or 
         client.additional.javac.classpath]]></xsl:comment> 
   </target>

   <target name="client:check-classpath-conditions" depends="client:init">
      <condition property="client.set.custom.javac.classpath">
         <isreference refid="client.javac.overriden.classpath" type="path" />
      </condition>
      <condition property="client.set.additional.javac.classpath">
         <and>
            <not>
               <isset property="client.set.custom.javac.classpath" />
            </not>
            <isreference refid="client.additional.javac.classpath" />
         </and>
      </condition>
      <condition property="client.set.standard.javac.classpath">
         <and>
            <not>
               <isset property="client.set.additional.javac.classpath" />
            </not>
            <not>
               <isset property="client.set.custom.javac.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="client:define-overriden-classpath"
           depends="client:init" if="client.set.custom.javac.classpath">
      <path id="client.javac.classpath">
         <path refid="client.javac.overriden.classpath" />
      </path>
   </target>

   <target name="client:define-classpath-with-extensions" 
           depends="client:init" if="client.set.additional.javac.classpath">
      <path id="client.javac.classpath">
         <path refid="client.standard.javac.classpath" />
         <path refid="client.additional.javac.classpath" />
      </path>
   </target>

   <target name="client:define-standard-classpath"
           depends="client:init" if="client.set.standard.javac.classpath">
      <path id="client.javac.classpath">
         <path refid="client.standard.javac.classpath" />
      </path>
   </target>

   <target name="client:define-classpath" 
           depends="client:init,client:pre-define-classpath,
                    client:check-classpath-conditions,
                    client:define-overriden-classpath,
                    client:define-classpath-with-extensions,
                    client:define-standard-classpath"/>

   <target name="client:javac" if="client.compile" 
           depends="client:clean,client:define-classpath">
      <mkdir dir="${{client.classes.dir}}" />
      <javac destdir="${{client.classes.dir}}"
            debug="${{keep.debug.information}}"
            source="${{java.source.level}}"
            classpathref="client.javac.classpath">
         <src path="${{client.sources.dir}}" />
      </javac>
   </target>

   <target name="client:compile" depends="client:javac" />

   <target name="-do-custom-compile">
      <xsl:comment><![CDATA[ Override to implement extra tasks that should be performed after 
           compiling client/shared sources]]></xsl:comment> 
   </target>

   <target name="compile" 
           depends="shared:compile,client:compile,-do-custom-compile"/>

   <target name="weaving:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the weaving:init target ]]></xsl:comment> 
   </target>

   <target name="weaving:master-init">
      <property name="needs.weaving" value="true" />
      <property name="weaving.dir" value="${{build.dir}}/weaving" />
      <property name="preweaving.dir" value="${{build.dir}}/pre-weaving" />
   </target>

   <target name="weaving:additional-init" />

   <target name="weaving:init" depends="init-paths,
                                        weaving:pre-init,
                                        client:init,
                                        shared:define-classpath,
                                        weaving:master-init,
                                        weaving:additional-init" />

   <target name="weaving:define-conditions" depends="weaving:init">
      <condition property="weaving.clean">
         <istrue value="${{clean.build}}" />
      </condition>
   </target>

   <target name="weaving:clean" if="weaving.clean" 
              depends="weaving:define-conditions" />

   <target name="weaving:define-default-copy-fileset">
      <condition property="default-copy-fileset" value="${{shared.annotated.dir}}">
         <istrue value="${{shared.annotationc.tasks.needed}}" />
      </condition>
      <condition property="default-copy-fileset" value="${{shared.classes.dir}}">
         <not>      
            <istrue value="${{shared.annotationc.tasks.needed}}" />
         </not>
      </condition>
   </target>

   <target name="weaving:master-macrodefs" depends="weaving:define-default-copy-fileset">
      <macrodef name="weaving-copy-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="targetdir" />
         <attribute name="xml" />
         <element name="default-copy-fileset" optional="true" />
         <element name="copy-fileset" optional="true" />
         <element name="default-unjar-fileset" optional="true" />
         <element name="unjar-fileset" optional="true" />
         <sequential>
            <mkdir dir="@{{targetdir}}" />

            <copy todir="@{{targetdir}}">
               <default-copy-fileset />
               <copy-fileset />
            </copy>
            <unjar dest="@{{targetdir}}" overwrite="false">
               <default-unjar-fileset />
               <unjar-fileset />
            </unjar>
            <copy file="@{{xml}}" 
                  tofile="@{{targetdir}}/META-INF/aop.xml" />
         </sequential>
      </macrodef>

      <presetdef name="weaving-copy"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:weaving-copy-macro>
            <default-copy-fileset>
               <fileset dir="${{default-copy-fileset}}" includes="**/*.class" />
            </default-copy-fileset>
            <default-unjar-fileset>
               <fileset dir="${{genesis.dist}}"
                        includes="genesis-shared-annotated-${{genesis.version}}.jar" />
            </default-unjar-fileset>
         </genesis:weaving-copy-macro>
      </presetdef>
   </target>

   <target name="weaving:additional-macrodefs" />

   <target name="weaving:macrodefs"
           depends="weaving:master-macrodefs,weaving:additional-macrodefs"/>

   <target name="-do-custom-weaving">
      <xsl:comment><![CDATA[ Override to implement extra tasks that should be performed after 
           the weaving process ]]></xsl:comment> 
   </target>

   <target name="weaving" depends="-do-custom-weaving" />

   <target name="jar:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the jar:init target ]]></xsl:comment> 
   </target>

   <target name="jar:master-init">
      <property name="needs.jar" value="true" />
   </target>

   <target name="jar:additional-init" />

   <target name="jar:init" 
              depends="init-paths,jar:pre-init,weaving:init,jar:master-init,
                       jar:additional-init" />

   <target name="jar:clean" depends="jar:init" />

   <target name="jar:master-check-conditions">
      <condition property="jar.needed">
         <istrue value="${{needs.jar}}" />
      </condition>

      <condition property="jar.shared.needed">
         <istrue value="${{needs.jar}}" />
      </condition>
   </target>

   <target name="jar:additional-check-conditions" />

   <target name="jar:check-conditions"
           depends="jar:init,jar:master-check-conditions,
                    jar:additional-check-conditions" />

   <target name="jar:master-macrodefs" if="jar.needed">
      <patternset id="sources.patternset">
         <exclude name="${{timestamp.file}}" />
         <exclude name="**/CVS/**" />
         <exclude name="**/*.bkp" />
         <exclude name="**/*.bak" />
         <exclude name="**/*.class" />
         <exclude name="**/*.jav~" />
         <exclude name="**/*.java" />
         <exclude name="**/*.java~" />
         <exclude name="META-INF/local-aop.xml" />
         <exclude name="META-INF/remote-aop.xml" />
         <exclude name="META-INF/aop.xml" />
      </patternset>

      <patternset id="classes.patternset">
         <exclude name="${{timestamp.file}}"/>
      </patternset>

      <macrodef name="jar-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="destfile" />
         <attribute name="classesdir" />
         <attribute name="sourcesdir" />
         <element name="classesdir-selector" optional="true"/>
         <element name="sourcesdir-selector" optional="true"/>
         <element name="additional-filesets" optional="true"/>
         <element name="customize" optional="true"/>
         <sequential>
            <jar destfile="@{{destfile}}">
               <fileset dir="@{{classesdir}}">
                  <classesdir-selector />
               </fileset>
               <fileset dir="@{{sourcesdir}}">
                  <sourcesdir-selector />
               </fileset>
               <additional-filesets />
               <customize />
            </jar>
         </sequential>
      </macrodef>
      <presetdef name="jar"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:jar-macro>
            <classesdir-selector>
               <patternset refid="classes.patternset" />
            </classesdir-selector>
            <sourcesdir-selector>
               <patternset refid="sources.patternset" />
            </sourcesdir-selector>
         </genesis:jar-macro>
      </presetdef>
   </target>

   <target name="jar:additional-macrodefs" if="jar.needed" />

   <target name="jar:macrodefs" if="jar.needed"
           depends="jar:init,jar:master-macrodefs,jar:additional-macrodefs" />

   <target name="-do-custom-jar">
      <xsl:comment><![CDATA[ Override to implement extra tasks that should be performed after 
           generating the default jar files ]]></xsl:comment> 
   </target>

   <target name="jar" depends="-do-custom-jar" />

   <target name="server-artifact:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the server-artifact:init target ]]></xsl:comment> 
   </target>

   <target name="server-artifact:master-init">
      <property name="needs.server.artifact" value="${{remote.mode}}" />

      <property name="server.artifact.location" 
               location="${{build.dir}}/${{genesisBasedApplication.name}}.sar" />
   </target>

   <target name="server-artifact:additional-init" />

   <target name="server-artifact:init" 
           depends="init-paths,jar:init,shared:init,server-artifact:pre-init,
                    server-artifact:master-init,server-artifact:additional-init" />

   <target name="server-artifact:master-clean">
      <delete file="${{server.artifact.location}}" />
   </target>

   <target name="server-artifact:additional-clean" />

   <target name="server-artifact:clean"
           depends="server-artifact:init,server-artifact:master-clean,
                    server-artifact:additional-clean" />

   <target name="server-artifact:check-conditions" 
           depends="server-artifact:init">
      <condition property="server.artifact.needed">
         <istrue value="${{needs.server.artifact}}" />
      </condition>
   </target>

   <target name="server-artifact:master-macrodefs" if="server.artifact.needed">
      <macrodef name="sar-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <element name="customize" optional="true"/>
         <element name="additional-fileset" optional="true" />
         <sequential>
            <zip destfile="${{server.artifact.location}}">
               <fileset dir="${{shared.hibernate.dir}}">
                  <include name="**/*.hbm.xml" />
               </fileset>
               <zipfileset dir="${{shared.hibernate.dir}}"
                     includes="jboss-service.xml" prefix="META-INF" />
               <fileset dir="${{shared.sources.dir}}">
                  <include name="hibernate.properties" />
               </fileset>
               <fileset dir="${{hibernate.dist}}">
                  <include name="commons-logging*.jar" />
                  <include name="log4j*.jar" />
                  <include name="dom4j*.jar" />
                  <include name="optional*.jar" />
                  <include name="commons-collections*.jar" />
                  <include name="ehcache*.jar" />
                  <include name="cglib*.jar" />
                  <include name="odmg*.jar" />
                  <include name="hibernate*.jar" />
               </fileset>
               <fileset dir="${{commons.dist}}">
                  <include name="commons-beanutils*.jar" />
                  <include name="reusable-components*.jar" />
               </fileset>
               <additional-fileset />
               <customize />
            </zip>
         </sequential>
      </macrodef>
   </target>

   <target name="server-artifact:additional-macrodefs" if="server.artifact.needed"/>

   <target name="server-artifact:macrodefs" if="server.artifact.needed"
           depends="server-artifact:check-conditions,server-artifact:master-macrodefs,
                    server-artifact:additional-macrodefs" />

   <target name="-do-server-artifact" />

   <target name="server-artifact" depends="define-call-task,server-artifact:macrodefs"
           if="server.artifact.needed">
      <genesis:call target="-do-server-artifact" />
   </target>

   <target name="deploy:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the deploy:init target ]]></xsl:comment> 
   </target>

   <target name="deploy:master-init">
      <property name="jboss.app.deploy" location="${{jboss.app}}/deploy" />
      <property name="jboss.default.server" 
                location="${{jboss.home}}/server/default" />
      <property name="jboss.datasource.config.xml" 
                location="${{jboss.default.server}}/deploy/hsqldb-ds.xml" />
   </target>

   <target name="deploy:additional-init" />

   <target name="deploy:init" 
           depends="init-paths,server-artifact:init,deploy:pre-init,
                    deploy:master-init,deploy:additional-init" />

   <target name="deploy:clean" depends="deploy:init" />

   <target name="deploy:master-check-conditions">
      <condition property="deploy.needed">
         <istrue value="${{needs.deploy}}" />
      </condition>
      
      <condition property="deploy.create.server.needed">
         <and>
            <istrue value="${{deploy.needed}}" />
            <not>
               <available type="dir" file="${{jboss.app}}" />
            </not>
         </and>
      </condition>
   </target>

   <target name="deploy:additional-check-conditions" />

   <target name="deploy:check-conditions"
           depends="deploy:init,deploy:master-check-conditions,
                    deploy:additional-check-conditions" />

   <target name="deploy:macrodefs" depends="deploy:check-conditions"
           if="deploy.needed">
      <macrodef name="deploy-server-artifact-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <element name="default-filesets" optional="true" />
         <element name="filesets" optional="true" />
         <element name="validation-task" optional="false" />
         <sequential>
            <copy todir="${{jboss.app.deploy}}">
               <default-filesets />
               <filesets />
            </copy>
         </sequential>
      </macrodef>

      <presetdef name="deploy-server-artifact"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:deploy-server-artifact-macro>
            <default-filesets>
               <fileset file="${{jboss.datasource.config.xml}}" />
               <fileset dir="${{genesis.dist}}">
                  <include name="genesis-server-${{genesis.version}}.jar"/>
               </fileset>
               <fileset file="${{server.artifact.location}}" />
            </default-filesets>
         </genesis:deploy-server-artifact-macro>
      </presetdef>
   </target>

   <target name="server:check-conditions" if="deploy.needed">
      <condition property="jboss.home.not.set">
         <not>
            <isset property="jboss.home" />
         </not>
      </condition>
      <fail if="jboss.home.not.set">
         Set 'jboss.home' properly in buid.properties file. 
         See build.properties.sample for details.
      </fail>
      <condition property="jboss.home.not.found">
         <not>
            <available type="dir" file="${{jboss.home}}" />
         </not>
      </condition>
      <fail if="jboss.home.not.found">
         JBoss Home not found. It's current value is ${{jboss.home}}. 
         Set 'jboss.home' properly in buid.properties file. 
         See build.properties.sample for details.
      </fail>
   </target>
   
   <target name="deploy:create-server" if="deploy.create.server.needed"
           depends="deploy:check-conditions,server:check-conditions">
      <mkdir dir="${{jboss.app}}" />
      <copy todir="${{jboss.app}}">
         <fileset dir="${{jboss.default.server}}">
            <xsl:comment><![CDATA[ Common files (JBoss 3.2.x and JBoxx 4.x) ]]></xsl:comment> 
            <include name="deploy/jbossweb-tomcat*.sar/**" />
            <include name="deploy/jboss-local-jdbc.rar" />
            <include name="conf/**" />
            <include name="lib/**" />
            <exclude name="lib/hibernate*.jar" />
            
            <xsl:comment><![CDATA[ JBoss 3.2.x specific files  ]]></xsl:comment> 
            <include name="deploy/jboss-jca.sar" />
            <include name="deploy/transaction-service.xml" />
            
            <xsl:comment><![CDATA[ JBoss 4.x specific files  ]]></xsl:comment> 
            <include name="deploy/ejb-deployer.xml" />
            <include name="deploy/jbossjca-service.xml" />
         </fileset>
      </copy>
   </target>

   <target name="-do-deploy-server-artifact">
      <genesis:deploy-server-artifact />
   </target>

   <target name="deploy:server-artifact" depends="define-call-task,deploy:macrodefs"
           if="deploy.needed">
      <genesis:call target="-do-deploy-server-artifact" />
   </target>

   <target name="deploy" 
           depends="deploy:create-server,deploy:server-artifact" />

   <target name="all" depends="compile,weaving,jar,server-artifact,deploy" 
           description="Builds the whole project"/>

   <target name="pre-clean">
      <property name="clean.build" value="true" />
   </target>

   <target name="-do-custom-clean">
      <xsl:comment><![CDATA[ Override to implement custom clean tasks ]]></xsl:comment> 
   </target>

   <target name="clean" 
           depends="pre-clean,shared:clean,client:clean,weaving:clean,jar:clean,
                    server-artifact:clean,-do-custom-clean" 
           description="Deletes build artifacts" />

   <target name="clean-build" depends="clean,all" 
           description="Deletes build artifacts before building the project"/>

</project>
   </xsl:template>
</xsl:stylesheet> 
