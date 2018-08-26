# import socket programming library
import socket
 
# import thread module
from subprocess import call
import socket 
from threading import Thread 
import time
#from SocketServer import ThreadingMixIn


# Multithreaded Python server : TCP Server Socket Thread Pool
class ClientThread(Thread): 
 
    def __init__(self,ip,port): 
        Thread.__init__(self) 
        self.ip = ip 
        self.port = port 
        print ("[+] New server socket thread started for " + ip + ":" + str(port))
 
    def run(self): 
        while True : 
            data = conn.recv(2048) 
            print("Server received data:", data)

            key = "ID_AST"
            if  key in str(data):
                    user_id = data.decode("utf-8").split("#")
                    print("adding new user ...."+user_id[1]) # always second index of  
                    try:
                        
                        # add new user sip
                        f=open("sip.conf", "a+")
                        f.write("["+str(user_id[1])+"]\n")
                        f.write("type=friend \n")
                        f.write("host=dynamic \n")
                        f.write("secret=1234 \n")
                        f.write("context=internal \r\r\n")
                        f.close

                        print(" sip new user ....")

                        # add new extension user
                        e=open("extensions.conf", "a+")
                        e.write("exten => "+str(user_id[1]) +",1,Answer() \n")
                        e.write("exten => "+str(user_id[1]) +",2,Dial(SIP/"+str(user_id[1])+",60) \n")
                        e.write("exten => "+str(user_id[1]) +",3,Playback(vm-nobodyavail) \n")
                        e.write("exten => "+str(user_id[1]) +",4,VoiceMail(" +str(user_id[1])+"@main) \n")
                        e.write("exten => "+str(user_id[1]) +",5,Hangup()\r\n")

                        e.write("exten => "+str(int(user_id[1])+1) +",1,VoicemailMain("+str(user_id[1])+"@main) \n")
                        e.write("exten => "+str(int(user_id[1])+1) +",2,Hangup() \r\r\n")            
                        e.close

                        print("extension new user ....")
                        
                        # send back ID to client
                        response="ID="+str(user_id[1])
                        conn.sendall(response.encode('utf-8'))
                        print(str(user_id[1])+" user added .")
                        
                        #nb_users    # Needed to modify global copy of nbusers
                        time.sleep(1)
                        conn.close
                        break
                        
                    except Exception as e:
                        print ("Unexpected error : "+str(e))
                        #print (e.message)
                        conn.close()
                        break
                                             
           

            
            #conn.send(MESSAGE)  # echo 

# Multithreaded Python server : TCP Server Socket Program Stub
TCP_IP = '206.189.16.110'
#TCP_IP = '127.0.0.1' 
TCP_PORT = 2004 
BUFFER_SIZE = 20  # Usually 1024, but we need quick response 

tcpServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
tcpServer.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
tcpServer.bind((TCP_IP, TCP_PORT)) 
threads = [] 
 
while True: 
    tcpServer.listen(4) 
    print("Multithreaded Python server : Waiting for connections from TCP clients...") 
    (conn, (ip,port)) = tcpServer.accept() 
    newthread = ClientThread(ip,port) 
    newthread.start() 
    threads.append(newthread) 
 
for t in threads: 
    t.join() 
