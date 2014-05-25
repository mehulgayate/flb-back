#!/bin/bash

PROJECT_NAME1=flb-back1
PROJECT_NAME=flb-back
TOMCAT_HOME_DIR1=/evalua/servers/$PROJECT_NAME1/apache-tomcat-6.0.37
TOMCAT_BIN1=$TOMCAT_HOME_DIR1/bin
PROJECT_HOME=`pwd`
TOMCAT_WEBAPP1=$TOMCAT_HOME_DIR1/webapps
TARGET_WEBAPP=$PROJECT_HOME/target/$PROJECT_NAME




echo "PROJECT HOME : $PROJECT_HOME"

cd $TOMCAT_BIN1
  ./catalina.sh stop

cd $PROJECT_HOME
mvn clean

mvn compile war:exploded

if [ ! -L $TOMCAT_WEBAPP1/ROOT ]; then
cd $TOMCAT_WEBAPP1
ln -s $TARGET_WEBAPP ROOT
fi

cd $TARGET_WEBAPP 
rm -rf static
ln -s $PROJECT_HOME/src/main/webapp/static static

cd WEB-INF
rm -rf jsp
ln -s $PROJECT_HOME/src/main/webapp/WEB-INF/jsp jsp

cd $TOMCAT_BIN1
  ./catalina.sh run
