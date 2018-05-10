import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.*;

/**
 * Created with IntelliJ IDEA.
 * User: joning
 * Date: 06.09.13
 * Time: 20:49
 * To change this template use File | Settings | File Templates.
 */
public class TwitterFilterTopology {

    private static String consumerKey = "w3EU8GdlaoBRKpV1Fsv6gz4MG";
    private static String consumerSecret = "3dMfugvWRYX6olkgU7afSUPlMckJ9feuT5KMerub4TCJ9c4dwN";
    private static String accessToken = "932643962959093760-vcx8nb5umxBo8XnBlBEJuGfHOpycCbF";
    private static String accessTokenSecret = "d7AxAhi1zrPjpeVABssIY0tswHXxr5gMUaaNJvRuRxsmA";
    
    private static List<String> readCsvToArray(String fileLocation) throws IOException {
        String city = "";
        List<String> cityList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));
        while ((city = br.readLine()) != null) {
        	cityList.add(city);
        }   
        return cityList;
    }

    public static void main(String[] args) throws Exception {                
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        
        List<String> cityList = readCsvToArray("./static/simplemaps-worldcities-basic-unfiltered.csv");

        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
        
        TopologyBuilder builder = new TopologyBuilder();

        FilterQuery tweetFilterQuery = new FilterQuery();
        
      	tweetFilterQuery.track(new String[]{"going to", 
      										"on my way to", 
      										"travelling to", 
      										"popping to", 
      										"vacationing to", 
      										"driving to", 
      										"flying to", 
      										"cycling to",
      										"on the train to",
      										"sailing to",
      										"business trip to",
      										"headed to",
      										"hollybops",
      										"holiday in",
      										"long weekend in",
      										"off to",
      										"popping to",
      										"away to"});
      	
      	// See https://github.com/kantega/storm-twitter-workshop/wiki/Basic-Twitter-stream-reading-using-Twitter4j
      	
        TwitterSpout spout = new TwitterSpout(consumerKey, consumerSecret, accessToken, accessTokenSecret, tweetFilterQuery);
        discardNonPlace discardNonPlace = new discardNonPlace();
        CheckFluPyBolt checkFluPyBolt = new CheckFluPyBolt();
        destinationEstimator destinationEstimator = new destinationEstimator(cityList);
        FileWriterBolt fileWriterBolt = new FileWriterBolt("MyTweets.txt");
        
        builder.setSpout("TwitterSpout", spout);				// Initial spout to get data from twitter
        builder.setBolt("discardNonPlace", discardNonPlace).shuffleGrouping("TwitterSpout");	// Remove all tweets that don't have a place associated
        builder.setBolt("CheckFluPyBolt", checkFluPyBolt).shuffleGrouping("discardNonPlace");
        builder.setBolt("destinationEstimator", destinationEstimator).shuffleGrouping("CheckFluPyBolt");
        builder.setBolt("FileWriterBolt", fileWriterBolt).shuffleGrouping("destinationEstimator");	// Write to file


        Config conf = new Config();
        conf.setDebug(false);

        conf.setMaxTaskParallelism(1);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("twitter-fun", conf, builder.createTopology());

        Thread.sleep(100000);

        cluster.shutdown();
    }
}
