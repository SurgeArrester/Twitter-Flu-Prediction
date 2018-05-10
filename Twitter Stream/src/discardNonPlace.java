import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import twitter4j.JSONObject;
import twitter4j.Status;


public class discardNonPlace extends BaseRichBolt {
    private OutputCollector collector;

    
	@Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }
	
	@Override
    public void execute(Tuple input) {
		Object value = input.getValue(0);
        String text = ((Status) value).getText();
        String fullString = value.toString();
        Integer startPlaceString = fullString.indexOf("place=");
        Integer endPlaceString = fullString.indexOf(", retweetCount=");
        String placeString = fullString.substring(startPlaceString, endPlaceString);
        
        if (!Objects.equals(placeString, "place=null")) {
        	collector.emit(new Values(placeString, text));	
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("place", "text"));
    }

}
