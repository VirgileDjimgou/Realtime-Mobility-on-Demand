# import socket programming library
import socket
 
# import thread module
from _thread import *
import threading
from subprocess import call
import time
 
print_lock = threading.Lock()
nb_users = 100
 
# thread fuction
def add_users():
    try:
                    
            while True:
                        print("adding new user ....")
                        global nb_users 
                        try:
                            '''
                            #read last line to read the correct index
                            fileHandle = open ( 'index_user.txt',"r" )
                            lineList = fileHandle.readlines()
                            fileHandle.close()
                            print ("The last line is:")
                            print (lineList[-1])
                            nb_users = int(lineList[-1])
                            fileHandle.close
                            
                            # update index user
                            index_user=open("index_user.txt", "a+")
                            index_user.write(str(nb_users+5)+"\n")
                            index_user.close
                            '''

                            
                            # add new user sip
                            f=open("sip.conf", "a+")
                            f.write("["+str(nb_users)+"]\n")
                            f.write("type=friend \n")
                            f.write("host=dynamic \n")
                            f.write("secret=1234 \n")
                            f.write("context=internal \r\r\n")
                            f.close

                            # add new extension user
                            e=open("extensions.conf", "a+")
                            e.write("exten => "+str(nb_users) +",1,Answer() \n")
                            e.write("exten => "+str(nb_users) +",2,Dial(SIP/"+str(nb_users)+",60) \n")
                            e.write("exten => "+str(nb_users) +",3,Playback(vm-nobodyavail) \n")
                            e.write("exten => "+str(nb_users) +",4,VoiceMail(" +str(nb_users)+"@main) \n")
                            e.write("exten => "+str(nb_users) +",5,Hangup()\r\n")

                            e.write("exten => "+str(nb_users+1) +",1,VoicemailMain("+str(nb_users)+"@main) \n")
                            e.write("exten => "+str(nb_users+1) +",2,Hangup() \r\r\n")            
                            e.close

                            # reload dialplan
                            #call('ls')
                            # reload sip users
                            #call('ls')
                            print(str(nb_users)+" user added .")
                            #nb_users    # Needed to modify global copy of nbusers
                            nb_users=nb_users + 2
                            
                            
                            # send back ID to client
                            #client.sendall("ID="+str(nb_users+1))
                            
                            #client.close
							time.sleep(1)
                            
                        except Exception as e:
                            print ("Unexpected error")
                            #print (e.message)
                            break
                               

    except Exception as e:
        print(e.message)

    
 
 
if __name__ == '__main__':
    add_users()
