#!/usr/bin/env python
# -*- coding:utf-8 -*-
import requests
import json

provinces = ['beijing','tianjin','hebei','shan1xi','neimenggu','liaoning','jilin','heilongjiang','shanghai','jiangsu','zhejiang','anhui','fujian','jiangxi','shandong','henan','hubei','hunan','guangdong','guangxi','hainan','chongqing','sichuang','guizhou','yunnan','xizang','shan3xi','gansu','qinghai','ningxia','xinjiang','taiwai','xianggang','aomen']

class Create_mysensor():
    def __init__(self, url, body):
        self.headers = {
            'X-M2M-Origin': 'admin:admin',
            'Content-Type': 'application/json;ty=2'
        }
        self.url = url
        self.body = body

    def create(self):
        r = requests.post(url=self.url, data=self.body,headers=self.headers)
        print(r.status_code)
        print(r.headers)
        print(r.text)

in_cse = 'http://127.0.0.1:8080/~/in-cse'
f = open('province.json')
body = f.read()
f.close()
data = json.loads(body)
for i in provinces:
    data['m2m:ae']['rn'] = i
    mysensor = Create_mysensor(in_cse, json.dumps(data))
    mysensor.create()
#data['m2m:ae']['rn'] = 'shan3xi'
#mysensor = Create_mysensor(in_cse, json.dumps(data))
#mysensor.create()

