/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.Snapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class TP_Metric.
 * Implements a metric based on true positives.
 *
 * @author Tommy
 */
public class TP_Metric extends BetterMaxMetric {
	
	/** The absolute flag. */
	private boolean absolute;

	/**
	 * Instantiates a new tp_ metric.
	 *
	 * @param absolute the absolute flag
	 */
	public TP_Metric(boolean absolute) {
		this.absolute = absolute;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(LinkedList<Snapshot> snapList, HashMap<Date, Double> anomalyEvaluations) {
		int detectionHits = 0;
		int undetectable = 0;
		Snapshot snap;
		for(int i=0;i<snapList.size();i++){
			snap = snapList.get(i);
			if(snap.getInjectedElement() != null && snap.getInjectedElement().happensAt(snap.getTimestamp())){
				if(Metric.anomalyTrueFalse(anomalyEvaluations.get(snap.getTimestamp())))
					detectionHits++;
				i++;
				while(i<snapList.size()){
					if(snap.getInjectedElement().compliesWith(snapList.get(i).getTimestamp())){
						i++;
						undetectable++;
					}
					else break;
				}
				i--;
			} 
		}
		if(snapList.size() > 0){
			if(!absolute)
				return 1.0*detectionHits/(snapList.size()-undetectable);
			else return detectionHits;
		} else return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "True Positives";
	}

}
