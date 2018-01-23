#!/usr/bin/python
'''
simple mining code from https://en.bitcoin.it/wiki/Getblocktemplate
'''
from __future__ import print_function
import hashlib, httplib, json, base64, binascii, logging
logging.basicConfig(level=logging.DEBUG)

def dblsha(data):
 	return hashlib.sha256(hashlib.sha256(data).digest()).digest()

def getmerkleroot(template):
    logging.debug('template: %s', template)
    result = template['result']
    coinbase = binascii.a2b_hex(result['coinbasetxn']['data'])
    txnlist = [binascii.a2b_hex(a['data']) for a in result['transactions']]
    merklehashes = [dblsha(t) for t in ([coinbase] + txnlist)]
    while len(merklehashes) > 1:
        if len(merklehashes) % 2:
            merklehashes.append(merklehashes[-1])
        merklehashes = [dblsha(merklehashes[i] + merklehashes[i + 1])
                        for i in range(0, len(merklehashes), 2)]
    merkleroot = merklehashes[0]
    return merkleroot

def gettemplate():
    rpcserver = httplib.HTTPConnection('localhost', 18332, False, 3);
    authorization = base64.b64encode('bitcoinrpc:reallysecurepassword')
    rpcserver.set_debuglevel(__debug__ or 0)
    rpccall = {'version': '1.1', 'method': 'getblocktemplate',
               'id': 0, 'params': [{'capabilities': [
               'coinbasetxn', 'workid']}],
               'mode': 'template'}
    rpcserver.request('POST', '/', json.dumps(rpccall),
                      {'Authorization': 'Basic %s' % authorization,
                       'Content-type': 'application/json'})
    response = rpcserver.getresponse()
    message = json.loads(response.read())
    response.close()
    return message

if __name__ == '__main__':
    print(getmerkleroot(gettemplate()))
