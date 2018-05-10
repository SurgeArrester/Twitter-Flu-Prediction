# from twitter_machine_learning import load_tweets, TweetFeaturizer, create_train_set, create_logistic_classifier
from os import listdir
# import json
import pickle as pk
import sys


train_file = r"data/train_set_1.json"
new_dir = r"flu_related_tweets"
classified_dir = r"classified_tweets"

'''
print("Loading tweets")
tweets = load_tweets(train_file)
print("Done")
      
print("Creating featurizer")
tf = TweetFeaturizer(tweets, 4, 7)
pickleOut = open("pickles/tweetFeaturizer.pickle", "wb")
pk.dump(tf, pickleOut)
pickleOut.close()
print("Done")

print("Creating training set")
train_set = create_train_set(tf, tweets)
print("Done")

print("Creating classifier")
classifier = create_logistic_classifier(train_set)

pickleOut = open("pickles/classifier.pickle", "wb")
pk.dump(classifier, pickleOut)
pickleOut.close()
print("Done")
'''

tf = pk.load(open("pickles/tweetFeaturizer.pickle", "rb"))
classifier = pk.load(open("pickles/classifier.pickle", "rb"))

text = sys.argv[1]
label = classifier.classify(tf.featurize(text))
print(label)
'''
for file in listdir(new_dir):
    rel_count = 0
    irr_count = 0
    out = open(r"{}\{}".format(classified_dir,file), "w", encoding="utf8")
    print(r"{}\{}".format(new_dir, file))
    for line in open(r"{}/{}".format(new_dir, file), "r", encoding="utf8"):
        parsed = json.loads(line)
        label = classifier.classify(tf.featurize(parsed["text"]))
        if label == "REL":
            rel_count = rel_count + 1
        else:
            irr_count = irr_count + 1
        out.write(json.JSONEncoder().encode({"text":parsed["text"], "created_at":parsed["created_at"], "type":label}) + "\n")
    print("REL: {} IRR: {}".format(rel_count, irr_count))
'''     