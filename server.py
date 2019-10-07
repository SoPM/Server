import os,time,sys
from socket import *
meHost=''
mePort=1488
sockobj=socket(AF_INET,SOCK_STREAM)
sockobj.bind((meHost,mePort))
sockobj.listen(5) #Максимум 5 ожидающих запросов(очередь)
def now():
    return time.ctime(time.time())
activeChildren=[]
def reapChildren():
    while activeChildren:
        pid,stat=os.waitpid(0,os.WNOHANG)
        activeChildren.remove(pid)
def handleClient(connection):
    time.sleep(5)
    while True:
        data=connection.recv(1024)
        if not data:break
        reply='Echo=>%s at %s' % (data, now())
        connection.send(reply.encode())
    connection.close()
    os._exit(0)
def dispatcher():
    while True:
        connection , address=sockobj.accept()
        print('Server connected by ',address,end='')
        print('at',now())
        reapChildren()
        childPid=os.fork()
        if childPid==0:
            handleClient(connection)
        else:
            activeChildren.append(childPid)
dispatcher()
