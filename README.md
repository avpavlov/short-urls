To clone, build and launch
  
    git clone https://github.com/avpavlov/short-urls.git
    cd short-urls
    sbt "runMain HttpServer localhost:8080 localhost:8080"

Simple form will be available at http://localhost:8080

URLs are stored in external MongoDB instance (I used free sandbox on https://mlab.com/)

To shorten URL I use SHA1 hash and iterate over it by taking 8 subsequent chars (0-7, 1-8, 2-9 etc) until I find sequence which is not yet used. 
