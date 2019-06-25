import requests 
import pymysql
import threading
import time
import timeit
from threading import Timer

try:
    conn = pymysql.connect('localhost', user='root', password='test', db = 'TestDB', charset = 'utf8mb4',  cursorclass=pymysql.cursors.DictCursor)
    cur = conn.cursor(pymysql.cursors.DictCursor)
cur2 = conn.cursor(pymysql.cursors.DictCursor)  
except Exception:
    print('Unable to Connect to Database\n')


x = None
choice = None

#will check all the connectivity of all sites saved in database
#will update if status of site has changed and inform the user
def printSites():
   
    changeMade = 0 #will be used to signal a change in status 
    
    GoodList = None #store Urls for display 
    BadList = None
    
    oldStatus = None #compare new status to old status 
    
    getUrl = """select url from Sites""" 
    getStatus = """select status from Sites where url = %s"""
    uptadeStatus = """update Sites set status = %s where url = %s"""
    
    try:
        cur.execute(getUrl)
    except Exception:
        print('Unknown database error\n')
    
    for row in cur:
        try:
            checkUrl = row['url'] #Url with protocal for .get
            Url = row['url'] #saved url
            
            changeStatusGood = (1, Url)
            changeStatusBad = (0, Url)
            
            if Url.find('http://') != 0:
                if Url.find('https://') != 0:
                    checkUrl = 'http://' + Url
        
            r = requests.get(checkUrl)
            try:
                cur2.execute(getStatus, Url)
            except Exception:
                print('Unknown database error\n')
                
            for row in cur2:
                oldStatus = row['status']
             
            if r.status_code == 200:
                if GoodList == None:
                    GoodList = ' ' + Url + '\n'
                else:
                    GoodList = GoodList + ' ' + Url + '\n'
                    
                if oldStatus in [0]:
                    changeMade = 1
                    cur.execute(uptadeStatus, changeStatusGood)
            else:
                if BadList == None:
                    BadList = ' ' + Url + '\n'
                else:
                    BadList = BadList + ' ' + Url + '\n'
                if oldStatus in [1]:
                    print('Hello\\\n')
                    changeMade = 1
                    cur.execute(uptadeStatus, changeStatusBad)
                    
        except requests.ConnectionError:
            if BadList == None:
                BadList = ' ' + Url + '\n'
            else:
                BadList = BadList + ' ' + Url + '\n'
            if oldStatus in [1]:
                
                changeMade = 1
                cur.execute(uptadeStatus, changeStatusBad)
    
    if changeMade in [1]:
        print('************')
        print('CHANGE MADE')
        print('************\n')
         
    print('Successful Connections:')
    print(GoodList)
    print('Unsuccessful Connection:')
    print(BadList)
    print("")
    
    try:
        conn.commit()
    except Exception:
        print('Unable to update database\n')
    mainMenu()

#find initial status of site and save into the database 
def inputSites(): 

    Url = input('Please enter a url:')
    
    if Url.find('http://') != 0:
        if Url.find('https://') != 0:
            inputUrl = 'http://' + Url
           

    sql_insert_query = """insert into Sites (url, status) values (%s, %s)"""
    try:
        r = requests.get(inputUrl)
        if r.status_code == 200:
            print('connection successful')
            query_tuple = (Url, 1)
            cur.execute(sql_insert_query, query_tuple)
            
        else:
            print('failed to connect')
            query_tuple = (Url, 0)
            cur.execute(sql_insert_query, query_tuple)

    except requests.ConnectionError:
        print("failed to connect")
        query_tuple = (Url, 0)
        cur.execute(sql_insert_query, query_tuple)
     
    try:
        conn.commit()
    except Exception:
        print('Unable to update database\n')
        
    mainMenu()
    
#find and delete site 
def deleteSite():
    
    Url = input('Please enter a url: ')
    deleteQuery = """delete from Sites where url = %s"""
    
    try:
        cur.execute(deleteQuery, Url)
        conn.commit()
    except Exception:
        print('Unkown Error Deleting Site\n')
        mainMenu()
    
    
    print('Deletion Successful\n')
    mainMenu()	
	
def mainMenu():
        
        print('Please Choose:\n R) Run site connection checker \n I) Input a Site for testing \n D) Delete a site from testing \n ' +
        'X) To exit program \n')
        choice = input('Enter: ')
        
        
        if choice in ['R', 'r']:
            printSites()
        elif choice in ['i','I']:
            inputSites()
        elif choice in ['d', 'D']:
            deleteSite()
        elif choice in ['x','X']:
            exit()
        else:
            print(choice + ': invalid input\n')
            mainMenu()

    
            
mainMenu()             
           
