from datetime import datetime
from mlxtend.frequent_patterns import apriori
from mlxtend.frequent_patterns import association_rules
import pandas as pd
import numpy as np
from typing import List,Dict
import os
import re
from pymongo import MongoClient, UpdateOne
from tqdm import tqdm, trange

def removeProductAddedAndRemoved(df: pd.DataFrame) -> pd.DataFrame:
    def removeDuplicates(row):
        if len(row.volume) == 1 or all(vol > 0 for vol in row.volume):
            return row
        selectedIndexes = set()
        for i in range(len(row['volume'])):
            if row['volume'][i] < 0:
                selectedProduct = row['product'][i]
                for j in range(len(row['product'])):
                    if row['product'][j] is selectedProduct and row['volume'][j] is abs(row['volume'][i]) and i is not j and (i and j not in selectedIndexes):
                        selectedIndexes.add(i)
                        selectedIndexes.add(j)
        selectedIndexes = list(selectedIndexes)
        row['volume'] = np.delete(row['volume'], selectedIndexes)
        row['product'] = np.delete(row['product'], selectedIndexes)
        row['product_category'] = np.delete(row['product_category'], selectedIndexes)
        row['price'] = np.delete(row['price'], selectedIndexes)
        row['payment_type'] = np.delete(row['payment_type'], selectedIndexes)
        return row
    df = df.progress_apply(lambda row: removeDuplicates(row), axis=1)
    return df

def removeBrands(df: pd.DataFrame) -> pd.DataFrame:
    def removeProductBrands(row):
        regex = r' BD| ST| TR| TN| ST/MEZZO| MEZZO| CREPEAT| LMC'
        for i in range(len(row['product'])):
            productSplit = re.split(regex, row['product'][i])
            row['product'][i] = productSplit[0]
        for i in range(len(row['product_category'])):
            productSplit = re.split(regex, row['product_category'][i])
            row['product_category'][i] = productSplit[0]

        return row

    df = df.progress_apply(lambda row: removeProductBrands(row), axis=1)

    return df

def basketContains(df: pd.DataFrame) -> List[pd.DataFrame]:
    gas_types = r'ADBLUE*|B10|E10|E85|FOD|GO\+|GPLC|SP 95|SP 98|GA .\d+|GNR'
    df['contains_gas'], df['contains_products'],df['contains_gas_and_products'] = False, False, False

    def check_contains(row):
        product_categories = row['product_category']
        for product_category in product_categories:
                result = re.search(gas_types, product_category)
                if result == None and row['contains_products'] != True:
                    row['contains_products'] = True
                if result != None and row['contains_gas'] != True:
                    row['contains_gas'] = True
                if row["contains_gas"] == True and row["contains_products"] == True:
                    row['contains_gas_and_products'] = True
        return row
    
    df = df.progress_apply(lambda row: check_contains(row), axis=1)

    gas = df.loc[df['contains_gas'] == True]
    products = df.loc[df['contains_products'] == True]

    return gas, products

def addDaypartAndWeekday(df: pd.DataFrame) -> pd.DataFrame:
    # dict = {1: 'Morning', 2: 'Day', 3: 'Evening', 4: 'Night', 5: 'Late Night'}
    df['daypart'] = np.NaN
    df['weekday'] = np.NaN

    morning = pd.Interval(pd.Timestamp('06:00'), pd.Timestamp('09:59:59'))
    day = pd.Interval(pd.Timestamp('10:00'), pd.Timestamp('14:59:59'))
    evening = pd.Interval(pd.Timestamp('15:00'), pd.Timestamp('18:59:59'))
    night = pd.Interval(pd.Timestamp('19:00'), pd.Timestamp('23:59:59'))
    latenight = pd.Interval(pd.Timestamp('00:00'), pd.Timestamp('05:59:59'))

    def apply_daypart(row):
            timestamp = pd.Timestamp(pd.to_datetime(row['timestamp']).strftime("%H:%M:%S"))
            if timestamp in morning:
                    return 'Morning'
            elif timestamp in day:
                    return 'Day'
            elif timestamp in evening:
                    return 'Evening'
            elif timestamp in night:
                    return 'Night'
            elif timestamp in latenight:
                    return 'Latenight'

    df['daypart'] = df.progress_apply(lambda row: apply_daypart(row), axis=1)

    def apply_weekday(row):
        return pd.to_datetime(row['timestamp']).day_name()

    df['weekday'] = df.progress_apply(lambda row: apply_weekday(row), axis=1)

    return df

