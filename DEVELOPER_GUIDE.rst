.. highlight:: sh

===============
Developer Guide
===============

.. rubric:: Table of contents

.. contents::
   :local:
   :depth: 2


Prerequisites
=============

JDK
---

OpenSearch builds using Java 11 at a minimum and supports JDK 11, 14 and 17. This means you must have a JDK of supported version installed with the environment variable `JAVA_HOME` referencing the path to Java home for your JDK installation::

   $ echo $JAVA_HOME
   /Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home

   $ java -version
    openjdk version "11.0.1" 2018-10-16
    OpenJDK Runtime Environment 18.9 (build 11.0.1+13)
    OpenJDK 64-Bit Server VM 18.9 (build 11.0.1+13, mixed mode)

Here are the official instructions on how to set ``JAVA_HOME`` for different platforms: https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/. 


Getting Source Code
===================

Now you can check out the code from your forked GitHub repository and create a new branch for your bug fix or enhancement work::

   $ git clone git@github.com:<your_account>/opensearch-ppl.git
   $ git checkout -b <branch_name>

If there is update in main or you want to keep the forked repository long living, you can sync it by following the instructions: https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/syncing-a-fork. Basically you just need to pull latest changes from upstream main once you add it for the first time::

   #Merge to your local main
   $ git fetch upstream
   $ git checkout main
   $ git merge upstream/main

   #Merge to your branch if any
   $ git checkout <branch_name>
   $ git merge main


Configuring IDEs
================

You can develop the plugin in your favorite IDEs such as Eclipse and IntelliJ IDEs. Before start making any code change, you may want to configure your IDEs. In this section, we show how to get IntelliJ up and running.

Java Language Level
-------------------

Although later version of JDK is required to build the plugin, the Java language level needs to be Java 8 for compatibility. Only in this case your plugin works with OpenSearch running against JDK 8. Otherwise it will raise runtime exception when executing new API from new JDK. In case your IDE doesn’t set it right, you may want to double check your project setting after import.

Remote Debugging
----------------

Firstly you need to add the following configuration to the JVM used by your IDE. For Intellij IDEA, it should be added to ``<OpenSearch installation>/config/jvm.options`` file. After configuring this, an agent in JVM will listen on the port when your OpenSearch bootstraps and wait for IDE debugger to connect. So you should be able to debug by setting up a “Remote Run/Debug Configuration”::

   -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005

License Header
--------------

Because our code is licensed under Apache 2, you need to add the following license header to all new source code files. To automate this whenever creating new file, you can follow instructions for your IDE::

   /*
    * Licensed under the Apache License, Version 2.0 (the "License").
    * You may not use this file except in compliance with the License.
    * A copy of the License is located at
    * 
    *    http://www.apache.org/licenses/LICENSE-2.0
    * 
    * or in the "license" file accompanying this file. This file is distributed 
    * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
    * express or implied. See the License for the specific language governing 
    * permissions and limitations under the License.
    */

For example, `here are the instructions for adding copyright profiles in IntelliJ IDEA <https://www.jetbrains.com/help/idea/copyright.html>`__.

Note that missing license header will be detected by Gradle license plugin and fails the build.


Making Code Changes
===================

Project Structure
-----------------

The plugin codebase is in standard layout of Gradle project::

   .
   ├── CODE_OF_CONDUCT.md
   ├── CONTRIBUTING.md
   ├── LICENSE.TXT
   ├── NOTICE
   ├── README.md
   ├── THIRD-PARTY
   ├── build.gradle
   ├── config
   ├── docs
   │   ├── attributions.md
   │   ├── category.json
   │   ├── dev
   │   └── user
   ├── gradle.properties
   ├── gradlew
   ├── gradlew.bat
   ├── settings.gradle
   ├── ppl
   └── spark

Here are sub-folders (Gradle modules) for plugin source code:

- ``ppl``: PPL language processor.
- ``spark`` : Spark PPL related query transformer
- ``protocol``: request/response protocol formatter.

Here are other files and sub-folders that you are likely to touch:

- ``build.gradle``: Gradle build script.
- ``docs``: documentation for developers and reference manual for users.
- ``doc-test``: code that run .rst docs in ``docs`` folder by Python doctest library.



Code Convention
---------------

Java files in the OpenSearch codebase are formatted with the Eclipse JDT formatter, using the `Spotless Gradle <https://github.com/diffplug/spotless/tree/master/plugin-gradle>`_ plugin. This plugin is configured in the project  `./gradle.properties`.

The formatting check can be run explicitly with::

./gradlew spotlessCheck

The code can be formatted with::

./gradlew spotlessApply

These tasks can also be run for specific modules, e.g.::

./gradlew server:spotlessCheck

For more information on the spotless for the OpenSearch project please see `https://github.com/opensearch-project/OpenSearch/blob/main/DEVELOPER_GUIDE.md#java-language-formatting-guidelines <https://github.com/opensearch-project/OpenSearch/blob/main/DEVELOPER_GUIDE.md#java-language-formatting-guidelines>`_.

