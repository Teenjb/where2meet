import pandas as pd
import tensorflow as tf
import tensorflow_recommenders as tfrs
from flask import Flask, jsonify
from sklearn.model_selection import train_test_split
from sklearn.cluster import KMeans

app = Flask(__name__)

# Load the data into pandas dataframe
data = pd.read_excel('restaurant_final_mood.xlsx')
pd.set_option('display.max_columns', None)

# Preprocessing data
null_columns = data.isnull().any(axis=0)
columns_with_null = ['Lat', 'Lang']
data[columns_with_null] = data[columns_with_null].fillna(0)  # Replace Lat Lang Null

new_harga_label = "Missing"
data['Harga'] = data['Harga'].fillna(new_harga_label)  # Replace Harga Null

original_preprocessed_data = data.copy()  # Make a copy of the original data frame
important_data = data

# One-hot encoding
all_keywords = important_data['Keyword 1'].unique().tolist() + important_data['Keyword 2'].unique().tolist()
all_keywords = list(set(all_keywords))

keyword1_encoded = pd.get_dummies(important_data['Keyword 1'])
keyword2_encoded = pd.get_dummies(important_data['Keyword 2'])

data_encoded = pd.concat([important_data, keyword1_encoded, keyword2_encoded], axis=1)
data_encoded.drop(columns=['Keyword 1', 'Keyword 2'], axis=1, inplace=True)

data_combined = pd.concat(
    [data_encoded[columns].sum(axis=1).rename(columns) if len(data_encoded[columns].shape) == 2 else data_encoded[columns]
     for columns in data_encoded.columns.unique()], axis=1)

# K-means clustering
X_train, X_test = train_test_split(data_combined, test_size=0.2, random_state=42)
coordinates = X_train[['Lat', 'Lang']]

k = 5  # Number of clusters
n_init = 10  # Number of times the algorithm will be run
model = KMeans(n_clusters=k, n_init=n_init, random_state=42)
model.fit(coordinates)

X_train.loc[:, 'Cluster'] = model.labels_

dataset = tf.data.Dataset.from_tensor_slices((X_train[['Lat', 'Lang']], X_train['Cluster']))


# Recommendation model using TFRS
class RecommendationModel(tfrs.Model):
    def __init__(self):
        super().__init__()
        self.embedding = tf.keras.layers.Embedding(k, embedding_dim)
        self.flatten = tf.keras.layers.Flatten()
        self.dense = tf.keras.layers.Dense(64, activation='relu')
        self.last_dense = tf.keras.layers.Dense(1)

    def call(self, inputs):
        lat, lang = inputs
        embedding = self.embedding(lang)
        flattened = self.flatten(embedding)
        dense_output = self.dense(flattened)
        return self.last_dense(dense_output)

    def compute_loss(self, inputs, training=False):
        lat, lang = inputs
        predictions = self.call((lat, lang))
        loss = tf.reduce_mean(tf.square(predictions - tf.cast(lang, tf.float32)))
        return loss


embedding_dim = 16  # Dimensionality of the embedding layer
recommendation_model = RecommendationModel()

loss = tf.keras.losses.MeanSquaredError()
metrics = [tf.keras.metrics.MeanSquaredError()]

recommendation_model.compile(optimizer=tf.keras.optimizers.Adam(), loss=loss, metrics=metrics)

batch_size = 32
epochs = 10
recommendation_model.fit(dataset.batch(batch_size), epochs=epochs, verbose=0)  # Set verbose=0 to disable printing epochs

@app.route('/recommend', methods=['POST'])
def hello_http(request):
    request_json = request.get_json(silent=True)
    latitude = request_json['latitude']
    longitude = request_json['longitude']
    keywords = request_json['keywords']

    recommended_restaurants = recommend_restaurants(latitude, longitude, keywords, model)
    return jsonify(recommended_restaurants.to_dict(orient='records'))  # Convert DataFrame to a JSON-serializable format

def recommend_restaurants(latitude, longitude, keywords, model, n=10):
    query_data = pd.DataFrame({
        'Lat': [latitude],
        'Lang': [longitude]
    })

    query_data['Cluster'] = model.predict(query_data[['Lat', 'Lang']])

    cluster_data = X_train[X_train['Cluster'] == query_data['Cluster'].values[0]].copy()
    cluster_data['Score'] = cluster_data[keywords].sum(axis=1)

    recommendations = cluster_data.nlargest(n, 'Score')

    return recommendations[['Nama', 'Location ID', 'Jenis', 'Rating', 'Harga', 'Jumlah Review', 'Lat', 'Lang']]

if __name__ == '__main__':
    app.run()