def addFuelPersonas(df: pd.DataFrame) -> pd.DataFrame:
    # Gas types excluding adblue as adblue is purchased in cannisters
    gas_types = r'B10|E10|E85|FOD|GO\+|GPLC|SP 95|SP 98|GA .\d+|GNR'

    df['fuel_x_price'], df['fuel_x_volume'], df['full_tank'] = False, False, False

    def fuel_persona(row):
        product_categories = row['product_category']

        for i in range(len(product_categories)):
                    if row['fuel_x_price'] == True or row['fuel_x_volume'] == True or row['full_tank'] == True:
                                break
                    
                    result = re.search(gas_types, product_categories[i])
                    if result != None:        
                            prices = row['price']
                            volumes = row['volume']
                            
                            price = prices[i]
                            volume = volumes[i]

                            if (round((float(price) * float(volume)) % 10,2) in np.arange(9.00, 10.00, 0.01)) or (round((float(price) * float(volume)) % 10,2) in np.arange(0.00, 1.00, 0.01)):
                                row['fuel_x_price'] = True
                            if (round(float(volume) % 1,2) in np.arange(0.00, 0.11, 0.01)) or (round(float(volume) % 1,2) in np.arange(0.90, 1.00, 0.01)):
                                row['fuel_x_volume'] = True
                            if row['fuel_x_price'] == False and row['fuel_x_volume'] == False:
                                row['full_tank'] = True

        return row

    df = df.progress_apply(lambda row: fuel_persona(row), axis=1)
    return df

def addFuelCategories(df: pd.DataFrame) -> pd.DataFrame:
    gasType = r'SP 95|SP 98'
    diesel = r'GA .\d+'
    e10 = r'E10'

    df['gas_type'] = 'Other'

    def addGasType(row):
        products = row['product']

        for i in range(len(products)):
            isGas = re.search(gasType, products[i])
            isDiesel = re.search(diesel, products[i])
            isE10 = re.search(e10, products[i])

            if isGas != None:
                row['gas_type'] = 'Gazoline'
            elif isDiesel != None:
                row['gas_type'] = 'Diesel'
            elif isE10 != None:
                row['gas_type'] = 'E10'

        return row

    df = df.progress_apply(lambda row: addGasType(row), axis=1)
    return df

def fuelRuleMining(gas: pd.DataFrame) -> List[Dict]:
    resultList = list()
    for i in trange(len(stations)):
        toBeInserted = list()
        stationDf = gas.loc[gas['station'] == stations[i]]

        def oneHotFill(row):
            productSet = set(row['product'])
            dictionary_fuel = dict()
            for product in productSet:
                dictionary_fuel[product] = True
            
            dictionary_fuel['fuel_x_price'] = row['fuel_x_price']
            dictionary_fuel['fuel_x_volume'] = row['fuel_x_volume']
            dictionary_fuel['full_tank'] = row['full_tank']

            toBeInserted.append(dictionary_fuel)

        stationDf.apply(lambda row: oneHotFill(row), axis=1)
        del stationDf

        one_hot = pd.DataFrame(toBeInserted)
        one_hot.fillna(False, inplace=True)
        
        frequent_itemsets = apriori(one_hot, min_support=0.1, use_colnames=True)
        length = len(one_hot)
        del one_hot

        result = dict()
        result['station'] = stations[i]
        result['sample_size'] = length
        result['created_timestamp'] = now

        if not frequent_itemsets.empty:
            rules = association_rules(frequent_itemsets, metric="lift", min_threshold=1)

            del frequent_itemsets

            rules = rules[(rules['lift'] >= 1) &
                                (rules['confidence'] >= 0.5) &
                                (rules['support'] >= 0.05)]
            
            if not rules.empty:
                rules['antecedents'] = {key: list(value) for key, value in rules['antecedents'].items()}
                rules['consequents'] = {key: list(value) for key, value in rules['consequents'].items()}
                result['rules'] = rules.to_dict(orient='records')

        filter = {'station' : result['station']}
        update =  {'$set': result}
        resultList.append(UpdateOne(filter,update,upsert=True))

    return resultList

def fuelCategoryRuleMining(gas: pd.DataFrame) -> List[Dict]:        
    resultList = list()
    for i in trange(len(stations)):
        toBeInserted = list()
        stationDf = gas.loc[gas['station'] == stations[i]]

        def oneHotFill(row):
            gasType = row['gas_type']
            dictionary_fuel = dict()
            dictionary_fuel[gasType] = True
            
            dictionary_fuel['fuel_x_price'] = row['fuel_x_price']
            dictionary_fuel['fuel_x_volume'] = row['fuel_x_volume']
            dictionary_fuel['full_tank'] = row['full_tank']

            toBeInserted.append(dictionary_fuel)

        stationDf.apply(lambda row: oneHotFill(row = row), axis=1)
        del stationDf

        one_hot = pd.DataFrame(toBeInserted)
        one_hot.fillna(False, inplace=True)

        frequent_itemsets = apriori(one_hot, min_support=0.1, use_colnames=True)
        length = len(one_hot)
        del one_hot

        result = dict()
        result['station'] = stations[i]
        result['sample_size'] = length
        result['created_timestamp'] = now

        if not frequent_itemsets.empty:
            rules = association_rules(frequent_itemsets, metric="lift", min_threshold=1)
            del frequent_itemsets

            rules = rules[(rules['lift'] >= 1) &
                                        (rules['confidence'] >= 0.5) &
                                        (rules['support'] >= 0.05)]
            if not rules.empty:
                rules['antecedents'] = {key: list(value) for key, value in rules['antecedents'].items()}
                rules['consequents'] = {key: list(value) for key, value in rules['consequents'].items()}
                result['rules'] = rules.to_dict(orient='records')

        filter = {'station' : result['station']}
        update =  {'$set': result}
        resultList.append(UpdateOne(filter,update,upsert=True))
    return resultList

