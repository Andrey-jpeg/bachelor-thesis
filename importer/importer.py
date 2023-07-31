import pandas as pd
import os
from pymongo import MongoClient, UpdateOne

def cleanData(df: pd.DataFrame) -> pd.DataFrame:
    df = df.dropna()

    df['timestamp'] = pd.to_datetime(df['timestamp'])
    return df
    
def groupTransactions(df: pd.DataFrame) -> pd.DataFrame:
    def calculate_price(row):
        return row['volume'] * row['price']

    df['price_sum'] = df.apply(lambda row: calculate_price(row), axis=1)

    df_part_1 = df.groupby(['ticket_number'])[['price_sum']].sum().reset_index()
    df_part_2 = df.groupby(['ticket_number', 'station', 'timestamp']).agg({'product_category': list, 'product': list,'volume': list, 'price': list, 'payment_type':list}).reset_index()

    df = pd.merge(df_part_1, df_part_2, on='ticket_number')

    return df

mongo_uri = os.getenv('MONGO_URI', 'mongodb://root:example@localhost:27017')
client = MongoClient(mongo_uri)

db = client['cstore']

collection = db['transactions']
collection.create_index({ {"station": 1}, {"timestamp": 1} })
df = pd.read_csv("Transactions_all.csv", 
                 names=["guid","external","station","product_category","timestamp","ticket_number","volume", "price", "payment_type", "product"])

print("--------------------------------")
print("Cleaning")

df = cleanData(df)

print("--------------------------------")
print("Importing Transactions ")
requests = []
for _, row in df.iterrows():
    filter = {'guid' : row['guid']}
    update =  {'$set': row.to_dict()}
    requests.append(UpdateOne(filter,update,upsert=True))
result = collection.bulk_write(requests)
requests.clear()

print("Importing Baskets ")
collection = db['baskets']
collection.create_index({ {"station": 1}, {"timestamp": 1} })
df = groupTransactions(df)

requests = []
for _, row in df.iterrows():
    filter = {"ticket_number": row['ticket_number'],
              "station": row['station'],
              "timestamp": row['timestamp']}
    update =  {'$set': row.to_dict()}
    requests.append(UpdateOne(filter,update,upsert=True))
result = collection.bulk_write(requests)  

print("Done")
print("--------------------------------")