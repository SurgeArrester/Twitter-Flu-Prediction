import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.io.*;

import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import twitter4j.Status;


public class CheckFluPyBolt extends BaseRichBolt {
    private OutputCollector collector;
    
	@Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }
	
	@Override
    public void execute(Tuple input) {
		Object place = input.getValue(0);
		String text = (String) input.getValue(1);
		
		String consoleOutput = null;
		try {
	        Process p = Runtime.getRuntime().exec("./python/classify_new_data.py \"" + text + "\"");
	        
	        BufferedReader stdInput = new BufferedReader(new 
	             InputStreamReader(p.getInputStream()));
	
	        BufferedReader stdError = new BufferedReader(new 
	             InputStreamReader(p.getErrorStream()));
	        
	        consoleOutput = stdInput.readLine();
        
		} catch (IOException e) {
            consoleOutput = "An error occurred with the classifier";
        }
        
		collector.emit(new Values(place, text + " Related to flu: " + consoleOutput));	
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("place", "text"));
    }
}