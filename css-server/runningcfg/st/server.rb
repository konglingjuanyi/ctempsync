# -*- coding: utf-8 -*-


tomcat :csync_test do 
  stdout true
  java_opts "-Xms256m -Xmx2024m -XX:MaxPermSize=500m -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8"
  executor :shared, :min => 10, :max => 200
  connector :http, 9902, :executor => :shared, :URIEncoding => 'UTF-8'

  webapp '/content-sync-system' do 
    
    # cms-db(common) config
    env[:common_jdbc_url]      = "jdbc:oracle:thin:@10.211.93.178:1521:testdbb"
    env[:common_jdbc_username] = "mread"
    env[:common_jdbc_password] = "J3uOyS4H2q3wqD7eMkqyvw=="
    env[:common_jdbc_maxPoolSize] = 20
    
    # bks-db(book) config
    env[:book_jdbc_url]      = "jdbc:oracle:thin:@10.211.93.178:1521:testdbb"
    env[:book_jdbc_username] = "bks"
    env[:book_jdbc_password] = "J3uOyS4H2q3wqD7eMkqyvw=="
    env[:book_jdbc_maxPoolSize] = 20
    
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
    env[:task_clear_reserveDays] = 7
   
   	# csync log
    env[:log_cmread_root] = 'DEBUG'
    # sql-trace log
    env[:log_sqlonly]     = 'INFO'
    
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
