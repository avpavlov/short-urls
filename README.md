To launch
  
    git clone https://github.com/avpavlov/short-urls.git
    cd short-urls
    sbt "runMain HttpServer localhost:8080 localhost:8080"

where
    hostname1:port1 is entry point to the server
    hostname2:port2 are to be included to short URL

sbt "runMain HttpServer localhost:45276 https://shrouded-oasis-50298.herokuapp.com"
