#build the project
mvn clean package

#Update jar file to edge node
scp target/NodeScale.jar root@ted-malaska-cap1-barclay-1.vpc.cloudera.com:./

#Go to edge node
ssh root@ted-malaska-cap1-barclay-1.vpc.cloudera.com

#Run the code
hadoop jar NodeScale.jar com.cloudera.sa.exe.TestYarnCollector

#Tips if you need to update the java version
service cloudera-scm-server stop

sudo wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u91-b14/jdk-8u91-linux-x64.tar.gz"

sudo tar xzf jdk-8u91-linux-x64.tar.gz

mv jdk1.8.0_91 /opt

cd /opt/jdk1.8.0_91/

sudo alternatives --install /usr/bin/java java /opt/jdk1.8.0_91/bin/java 1

sudo alternatives --config java

sudo alternatives --install /usr/bin/jar jar /opt/jdk1.8.0_91/bin/jar 1

sudo alternatives --install /usr/bin/javac javac /opt/jdk1.8.0_91/bin/javac 1

sudo alternatives --set jar /opt/jdk1.8.0_91/bin/jar

sudo alternatives --set javac /opt/jdk1.8.0_91/bin/javac

sudo ln -sf /opt/jdk1.8.0_91/ /usr/java/latest/

service cloudera-scm-server start

export JAVA_HOME=/opt/jdk1.8.0_91/