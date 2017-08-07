To clone, build and launch
  
    git clone https://github.com/avpavlov/short-urls.git
    cd short-urls
    sbt "runMain HttpServer localhost:8080 localhost:8080"

Simple form will be available at http://localhost:8080

URLs are stored in external MongoDB instance (I used free sandbox on https://mlab.com/)

