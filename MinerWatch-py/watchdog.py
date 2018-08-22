import RPi.GPIO as GPIO
import time
import urllib2
import json

rig1 = 17
rig2 = 15
rig3 = 18
rigname1 = "37db33"
rigname2 = "9265bc"
rigname3 = "cae414"
threshold = 10.0

def reboot_it(rig):
    GPIO.setmode(GPIO.BCM)
    GPIO.setwarnings(False)
    GPIO.setup(rig,GPIO.OUT)
    print "LED on"
    GPIO.output(rig,GPIO.HIGH)
    time.sleep(1)
    print "LED off"
    GPIO.output(rig,GPIO.LOW)
    time.sleep(1)

def is_alive(rigname):
    req = urllib2.Request("http://b4637d.ethosdistro.com/?json=yes")
    opener = urllib2.build_opener()
    f = opener.open(req)
    j = json.loads(f.read())
    rigs = j["rigs"]
    rig = rigs[rigname]
    miner_hashes = rig["miner_hashes"]
    hashes = miner_hashes.split(' ')
    for h in hashes:
        f_h = float(h)
        if(f_h < threshold):
            return False
    #print(hashes)
    return True

#print(is_alive(rigname1))

while True:
    if(is_alive(rigname1) == True):
        print(rigname1 + " is alive.")
    else:
        print(rigname1 + " is dead.")
        reboot_it(rig1)

    time.sleep(1)

    if(is_alive(rigname2) == True):
        print(rigname2 + " is alive.")
    else:
        print(rigname2 + " is dead.")
        reboot_it(rig2)

    time.sleep(1)

    if(is_alive(rigname3) == True):
        print(rigname3 + " is alive.")
    else:
        print(rigname3 + " is dead.")
        reboot_it(rig3)

    time.sleep(10)
