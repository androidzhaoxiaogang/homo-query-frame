#!/usr/bin/env bash

if [ -z $JAVA_HOME ]; then
  echo "The $JAVA_HOME environment variable is not defined correctly"
  exit 1
fi
if [ ! -x "$JAVA_HOME"/bin/java ]; then
      echo "The JAVA_HOME environment variable is not defined correctly"
      echo "This environment variable is needed to run this program"
      echo "NB: JAVA_HOME should point to a JDK not a JRE"
      exit 1
fi
_RUNJAVA="$JRE_HOME"/bin/java;
PWD="pwd"
CLASSPATH="$CLASSPATH"
for i in ../WEB-INF/lib/*.jar;
do CLASSPATH=./$i:"$CLASSPATH";
done
export CLASSPATH=../WEB-INF/classes:$CLASSPATH
#echo $CLASSPATH
$JAVA_HOME/bin/java -Djava.compiler=none com.howbuy.appframework.homo.queryapi.cache.RedisInitDataCmd

