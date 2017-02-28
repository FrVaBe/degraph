rem
rem build project and run degraph
rem Maven and Degraph need to be installed and on the PATH
rem

call mvn -f ../../../pom.xml clean install
mkdir ..\..\..\target\degraph
call degraph -f packages.config
call degraph -f packagesWithExclude.config
call degraph -f packagesWithNamedSlices.config