

## 发布及部署

### 发布到mvn仓库

修改pom（父项目和子项目都修改）
在.me/setting.xml中增加
  <servers>
    <server>
      <id>ZJHZ-CMREAD-CGTEST36</id>
      <username>admin</username>
      <password>password</password>
    </server>
  </servers>
  

	    # cd [project-dir]
	    # mvn deploy



### 部署运行

测试环境下，目前cync-server部署在10.211.93.178的cmusync账号下，使用webjoin作服务器，启动三个tomcat，9901端口开发，9902测试，9903维优；

#### 部署war到服务器
	    
	    # cd [project-dir]/css-server/target
	    # scp csync-server-1.0.8.war cmusync@10.211.93.178:webjoin/webapps/content-sync-system.war # 把0.1.8改成要部署的版本号
	    # scp css-server/target/csync-server-1.1.0.war cmusync@10.211.93.178:webjoin/webapps/content-sync-system.war
	    # scp css-server/target/csync-server-1.1.0.war cmusync@10.211.93.178:webjoin/webapps/

#### 运行

10.211.93.178 cmusync 密码:123456

	    # ssh cmusync@10.211.93.178
	    # webjoin status # 查看服务器是否运行
	    # webjoin stop -y # 关闭webjoin
	    # webjoin start # 启动webjoin

#### 日志:

日志存放在 /home/cmusync/webjoin/logs目录下，其中每个tomcat一个目录；

	# 查找文件中的同步任务ID
	grep "\[t:" csync-server.log | cut -d ' ' -f 1 | sort | uniq


### 其他

scp csync-client-0.1.12.jar manager@10.211.93.35:apache-tomcat/webapps/iManager/WEB-INF/lib/csync-client-0.1.12.jar

	    
