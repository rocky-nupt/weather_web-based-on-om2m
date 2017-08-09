#!/usr/bin/env python
# -*- coding:utf-8 -*-

import json
from pymemcache.client.base import Client

# Memcached缓存
class MemClient():

    def __init__(self):
        self.client = Client(('localhost', 11211))

    def get(self, key):
        value = self.client.get(key)
        return json.loads(value)

    def set(self, key, value):
        self.client.set(key, json.dumps(value), expire=18000)
