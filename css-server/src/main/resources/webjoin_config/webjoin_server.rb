# -*- coding: utf-8 -*-

readonly_webapp_dir 'on'

tomcat :csync_server do 
  stdout true
  java_opts "-Xms1024m -Xmx2024m -XX:MaxPermSize=500m -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8"
  executor :shared, :min => 10, :max => 200
  connector :http, 9901, :executor => :shared, :URIEncoding => 'UTF-8'

  webapp '/content-sync-system' do 
    
    # cms-db(common) config
    env[:common_jdbc_url]      = "jdbc:oracle:thin:@10.211.93.178:1521:testdb"
    env[:common_jdbc_username] = "username"
    env[:common_jdbc_password] = "password"
    env[:common_jdbc_maxPoolSize] = 20
    
    # bks-db(book) config
    env[:book_jdbc_url]      = "jdbc:oracle:thin:@10.211.93.178:1521:testdb"
    env[:book_jdbc_username] = "username"
    env[:book_jdbc_password] = "password"
    env[:book_jdbc_maxPoolSize] = 20
    
    # ccs-db(cartoon) config
    env[:cartoon_jdbc_url]      = "jdbc:oracle:thin:@10.211.93.178:1521:testdb"
    env[:cartoon_jdbc_username] = "username"
    env[:cartoon_jdbc_password] = "password"
    env[:cartoon_jdbc_maxPoolSize] = 20
       
    # scheduler config
    
    # book-package scheduler 
    env[:package_trigger]  = 'true'
    env[:package_interval] = 10000
    
    # my-notice scheduler 
    env[:notice_trigger]   = 'true'
    env[:notice_interval]  = 10000
    
    # review scheduler
    env[:review_trigger]   = 'true'
    env[:review_interval]  = 50000
    
    # bookrank scheduler
    env[:bookrank_trigger] = 'true'
    env[:bookrank_interval] = 50000
    
    # chear-succcess-sync-task-record scheduler
    env[:task_clear_trigger] = 'true'
    env[:task_clear_reserveDays] = 30
    
    # mmlogmove scheduler
	env[:mmlogsync_trigger] = "true"
	env[:mmlogsync_cron] = "0 0/30 * * * ?"
	
	# on-off-shelf book scheduler
    env[:onoffshelf_trigger] = 'true'
    env[:onoffshelf_interval] = 30000
	
    # role-privilege table sync task scheduler
    env[:roleprivilege_trigger] = 'true'
    env[:roleprivilege_cron] = "0 0 2 * * ?"
	
	# qa bad keyword table sync scheduler
    env[:qakeyword_trigger] = "true"
    env[:qakeyword_cron] = "0 0 * * * ?"
	
   	# csync log
    env[:log_cmread_root] = 'DEBUG'
    # sql-trace log
    env[:log_sqlonly]     = 'INFO'
    
    env[:log_errorlog_maxdays]     = 30
    env[:log_tasklog_maxdays]      = 30
    env[:log_systemlog_maxdays]    = 30
    env[:log_sqlinvokelog_maxdays] = 30
    env[:log_taskrecord_maxdays]   = 30
    
    # sync-task threadpool 
    env[:task_priorityexecutor_threadpoolsize] = 10
    
    # sql-task threadpool
    env[:sql_priorityexecutor_threadpoolsize] = 10
    
    # sql speed (sql-number/second)
    env[:sql_limit_numbersPerSecond] = 100
    
    # global swith for sync task persistent
    env[:persistent_global_switch] = true
  
  end  
end