Java files are formatted using `Spotless <https://github.com/diffplug/spotless>`_ conforming to `Google Java Format <https://github.com/google/google-java-format>`_.
   * - New line at end of file
   * - No unused import statements
   * - Fix import order to be alphabetical with static imports first (one block for static and one for non-static imports)
   * - Max line length is 100 characters (does not apply to import statements)
   * - Line spacing is 2 spaces
   * - Javadocs should be properly formatted in accordance to `Javadoc guidelines <https://www.oracle.com/ca-en/technical-resources/articles/java/javadoc-tool.html>`_
   * - Javadoc format can be maintained by wrapping javadoc with `<pre></pre>` HTML tags
   * - Strings can be formatted on multiple lines with a `+` with the correct indentation for the string.

Building and Running Tests
==========================

Gradle Build
------------

Most of the time you just need to run ./gradlew build which will make sure you pass all checks and testing. While you’re developing, you may want to run specific Gradle task only. In this case, you can run ./gradlew with task name which only triggers the task along with those it depends on. Here is a list for common tasks:

.. list-table::
   :widths: 30 50
   :header-rows: 1

   * - Gradle Task
     - Description
   * - ./gradlew assemble
     - Generate jar and zip files in build/distributions folder.
   * - ./gradlew generateGrammarSource
     - (Re-)Generate ANTLR parser from grammar file.
   * - ./gradlew compileJava
     - Compile all Java source files.
   * - ./gradlew test
     - Run all unit tests.
   * - ./gradlew :integ-test:integTest
     - Run all integration test (this takes time).
   * - ./gradlew :doctest:doctest
     - Run doctests
   * - ./gradlew build
     - Build plugin by run all tasks above (this takes time).
   * - ./gradlew pitest
     - Run PiTest mutation testing (see more info in `#1204 <https://github.com/opensearch-project/sql/pull/1204>`_)
   * - ./gradlew spotlessCheck
     - Runs Spotless to check for code style.
   * - ./gradlew spotlessApply
     - Automatically apply spotless code style changes.

For integration test, you can use ``-Dtests.class`` “UT full path” to run a task individually. For example ``./gradlew :integ-test:integTest -Dtests.class="*QueryIT"``.

To run the task above for specific module, you can do ``./gradlew :<module_name>:task``. For example, only build core module by ``./gradlew :core:build``.

Troubleshooting
---------------

Sometimes your Gradle build fails or timeout due to OpenSearch integration test process hung there. You can check this by the following commands::

   #Check if multiple Gradle daemons started by different JDK.
   #Kill unnecessary ones and restart if necessary.
   $ ps aux | grep -i gradle
   $ ./gradlew stop
   $ ./gradlew start

   #Check if OpenSearch integTest process hung there. Kill it if so.
   $ ps aux | grep -i opensearch

   #Clean and rebuild
   $ ./gradlew clean
   $ ./gradlew build


Documentation
=============

Dev Docs
--------

For new feature or big enhancement, it is worth document your design idea for others to understand your code better. There is already a docs/dev folder for all this kind of development documents.

Reference Manual
----------------

Doc Generator
>>>>>>>>>>>>>

Currently the reference manual documents are generated from a set of special integration tests. The integration tests use custom DSL to build ReStructure Text markup with real query and result set captured and documented.

1. Add a new template to ``src/test/resources/doctest/templates``.
2. Add a new test class as below with ``@DocTestConfig`` annotation specifying template and test data used.
3. Run ``./gradlew build`` to generate the actual documents into ``docs/user`` folder.

Sample test class:

.. code:: java

   @DocTestConfig(template = "interfaces/protocol.rst", testData = {"accounts.json"})
   public class ProtocolIT extends DocTest {
   
       @Section(1)
       public void test() {
           section(
               title("A New Section"),
               description(
                   "Describe what is the use of new functionality."
               ),
               example(
                   description("Describe what is the use case of this example to show"),
                   post("SELECT ...")
               )
           );
       }
   }

Doctest
>>>>>>>

Python doctest library makes our document executable which keeps it up-to-date to source code. The doc generator aforementioned served as scaffolding and generated many docs in short time. Now the examples inside is changed to doctest gradually. For more details please read `Doctest <./dev/Doctest.md>`_.


Backports
>>>>>>>>>

The Github workflow in `backport.yml <.github/workflows/backport.yml>`_ creates backport PRs automatically when the original PR
with an appropriate label `backport <backport-branch-name>` is merged to main with the backport workflow run successfully on the
PR. For example, if a PR on main needs to be backported to `1.x` branch, add a label `backport 1.x` to the PR and make sure the
backport workflow runs on the PR along with other checks. Once this PR is merged to main, the workflow will create a backport PR
to the `1.x` branch.
