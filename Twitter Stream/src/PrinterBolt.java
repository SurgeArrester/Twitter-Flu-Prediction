import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class PrinterBolt extends BaseBasicBolt {

    private String prefix;

    public PrinterBolt(String prefix){
        this.prefix = prefix;
    }


    @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {

        System.err.println(prefix +": "+tuple);

        //This bolt does not emit any values further, only print object values to the console
  }

  public void declareOutputFields(OutputFieldsDeclarer ofd) {
      //This bolt does not emit any values. Hence, it does not declare any fields for its output.
  }

}