/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Snapshot;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class FN_Metric.
 * Implements a metric based on false negatives.
 *
 * @author Tommy
 */
public class FN_Metric extends BetterMinMetric {

	/** The absolute. */
	private boolean absolute;

	/**
	 * Instantiates a new fn_ metric.
	 *
	 * @param absolute the absolute flag
	 */
	public FN_Metric(boolean absolute) {
		this.absolute = absolute;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		int detectionHits = 0;
		Snapshot snap;
		expData.resetIterator();
		while(expData.hasNextSnapshot()){
			snap = expData.nextSnapshot();
			if(!Metric.anomalyTrueFalse(anomalyEvaluations.get(snap.getTimestamp()))){
				if(snap.getInjectedElement() != null && snap.getInjectedElement().getTimestamp().compareTo(snap.getTimestamp()) == 0)
					detectionHits++;
			} 
		}
		if(!absolute)
			return 1.0*detectionHits/expData.obsNumber();
		else return detectionHits;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "False Negatives";
	}

}
