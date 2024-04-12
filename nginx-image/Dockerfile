#download ubuntu image 
FROM ubuntu:latest
#install curl and nginx
RUN apt update && apt install -y nginx  && apt install -y curl  
#copy default cfg 
COPY default /etc/nginx/sites-available/default.conf	
#copy index html
COPY  ./tms-by/index.html /var/www/tms-by/index.html	
#open port
EXPOSE 8081
CMD ["/usr/sbin/nginx","-g","daemon off;"]
