Launch on default port
  
    git clone https://github.com/avpavlov/short-urls.git
    cd short-urls
    sbt run
  
To listen on specific port, e.g. 9999
  
    sbt "runMain HttpServer 9999"
