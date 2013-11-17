# $1 = IP Address of the target machine
# $2 = the location of the private key file

# Make sure that the private key file is write protected or something
chmod 400 $2

# configure the target by ssh
# "UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" for bypassing any further questions
# the following long script to be executed in the target machine are embraced by ''
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i $2 ubuntu@$1 '

# install openjdk, git and unzip;
sudo wget http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip;
yes | sudo apt-get update;
yes | sudo apt-get install unzip;
yes | sudo apt-get install openjdk-7-jdk;
yes | sudo apt-get install git-core;

# install play framework;
sudo unzip ~/play-2.2.1.zip;
sudo chmod a+x ~/play-2.2.1/play;

# download configuration or project from github;
sudo git clone https://github.com/jhejderup/aws-tesseract.git;
sudo git clone https://github.com/winglungngai/EC2Configuration.git;

# copy the configuration;
sudo cp ~/EC2Configuration/.profile ~/.profile;
sudo unzip ~/EC2Configuration/ec2-api-tools.zip;

# run play when rebooting;
$(crontab -l | { cat; echo "@reboot sudo bash -c \"~/EC2Configuration/startup.sh\""; } | crontab -);
sudo chmod a+x ~/EC2Configuration/startup.sh;

# start the web server;
cd ~/play-2.2.1/samples/scala/comet-live-monitoring/;
sudo ~/play-2.2.1/play start;
exit 0 '
