import pandas as pd
import os
from pymongo import MongoClient, UpdateOne

mongo_uri = os.getenv('MONGO_URI', 'mongodb://root:example@localhost:27017')
client = MongoClient(mongo_uri)

db = client['cstore']
collection = db['cluster_data']

df = pd.read_pickle("clustering_result.pkl")

requests = []
for _, row in df.iterrows():
    filter = {'station' : row['station']}
    update =  {'$set': row.to_dict()}
    requests.append(UpdateOne(filter,update,upsert=True))
result = collection.bulk_write(requests)
requests.clear()