def productRuleMining(products: pd.DataFrame) -> List[Dict]:
    resultList = list()
    weekdays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
    dayparts = ['Morning', 'Day', 'Evening', 'Night', 'Latenight']

    for i in trange(len(stations)):
        for j in trange(len(weekdays), leave=False):
            for k in trange(len(dayparts), leave=False):   
                toBeInserted = list()
                product_df = products.loc[(products['station'] == stations[i]) & (products['weekday'] == weekdays[j]) & (products['daypart'] == dayparts[k])]

                def oneHotFillproducts(row):
                    productSet = set(row['product'])
                    dictionary = dict()
                    for product in productSet:
                        dictionary[product] = True

                    toBeInserted.append(dictionary)

                product_df.apply(lambda row: oneHotFillproducts(row), axis=1)
                del product_df

                product_binary_df = pd.DataFrame(toBeInserted)
                product_binary_df.fillna(False, inplace=True)

                frequent_itemsets = apriori(product_binary_df, min_support=0.0125, use_colnames=True)
                length = len(product_binary_df)
                del product_binary_df

                stationDict = dict()

                stationDict['station'] = stations[i]
                stationDict['weekday'] = weekdays[j]
                stationDict['daypart'] = dayparts[k]
                stationDict['created_timestamp'] = now
                stationDict['sample_size'] = length

                if not frequent_itemsets.empty:
                    rules = association_rules(frequent_itemsets, metric="lift", min_threshold=1)

                    del frequent_itemsets

                    rules = rules[(rules['lift'] >= 1) &
                                    (rules['confidence'] >= 0.5) &
                                    (rules['support'] >= 0.01)]

                    rules = rules[rules.apply(lambda x: (not re.search('SERVICE TABAC', str(x['antecedents']))) and (not re.search('SERVICE TABAC', str(x['consequents']))), axis=1)]

                    if not rules.empty:
                        rules['antecedents'] = {key: list(value) for key, value in rules['antecedents'].items()}
                        rules['consequents'] = {key: list(value) for key, value in rules['consequents'].items()}
                        stationDict['rules'] = rules.to_dict(orient='records')
                
                filter = {'station' : stationDict['station'],
                          'weekday': stationDict['weekday'],
                          'daypart': stationDict['daypart']
                          }
                update =  {'$set': stationDict}
                resultList.append(UpdateOne(filter,update,upsert=True))
    return resultList

tqdm.pandas()

mongo_uri = os.getenv('MONGO_URI', 'mongodb://root:example@localhost:27017')
client = MongoClient(mongo_uri)

db = client['cstore']

collection = db['baskets']
projection = {'_id': 0}
query = {"price_sum": {"$gt": 0}}
df = pd.DataFrame(list(collection.find(query, projection)))
stations = list(df['station'].unique())
df["station"] = df["station"].astype("category")
df[['price_sum']] = df[['price_sum']].apply(pd.to_numeric, downcast="float")

print("--------------------------------")
print("Removing duplicates")
df = removeProductAddedAndRemoved(df)

print("--------------------------------")
print("Removing Brands")
df = removeBrands(df)

print("--------------------------------")
print("Splitting dataframe")
gas,products = basketContains(df)

print("--------------------------------")
print("Modifying Products")
products = addDaypartAndWeekday(products)

print("--------------------------------")
print("Modifying Gas")
gas = addFuelPersonas(gas)
gas = addFuelCategories(gas)

now = datetime.utcnow()

print("-- product rule mining --")
collection = db['product_rules']
collection.create_index([("station", -1), ("weekday", -1), ("daypart", -1)], unique=True)
collection.bulk_write(productRuleMining(products))

del products

print("-- Fuel rule mining --")
collection = db['fuel_rules']
collection.create_index([("station", -1)], unique=True)
collection.bulk_write(fuelRuleMining(gas))

print("-- Fuel category rule mining --")
collection = db['fuel_category_rules']
collection.create_index([("station", -1)], unique=True)
collection.bulk_write(fuelCategoryRuleMining(gas))

print("Rule mining done")
print("--------------------------------")