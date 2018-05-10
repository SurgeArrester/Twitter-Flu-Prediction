import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import twitter4j.Status;


public class destinationEstimator extends BaseRichBolt {
    private OutputCollector collector;
    private List<String> cityList;
    private String patternString;
    private Pattern pattern;
    private Matcher matcher;
    
    public destinationEstimator(List<String> cityList){
        this.cityList = cityList;
        this.patternString = "\\b(" + StringUtils.join(cityList, "|") + ")\\b";
        this.pattern = Pattern.compile(patternString);
        
    }
    
	@Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }
	
	@Override
    public void execute(Tuple input) {
		Object place = input.getValue(0);
		String placeString = place.toString();
		String text = (String) input.getValue(1);
		matcher = pattern.matcher(text);
		
		while (matcher.find()) {
        	collector.emit(new Values(place + text, matcher.group(1)));	
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("place", "text"));
    }
}
