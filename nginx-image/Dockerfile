#download ubuntu image 
FROM ubuntu:latest
#install curl and nginx
RUN apt-get update && apt install -y nginx  && apt install -y curl  
#copy default cfg 
COPY default /etc/nginx/sites-available/default	
#copy index html
COPY  ./tms-by/ /var/www/tms-by/	
#open port
EXPOSE 8081
CMD ["/usr/sbin/nginx","-g","daemon off;"]
