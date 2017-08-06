To launch
  
    git clone https://github.com/avpavlov/short-urls.git
    cd short-urls
    sbt "runMain HttpServer <hostname1>[:<port1>] [<hostname2>[:<port2>]]"

where
    hostname1:port1 is entry point to the server
    hostname2:port2 are to be included to short URL

