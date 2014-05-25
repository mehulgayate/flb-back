#!/bin/bash

PROJECT_NAME2=flb-back2
PROJECT_NAME=flb-back
PROJECT_HOME=`pwd`
TARGET_WEBAPP=$PROJECT_HOME/target/$PROJECT_NAME

TOMCAT_HOME_DIR2=/evalua/servers/$PROJECT_NAME2/apache-tomcat-6.0.37
TOMCAT_BIN2=$TOMCAT_HOME_DIR2/bin
TOMCAT_WEBAPP2=$TOMCAT_HOME_DIR2/webapps

echo "PROJECT HOME : $PROJECT_HOME"

cd $TOMCAT_BIN2
  ./catalina.sh stop

cd $PROJECT_HOME
mvn clean

mvn compile war:exploded

if [ ! -L $TOMCAT_WEBAPP2/ROOT ]; then
cd $TOMCAT_WEBAPP2
ln -s $TARGET_WEBAPP ROOT
fi

cd $TARGET_WEBAPP
rm -rf static
ln -s $PROJECT_HOME/src/main/webapp/static static

cd WEB-INF
rm -rf jsp
ln -s $PROJECT_HOME/src/main/webapp/WEB-INF/jsp jsp

cd $TOMCAT_BIN2
  ./catalina.sh run
